package com.fqingz.demo.file

/**
  * @author Fang Qing
  * @date 2019/11/6 17:52
  */
class RawFileHandleThread(regexName:String,regex:String,config:RawConfig) extends Thread{
  var interrupted = false

  val prop = PropertiesUtil.loadProperties(new FileReader("etc/producer.properties"))
  var kafkaProducer:KafkaProducer[String,String] = null

  val sourcePaths = config.sourceFilePaths
  val regexFilter = new Regex(regex)

  val workPath = config.handleFilePath  + s"${regexName}/"

  val backPath = config.backupFilePath

  def initW() = {
    val workDir = new File(workPath)
    workDir.mkdirs()
  }

  override def run(): Unit = {

    initW()
    try {
      while (!Thread.currentThread().isInterrupted && !interrupted) {
        runLoop()
      }
    } catch {
      case e: Throwable => error("RedisRawDataFetchThread error",e)
        throw e
    } finally {

    }
  }


  def runLoop() = {

    sourcePaths.foreach(sourcePath => {
      val fileList = subDir(new File(sourcePath))
      fileList.foreach(f => {
        val tmp = new File(workPath  + f.getName + ".tmp")
        f.renameTo(tmp)
        tmp.renameTo(new File(workPath + f.getName))
      })
    })

    val fileList = subDir(new File(workPath)).toList
    import sdc.utils.date.DateUtils
    if(fileList.nonEmpty) {
      if(kafkaProducer == null) kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer[String, String](prop)
      handleFiles(fileList)

      fileList.foreach(f => {
        val tableName = RawTools.getTableName(f.getName)
        val backupDir = backPath + s"/${DateUtils.getStandCurrentDay}/${tableName}/"
        val dir = new File(backupDir)
        dir.mkdirs()
        val desFile = new File(backupDir + s"${f.getName}")
        if(desFile.exists()) desFile.delete()
        f.renameTo(desFile)
      })
    } else {
      if(kafkaProducer != null) {
        kafkaProducer.close()
        kafkaProducer = null
      }
      Thread.sleep(2000)
    }
  }

  def subDir(dir:File):Iterator[File] ={
    dir.listFiles().filter(_.isFile()).filter(f => {
      val name = f.getName
      regexFilter.pattern.matcher(name).find()
    }
    ).toIterator
  }



  def handleFiles(fileList:List[File]) = {
    fileList.foreach(f => {
      val tableName = RawTools.getTableName(f.getName)
      val splitCol = config.getPreSplitRegionCol(tableName)
      val columns = config.getColumns(tableName)
      val records = getByEFileData(f)
      val idx = RawTools.getColumnPos(columns,splitCol)
      val par = 20
      records.groupBy(r => {
        val hashCode = RawTools.getHashCode(idx,r)
        val p = RawTools.getPartition(hashCode,par)
        p
      }).foreach(r => {
        val partition = r._1
        val rs = r._2
        sendToKafka(tableName,columns,rs,partition)
      })
    })
  }



  def sendToKafka(tableName:String,columns:Array[String],records:Array[Row],partition:Int) = {
    try {
      val opt = config.getTopic(tableName)
      opt.foreach(topic =>{
        val e = RawEntity(tableName,columns,records)
        val json = JsonUtils.toJson(e)
        val pr = new ProducerRecord[String, String](topic, partition, partition + "", json)
        kafkaProducer.send(pr).get(15,TimeUnit.SECONDS)
      })
    } catch {
      case e:Throwable => error(e.toString)
        kafkaProducer.close()
        throw e
    }


  }

  def getByEFileData(fileMs:File):Array[Array[String]] = {
    val content = EFileUtils.getXmlData(fileMs.getPath)
    content
  }

  def close = {
    interrupted = true
  }

}

class RawFileFilter(regexName:String,regex:String,config:RawConfig) extends Actor with Logging {

  override def receive: Receive = null

  val handleThread = new RawFileHandleThread(regexName,regex,config)

  override def preStart(): Unit = {
    start
  }

  def start = {
    handleThread.start()
  }

  def close = {
    handleThread.close
  }


}

class FileWorker(config:Config) extends Actor with TimeOutScheduler with Logging {

  override  val loggerName = "worker"


  private val address = ActorUtil.getFullPath(context.system, self.path)

  val rawConf = new RawConfig
  rawConf.init

  override val supervisorStrategy =
    OneForOneStrategy() {
      case ex: Throwable =>
        error(s"supervisorStrategy $address",ex)
        Restart
    }

  override def receive: Receive = {
    case Terminated(_) =>
  }

  override def preStart(): Unit = {
    start
  }


  def start = {
    val l = rawConf.getRegexRules()
    l.foreach(r => {
      val name = r._1 + "_handle_actor"
      context.actorOf(Props(new RawFileFilter(r._1,r._2,rawConf)).withDispatcher("file-worker.single-thread-dispatcher"),name)
    })
  }

  def close = {
    info(s"file worker close")
    context.stop(self)
    context.system.terminate()
  }


  override def postStop(): Unit = {
    info(s"Worker is going down....")
    context.system.terminate()
  }
}

object FileWorkerApp extends Logging {

  override  val loggerName = "file worker app"
  private def uuid = java.util.UUID.randomUUID.toString


  def main(args: Array[String]): Unit = {

    val id = uuid

    val config = ConfigFactory.parseFileAnySyntax(new File("etc/fileworker.conf"),
      ConfigParseOptions.defaults.setAllowMissing(true))

    val workConfig =  config
      .withValue("akka.actor.provider", ConfigValueFactory.fromAnyRef("akka.remote.RemoteActorRefProvider"))
      .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef("0"))


    implicit val system = ActorSystem("FileHandleWorker", workConfig)

    system.actorOf(Props(classOf[app.raw.filetask.FileWorker],workConfig).withDispatcher("file-worker.single-thread-dispatcher"),
      classOf[app.raw.filetask.FileWorker].getSimpleName)

    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def run(): Unit = {
        info("Maybe we have received a SIGINT signal from parent process, " +
          "start to cleanup resources....")
        system.terminate()
      }
    })

    Await.result(system.whenTerminated, Duration.Inf)
  }

}

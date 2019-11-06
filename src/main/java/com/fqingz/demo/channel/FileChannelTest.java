package com.fqingz.demo.channel;

import io.netty.util.CharsetUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author FangQing
 * @date 2019/11/6 0:18
 */
public class FileChannelTest {

    public static void main(String[] args) {
        File file = new File ("C:\\Users\\Administrator\\Desktop\\test.txt");
        write (file);
        read (file);
    }

    private static void read(File file) {
        try (FileInputStream fis = new FileInputStream (file);
             FileChannel channel = fis.getChannel ( )) {
            ByteBuffer buffer = ByteBuffer.allocate (1024);
            channel.read (buffer);
            System.out.println (buffer );
            buffer.flip ( );
            System.out.println (buffer );
            String str = Charset.forName ("UTF-8").decode (buffer).toString ( );
            System.out.println (buffer );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    private static void write(File file) {
        try (FileOutputStream fos = new FileOutputStream (file, true);
             FileChannel fileChannel = fos.getChannel ( )) {
            ByteBuffer buffer = ByteBuffer.allocate (1024);
            System.out.println (buffer.capacity ( ));
            String str = "我要测试的内容121231231231231234是的啊啊啊啊啊啊啊啊啊啊啊" + new Date ( ) + "\\n";
            System.out.println (str.getBytes (CharsetUtil.UTF_8).length);
            buffer.put (str.getBytes ( ));
            buffer.flip ( );
            fileChannel.write (buffer);
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }
}

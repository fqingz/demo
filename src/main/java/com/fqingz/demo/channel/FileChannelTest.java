package com.fqingz.demo.channel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * @author FangQing
 * @date 2019/11/6 0:18
 */
public class FileChannelTest {

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\test.txt");
        try (FileOutputStream fos = new FileOutputStream(file, true);
             FileChannel fileChannel = fos.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            System.out.println(buffer.capacity());
            String str = "我要测试的内容" + new Date() + "\\n";
            System.out.println(str.getBytes().length);
            buffer.put(str.getBytes());
            buffer.flip();
            fileChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

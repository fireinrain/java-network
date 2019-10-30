package com.sunrise.network.studyapi.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * @description: 读取一个文件中的内容
 * @date: 2019/10/28
 * @author: lzhaoyang
 */
public class Demo2 {
    public static void main(String[] args) {
        // testReadDataFromFile();
        //testGetChannle();
        testWriteDataToFile();
    }

    private static void testWriteDataToFile() {
        Path file = Paths.get(".gitignore-write");
        if (!Files.exists(file)){
            try {
                Files.createFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(FileChannel fileChannel = FileChannel.open(Paths.get(".gitignore-write"), StandardOpenOption.WRITE)) {
            byte[] bytes = new byte[1024];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            String message = "I am a git copy";
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 10 ; i++) {
                stringBuilder.append(message).append("\r\n");
            }
            byte[] datas = stringBuilder.toString().getBytes();
            byteBuffer.put(datas);
            //切换为写模式
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            System.out.println("写入完成");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void testGetChannle() {
        try {
            //工厂类 获取通道
            ReadableByteChannel readableByteChannel = Channels.newChannel(new FileInputStream(".gitignore"));
            FileChannel fileChannel = FileChannel.open(Paths.get(".gitignore"), StandardOpenOption.READ);
            SocketChannel.open();
            ServerSocketChannel.open();
            DatagramChannel.open();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void testReadDataFromFile() {
        FileChannel channel = null;
        try {
            RandomAccessFile accessFile = new RandomAccessFile(".gitignore", "rw");
            channel = accessFile.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            StringBuilder stringBuilder = new StringBuilder();
            int length;
            while ((length = channel.read(byteBuffer)) != -1) {
                byteBuffer.flip();
                byte[] array = byteBuffer.array();
                stringBuilder.append(new String(array, 0, length));
                byteBuffer.clear();

            }
            System.out.println(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //channel 实现了Closeable 接口，可以使用try-wuth-resource
    private static void testReadDataFromFile2() {
        try(RandomAccessFile accessFile = new RandomAccessFile(".gitignore", "rw");
            FileChannel channel = accessFile.getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            StringBuilder stringBuilder = new StringBuilder();
            int length;
            while ((length = channel.read(byteBuffer)) != -1) {
                byteBuffer.flip();
                byte[] array = byteBuffer.array();
                stringBuilder.append(new String(array, 0, length));
                byteBuffer.clear();

            }
            System.out.println(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

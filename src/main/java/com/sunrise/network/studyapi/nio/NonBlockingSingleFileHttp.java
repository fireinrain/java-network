package com.sunrise.network.studyapi.nio;

import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

/**
 * @description: 非阻塞单文件Http服务器
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/19 11:17 PM
 */
public class NonBlockingSingleFileHttp {
    private ByteBuffer contentByteBuff;
    private int port = 80;

    /**
     * http header 头
     *
     * data 数据
     * @param contentByteBuff
     * @param encoding
     * @param MIMEType
     * @param port
     */
    public NonBlockingSingleFileHttp(ByteBuffer contentByteBuff, String encoding, String MIMEType, int port) {
        this.port = port;

        String header = "HTTP1.0 200 OK\r\n"
                + "Server: NonBlockingSingleFileHttp\r\n"
                + "Content-length: " + contentByteBuff.limit() + "\r\n"
                + "Content-type: " + MIMEType + "\r\n\r\n";
        byte[] headerBytes = header.getBytes();

        //所需要的缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(contentByteBuff.limit() + headerBytes.length);
        //将数据存入缓冲区
        byteBuffer.put(headerBytes);
        byteBuffer.put(contentByteBuff);
        //切换为读模式
        byteBuffer.flip();
        this.contentByteBuff = contentByteBuff;
    }

    //服务器运行入口
    public void run() throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    }
}

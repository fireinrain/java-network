package com.sunrise.network.studyapi;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description: 安全的套接字 SSL支持
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/4 10:30 PM
 */
public class Demo12 {
    public static void main(String[] args) {
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        try {
            //安全客户端套接字
            Socket secureSocket = socketFactory.createSocket("127.0.0.1", 8080);
            //服务端安全套接字
            ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
            ServerSocket secureServerSocket = serverSocketFactory.createServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/30 8:56 PM
 */
public class Demo11 {
    public static void main(String[] args) {
        //testLocalPortScan();
        testServceSocketSet();
    }

    //服务端套接字设置参数
    //SO_TIMEOUT socket accept() 超时
    //SO_REUSEADDR 重用端口
    //SO_RCVBUF 接受缓冲大小
    private static void testServceSocketSet() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setSoTimeout(1000);
            if (!serverSocket.getReuseAddress()) {
                serverSocket.setReuseAddress(true);
            }
            System.out.println(serverSocket.getReceiveBufferSize());
            if (serverSocket.getReceiveBufferSize() < 1024) {
                serverSocket.setReceiveBufferSize(1024);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testLocalPortScan() {
        for (int i = 1; i <= 65535; i++) {
            try {
                ServerSocket serverSocket = new ServerSocket(i);
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("this port: " + i + " is taken");
            }
        }
    }
}

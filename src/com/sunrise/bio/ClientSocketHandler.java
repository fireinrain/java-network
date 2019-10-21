package com.sunrise.bio;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 客户端client处理器
 * @date: 2019/10/21
 * @author: lzhaoyang
 */
public class ClientSocketHandler implements Runnable {
    private Socket clientSocket;
    private AtomicInteger clientCount;

    public ClientSocketHandler(Socket clientSocket, AtomicInteger clientCount) {
        this.clientSocket = clientSocket;
        this.clientCount = clientCount;
    }

    @Override
    public void run() {
        System.out.println("get a client, handler------2s");
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("handler finish-------");
        //处理完客户端-1
        int decrementAndGet = clientCount.decrementAndGet();
        System.out.println("------->此时连接客户端数量为: "+decrementAndGet);
    }
}

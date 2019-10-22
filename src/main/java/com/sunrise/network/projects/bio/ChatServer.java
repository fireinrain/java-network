package com.sunrise.network.projects.bio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @date: 2019/10/21
 * @author: lzhaoyang
 */
public class ChatServer {
    private static InetAddress INET_ADDRESS;
    private static final int WAIT_QUEUE = 100;
    private static final int PORT = 8080;
    private static final String secreteKey = "chatwithme";

    private static AtomicInteger clientCount = new AtomicInteger();
    private static ExecutorService executorService = Executors.newScheduledThreadPool(4);
    private static ConcurrentHashMap<String,Socket> handleClientSocketMap = new ConcurrentHashMap<>();
    //获取本机ip
    static {
        try {
            INET_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT, WAIT_QUEUE, INET_ADDRESS);
            String bindIp = serverSocket.getInetAddress().getHostAddress();
            int port = serverSocket.getLocalPort();
            System.out.println("|----------ChatServer listen at: " + bindIp + ":" + port+"----------|");

            while (true){
                Socket clientSocket = serverSocket.accept();
                //客户端连接数量+1
                int incrementAndGet = clientCount.incrementAndGet();
                handleClientSocketMap.put(String.valueOf(incrementAndGet),clientSocket);
                System.out.println("当前连接客户端数量为: " + incrementAndGet);
                //提交线程池
                executorService.submit(new ClientSocketHandler(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static InetAddress getInetAddress() {
        return INET_ADDRESS;
    }

    public static int getPORT() {
        return PORT;
    }

    public static ConcurrentHashMap<String, Socket> getHandleClientSocketMap() {
        return handleClientSocketMap;
    }

    public static AtomicInteger getClientCount() {
        return clientCount;
    }

    public static String getSecreteKey() {
        return secreteKey;
    }
}

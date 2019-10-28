package com.sunrise.network.studyapi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: 服务端socket
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/27 10:09 PM
 */
public class Demo9 {
    public static void main(String[] args) {
        //testServeSocket();
        //testMultiThreadServer();
        testPooledThreadServer();

    }

    private static void testPooledThreadServer() {
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(8888));
            while (true){
                try  {
                    Socket accept = serverSocket.accept();
                    executorService.submit(new PooledDynamicTask(accept));
                }catch (IOException e){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testMultiThreadServer() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(8888));
            while (true){
                try  {
                    Socket accept = serverSocket.accept();
                    DynamicTime dynamicTime = new DynamicTime(accept);
                    dynamicTime.start();
                }catch (IOException e){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testServeSocket() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(8888));
            while (true){
                try (Socket accept = serverSocket.accept()) {
                    OutputStream outputStream = accept.getOutputStream();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    outputStream.write((localDateTime.toString()+"\r\n").getBytes());
                    outputStream.flush();
                }catch (IOException e){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class PooledDynamicTask implements Runnable{

    private Socket socket;
    public PooledDynamicTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = this.socket.getOutputStream()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String name = Thread.currentThread().getName();
            outputStream.write((localDateTime.toString()+": "+name+"\r\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (this.socket!=null){
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
class DynamicTime extends Thread{
    private Socket socket;

    public DynamicTime(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = this.socket.getOutputStream()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String name = Thread.currentThread().getName();
            outputStream.write((localDateTime.toString()+": "+name+"\r\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (this.socket!=null){
                try {
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

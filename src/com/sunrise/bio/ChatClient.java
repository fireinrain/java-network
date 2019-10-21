package com.sunrise.bio;

import javax.sql.rowset.spi.SyncResolver;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

/**
 * @description:
 * @date: 2019/10/21
 * @author: lzhaoyang
 */
public class ChatClient {
    private static SocketAddress socketAddress = new InetSocketAddress(ChatServer.getInetAddress(), ChatServer.getPORT());

    public static void main(String[] args) throws InterruptedException {
        System.out.println("|--------ChatClient start---------|");

        Socket socket;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        socket = new Socket();

        try {
            socket.connect(socketAddress, 5 * 1000);
            System.out.println("connect to: " + socket);
            outputStream = socket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutputStream);

            //输入用户名
            String userName = null;
            String password = null;
            System.out.println("#####请输入昵称#########");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                userName = scanner.next();
            }
            System.out.println("#####请输入秘钥#######");
            if (scanner.hasNext()) {
                password = scanner.next();
            }

            System.out.println();
            //验证秘钥和用户名
            processPassAndUsername()

            while (scanner.hasNext()) {
                String consoleStr = scanner.next();
                if (consoleStr.startsWith("-c quit")) {
                    printWriter.println();
                    printWriter.flush();
                    System.out.println("退出聊天");
                    break;
                }
                printWriter.println(consoleStr);
                printWriter.flush();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("|--------ChatClient stop----------|");
    }

    private static void processPassAndUsername() {
    }

}

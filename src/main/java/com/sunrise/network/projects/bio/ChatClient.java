package com.sunrise.network.projects.bio;

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
            //写入数据
            outputStream = socket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream);

            //读取数据
            inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream);

            //输入用户名
            String userName = null;
            String password = null;
            System.out.println("###### 请输入昵称 ######");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                userName = scanner.next();
            }
            System.out.println("###### 请输入秘钥 ######");
            if (scanner.hasNext()) {
                password = scanner.next();
            }
            UserInfo userInfo = new UserInfo(userName, password);

            System.out.println("------ 正在校验密钥 ------");
            //验证秘钥和用户名
            boolean passed = checkPassAndUsername(outputStreamWriter, inputStreamReader, userInfo);
            if (passed) {
                //进入聊天
                System.out.println("###### 进入聊天 ######");
                System.out.println("###### 输入-c quit 退出聊天 ######");
                while (scanner.hasNext()) {
                    String consoleStr = scanner.next();
                    if (consoleStr.startsWith("-c quit")) {
                        System.out.println("退出聊天");
                        socket.shutdownOutput();
                        break;
                    }
                    outputStreamWriter.write(consoleStr);
                    outputStreamWriter.flush();

                }
            }
            System.out.println("------密钥错误，退出程序------");


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

    /**
     * 校验密钥
     *
     * @param outputStreamWriter
     * @param inputStreamReader
     * @param userInfo
     * @return bool
     */
    private static boolean checkPassAndUsername(OutputStreamWriter outputStreamWriter, InputStreamReader inputStreamReader, UserInfo userInfo) {
        String userName = userInfo.getUserName();
        String secretKey = userInfo.getSecretKey();
        int c;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            outputStreamWriter.write(userName + "|" + secretKey);
            outputStreamWriter.flush();
            while ((c = inputStreamReader.read()) != -1) {
                stringBuilder.append((char)c);
            }
            String str = stringBuilder.toString();
            if ("YES".equals(str)){
                return true;
            }
            if ("NO".equals(str)){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}

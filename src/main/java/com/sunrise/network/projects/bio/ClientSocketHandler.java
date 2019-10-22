package com.sunrise.network.projects.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @description: 客户端client处理器
 * @date: 2019/10/21
 * @author: lzhaoyang
 */
public class ClientSocketHandler implements Runnable {
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientSocketHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            //给客户端发送文本消息
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

            String welcomeStr = ConsoleUtils.prettifyInput("Welcome to sunrise ChatServe!");

            sendMsgToClient(outputStream, welcomeStr);
            String nickName = ConsoleUtils.prettifyInput("Please input your nickname to chat with others?");
            sendMsgToClient(outputStream, nickName);
            //获取客户端发送的nickName
            String name = readMsgFromClient(inputStream);
            //输入密钥
            String inputSecreteKey = ConsoleUtils.prettifyInput("Please input your secret key?");
            sendMsgToClient(outputStream,inputSecreteKey);
            //读取密钥
            String key = readMsgFromClient(inputStream);

            boolean isPass = checkSecreteKey(key);
            //校验不通过
            while (!isPass){
                String failMsg = ConsoleUtils.prettifyInput("Ops, fail to login in ChatServer due to your wrong secret key.");
                sendMsgToClient(outputStream,failMsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        System.out.println("handler finish-------");
        //处理完客户端-1
        int decrementAndGet = ChatServer.getClientCount().decrementAndGet();
        System.out.println("------->此时连接客户端数量为: " + decrementAndGet);
    }

    private boolean checkSecreteKey(String key) {
        return ChatServer.getSecreteKey().equals(key);
    }

    /**
     * 从客户端读取数据
     *
     * @param inputStream
     * @return string
     */
    private String readMsgFromClient(InputStream inputStream) throws IOException {
        int readValue = inputStream.read();
        StringBuilder stringBuilder = new StringBuilder();
        while (readValue != 13) {
            //客户端连接断开
            if (readValue == -1) {
                throw new IOException("client disconnect");
            }
            stringBuilder.append((char) readValue);
            readValue = inputStream.read();
        }
        return stringBuilder.toString().trim();

    }

    /**
     * 给客户端发送信息
     *
     * @param outputStream
     * @param welcomeStr
     */
    private void sendMsgToClient(OutputStream outputStream, String welcomeStr) {
        try {
            outputStream.write(welcomeStr.getBytes());
            //写入回车换行 \r 13 \n 10
            outputStream.write("\r".getBytes());
            outputStream.write("\n".getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

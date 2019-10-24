package com.sunrise.network.projects.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 客户端client处理器
 * @date: 2019/10/21
 * @author: lzhaoyang
 */
public class ClientSocketHandler implements Runnable {
    private Socket clientSocket;
    private InputStream inputStream;
    private String clientId;
    private String userName;
    private OutputStream outputStream;

    ClientSocketHandler(String clientId, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
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
            this.userName = readMsgFromClient(inputStream);
            //输入密钥
            String inputSecreteKey = ConsoleUtils.prettifyInput("Please input your secret key. You have 3 chances");
            sendMsgToClient(outputStream, inputSecreteKey);
            //读取密钥
            String key = readMsgFromClient(inputStream);
            int inputChance = 0;
            boolean isPass = checkSecreteKey(key);
            inputChance += 1;
            //校验不通过
            while (!isPass && inputChance <= 2) {
                String failMsg = ConsoleUtils.prettifyInput("Ops, fail to login in ChatServer due to your wrong secret key.");
                sendMsgToClient(outputStream, failMsg);
                String repeatKey = ConsoleUtils.prettifyInput("Please check your key and input again(chance " + (3 - inputChance) + ").");
                sendMsgToClient(outputStream, repeatKey);

                //读取key
                String repeatKeyStr = readMsgFromClient(inputStream);
                inputChance += 1;
                isPass = checkSecreteKey(repeatKeyStr);
            }

            if (inputChance > 2) {
                String discMsg = "You have input wrong key 3 times,Bye!";
                sendMsgToClient(outputStream, discMsg);
                throw new AuthException("Auth exception!");
            }

            // 提示校验成功   授权成功
            String successAuth = "Authorized success! Let's chat with others";
            sendMsgToClient(outputStream, ConsoleUtils.prettifyInput(successAuth));

            //告知所有授权客户端 来人啦(不发给自己)
            sendEnterChatToClients(this.userName);

            //如果授权成功,将其加入到socketMap中
            ChatServer.getHandleClientSocketMap().put(this.clientId + "&" + this.userName, clientSocket);
            ConsoleUtils.log("Now auth clients are: " + ChatServer.getHandleClientSocketMap().size());

            //发送聊天规则
            String chatRulesForClient = getChatRulesForClient();
            sendMsgToClient(outputStream,chatRulesForClient);
            //读取客户端发送消息
            String fromClient = readMsgFromClient(inputStream);

            parseClientMsg(fromClient);


        } catch (IOException | AuthException e) {
            //e.printStackTrace();
            ConsoleUtils.log(e.getMessage());
        }


        //有异常后统一将流关闭
        try {
            inputStream.close();
            outputStream.close();
            this.clientSocket.close();
            //将当前已经关闭的客户端从容器中移除
            ChatServer.getHandleClientSocketMap().remove(clientId);
            //给所有客户端发送 该用户下线的消息
            sendLeaveChatToClients(this.userName);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ConsoleUtils.log("handle finish");
        //剩余客户端数量
        int size = ChatServer.getHandleClientSocketMap().size();
        ConsoleUtils.log("Client online count: " + size);
    }

    /**
     * 解析客户端发送来的字符串，是否符合规则
     * @param fromClient
     */
    private void parseClientMsg(String fromClient) {
    }


    /**
     * 针对不同的客户端消息 分别处理
     * @param fromClient
     */
    private void handleClientMsg(String fromClient) {
    }


    /**
     * 私信
     * 发送聊天规则给客户端
     */
    private String getChatRulesForClient() {
        String rule =  "********************************* Chat Rules *********************************";
        String rule2 = "*******************| message format   |   means          |********************";
        String rule3 = "*******************| # your-message   |   send public message|****************";
        String rule4 = "******| @ people-name/id your-message |   send private message|***************";
        String rule5 = "***************| $ all                |   get all auth people list|************";

        return rule + "\r\n" + rule2 + "\r\n" + rule3 + "\r\n" + rule4 + "\r\n" + rule5;
    }

    /**
     * 告知所有的授权客户端，有客户端离开
     *
     * @param userName
     */
    private void sendLeaveChatToClients(String userName) {
        sendMsgChatToClients(userName, "leave the chat");
    }

    private void sendEnterChatToClients(String userName) {
        sendMsgChatToClients(userName, "enter the chat");
    }

    /**
     * 给所有授权的客户端发送消息
     *
     * @param userName
     * @param message
     */
    private void sendMsgChatToClients(String userName, String message) {
        ConcurrentHashMap<String, Socket> handleClientSocketMap = ChatServer.getHandleClientSocketMap();
        for (Map.Entry<String, Socket> clientSocket : handleClientSocketMap.entrySet()) {
            //String clientId = clientSocket.getKey();
            Socket socket = clientSocket.getValue();
            try {
                OutputStream outputStream = socket.getOutputStream();
                sendMsgToClient(outputStream, userName + " " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查秘钥是否正确
     *
     * @param key
     * @return
     */
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

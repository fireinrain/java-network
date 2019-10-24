package com.sunrise.network.projects.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            String userNickName = readMsgFromClient(inputStream);
            //校验名字内部不能有非法字符 如&
            boolean nicknameValid = checkNicknameValid(userNickName);
            while (!nicknameValid) {
                sendMsgToClient(outputStream, "Name can't contains &!!!");
                String s = readMsgFromClient(inputStream);
                nicknameValid = checkNicknameValid(s);
            }

            this.userName = userNickName;
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
            String chatRulesForClient = ConsoleUtils.sendClientChatRuleTable();
            sendMsgToClient(outputStream, chatRulesForClient);
            //读取客户端发送消息
            String fromClient = readMsgFromClient(inputStream);

            while (true) {
                boolean ifClientMsg = checkIfClientMsg(fromClient);
                while (!ifClientMsg) {
                    sendMsgToClient(outputStream, ConsoleUtils.prettifyInput("Invalid message format,Please check!"));
                    String msgFromClient = readMsgFromClient(inputStream);
                    ifClientMsg = checkIfClientMsg(msgFromClient);
                }
                //消息通过规则检测
                parseAndHandleClientMsg(fromClient);
                fromClient = readMsgFromClient(inputStream);
            }


        } catch (IOException | AuthException | ClientQuitException e) {
            //e.printStackTrace();
            ConsoleUtils.log(ConsoleUtils.prettifyInput(e.getMessage()));
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
            //e.printStackTrace();
            ConsoleUtils.log(ConsoleUtils.prettifyInput(e.getMessage()));

        }

        ConsoleUtils.log("handle finish");
        //剩余客户端数量
        int size = ChatServer.getHandleClientSocketMap().size();
        ConsoleUtils.log("Client online count: " + size);
    }

    /**
     * 校验name字符是否包含非法字符&
     * 因为在客户端map中使用了 id&name 来作为key
     *
     * @param userNickName
     * @return
     */
    private boolean checkNicknameValid(String userNickName) {
        return !userNickName.contains("&");
    }

    /**
     * 解析客户端发送来的字符串，是否符合规则
     *
     * @param fromClient
     */
    private void parseAndHandleClientMsg(String fromClient) throws ClientQuitException {
        String[] strings = fromClient.split(" ");
        //第一位为操作类型标志
        String operateType = strings[0];
        switch (operateType) {
            case "@":
                handleSecretChatClientMsg(strings[1], strings[2]);
                break;
            case "#":
                sendMsgChatToClients(this.userName, strings[1],true);
                break;
            case "$":
                if (strings[1].equals("all")) {
                    handleQueryAllClients(this.clientSocket);
                }
                if (strings[1].equals("quit")) {
                    //退出
                    throw new ClientQuitException("Client quit by self");

                }
                break;
            default:
                sendMsgToClient(this.outputStream, ConsoleUtils.prettifyInput("Unsurpport operation!!!"));
        }
    }

    private void handleQueryAllClients(Socket clientSocket) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            String allAuthClient = ConsoleUtils.getAllAuthClient(ChatServer.getHandleClientSocketMap());
            sendMsgToClient(outputStream, allAuthClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 校验客户端的信息是否是符合解析规则的
     * 只有满足解析规则的 才会去解析
     *
     * @param fromClient
     */
    private boolean checkIfClientMsg(String fromClient) {
        List<String> chatRuleList = ConsoleUtils.getChatRuleList();
        List<String> collect = chatRuleList.stream()
                .filter(e -> e.charAt(0) == fromClient.charAt(0))
                .collect(Collectors.toList());
        if (collect.size() <= 0) {
            return false;
        }
        //获取匹配到的第一个
        String ruleStr = collect.get(0);
        String[] strings = ruleStr.split(" ");
        String[] clientStrs = fromClient.split(" ");
        if ((strings[0].equals(clientStrs[0]) && (strings.length == clientStrs.length))) {
            return true;
        }
        return false;
    }


    /**
     * 给某个客户端私密消息
     *
     * @param idOrName
     * @param message
     */
    private void handleSecretChatClientMsg(String idOrName, String message) {
        Integer userId = null;
        ConcurrentHashMap<String, Socket> handleClientSocketMap = ChatServer.getHandleClientSocketMap();
        try {
            userId = Integer.valueOf(idOrName);
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            ConsoleUtils.log(e.getMessage());
            ConsoleUtils.log(ConsoleUtils.prettifyInput("Client chat use userName: " + userName));
        }
        if (userId != null) {
            //使用userId
            for (Map.Entry<String, Socket> clientSocket : handleClientSocketMap.entrySet()) {
                if (clientSocket.getKey().split("&")[0].equals(userId)) {
                    try {
                        OutputStream outputStream = clientSocket.getValue().getOutputStream();
                        sendMsgToClient(outputStream, message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //使用userName
            for (Map.Entry<String, Socket> clientSocket : handleClientSocketMap.entrySet()) {
                if (clientSocket.getKey().split("&")[1].equals(userName)) {
                    try {
                        OutputStream outputStream = clientSocket.getValue().getOutputStream();
                        sendMsgToClient(outputStream, message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 告知所有的授权客户端，有客户端离开
     *
     * @param userName
     */
    private void sendLeaveChatToClients(String userName) {
        sendMsgChatToClients(userName, "leave the chat",true);
    }

    private void sendEnterChatToClients(String userName) {
        sendMsgChatToClients(userName, "enter the chat",true);
    }

    /**
     * 给所有授权的客户端发送消息
     * @param userName
     * @param message
     * @param exceptSelf 是否排除自己
     */
    private void sendMsgChatToClients(String userName, String message,boolean exceptSelf) {
        ConcurrentHashMap<String, Socket> handleClientSocketMap = ChatServer.getHandleClientSocketMap();
        for (Map.Entry<String, Socket> clientSocket : handleClientSocketMap.entrySet()) {
            //String clientId = clientSocket.getKey();
            Socket socket = clientSocket.getValue();
            if (exceptSelf){
                if (this.clientSocket == socket){
                    continue;
                }
            }
            try {
                OutputStream outputStream = socket.getOutputStream();
                sendMsgToClient(outputStream, userName + ": " + message);
            } catch (IOException e) {
                //e.printStackTrace();
                ConsoleUtils.log(ConsoleUtils.prettifyInput(e.getMessage()));
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

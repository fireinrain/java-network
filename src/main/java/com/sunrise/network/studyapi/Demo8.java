package com.sunrise.network.studyapi;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/27 5:36 PM
 */
public class Demo8 {
    public static void main(String[] args) {

        //testSocketException();
        //testCheckPort();
        //testLocalNetInterface();
        //testConstructNoConnect();
        //testSocketAdress();
        //testUsePorxy();
        //testSocketInformation();
        //testSocketOptions();
        testSocketExp();


    }

    //更多异常的种类
    private static void testSocketExp() {
        new BindException();
        new ConnectException();
        new NoRouteToHostException();
        new ProtocolException();
    }

    //socket 可以调整的一些选项
    /*
    TCP_NODELY socket 缓冲,尽可能的快的发包 而不是等缓冲区满了再发包
    SO_LINGER socket 关闭时，如何处理剩下的数据
    SO_TIMEOUT socket 读取数据超时
    SO_RCVBUF 接受数据缓冲区大小
    SO_SENDBUGF 发送数据缓冲区大小
    SO_KEEPALIVE 保持连接，防止断开
    OOBINLINE out of band, 发送紧急数据
    SO_REUSEADDR 复用端口
    IP_TOS 服务类型

     */
    private static void testSocketOptions() {
        Socket socket = new Socket();
        try {
            if (!socket.getTcpNoDelay()) {
                socket.setTcpNoDelay(true);
                System.out.println("开启tcp no delay");
            }
            if (socket.getSoLinger() == -1) {
                socket.setSoLinger(true, 240);
            }
            if (socket.getSoTimeout() != -1) {
                socket.setSoTimeout(180 * 1000);
            }
            System.out.println("发送缓冲区大小： " + socket.getSendBufferSize() / 1024 + "kb");
            System.out.println("接受缓冲区大小： " + socket.getReceiveBufferSize());

            if (!socket.getKeepAlive()){
                socket.setKeepAlive(true);
            }

            if (!socket.getOOBInline()){
                socket.setOOBInline(true);
                //socket.sendUrgentData(1);
            }
            if (!socket.getReuseAddress()){
                socket.setReuseAddress(true);
            }
            socket.setTrafficClass(0xB8);


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //socket 获取一些信息
    private static void testSocketInformation() {
        try (Socket socket = new Socket("www.baidu.com", 80)) {
            System.out.println(socket.getLocalPort());
            System.out.println(socket.getLocalAddress());
            System.out.println(socket.getRemoteSocketAddress());
            System.out.println(socket.isClosed());
            System.out.println(socket.isBound());
            System.out.println(socket.isConnected());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //使用代理
    private static void testUsePorxy() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 1081);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, inetSocketAddress);
        Socket socket = new Socket(proxy);
        InetSocketAddress inetSocketAddress1 = new InetSocketAddress("www.baidu.com", 80);
        try {
            //15 s超时
            socket.connect(inetSocketAddress1, 1000 * 15);

            //发送请求数据
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "8859_1"));
            bufferedWriter.write("GET / HTTP/1.1\n" +
                    "Host: www.baidu.com\n\n");
            bufferedWriter.flush();


            InputStream inputStream = socket.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int c;
            StringBuilder result = new StringBuilder();
            while ((c = bufferedInputStream.read()) != -1) {
                result.append((char) c);
            }
            System.out.println("result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //socket 地址
    private static void testSocketAdress() {
        try (Socket socket = new Socket("www.yahoo.com", 80)) {
            SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
            socket.close();
            Socket socket1 = new Socket();
            socket1.connect(remoteSocketAddress);
            InputStream inputStream = socket1.getInputStream();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //构造但不连接
    public static void testConstructNoConnect() {
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("www.baidu.com", 80);
        try {
            socket.connect(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //选择指定的本地接口
    public static void testLocalNetInterface() {

        try {
            InetAddress[] allByName = InetAddress.getAllByName("127.0.0.1");
            for (InetAddress a : allByName) {
                System.out.println(a.toString());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    //抛异常
    public static void testSocketException() {
        try (Socket socket = new Socket("www.oreilly.com", 80)) {


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //检查端口
    public static void testCheckPort() {
        String url = "127.0.0.1";
        int port = 1024;
        List<Integer> listClose = new ArrayList<>();
        List<Integer> listOpen = new ArrayList<>();

        for (int i = 1; i < port; i++) {
            try {
                Socket socket = new Socket(url, i);
                listOpen.add(i);
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                listClose.add(i);
                continue;
            }
        }
        System.out.println("port open: " + listOpen.stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("port close: " + listClose.stream().map(String::valueOf).collect(Collectors.joining(",")));


    }

}

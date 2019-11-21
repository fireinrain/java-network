package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;

/**
 * @description: UDP 协议
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/21 11:16 PM
 */
public class Demo13 {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            Demo13.testUDPServer();
        };
        Thread thread = new Thread(runnable);
        thread.start();
        testUDPClient();
        //testUDPServer();
    }

    private static void testUDPServer() {
        Integer port = 8888;
        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {
            while (true) {
                byte[] bytes = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(bytes, 0, 1024);
                //阻塞  直到收到udp 数据包
                datagramSocket.receive(datagramPacket);
                System.out.println("服务器接收到数据");

                LocalDateTime localDateTime = LocalDateTime.now();
                byte[] dateBytes = localDateTime.toString().getBytes("UTF-8");
                DatagramPacket datagramPacket1 = new DatagramPacket(dateBytes, dateBytes.length,datagramPacket.getAddress(),datagramPacket.getPort());

                datagramSocket.send(datagramPacket1);
                System.out.println("服务器发送数据成功");


            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testUDPClient() {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(0);
            datagramSocket.setSoTimeout(10_0000);
            InetAddress inetAddress = InetAddress.getByName("localhost");
            //请求包
            DatagramPacket request = new DatagramPacket(new byte[1], 1, inetAddress, 8888);
            //构造响应包
            byte[] bytes = new byte[1024];
            DatagramPacket response = new DatagramPacket(bytes, 0,bytes.length);
            //发送udp数据
            datagramSocket.send(request);
            //接收udp数据
            datagramSocket.receive(response);

            //读取数据
            String s = new String(response.getData(), response.getLength());
            System.out.println(s);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }
}

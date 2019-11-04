package com.sunrise.network.studyapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @description:
 * @date: 2019/10/30
 * @author: lzhaoyang
 */
public class UDPServer {
    private final static int PORT = 1080;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                try {
                    DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
                    socket.receive(request); //获取发送端dp
                    String daytime = new Date().toString(); //系统时间
                    byte[] data = daytime.getBytes(StandardCharsets.US_ASCII);
                    String data2 = new String(request.getData(), 0, request.getLength()); //拼接字符串, 发送值

                    DatagramPacket response = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
                    socket.send(response);//发送返回值
                    System.out.println("i get:" + daytime + " " + request.getAddress() + "data:" + data2);
                    if (data2.equals("hello"))
                        System.out.println("i'm full!!!!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

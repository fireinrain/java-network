package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.*;

/**
 * @description: UDP 协议
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/21 11:16 PM
 */
public class Demo13 {
    public static void main(String[] args) {
        testUDPClient();
        testUDPServer();
    }

    private static void testUDPServer() {
        
    }

    private static void testUDPClient() {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(0);
            datagramSocket.setSoTimeout(10_0000);
            InetAddress inetAddress = InetAddress.getByName("tf.nist.gov");
            //请求包
            DatagramPacket request = new DatagramPacket(new byte[1], 1, inetAddress, 37);
            //构造响应包
            byte[] bytes = new byte[1024];
            DatagramPacket response = new DatagramPacket(bytes, bytes.length);
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
            if (datagramSocket!=null){
                datagramSocket.close();
            }
        }
    }
}

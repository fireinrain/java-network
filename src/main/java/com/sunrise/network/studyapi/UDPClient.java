package com.sunrise.network.studyapi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @description:
 * @date: 2019/10/30
 * @author: lzhaoyang
 */

public class UDPClient {
    public static void main(String[] args) throws Exception {

        //数据包套接字：相当于码头，专门处理通信数据及进行数据转发设置的
        DatagramSocket ds = new DatagramSocket();

        String message = "hello";

        //数据报包:类似于集装箱，用来存储所有的数据信息
        DatagramPacket dp = new DatagramPacket(
                message.getBytes(),   //数据都是已字节数据进行发送的，因此需要将数据进行转换
                message.length(), //发送数据的长度
                InetAddress.getByName("127.0.0.1"),  //发送数据的源ip地址
                1080   //发送数据的端口号
        );

        ds.send(dp);  //数据包通过码头DatagramSocket发送出去
        ds.close();   //数据发送之后关闭通道，减少资源浪费
        System.out.println("发送成功, 等待回应! ");

    }

}

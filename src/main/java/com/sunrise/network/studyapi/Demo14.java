package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * @description: DatagramChannel
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/25 11:25 PM
 */
public class Demo14 {
    private static Integer port = 3414;
    private static int maxBuff = 6_5507;
    public static void main(String[] args) {

        testDatagamChannelServer();

    }

    private static void testDatagamChannelServer() {
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress(port));
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxBuff);

            while (true){
                SocketAddress receive = datagramChannel.receive(byteBuffer);
                byteBuffer.flip();
                //读取
                System.out.println("the client say: ");
                while (byteBuffer.hasRemaining()){
                    System.out.write(byteBuffer.get());
                }
                //byteBuffer.array() 包含很多空的数组位置，也就当数据没有填满整个缓冲区，会有很多空的byte
                //System.out.println(new String(byteBuffer.array(),0,byteBuffer.array().length));
                byteBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

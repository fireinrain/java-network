package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @description: DatagramChannel
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/25 11:25 PM
 */
public class Demo14 {
    private static Integer port = 3414;
    private static int maxBuff = 6_5507;

    public static void main(String[] args) throws InterruptedException {

        //testDatagamChannelServer();
        Runnable server = ()->testEchoServerDatagamChannel();
        Runnable client = () -> testEchoDatagamChannel();
        new Thread(server).start();
        Thread.sleep(1000);
        Thread thread = new Thread(client);
        thread.start();
        thread.join();
        System.out.println("结束");
        //testEchoDatagamChannel();
        //testEchoServerDatagamChannel();

    }

    private static void testEchoServerDatagamChannel() {
        int port = 8888;
        int buffSize = 1024;
        try (DatagramChannel datagramChannel = DatagramChannel.open()) {
            DatagramSocket socket = datagramChannel.socket();
            socket.bind(new InetSocketAddress(port));
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(maxBuff);

            int count = 0;
            while (true){
                SocketAddress receive = datagramChannel.receive(byteBuffer);
                byteBuffer.flip();
                datagramChannel.send(byteBuffer,receive);
                byteBuffer.clear();
                count++;
                System.out.println("第"+count+"次 接收发送数据");
                if (count==100){
                    //接收完了客户端的数据 退出一下
                    System.out.println("服务器接收完，退出");
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 对于UDP来说 client server 的界限变得很模糊
     */
    private static void testEchoDatagamChannel() {
        int port = 8888;
        int countLimit = 100;

        //设置远程端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", port);

        try (DatagramChannel datagramChannel = DatagramChannel.open()) {
            //设置
            datagramChannel.configureBlocking(false);
            datagramChannel.connect(inetSocketAddress);

            //选择器
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            int n = 0;
            int numbersRead = 0;
            //循环读
            while (true) {
                //如果读完了发送的数据，就跳出
                if (numbersRead == countLimit) {
                    break;
                }
                //等待60s
                selector.select(6_0000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                if (selectionKeys.isEmpty() && n == countLimit) {
                    //不再触发事件，并且接收到指定数量的数据了，不会有更多的数据到达了
                    break;
                } else {
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        //及时删除这个key，因为这个key已经在处理流程了
                        iterator.remove();
                        if (selectionKey.isReadable()) {
                            byteBuffer.clear();
                            datagramChannel.read(byteBuffer);
                            byteBuffer.flip();
                            int anInt = byteBuffer.getInt();
                            System.out.println("Read: " + anInt);
                            numbersRead++;
                        }
                        if (selectionKey.isWritable()) {
                            byteBuffer.clear();
                            byteBuffer.putInt(n);
                            byteBuffer.flip();
                            datagramChannel.write(byteBuffer);
                            System.out.println("Write: " + n);
                            n++;
                            if (n == countLimit) {
                                //如果写的事件完全做完了，就可以将这个key切换到只对读感兴趣
                                selectionKey.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    }
                }
            }

            //统计发送和接收成功的数据比例
            System.out.println("Echoed： " + numbersRead + " out of " + countLimit + " send");
            System.out.println("Success Rate：" + 100.0 * numbersRead / countLimit + "%");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testDatagamChannelServer() {
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress(port));
            ByteBuffer byteBuffer = ByteBuffer.allocate(maxBuff);

            while (true) {
                SocketAddress receive = datagramChannel.receive(byteBuffer);
                byteBuffer.flip();
                //读取
                System.out.println("the client say: ");
                while (byteBuffer.hasRemaining()) {
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

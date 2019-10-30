package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @description: 使用select channel
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/29 10:23 PM
 */
public class Demo10 {
    public static final int port = 8000;

    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel;
        Selector selector;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket socket = serverSocketChannel.socket();
            socket.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            System.out.println("server is listenning at: " + port);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                int select = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //迭代到了这个key 下面就是处理步骤了，所以需要将这个键 删除掉
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        //强制转换为ServerSocketChannel 类型，本来是SelectableChannel 类型
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        //说明这个时候有客户端连接进来了
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("Accept connnection from：" + socketChannel.getRemoteAddress());
                        //这里也要讲这个为客户端服务的SocketChannel 设置为非阻塞的方式
                        socketChannel.configureBlocking(false);
                        //为这个通道注册selector，并设置关心的事件(这里设置为关心 读写操作)
                        SelectionKey clientKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        //挂载一个缓冲区
                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                        clientKey.attach(byteBuffer);
                    }
                    //如果是可读的状态
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer attachment = (ByteBuffer) key.attachment();
                        channel.read(attachment);
                    }
                    //如果是可写状态
                    if (key.isWritable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer attachment = (ByteBuffer) key.attachment();
                        //切换为读模式
                        attachment.flip();
                        //这里稍微操作一下，把发来的数据转换成大写
                        byte[] array = attachment.array();
                        //大小写转换，可以把byte数组中的值都减少32 就会变成大写形式的字节数组
                        for (byte b : array) {
                            b = (byte) (b - 32);
                        }
                        channel.write(ByteBuffer.wrap(array));
                        //compact 和clear 有什么区别呢？
                        attachment.compact();
                        //attachment.clear();
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}

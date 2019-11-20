package com.sunrise.network.studyapi.nio;

import java.io.IOException;
import java.net.SocketOption;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ServerSocketChannel;

/**
 * @description: NetworkChannel Interface
 * 用来支持各种TCP 选项
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/20 11:17 PM
 */
public class Demo5 {
    public static void main(String[] args) throws IOException {
        printOptions(ServerSocketChannel.open());
        //printOptions(SocketChannel.open());
        //printOptions(AsynchronousSocketChannel.open());
        //printOptions(AsynchronousSocketChannel.open());
        //printOptions(DatagramChannel.open());
    }

    private static void printOptions(NetworkChannel networkChannel) {
        System.out.println(networkChannel.getClass().getSimpleName()+" supports: ");
        for ( SocketOption<?> socketop: networkChannel.supportedOptions()) {
            try {
                System.out.println(socketop.name()+": "+networkChannel.getOption(socketop));
                //ServerSocketChannel 不支持IP_TOS  这个option 获取

                //这是异常信息，但是从源码可以看到， 其实在option set 中是含有IP_TOS 这个值的，不知道为啥
                //ServerSocketChannelImpl supports:
                //SO_RCVBUF:
                //IP_TOS:
                //Exception in thread "main" java.lang.AssertionError: Option not found
                //	at sun.nio.ch.Net.getSocketOption(Net.java:360)
                //	at sun.nio.ch.ServerSocketChannelImpl.getOption(ServerSocketChannelImpl.java:177)
                //	at com.sunrise.network.studyapi.nio.Demo5.printOptions(Demo5.java:28)
                //	at com.sunrise.network.studyapi.nio.Demo5.main(Demo5.java:16)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        try {
            networkChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @description: 广播
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/27 1:09 AM
 */
public class Demo16 {
    public static void main(String[] args) throws IOException {
        testBroadCast();
    }

    private static void testBroadCast() throws IOException {

        broadcast("Hello", InetAddress.getByName("255.255.255.255"));

    }

    public static void broadcast(
            String broadcastMessage, InetAddress address) throws IOException {
        DatagramSocket socket = null;

        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }

    /**
     * @description: 
     * @param:   null
     * @return: 寻找广播地址
     * @author: lzhaoyang
     * @date: 2019/11/27 1:13 AM
     */
    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}

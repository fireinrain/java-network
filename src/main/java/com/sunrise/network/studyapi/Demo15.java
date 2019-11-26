package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

/**
 * @description: IP组播
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/26 11:15 PM
 */
public class Demo15 {
    public static void main(String[] args) throws SocketException, InterruptedException {
        //testMulticastSocket();
        Runnable runnable = () -> testMulticastSocket2();
        new Thread(runnable).start();
        Thread.sleep(1000);
        Runnable c = () -> testSendMultiCastSocket2();
        Thread thread = new Thread(c);
        thread.start();
        thread.join();
        System.out.println("send  结束");
        //testMulticastSocket2();
        //testSendMultiCastSocket2();
    }

    private static void testSendMultiCastSocket2() {
        InetAddress inetAddress = null;
        try {
             inetAddress = InetAddress.getByName("230.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] bytesMessage = "Here come from multicast data".getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bytesMessage, bytesMessage.length,inetAddress,1900);
        try (MulticastSocket multicastSocket = new MulticastSocket()) {
            multicastSocket.setTimeToLive(10);
            multicastSocket.joinGroup(inetAddress);

            for (int i = 0; i <10 ; i++) {
                multicastSocket.send(datagramPacket);
            }

            multicastSocket.leaveGroup(inetAddress);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void testMulticastSocket2() {
        //设置vm option -Djava.net.preferIPv4Stack=true
        MulticastSocket multicastSocket = null;
        InetAddress inetAddress = null;
        try {
            multicastSocket = new MulticastSocket(1900);

            inetAddress = InetAddress.getByName("230.0.0.0");
            multicastSocket.joinGroup(inetAddress);
            byte[] bytes = new byte[8192];
            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                multicastSocket.receive(datagramPacket);
                String s = new String(datagramPacket.getData(),0,datagramPacket.getData().length, "8859_1");
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (multicastSocket != null) {
                try {
                    multicastSocket.leaveGroup(inetAddress);
                    multicastSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static void testMulticastSocket() throws SocketException {

        //不设置vm option -Djava.net.preferIPv4Stack=true
        MulticastSocket multicastSocket = null;
        InetAddress inetAddress1 = null;
        try {
            multicastSocket = new MulticastSocket(1900);
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addressesFromNetworkInterface = networkInterface.getInetAddresses();
                while (addressesFromNetworkInterface.hasMoreElements()) {
                    InetAddress inetAddress = addressesFromNetworkInterface.nextElement();
                    if (inetAddress.isSiteLocalAddress()
                            && !inetAddress.isAnyLocalAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && !inetAddress.isLoopbackAddress()
                            && !inetAddress.isMulticastAddress()) {
                        multicastSocket.setNetworkInterface(NetworkInterface.getByName(networkInterface.getName()));
                    }
                }
            }
            inetAddress1 = InetAddress.getByName("230.0.0.0");
            multicastSocket.joinGroup(inetAddress1);
            byte[] bytes = new byte[8192];
            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                multicastSocket.receive(datagramPacket);
                String s = new String(datagramPacket.getData(), "8859_1");
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (multicastSocket != null) {
                try {
                    multicastSocket.leaveGroup(inetAddress1);
                    multicastSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

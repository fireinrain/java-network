package com.sunrise.network.studyapi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/9/22 10:39 AM
 */
public class Demo2 {
    public static void main(String[] args) {
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName("208.201.239.100");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(ia.isReachable(1000));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取网络接口
        NetworkInterface eth0 = null;
        try {
            eth0 = NetworkInterface.getByName("en0");
            if (null == eth0) {
                System.out.println("no en0");
            } else {
                System.out.println("get en0");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        //通过ip来获取网络接口
        NetworkInterface byInetAddress = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            byInetAddress = NetworkInterface.getByInetAddress(localHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (null == byInetAddress) {
            System.out.println("cant get networkinterface by ip");

        } else {
            System.out.println("can get networkinterface by ip");
        }

        //列出所有网络接口
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (networkInterfaces.hasMoreElements()){
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            System.out.println(networkInterface);
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()){
                InetAddress inetAddress = inetAddresses.nextElement();
                System.out.println(inetAddress);
            }
            System.out.println("--------------");
        }


    }
}

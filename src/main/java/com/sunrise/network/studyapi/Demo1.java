package com.sunrise.network.studyapi;

import org.xbill.DNS.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/9/22 9:21 AM
 */
public class Demo1 {
    public static void main(String[] args) {
        InetAddress address = null;

        try {
            address = InetAddress.getByName("www.juejin.im");
            System.out.println(address);
            System.out.println(Arrays.toString(address.getAddress()));
            System.out.println(address.isAnyLocalAddress());
            //System.out.println(address.isReachable(2000));

            InetAddress baiduIP = InetAddress.getByName("39.156.69.79");
            // 想要在java 自带的api 根据ip获取hostname 不行
            //需要借助第三方的包
            //System.out.println(baiduIP.getCanonicalHostName());
            //dnsjava

            Resolver resolver = new SimpleResolver("114.114.114.114");
            Lookup lookup = new Lookup("www.baidu.com",Type.A);
            lookup.setResolver(resolver);
            Cache cache=new Cache();
            lookup.setCache(cache);
            lookup.run();

            //Thread.sleep(10*1000);
            if(lookup.getResult()==Lookup.SUCCESSFUL){
                String[] results=cache.toString().split("\\n");
                for(String result:results){
                    System.out.println(result);
                }
            }

            //String hostName = Address.getHostName(baiduIP);
            //System.out.println("baidu hostname: "+hostName);

            byte[] ipBytes = {119,(byte) 254,101,(byte) 231};
            InetAddress byAddress = InetAddress.getByAddress("www.juejin.im",ipBytes);
            System.out.println(byAddress);
            System.out.println(byAddress.getHostName());

            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println(localHost);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        }

    }

}

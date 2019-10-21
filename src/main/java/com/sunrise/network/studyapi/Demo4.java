package com.sunrise.network.studyapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/10 11:45 PM
 */
public class Demo4 {
    public static void main(String[] args) throws MalformedURLException {
        //testBaidu();
        //testUrlEncode();
        //testUrlDecode();
        //testProxy();
        testDynamicProxy();
    }


    public static void testUrlEncode() {
        String encode = null;
        try {
            encode = URLEncoder.encode("我是你大爷", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(encode);
    }

    public static void testUrlDecode() {
        try {
            String decode = URLDecoder.decode("%E6%88%91%E6%98%AF%E4%BD%A0%E5%A4%A7%E7%88%B7", "UTF-8");
            System.out.println(decode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void browserUrl(String urlLink){
        URL url = null;
        try {
            url = new URL(urlLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //System.out.println(Objects.requireNonNull(url).getHost());
        try (InputStream inputStream = url.openStream()) {
            int c;
            while ((c = inputStream.read()) != -1) {
                System.out.write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void testBaidu(){
        browserUrl("http://www.baidu.com");

    }

    public static void testProxy() {
        //不能像设置http代理那些 设置一些非代理的连接域名
        //socks代理只能是 代理所有 或者不代理
        //System.setProperty("socksProxyHost","127.0.0.1");
        //System.setProperty("socksProxyPort","1081");

        //
        Properties properties1 = System.getProperties();
        properties1.setProperty("socksProxyHost","127.0.0.1");
        properties1.setProperty("socksProxyPort","1081");
        System.setProperties(properties1);



        browserUrl("http://www.google.com");

    }

    public static void testDynamicProxy(){
        class LocalProxySelector extends ProxySelector{

            private List<URI> failedUri = new ArrayList<>();
            @Override
            public List<Proxy> select(URI uri) {
                List<Proxy> proxies = new ArrayList<>();
                if (failedUri.contains(uri)|| !"http".equalsIgnoreCase(uri.getScheme())&&
                !"https".equalsIgnoreCase(uri.getScheme())){
                    proxies.add(Proxy.NO_PROXY);

                }else {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",1081);
                    Proxy proxy = new Proxy(Proxy.Type.SOCKS, inetSocketAddress);
                    proxies.add(proxy);
                }
                return proxies;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                System.out.println("uri: "+uri +"socketAddress: " + sa +"failed");
                failedUri.add(uri);
            }
        }

        LocalProxySelector localProxySelector = new LocalProxySelector();
        ProxySelector.setDefault(localProxySelector);
        browserUrl("http://www.google.com");
    }
}

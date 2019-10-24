package com.sunrise.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author sunrise
 */
public class App {

    public static void main(String[] args) {
        // testProxyConfig();
        //testProxySelector();

        //System.out.println((int)'\r');
        System.out.println(Integer.valueOf("we"));

    }

    // 测试proxySelector
    public static void testProxySelector() {
        class LocalProxySelector extends ProxySelector {
            private List<URI> fail = new ArrayList<>();

            @Override
            public List<Proxy> select(URI uri) {
                ArrayList<Proxy> arrayList = new ArrayList<>();
                //如果链接是失败的 和或不是 http https的链接则 不代理
                if (fail.contains(uri) || !"http".equalsIgnoreCase(uri.getScheme())
                        && !"https".equalsIgnoreCase(uri.getScheme())) {
                    arrayList.add(Proxy.NO_PROXY);
                } else {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 1081);
                    // Proxy proxy = new Proxy(Proxy.Type.SOCKS, inetSocketAddress);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);

                    arrayList.add(proxy);
                }
                return arrayList;
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                fail.add(uri);
            }
        }

        //运行代理
        LocalProxySelector localProxySelector = new LocalProxySelector();
        ProxySelector.setDefault(localProxySelector);

        testBrowserGoogle();
    }

    //访问google
    public static void testBrowserGoogle() {
        try {
            URL url = new URL("http://www.google.com");
            InputStream inputStream = url.openStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int c;
            StringBuilder stringBuffer = new StringBuilder();
            while ((c = bufferedInputStream.read()) != -1) {
                stringBuffer.append((char) c);
                //System.out.write(c);
            }
            System.out.println(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 测试代理
    public static void testProxyConfig() {
        //以下两种方式都可以，但是需要注意的是

        //方式一：
        //如果直接使用一个新的properties，然后设置进系统
        //则会报出找不到java home的异常。
        //所以想要添加代理属性，还是要先获得之前的java属性，然后设置进去
        Properties properties1 = System.getProperties();
        // Properties properties = new Properties();
        properties1.setProperty("socksProxyHost", "127.0.0.1");
        properties1.setProperty("socksProxyPort", "1081");
        System.setProperties(properties1);

        // 方式二
        // System.setProperty("socksProxyHost", "127.0.0.1");
        // System.setProperty("socksProxyPort", "1081");

        //测试
        testBrowserGoogle();

    }
}

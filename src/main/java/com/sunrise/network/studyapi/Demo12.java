package com.sunrise.network.studyapi;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * @description: 安全的套接字 SSL支持
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/11/4 10:30 PM
 */
public class Demo12 {
    public static void main(String[] args) {
        //SocketFactory socketFactory = SSLSocketFactory.getDefault();
        //try {
        //    //安全客户端套接字
        //    Socket secureSocket = socketFactory.createSocket("127.0.0.1", 8080);
        //    //服务端安全套接字
        //    ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
        //    ServerSocket secureServerSocket = serverSocketFactory.createServerSocket(8080);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        testSSLSocket("www.usps.com", 443);
        testSSLServerSocket(443);

    }

    //服务端安全套接字
    private static void testSSLServerSocket(int port) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            KeyManagerFactory sunX509 = KeyManagerFactory.getInstance("SunX509");
            KeyStore jks = KeyStore.getInstance("JKS");

            char[] readPassword = System.console().readPassword();
            jks.load(new FileInputStream("jnp4e.keys"),readPassword);
            sunX509.init(jks,readPassword);
            sslContext.init(sunX509.getKeyManagers(),null,null);

            //插除口令
            Arrays.fill(readPassword,'0');
            SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket)serverSocketFactory.createServerSocket(port);

            //增加匿名密码组
            String[] supportedCipherSuites = serverSocket.getSupportedCipherSuites();
            String[] anoSupport = new String[supportedCipherSuites.length];
            int numSupport = 0;
            for (int i = 0; i <supportedCipherSuites.length ; i++) {
                if (supportedCipherSuites[i].indexOf("_anon_")>0){
                    anoSupport[numSupport++] = supportedCipherSuites[i];
                }
            }
            String[] oldEnable = serverSocket.getEnabledCipherSuites();
            String[] newEnable = new String[oldEnable.length + numSupport];
            System.arraycopy(oldEnable,0,newEnable,0,oldEnable.length);
            System.arraycopy(anoSupport,0,newEnable,oldEnable.length,numSupport);
            //设置工作已经做好了

            while (true){
                try (Socket clientSocket = serverSocket.accept()){
                    InputStream inputStream = clientSocket.getInputStream();
                    int c;
                    while ((c = inputStream.read())!=-1){
                        System.out.write(c);
                    }
                }
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    //客户端安全套接字
    private static void testSSLSocket(String host, int port) {
        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        BufferedReader bufferedReader = null;
        OutputStreamWriter outputStreamWriter = null;
        try (SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(host, port)) {
            //获取所有密码组
            String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
            //设置到socket上
            sslSocket.setEnabledCipherSuites(supportedCipherSuites);
            //写入数据
            outputStreamWriter = new OutputStreamWriter(sslSocket.getOutputStream(), "UTF-8");
            //写入一个简单的http头部
            outputStreamWriter.write("GET http://" + host + "/ HTTP/1.1\r\n");
            outputStreamWriter.write("Host: " + host + "\r\n");
            outputStreamWriter.write("\r\n");
            outputStreamWriter.flush();

            //读取响应
            bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            String s;
            while (!(s = bufferedReader.readLine()).equals("")) {
                System.out.println(s);
            }
            System.out.println();

            String contentLength = bufferedReader.readLine();
            int length = Integer.MAX_VALUE;
            try {
                length = Integer.parseInt(contentLength, 16);
            } catch (NumberFormatException e) {
                //这个服务器在响应的第一行 没有发送content-length
                System.out.println("******** content-length解析失败");
            }

            System.out.println("--------"+contentLength);
            //读取响应体内容
            int c;
            int i = 0;
            while ((c = bufferedReader.read()) != -1 && i++ < length) {
                System.out.write(c);
            }
            System.out.println();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

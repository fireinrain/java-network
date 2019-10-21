package com.sunrise.network.studyapi;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/9/22 4:58 PM
 */
public class Demo3 {

    public static void main(String[] args) {
        // hypertext transfer protocol
        testProtocol("http://www.adc.org");
        // secure http
        testProtocol("https://www.amazon.com/exec/obidos/order2/"); // file transfer protocol
        testProtocol("ftp://ibiblio.org/pub/languages/java/javafaq/"); // Simple Mail Transfer Protocol
        testProtocol("mailto:elharo@ibiblio.org"); // telnet
        testProtocol("telnet://dibner.poly.edu/"); // local file access
        testProtocol("file:///etc/passwd"); // gopher
        testProtocol("gopher://gopher.anc.org.za/");
        // Lightweight Directory Access Protocol
        testProtocol(
                "ldap://ldap.itd.umich.edu/o=University%20of%20Michigan,c=US?postalAddress");
        // JAR
        testProtocol(
                "jar:http://cafeaulait.org/books/javaio/ioexamples/javaio.jar!"
                        + "/com/macfaq/io/StreamCopier.class"); // NFS, Network File System
        testProtocol("nfs://utopia.poly.edu/usr/tmp/"); // a custom protocol for JDBC
        testProtocol("jdbc:mysql://luna.ibiblio.org:3306/NEWS"); // rmi, a custom protocol for remote method invocation
        testProtocol("rmi://ibiblio.org/RenderEngine");
        // custom protocols for HotJava
        testProtocol("doc:/UsersGuide/release.html");
        testProtocol("netdoc:/UsersGuide/release.html");
        testProtocol("systemresource://www.adc.org/+/index.html");
        testProtocol("verbatim:http://www.adc.org/");
        noLengthArgs("cake","name","piters");
    }

    public static void testProtocol(String protocolUrl){
        try {
            URL url = new URL(protocolUrl);
            System.out.println(url.getProtocol()+" is supported");
        } catch (MalformedURLException e) {
            //不支持
            String substring = protocolUrl.substring(0, protocolUrl.indexOf(":"));
            System.out.println(substring+" is not supported");
        }

    }

    public static void noLengthArgs(String ...args) {
        for (String s :args ) {
            System.out.println(s+"\r\n");
        }
    }
}

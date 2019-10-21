package com.sunrise.network.studyapi;

import java.nio.ByteBuffer;

/**
 * @description:
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/15 12:33 AM
 */
public class Demo5 {
    public static void main(String[] args) {
        ByteBuffer byteBuffer =  ByteBuffer.allocate(1024);

        System.out.println("--------allocate-------");
        System.out.println("mark: "+byteBuffer.mark());
        System.out.println("position: "+byteBuffer.position());
        System.out.println("limit: "+ byteBuffer.limit());
        System.out.println("capacity: "+byteBuffer.capacity());
        byte[] bytes = "abcd".getBytes();

        byteBuffer.put(bytes);
        System.out.println("-------put-----------");
        System.out.println("mark: "+byteBuffer.mark());
        System.out.println("position: "+byteBuffer.position());
        System.out.println("limit: "+ byteBuffer.limit());
        System.out.println("capacity: "+byteBuffer.capacity());

        byteBuffer.flip();
        System.out.println("-------flip-----------");
        System.out.println("mark: "+byteBuffer.mark());
        System.out.println("position: "+byteBuffer.position());
        System.out.println("limit: "+ byteBuffer.limit());
        System.out.println("capacity: "+byteBuffer.capacity());

        byte[] bytes1 = new byte[4];
        byteBuffer.get(bytes1,0,4);
        System.out.println("-------get-----------");
        System.out.println("read bytes: "+new String(bytes1));

        System.out.println("mark: "+byteBuffer.mark());
        System.out.println("position: "+byteBuffer.position());
        System.out.println("limit: "+ byteBuffer.limit());
        System.out.println("capacity: "+byteBuffer.capacity());

    }
}

package com.sunrise.network.studyapi.nio;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description:
 * nio 包下有：
 *          channels 包
 *          charset 包
 *          file 包
 *          。。。
 *          余下的都是类基本都是Buffer相关
 *
 *
 * @date: 2019/10/31
 * @author: lzhaoyang
 */
public class Demo3 {
    public static void main(String[] args) {
        // Path用于来表示文件路径和文件
        Path path = Paths.get(".gitignore");
        System.out.println(path.getFileSystem());

        Path path1 = FileSystems.getDefault().getPath(".gitignore");

        //Files 和文件相关的各种操作
        System.out.println(Files.isReadable(Paths.get(".gitignore")));
    }
}

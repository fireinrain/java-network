package com.sunrise.network.projects.bio;

/**
 * @description: 校验授权异常
 * @date: 2019/10/23
 * @author: lzhaoyang
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}

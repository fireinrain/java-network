package com.sunrise.network.projects.bio;

/**
 * @description: 用户信息
 * @version: 1.00
 * @author: lzhaoyang
 * @date: 2019/10/21 10:32 PM
 */
public class UserInfo {
    private String userName;
    private String secretKey;

    public UserInfo(String userName, String secretKey) {
        this.userName = userName;
        this.secretKey = secretKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}

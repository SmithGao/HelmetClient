package com.zskj.helmetclient.bean;

import java.io.Serializable;

/**
 * 作者：yangwenquan on 2016/11/22
 * 类描述：
 */
public class User implements Serializable{
    private String name;
    private String ip;
    private long OnlineTime;

    public User(String name, String ip, long onlineTime) {
        this.name = name;
        this.ip = ip;
        OnlineTime = onlineTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getOnlineTime() {
        return OnlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        OnlineTime = onlineTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}

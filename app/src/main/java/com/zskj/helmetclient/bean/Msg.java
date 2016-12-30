package com.zskj.helmetclient.bean;

import java.io.Serializable;

/**
 * 作者：yangwenquan on 2016/11/23
 * 类描述：
 */
public class Msg implements Serializable {
    private long date;//时间戳
    private String sendUserName;// 发送人 用户名
    private String sendUserIp;// 发送人ip
    private String receiveUserName;// 接收人
    private String receiveUserIp;//接收人IP
    private int msgType;// 消息类型
    private Object body;// 主体

    public Msg() {
    }

    public Msg( String sendUserName, String sendUserIp, String receiveUserName, String receiveUserIp, int msgType, Object body) {
        this.date = date;
        this.sendUserName = sendUserName;
        this.sendUserIp = sendUserIp;
        this.receiveUserName = receiveUserName;
        this.receiveUserIp = receiveUserIp;
        this.msgType = msgType;
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendUserIp() {
        return sendUserIp;
    }

    public void setSendUserIp(String sendUserIp) {
        this.sendUserIp = sendUserIp;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveUserIp() {
        return receiveUserIp;
    }

    public void setReceiveUserIp(String receiveUserIp) {
        this.receiveUserIp = receiveUserIp;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "date=" + date +
                ", sendUserName='" + sendUserName + '\'' +
                ", sendUserIp='" + sendUserIp + '\'' +
                ", receiveUserName='" + receiveUserName + '\'' +
                ", receiveUserIp='" + receiveUserIp + '\'' +
                ", msgtype=" + msgType +
                ", body=" + body +
                '}';
    }
}

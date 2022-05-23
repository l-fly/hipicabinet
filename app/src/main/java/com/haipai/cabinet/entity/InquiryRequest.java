package com.haipai.cabinet.entity;

import java.io.Serializable;


public class InquiryRequest implements Serializable {

    private String devId;
    private int msgType;
    private String batteryId;
    private String txnno;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getBatteryId() {
        return batteryId;
    }

    public void setBatteryId(String batteryId) {
        this.batteryId = batteryId;
    }

    public String getTxnno() {
        return txnno;
    }

    public void setTxnno(String txnno) {
        this.txnno = txnno;
    }
}

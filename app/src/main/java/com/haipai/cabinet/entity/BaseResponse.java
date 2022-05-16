package com.haipai.cabinet.entity;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    /**
     * devId : CHZD12GDTY200329109
     * result : 1
     * txnNo : 1651294115503
     * msgType : 411
     * currTime : 1651294147739
     * value : 1
     */

    private String devId;
    private int result;
    private String txnNo;
    private int msgType;
    private long currTime;
    private int value;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(String txnNo) {
        this.txnNo = txnNo;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

package com.haipai.cabinet.entity;

import java.io.Serializable;


public class LoginRequest implements Serializable {

    /**
     * devId : CHZD12GDTY200329109
     * devType : 2
     * softVersion : 4.0
     * txnNo : 1651713659000
     * msgType : 110
     * hardVersion : A4048512
     * ccid : 
     * cabSta : 1
     * imei : 
     * protocolVersion : V2
     * imsi : 
     */

    private String devId;
    private int devType;
    private String softVersion;
    private long txnNo;
    private int msgType;
    private String hardVersion;
    private String ccid;
    private String cabSta;
    private String imei;
    private String protocolVersion;
    private String imsi;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public long getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(long txnNo) {
        this.txnNo = txnNo;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getCcid() {
        return ccid;
    }

    public void setCcid(String ccid) {
        this.ccid = ccid;
    }

    public String getCabSta() {
        return cabSta;
    }

    public void setCabSta(String cabSta) {
        this.cabSta = cabSta;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}

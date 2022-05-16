package com.haipai.cabinet.entity;

public class OrderInfo {
    private String batteryId;
    private String id;
    private int scanBattery;
    private String value;
    private String userId;
    private String voltage;
    private String txnNo;

    public String getBatteryId() {
        return batteryId;
    }

    public void setBatteryId(String batteryId) {
        this.batteryId = batteryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScanBattery() {
        return scanBattery;
    }

    public void setScanBattery(int scanBattery) {
        this.scanBattery = scanBattery;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getTxnNo() {
        return txnNo;
    }

    public void setTxnNo(String txnNo) {
        this.txnNo = txnNo;
    }
}

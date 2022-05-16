package com.haipai.cabinet.entity;

import java.io.Serializable;
import java.util.List;


public class RemoteControlRequest implements Serializable {

    /**
     * devId : CHZD12GDTY200329109
     * txnNo : 1651294115503
     * msgType : 500
     * paramList : [{"batteryId":"PN204805508DGPN211230471","id":"switchControl","scanBattery":0,"value":"01","userId":"2004122","voltage":"48"}]
     */

    private String devId;
    private long txnNo;
    private int msgType;
    private List<ParamListBean> paramList;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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

    public List<ParamListBean> getParamList() {
        return paramList;
    }

    public void setParamList(List<ParamListBean> paramList) {
        this.paramList = paramList;
    }

    public static class ParamListBean implements Serializable {
        /**
         * batteryId : PN204805508DGPN211230471
         * id : switchControl
         * scanBattery : 0
         * value : 01
         * userId : 2004122
         * voltage : 48
         */

        private String batteryId;
        private String id;
        private int scanBattery;
        private String value;
        private String userId;
        private String voltage;

        private int doorId;

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

        public int getDoorId() {
            return doorId;
        }

        public void setDoorId(int doorId) {
            this.doorId = doorId;
        }
    }
}

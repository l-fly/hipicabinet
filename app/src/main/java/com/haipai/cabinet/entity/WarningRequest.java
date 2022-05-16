package com.haipai.cabinet.entity;

import java.io.Serializable;
import java.util.List;



public class WarningRequest implements Serializable {


    /**
     * devId : CHZD12GDTY200329109
     * txnNo : 1651294115503
     * msgType : 410
     * alarmList : [{"alarmDesc":"26","fullBatteryId":"PN204805508DGPN220406010","emptBatteryId":"PN204805508DGPN211230471","alarmTime":"1651294129000","fullBatsoc":86,"emptDoorID":5,"id":"switchFinish","fullDoorID":7,"doorId":7,"userId":"2004122","alarmFlag":0,"emptBatsoc":75}]
     */

    private String devId;
    private String txnNo;
    private int msgType;
    private List<AlarmListBean> alarmList;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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

    public List<AlarmListBean> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<AlarmListBean> alarmList) {
        this.alarmList = alarmList;
    }

    public static class AlarmListBean implements Serializable {
        /**
         * alarmDesc : 26
         * fullBatteryId : PN204805508DGPN220406010
         * emptBatteryId : PN204805508DGPN211230471
         * alarmTime : 1651294129000
         * fullBatsoc : 86
         * emptDoorID : 5
         * id : switchFinish
         * fullDoorID : 7
         * doorId : 7
         * userId : 2004122
         * alarmFlag : 0
         * emptBatsoc : 75
         */

        private String alarmDesc;
        private String fullBatteryId;
        private String emptBatteryId;
        private String alarmTime;
        private int fullBatsoc;
        private int emptDoorID;
        private String id;
        private int fullDoorID;
        private int doorId;
        private String userId;
        private int alarmFlag;
        private int emptBatsoc;

        private int dataType;
        private String batteryId;

        public String getAlarmDesc() {
            return alarmDesc;
        }

        public void setAlarmDesc(String alarmDesc) {
            this.alarmDesc = alarmDesc;
        }

        public String getFullBatteryId() {
            return fullBatteryId;
        }

        public void setFullBatteryId(String fullBatteryId) {
            this.fullBatteryId = fullBatteryId;
        }

        public String getEmptBatteryId() {
            return emptBatteryId;
        }

        public void setEmptBatteryId(String emptBatteryId) {
            this.emptBatteryId = emptBatteryId;
        }

        public String getAlarmTime() {
            return alarmTime;
        }

        public void setAlarmTime(String alarmTime) {
            this.alarmTime = alarmTime;
        }

        public int getFullBatsoc() {
            return fullBatsoc;
        }

        public void setFullBatsoc(int fullBatsoc) {
            this.fullBatsoc = fullBatsoc;
        }

        public int getEmptDoorID() {
            return emptDoorID;
        }

        public void setEmptDoorID(int emptDoorID) {
            this.emptDoorID = emptDoorID;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getFullDoorID() {
            return fullDoorID;
        }

        public void setFullDoorID(int fullDoorID) {
            this.fullDoorID = fullDoorID;
        }

        public int getDoorId() {
            return doorId;
        }

        public void setDoorId(int doorId) {
            this.doorId = doorId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getAlarmFlag() {
            return alarmFlag;
        }

        public void setAlarmFlag(int alarmFlag) {
            this.alarmFlag = alarmFlag;
        }

        public int getEmptBatsoc() {
            return emptBatsoc;
        }

        public void setEmptBatsoc(int emptBatsoc) {
            this.emptBatsoc = emptBatsoc;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public String getBatteryId() {
            return batteryId;
        }

        public void setBatteryId(String batteryId) {
            this.batteryId = batteryId;
        }
    }
}

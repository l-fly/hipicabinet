package com.haipai.cabinet.entity;

import java.io.Serializable;
import java.util.List;

public class AllPropertiesRequest implements Serializable {

    /**
     * devId : CHZD12GDTY200329109
     * txnNo : 1651287811000
     * boxList : [{"ChgTime":"2022-01-0100:00:01","boxFault":["000"],"boxEnable":"0","boxAlarm":["000"],"boxChgSta":"0","doorSta":"0","boxSta":"2","doorId":"1"},{"ChgTime":"2022-01-0100:00:01","boxFault":["000"],"boxEnable":"0","boxAlarm":["000"],"boxChgSta":"0","doorSta":"0","boxSta":"0","doorId":"2"},{"ChgTime":"2022-01-0100:00:01","boxFault":["000"],"boxEnable":"0","boxAlarm":["000"],"boxChgSta":"0","doorSta":"0","boxSta":"0","doorId":"3"},{"ChgTime":"2022-01-0100:00:01","boxFault":["000"],"boxEnable":"0","boxAlarm":["000"],"boxChgSta":"0","doorSta":"0","boxSta":"0","doorId":"4"},{"ChgTime":"2022-04-3011:03:24","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN211230248","doorSta":"0","boxSta":"1","doorId":"5"},{"ChgTime":"2022-04-3011:03:25","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN220426081","doorSta":"0","boxSta":"1","doorId":"6"},{"ChgTime":"2022-04-3011:03:25","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN220406013","doorSta":"0","boxSta":"1","doorId":"7"},{"ChgTime":"2022-04-3011:03:25","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN211230607","doorSta":"0","boxSta":"1","doorId":"8"},{"ChgTime":"2022-04-3009:21:42","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","doorSta":"0","boxSta":"0","doorId":"9"},{"ChgTime":"2022-04-3011:03:25","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN220218059","doorSta":"0","boxSta":"1","doorId":"10"},{"ChgTime":"2022-04-3010:01:19","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","doorSta":"0","boxSta":"0","doorId":"11"},{"ChgTime":"2022-04-3011:03:26","boxFault":["000"],"boxEnable":"1","boxAlarm":["000"],"boxChgSta":"2","batteryId":"PN204805508DGPN211230471","doorSta":"0","boxSta":"1","doorId":"12"}]
     * msgType : 310
     * cabList : [{"cabEnable":"1","cabSta":"1","cabCur":"9.7","cabFault":["000"],"batFullC":"0","batFullB":"0","dBM":"-40","batFullA":"5","emKwh":"5070.6","batNum":"6","cabVol":"216.4","cabT":"30.6","cabAlarm":["000"]}]
     * isFull : 1
     * batList : [{"bmsT":"33","batteryId":"PN204805508DGPN211230248","soc":"63","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"5"},{"bmsT":"35","batteryId":"PN204805508DGPN220426081","soc":"66","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"6"},{"bmsT":"33","batteryId":"PN204805508DGPN220406013","soc":"62","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"7"},{"bmsT":"34","batteryId":"PN204805508DGPN211230607","soc":"62","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"8"},{"bmsT":"34","batteryId":"PN204805508DGPN220218059","soc":"26","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"10"},{"bmsT":"31","batteryId":"PN204805508DGPN211230471","soc":"56","totalAH":"55","chgCur":"0.0","bmsAlarm":["000"],"batVol":"48","bmsFault":["000"],"batCycle":"1","doorId":"12"}]
     */

    private String devId;
    private String txnNo;
    private int msgType;
    private int isFull;
    private List<BoxListBean> boxList;
    private List<CabListBean> cabList;
    private List<BatListBean> batList;

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

    public int getIsFull() {
        return isFull;
    }

    public void setIsFull(int isFull) {
        this.isFull = isFull;
    }

    public List<BoxListBean> getBoxList() {
        return boxList;
    }

    public void setBoxList(List<BoxListBean> boxList) {
        this.boxList = boxList;
    }

    public List<CabListBean> getCabList() {
        return cabList;
    }

    public void setCabList(List<CabListBean> cabList) {
        this.cabList = cabList;
    }

    public List<BatListBean> getBatList() {
        return batList;
    }

    public void setBatList(List<BatListBean> batList) {
        this.batList = batList;
    }

    public static class BoxListBean implements Serializable {
        /**
         * ChgTime : 2022-01-0100:00:01
         * boxFault : ["000"]
         * boxEnable : 0
         * boxAlarm : ["000"]
         * boxChgSta : 0
         * doorSta : 0
         * boxSta : 2
         * doorId : 1
         * batteryId : PN204805508DGPN211230248
         */

        private String ChgTime;
        private String boxEnable;
        private String boxChgSta;
        private String doorSta;
        private String boxSta;
        private String doorId;
        private String batteryId;
        private List<String> boxFault;
        private List<String> boxAlarm;

        public String getChgTime() {
            return ChgTime;
        }

        public void setChgTime(String chgTime) {
            ChgTime = chgTime;
        }

        public String getBoxEnable() {
            return boxEnable;
        }

        public void setBoxEnable(String boxEnable) {
            this.boxEnable = boxEnable;
        }

        public String getBoxChgSta() {
            return boxChgSta;
        }

        public void setBoxChgSta(String boxChgSta) {
            this.boxChgSta = boxChgSta;
        }

        public String getDoorSta() {
            return doorSta;
        }

        public void setDoorSta(String doorSta) {
            this.doorSta = doorSta;
        }

        public String getBoxSta() {
            return boxSta;
        }

        public void setBoxSta(String boxSta) {
            this.boxSta = boxSta;
        }

        public String getDoorId() {
            return doorId;
        }

        public void setDoorId(String doorId) {
            this.doorId = doorId;
        }

        public String getBatteryId() {
            return batteryId;
        }

        public void setBatteryId(String batteryId) {
            this.batteryId = batteryId;
        }

        public List<String> getBoxFault() {
            return boxFault;
        }

        public void setBoxFault(List<String> boxFault) {
            this.boxFault = boxFault;
        }

        public List<String> getBoxAlarm() {
            return boxAlarm;
        }

        public void setBoxAlarm(List<String> boxAlarm) {
            this.boxAlarm = boxAlarm;
        }
    }

   
    public static class CabListBean implements Serializable {
        /**
         * cabEnable : 1
         * cabSta : 1
         * cabCur : 9.7
         * cabFault : ["000"]
         * batFullC : 0
         * batFullB : 0
         * dBM : -40
         * batFullA : 5
         * emKwh : 5070.6
         * batNum : 6
         * cabVol : 216.4
         * cabT : 30.6
         * cabAlarm : ["000"]
         */

        private String cabEnable;
        private String cabSta;
        private String cabCur;
        private String batFullC;
        private String batFullB;
        private String dBM;
        private String batFullA;
        private String emKwh;
        private String batNum;
        private String cabVol;
        private String cabT;
        private List<String> cabFault;
        private List<String> cabAlarm;

        public String getCabEnable() {
            return cabEnable;
        }

        public void setCabEnable(String cabEnable) {
            this.cabEnable = cabEnable;
        }

        public String getCabSta() {
            return cabSta;
        }

        public void setCabSta(String cabSta) {
            this.cabSta = cabSta;
        }

        public String getCabCur() {
            return cabCur;
        }

        public void setCabCur(String cabCur) {
            this.cabCur = cabCur;
        }

        public String getBatFullC() {
            return batFullC;
        }

        public void setBatFullC(String batFullC) {
            this.batFullC = batFullC;
        }

        public String getBatFullB() {
            return batFullB;
        }

        public void setBatFullB(String batFullB) {
            this.batFullB = batFullB;
        }

        public String getdBM() {
            return dBM;
        }

        public void setdBM(String dBM) {
            this.dBM = dBM;
        }

        public String getBatFullA() {
            return batFullA;
        }

        public void setBatFullA(String batFullA) {
            this.batFullA = batFullA;
        }

        public String getEmKwh() {
            return emKwh;
        }

        public void setEmKwh(String emKwh) {
            this.emKwh = emKwh;
        }

        public String getBatNum() {
            return batNum;
        }

        public void setBatNum(String batNum) {
            this.batNum = batNum;
        }

        public String getCabVol() {
            return cabVol;
        }

        public void setCabVol(String cabVol) {
            this.cabVol = cabVol;
        }

        public String getCabT() {
            return cabT;
        }

        public void setCabT(String cabT) {
            this.cabT = cabT;
        }

        public List<String> getCabFault() {
            return cabFault;
        }

        public void setCabFault(List<String> cabFault) {
            this.cabFault = cabFault;
        }

        public List<String> getCabAlarm() {
            return cabAlarm;
        }

        public void setCabAlarm(List<String> cabAlarm) {
            this.cabAlarm = cabAlarm;
        }
    }

   
    public static class BatListBean implements Serializable {
        /**
         * bmsT : 33
         * batteryId : PN204805508DGPN211230248
         * soc : 63
         * totalAH : 55
         * chgCur : 0.0
         * bmsAlarm : ["000"]
         * batVol : 48
         * bmsFault : ["000"]
         * batCycle : 1
         * doorId : 5
         */

        private String bmsT;
        private String batteryId;
        private String soc;
        private String totalAH;
        private String chgCur;
        private String batVol;
        private String batCycle;
        private String doorId;
        private List<String> bmsAlarm;
        private List<String> bmsFault;

        public String getBmsT() {
            return bmsT;
        }

        public void setBmsT(String bmsT) {
            this.bmsT = bmsT;
        }

        public String getBatteryId() {
            return batteryId;
        }

        public void setBatteryId(String batteryId) {
            this.batteryId = batteryId;
        }

        public String getSoc() {
            return soc;
        }

        public void setSoc(String soc) {
            this.soc = soc;
        }

        public String getTotalAH() {
            return totalAH;
        }

        public void setTotalAH(String totalAH) {
            this.totalAH = totalAH;
        }

        public String getChgCur() {
            return chgCur;
        }

        public void setChgCur(String chgCur) {
            this.chgCur = chgCur;
        }

        public String getBatVol() {
            return batVol;
        }

        public void setBatVol(String batVol) {
            this.batVol = batVol;
        }

        public String getBatCycle() {
            return batCycle;
        }

        public void setBatCycle(String batCycle) {
            this.batCycle = batCycle;
        }

        public String getDoorId() {
            return doorId;
        }

        public void setDoorId(String doorId) {
            this.doorId = doorId;
        }

        public List<String> getBmsAlarm() {
            return bmsAlarm;
        }

        public void setBmsAlarm(List<String> bmsAlarm) {
            this.bmsAlarm = bmsAlarm;
        }

        public List<String> getBmsFault() {
            return bmsFault;
        }

        public void setBmsFault(List<String> bmsFault) {
            this.bmsFault = bmsFault;
        }
    }
}

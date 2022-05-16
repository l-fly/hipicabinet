package com.haipai.cabinet.model.entity;

import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;

public class ChargerEntity extends BaseEntity{
    int fwVer4;
    int chgState;
    int opState;
    int realTimeCur;
    int realTimeVol;
    int realTimePwr;
    int workingtemp;
    int maxCur;
    int maxCol;
    int maxPwr;
    int settingVol;
    int settingCur;
    int fault;
    int maxChgrPwr;
    int maxChgrCur;
    int maxChgrVol;
    int port;
    Thread getDataThread;
    public ChargerEntity(int mport){
        port = mport;
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {//升级时退出循环
                    if(!hasPartOneData){
                        LocalDataManager.getInstance().getChargerDataPartOne(port);
                    }
                    if (!hasPartThreeData){
                        LocalDataManager.getInstance().getChargerDataPartThree(port);
                    }
                    if (!hasPartFourData){
                        LocalDataManager.getInstance().getChargerDataPartFour(port);
                    }
                    LocalDataManager.getInstance().getChargerDataPartTow(port);
                    try {
                        Thread.sleep(20000);
                    }catch (Exception e){}
                }
            }
        });
        getDataThread.start();
    }
    boolean hasPartOneData;
    byte[] dataPartOne; //500-513
    public void setDataPartOne(byte[] data){
        LogUtil.i("charge setdata  111  " + port);
        hasPartOneData = true;
        dataPartOne = data;
        if (data.length > 11){
            setHwMainVer(data[2]);
            setHwSubVer(data[3]);
            setFwMainVer(data[4]);
            setFwSubVer(data[5]);
            setFwMinorVer(data[6]);

            byte[] bytes = new byte[4];
            bytes[0] = data[8];
            bytes[1] = data[9];
            bytes[2] = data[10];
            bytes[3] = data[11];
            setFwBuildNum(NumberBytes.bytesToInt(bytes));
        }
        if (data.length > 13){
            setFwVer4(NumberBytes.bytesToInt(new byte[]{data[12], data[13]}));
        }
        if (data.length > 27){
            byte[] bytes = new byte[14];
            System.arraycopy(data,14,bytes,0,14);
            setSn(NumberBytes.byte2String(bytes,14));
        }
    }
    boolean hasPartTowData;
    byte[] dataPartTow; //600 -610
    public void setDataPartTow(byte[] data){
        LogUtil.i("charge setdata  222  " + port);
        hasPartTowData = true;
        dataPartTow = data;
        if(data.length > 0){
            setChgState(data[0]);
        }
        if(data.length > 1){
            setChgState(data[1]);
        }
        if(data.length > 3){
            setRealTimeCur(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 5){
            setRealTimeVol(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
        if(data.length > 7){
            setRealTimePwr(NumberBytes.bytesToInt(new byte[]{data[6], data[7]}));
        }
        if(data.length > 9){
            setWorkingtemp(NumberBytes.bytesToInt(new byte[]{data[8], data[9]}));
        }
        if(data.length > 11){
            setMaxCur(NumberBytes.bytesToInt(new byte[]{data[10], data[11]}));
        }
        if(data.length > 13){
            setMaxCol(NumberBytes.bytesToInt(new byte[]{data[12], data[13]}));
        }
        if(data.length > 15){
            setMaxPwr(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if(data.length > 17){
            setSettingVol(NumberBytes.bytesToInt(new byte[]{data[16], data[17]}));
        }
        if(data.length > 19){
            setSettingCur(NumberBytes.bytesToInt(new byte[]{data[18], data[19]}));
        }

    }
    boolean hasPartThreeData;
    byte[] dataPartThree; //500-502
    public void setDataPartThree(byte[] data){
        LogUtil.i("charge setdata  333  " + port);
        hasPartThreeData = true;
        dataPartThree = data;
        if(data.length > 1){
            setMaxChgrPwr(NumberBytes.bytesToInt(new byte[]{data[0], data[1]}));
        }
        if(data.length > 3){
            setMaxChgrCur(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 5){
            setMaxChgrVol(NumberBytes.bytesToInt(new byte[]{data[3], data[5]}));
        }
    }
    boolean hasPartFourData;
    byte[] dataPartFour;
    public void setDataPartFour(byte[] data){
        LogUtil.i("charge setdata  444  " + port);
        hasPartFourData = true;
        dataPartFour = data;
        if(data.length > 0){
            setChgState(data[0]);
        }
        if(data.length > 1){
            setOpState(data[1]);
        }
        if(data.length > 2){
            setSettingVol(data[2]);
        }
        if(data.length > 3){
            setSettingCur(data[3]);
        }

    }

    public int getMaxChgrPwr() {
        return maxChgrPwr;
    }

    public void setMaxChgrPwr(int maxChgrPwr) {
        this.maxChgrPwr = maxChgrPwr;
    }

    public int getMaxChgrCur() {
        return maxChgrCur;
    }

    public void setMaxChgrCur(int maxChgrCur) {
        this.maxChgrCur = maxChgrCur;
    }

    public int getMaxChgrVol() {
        return maxChgrVol;
    }

    public void setMaxChgrVol(int maxChgrVol) {
        this.maxChgrVol = maxChgrVol;
    }

    public int getFwVer4() {
        return fwVer4;
    }

    public void setFwVer4(int fwVer4) {
        this.fwVer4 = fwVer4;
    }

    public int getChgState() {
        return chgState;
    }

    public void setChgState(int chgState) {
        this.chgState = chgState;
    }

    public int getOpState() {
        return opState;
    }

    public void setOpState(int opState) {
        this.opState = opState;
    }

    public int getRealTimeCur() {
        return realTimeCur;
    }

    public void setRealTimeCur(int realTimeCur) {
        this.realTimeCur = realTimeCur;
    }

    public int getRealTimeVol() {
        return realTimeVol;
    }

    public void setRealTimeVol(int realTimeVol) {
        this.realTimeVol = realTimeVol;
    }

    public int getRealTimePwr() {
        return realTimePwr;
    }

    public void setRealTimePwr(int realTimePwr) {
        this.realTimePwr = realTimePwr;
    }

    public int getWorkingtemp() {
        return workingtemp;
    }

    public void setWorkingtemp(int workingtemp) {
        this.workingtemp = workingtemp;
    }

    public int getMaxCur() {
        return maxCur;
    }

    public void setMaxCur(int maxCur) {
        this.maxCur = maxCur;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }

    public int getMaxPwr() {
        return maxPwr;
    }

    public void setMaxPwr(int maxPwr) {
        this.maxPwr = maxPwr;
    }

    public int getSettingVol() {
        return settingVol;
    }

    public void setSettingVol(int settingVol) {
        this.settingVol = settingVol;
    }

    public int getSettingCur() {
        return settingCur;
    }

    public void setSettingCur(int settingCur) {
        this.settingCur = settingCur;
    }

    public int getFault() {
        return fault;
    }

    public void setFault(int fault) {
        this.fault = fault;
    }

    public String getDescribe() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("硬件版本:").append(getHwVersion()).append("\n");
        stringBuffer.append("固件版本:").append(getFwVersion()).append("\n");

        stringBuffer.append("ChgState=").append(chgState).append("-")
                .append("充电状态:").append(chgState).append("\n");

        stringBuffer.append("OpState=").append(opState).append("-")
                .append("操作状态:").append(opState).append("\n");

        stringBuffer.append("RealTimeCur=").append(realTimeCur).append("-")
                .append("实时输出电流:").append(realTimeCur).append("\n");

        stringBuffer.append("RealTimeVol=").append(realTimeVol).append("-")
                .append("实时输出电压:").append(realTimeVol).append("\n");

        stringBuffer.append("working_temp=").append(workingtemp).append("-")
                .append("实时工作温度:").append(workingtemp).append("\n");

        stringBuffer.append("MaxCur=").append(maxCur).append("-")
                .append("最大输出电流:").append(maxCur).append("\n");

        stringBuffer.append("MaxCol=").append(maxCol).append("-")
                .append("最大输出电压:").append(maxCol).append("\n");

        stringBuffer.append("MaxPwr=").append(maxPwr).append("-")
                .append("最大输出功率:").append(maxPwr).append("\n");

        stringBuffer.append("SettingVol=").append(settingVol).append("-")
                .append("设置充电电压:").append(settingVol).append("\n");

        stringBuffer.append("SettingCur=").append(settingCur).append("-")
                .append("设置充电电流:").append(settingCur).append("\n");

        return stringBuffer.toString();
    }
}

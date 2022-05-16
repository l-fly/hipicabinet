package com.haipai.cabinet.model.entity;

import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;

public class PmsEntity extends BaseEntity{

    /**
     * 换电柜能力描述，0：否；1：是。
     * BIT[0]:是否支持风扇电源控制。
     * BIT[1]:是否支持风扇电源反馈。
     * BIT[2]:是否仓内防盗锁。
     * BIT[3]:是否支持加热功能。
     * BIT[4]:是否支持电池电量测试功能。
     * BIT[5]:是否支持舱内 NTC 功能。
     * BIT[6]:是否支持板上 NTC 功能。
     * BIT[7-15]保留
     */
    int capacity0;

    int resetCount;
    int cabinID;
    int port;

    int devState;
    int opState;
    int onBoardNtc;
    int cabinetNtc;
    int batStatus;
    int batFault;
    int chgrStatus;
    int chgrFault;
    int counter;
    int fireAlarmStatus;

    int fullSoc;
    int fanOnTemp;
    int fanOnTempBacklash;
    int pmsTempOnBoardAlarm;
    int batTempAlarm;
    int deviceEnable;

    int opStateCtrl;
    int devCtrl;
    ChargerEntity charger;
    BatteryEntity battery ;


    Thread getDataThread;
    public PmsEntity(int mport){
        port = mport;
        charger = new ChargerEntity(port);
        battery = new BatteryEntity(port);
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {//升级时退出循环
                    if(!hasPartOneData){
                        LocalDataManager.getInstance().getPmsDataPartOne(port);
                    }
                    if (!hasPartThreeData){
                        LocalDataManager.getInstance().getPmsDataPartThree(port);
                    }
                    if (!hasPartFourData){
                        LocalDataManager.getInstance().getPmsDataPartFour(port);
                    }
                    LocalDataManager.getInstance().getPmsDataPartTow(port);
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){}
                }
            }
        });
        getDataThread.start();
    }

    public BatteryEntity getBattery() {
        return battery;
    }

    public void setBattery(BatteryEntity battery) {
        this.battery = battery;
    }


    public ChargerEntity getCharger() {
        return charger;
    }

    public void setCharger(ChargerEntity charger) {
        this.charger = charger;
    }
    boolean hasPartOneData;
    byte[] dataPartOne;
    public void setDataPartOne(byte[] data){
        hasPartOneData = true;
        dataPartOne = data;
        if (data.length > 2){
            setCabinID(data[2]);
        }
        if (data.length > 3){
            setResetCount(data[3]);
        }
        if (data.length > 13){
            setHwMainVer(data[4]);
            setHwSubVer(data[5]);
            setFwMainVer(data[6]);
            setFwSubVer(data[7]);
            setFwMinorVer(data[8]);

            byte[] bytes = new byte[4];
            bytes[0] = data[10];
            bytes[1] = data[11];
            bytes[2] = data[12];
            bytes[3] = data[13];
            setFwBuildNum(NumberBytes.bytesToInt(bytes));
        }
        if (data.length > 15){
            setCapacity0(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if (data.length > 27){
            byte[] bytes = new byte[12];
            System.arraycopy(data,16,bytes,0,12);
            setPid(NumberBytes.byte2String(bytes,12));
        }
        if (data.length > 41){
            byte[] bytes = new byte[14];
            System.arraycopy(data,28,bytes,0,14);
            setSn(NumberBytes.byte2String(bytes,14));
        }
        LogUtil.i("pms setdata  111  " + port);
    }
    boolean hasPartTowData;
    byte[] dataPartTow;
    public void setDataPartTow(byte[] data){
        hasPartTowData = true;
        LogUtil.i("pms setdata  222  " + port);
        dataPartTow = data;
        if(data.length > 1){
            setDevState(NumberBytes.bytesToInt(new byte[]{data[0], data[1]}));
        }
        if(data.length > 2){
            setOpState(data[2]);
        }
        if(data.length > 4){
            setOnBoardNtc(data[4] - 40);
        }
        if(data.length > 5){
            setCabinetNtc(data[5] - 40);
        }
        if(data.length > 7){
            setBatStatus(NumberBytes.bytesToInt(new byte[]{data[6], data[7]}));
        }
        if(data.length > 11){
            byte[] bytes = new byte[4];
            System.arraycopy(data,8,bytes,0,4);
            setBatFault(NumberBytes.bytesToInt(bytes));
        }
        if(data.length > 13){
            setChgrStatus(NumberBytes.bytesToInt(new byte[]{data[12], data[13]}));
        }
        if(data.length > 15){
            setChgrFault(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if(data.length > 17){
            setCounter(NumberBytes.bytesToInt(new byte[]{data[16], data[17]}));
        }
        if(data.length > 18){
            setFireAlarmStatus(data[18]);
        }
    }
    boolean hasPartThreeData;
    byte[] dataPartThree;
    public void setDataPartThree(byte[] data) {
        hasPartThreeData = true;
        LogUtil.i("pms setdata  333  " + port);
        dataPartThree = data;
        if(data.length > 0){
            setFullSoc(data[0]);
        }
        if(data.length > 2){
            setFanOnTemp(data[2]);
        }
        if(data.length > 3){
            setFanOnTempBacklash(data[3]);
        }
        if(data.length > 4){
            setPmsTempOnBoardAlarm(data[4]);
        }
        if(data.length > 5){
            setBatTempAlarm(data[5]);
        }
    }
    boolean hasPartFourData;
    byte[] dataPartFour;
    public void setDataPartFour(byte[] data){
        hasPartFourData = true;
        LogUtil.i("pms setdata  444  " + port);
        dataPartFour = data;
        if(data.length > 0){
            setOpStateCtrl(data[0]);
        }
        if(data.length > 3){
            setDevCtrl(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }

    }
    public int getCapacity0() {
        return capacity0;
    }

    public void setCapacity0(int capacity0) {
        this.capacity0 = capacity0;
    }

    public int getDevState() {
        return devState;
    }

    public void setDevState(int devState) {
        this.devState = devState;
    }

    public int getOpState() {
        return opState;
    }

    public void setOpState(int opState) {
        this.opState = opState;
    }

    public int getOnBoardNtc() {
        return onBoardNtc;
    }

    public void setOnBoardNtc(int onBoardNtc) {
        this.onBoardNtc = onBoardNtc;
    }

    public int getCabinetNtc() {
        return cabinetNtc;
    }

    public void setCabinetNtc(int cabinetNtc) {
        this.cabinetNtc = cabinetNtc;
    }

    public int getBatStatus() {
        return batStatus;
    }

    public void setBatStatus(int batStatus) {
        this.batStatus = batStatus;
    }

    public int getBatFault() {
        return batFault;
    }

    public void setBatFault(int batFaultLSB) {
        this.batFault = batFaultLSB;
    }

    public int getChgrStatus() {
        return chgrStatus;
    }

    public void setChgrStatus(int chgrStatus) {
        this.chgrStatus = chgrStatus;
    }

    public int getChgrFault() {
        return chgrFault;
    }

    public void setChgrFault(int chgrFault) {
        this.chgrFault = chgrFault;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getFireAlarmStatus() {
        return fireAlarmStatus;
    }

    public void setFireAlarmStatus(int fireAlarmStatus) {
        this.fireAlarmStatus = fireAlarmStatus;
    }

    public int getFullSoc() {
        return fullSoc;
    }

    public void setFullSoc(int fullSoc) {
        this.fullSoc = fullSoc;
    }

    public int getFanOnTemp() {
        return fanOnTemp;
    }

    public void setFanOnTemp(int fanOnTemp) {
        this.fanOnTemp = fanOnTemp;
    }

    public int getFanOnTempBacklash() {
        return fanOnTempBacklash;
    }

    public void setFanOnTempBacklash(int fanOnTempBacklash) {
        this.fanOnTempBacklash = fanOnTempBacklash;
    }

    public int getPmsTempOnBoardAlarm() {
        return pmsTempOnBoardAlarm;
    }

    public void setPmsTempOnBoardAlarm(int pmsTempOnBoardAlarm) {
        this.pmsTempOnBoardAlarm = pmsTempOnBoardAlarm;
    }

    public int getBatTempAlarm() {
        return batTempAlarm;
    }

    public void setBatTempAlarm(int batTempAlarm) {
        this.batTempAlarm = batTempAlarm;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getOpStateCtrl() {
        return opStateCtrl;
    }

    public void setOpStateCtrl(int opStateCtrl) {
        this.opStateCtrl = opStateCtrl;
    }

    public int getDevCtrl() {
        return devCtrl;
    }

    public void setDevCtrl(int devCtrl) {
        this.devCtrl = devCtrl;
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
    }

    public int getCabinID() {
        return cabinID;
    }

    public void setCabinID(int cabinID) {
        this.cabinID = cabinID;
    }

    public boolean hasBattery(){
        return ((batStatus&1) != 0)&&((batStatus&(1<<1)) != 0)&&((batStatus&(1<<2)) != 0);
    }
    public boolean isOpen(){
        //todo
        return false;
    }
    public BatteryInfo getBatteryInfo(){
        BatteryInfo info = new BatteryInfo();
        info.setpId(battery.getPid());
        info.setSn(battery.getSn());
        info.setPort(battery.getPort());
        info.setCycle(battery.getCycle());
        info.setFault(battery.getFault());
        info.setVoltage(battery.getVoltage());
        info.setResidualmAh(battery.getResidualmAh());
        info.setSoc(battery.getSoc());
        info.setCurrent(battery.getCurrent());
        return (info);
    }
    public String getDescribe() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("dataPartOne:").append(NumberBytes.getHexString(dataPartOne)).append("\n");
        stringBuffer.append("dataPartTow:").append(NumberBytes.getHexString(dataPartTow)).append("\n");
        stringBuffer.append("dataPartThree:").append(NumberBytes.getHexString(dataPartThree)).append("\n");
        stringBuffer.append("dataPartFour:").append(NumberBytes.getHexString(dataPartFour)).append("\n");

        stringBuffer.append("硬件版本:").append(getHwVersion()).append("\n");
        stringBuffer.append("固件版本:").append(getFwVersion()).append("\n");

        stringBuffer.append("Capacity0=").append(capacity0).append("-")
                .append("支持风扇电源控制:").append((capacity0&(1<<0))!=0?"是":"否")
                .append(" ;支持风扇电源反馈:").append((capacity0&(1<<1))!=0?"是":"否")
                .append(" ;仓内防盗锁:").append((capacity0&(1<<2))!=0?"是":"否")
                .append(" ;支持加热功能:").append((capacity0&(1<<3))!=0?"是":"否")
                .append(" ;支持电池电量测试功能:").append((capacity0&(1<<4))!=0?"是":"否")
                .append(" ;支持舱内NTC功能:").append((capacity0&(1<<5))!=0?"是":"否")
                .append(" ;支持板上NTC功能:").append((capacity0&(1<<6))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("DevState=").append(devState).append("-")
                .append("打开仓门:").append((devState&(1<<0))!=0?"是":"否")
                .append(" ;启动充电:").append((devState&(1<<1))!=0?"是":"否")
                .append(" ;启动放电:").append((devState&(1<<2))!=0?"是":"否")
                .append(" ;启动仓内风扇:").append((devState&(1<<3))!=0?"是":"否")
                .append(" ;启动加热功能:").append((devState&(1<<4))!=0?"是":"否")
                .append(" ;亮仓门红色LED:").append((devState&(1<<5))!=0?"是":"否")
                .append(" ;亮仓门绿色LED:").append((devState&(1<<6))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("onBoardNtc=").append(onBoardNtc).append("-")
                .append("板上NTC值:").append(onBoardNtc).append("\n");

        stringBuffer.append("cabinetNtc=").append(cabinetNtc).append("-")
                .append("板上NTC值:").append(cabinetNtc).append("\n");

        stringBuffer.append("BatStatus=").append(batStatus).append("-")
                .append("电池是否在位:").append((batStatus&(1<<0))!=0?"是":"否")
                .append(" ;电池是否连接:").append((batStatus&(1<<1))!=0?"是":"否")
                .append(" ;电池是否准备好:").append((batStatus&(1<<2))!=0?"是":"否")
                .append(" ;电池防盗锁是否锁住:").append((batStatus&(1<<3))!=0?"是":"否")
                .append(" ;电池在位传感器是否触发:").append((batStatus&(1<<4))!=0?"是":"否")
                .append(" ;电池是否通信故障:").append((batStatus&(1<<5))!=0?"是":"否")
                .append(" ;电池正在更新固件:").append((batStatus&(1<<6))!=0?"是":"否")
                .append(" ;电池运行在Bootloader状态:").append((batStatus&(1<<7))!=0?"是":"否")
                .append(" ;电池非法取走:").append((batStatus&(1<<8))!=0?"是":"否")
                .append(" ;电池严重故障:").append((batStatus&(1<<9))!=0?"是":"否")
                .append(" ;电池普通故障:").append((batStatus&(1<<10))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("BatFault=").append(batFault).append("-")
                .append("充电器电流和电池电流值不匹配:").append((batFault&(1<<0))!=0?"是":"否")
                .append(" ;充电时电池传感器温度大于预期值:").append((batFault&(1<<1))!=0?"是":"否")
                .append(" ;电池预启动失败:").append((batFault&(1<<2))!=0?"是":"否")
                .append(" ;充电时电池电压故障:").append((batFault&(1<<3))!=0?"是":"否")
                .append(" ;电池满电时充电电流异常:").append((batFault&(1<<4))!=0?"是":"否")
                .append(" ;电池有超过600mA的电流连续输出10S:").append((batFault&(1<<5))!=0?"是":"否")
                .append(" ;电池SOC-电压不匹配故障:").append((batFault&(1<<6))!=0?"是":"否")
                .append(" ;有UVP故障:").append((batFault&(1<<7))!=0?"是":"否")
                .append(" ;同时出现欠压（UVP）和过压（OVP）告警:").append((batFault&(1<<8))!=0?"是":"否")
                .append(" ;同时出现低温（UTP）和高温（OTP）告警:").append((batFault&(1<<9))!=0?"是":"否")
                .append(" ;单芯电压低于2V:").append((batFault&(1<<10))!=0?"是":"否")
                .append(" ;电芯电压差异大:").append((batFault&(1<<11))!=0?"是":"否")
                .append(" ;充电电流故障:").append((batFault&(1<<12))!=0?"是":"否")
                .append(" ;电池和Pms通信不稳定:").append((batFault&(1<<13))!=0?"是":"否")
                .append(" ;电池和Pms通信恢复:").append((batFault&(1<<14))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("ChgrStatus=").append(chgrStatus).append("-")
                .append("充电器是否连接:").append((chgrStatus&(1<<0))!=0?"是":"否")
                .append(" ;充电器是否准备好:").append((chgrStatus&(1<<1))!=0?"是":"否")
                .append(" ;充电器运行在Bootloader状态:").append((chgrStatus&(1<<2))!=0?"是":"否")
                .append(" ;充电器充电功能是否启动:").append((chgrStatus&(1<<3))!=0?"是":"否")
                .append(" ;正在更新充电器固件:").append((chgrStatus&(1<<4))!=0?"是":"否")
                .append(" ;加热功能启动:").append((chgrStatus&(1<<5))!=0?"是":"否")
                .append(" ;充电器是严重故障:").append((chgrStatus&(1<<6))!=0?"是":"否")
                .append(" ;充电器是普通故障:").append((chgrStatus&(1<<7))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("ChgrFault=").append(chgrFault).append("-")
                .append("充电器过流故障:").append((chgrFault&(1<<0))!=0?"是":"否")
                .append(" ;充电器短路故障:").append((chgrFault&(1<<1))!=0?"是":"否")
                .append(" ;充电器过压故障:").append((chgrFault&(1<<2))!=0?"是":"否")
                .append(" ;充电器过温故障:").append((chgrFault&(1<<3))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("FireAlarmStatus=").append(fireAlarmStatus).append("-")
                .append("电池高温触发火警:").append((chgrFault&(1<<0))!=0?"是":"否")
                .append(" ;仓内NTC高温触发火警:").append((chgrFault&(1<<1))!=0?"是":"否")
                .append(" ;PMS板上NTC高温触发火警:").append((chgrFault&(1<<2))!=0?"是":"否")
                .append(" ;电池热失控触发火警:").append((chgrFault&(1<<3))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("fullSoc=").append(fullSoc).append("-")
                .append("允许借出电量值:").append(fullSoc).append("\n");

        stringBuffer.append("FanOnTemp=").append(fanOnTemp).append("-")
                .append("风扇启动温度:").append(fanOnTemp).append("\n");

        stringBuffer.append("FanOnTempBacklash=").append(fanOnTempBacklash).append("-")
                .append("风扇温度回差:").append(fanOnTempBacklash).append("\n");

        stringBuffer.append("PmsTempOnBoardAlarm=").append(pmsTempOnBoardAlarm).append("-")
                .append("PMS板上温度告警值:").append(pmsTempOnBoardAlarm).append("\n");

        stringBuffer.append("BatTempAlarm=").append(batTempAlarm).append("-")
                .append("电池内部温度告警值:").append(batTempAlarm).append("\n");

        stringBuffer.append("DeviceEnable=").append(deviceEnable).append("-")
                .append("充电使能:").append((deviceEnable&(1<<0))!=0?"是":"否")
                .append(" ;板上温度传感器使能:").append((deviceEnable&(1<<1))!=0?"是":"否")
                .append(" ;根据温度动态充电使能:").append((deviceEnable&(1<<2))!=0?"是":"否")
                .append("\n");
        return stringBuffer.toString();
    }
}

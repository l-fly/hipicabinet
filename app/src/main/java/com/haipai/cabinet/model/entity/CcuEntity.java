package com.haipai.cabinet.model.entity;

import com.google.gson.Gson;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;

import java.util.List;

public class CcuEntity extends BaseEntity{
    /**
     * 换电柜能力描述，0：否；1：是。
     * BIT[0]:是否有电表。
     * BIT[1]:是否有柜门锁。
     * BIT[2]:是否有柜内 NTC。
     * BIT[3]:是否有 LCD 区 NTC。
     * BIT[4]:是否有机柜进水传感器。
     * BIT[5]:是否支持 PMS 电源控制。
     * BIT[6]:是否支持 PMS 电源反馈。
     * BIT[7]:是否支持柜内风扇电源控制。
     * BIT[8]:是否支持柜内风扇电源反馈。
     * BIT[9]:是否支持仓内防盗锁。
     * BIT[10]:是否支持加热功能。
     */
    int capacity0;

    /**
     * 保留
     */
    int capacity1;

    /**
     * 仓位总数
     */
    int cabinCount;

    /**
     * 设备状态，1-是；0-否；
     */
    int devState;

    int devFault;

    /**
     * 复位计数器
     */
    int resetCount;

    /**
     * LCD 区温度传感器值, 单位：℃, 偏移+40 度
     */
    int lcdTemp;

    /**
     *充电器区温度传感器值, 单位：℃, 偏移+40 度
     */
    int chargerTemp;

    /**
     * Pms 火警标志
     */
    int fireAlarmMask;


    int deviceEnable;
    int fanPwrOnTemp;
    int fanPwrOffTemp;
    int maxInputCurrent;
    int fullSoc;
    int highTempAlarm;
    int batTempAlarm;

    int ccuCtrl;
    int allPmsCtrl;
    int opStateCtrl;

    Thread getDataThread;
    public CcuEntity(){
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {//升级时退出循环
                    if(!hasPartOneData){
                        LocalDataManager.getInstance().getCcuDataPartOne();
                    }
                    if (!hasPartThreeData){
                        LocalDataManager.getInstance().getCcuDataPartThree();
                    }
                    if (!hasPartFourData){
                        LocalDataManager.getInstance().getCcuDataPartFour();
                    }
                    LocalDataManager.getInstance().getCcuDataPartTow();
                    try {
                        Thread.sleep(4000);
                    }catch (Exception e){}
                }
            }
        });
        getDataThread.start();
    }
    boolean hasPartOneData;
    byte[] dataPartOne;
    public void setDataPartOne(byte[] data){
        hasPartOneData = true;
        LogUtil.i("ccu setdata 111");
        LocalDataManager.initStatus = 1;
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
            setCapacity0(NumberBytes.bytesToInt(new byte[]{data[12], data[13]}));
        }
        if (data.length > 15){
            setCapacity1(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if (data.length > 17){
            int cabinCount = NumberBytes.bytesToInt(new byte[]{data[16], data[17]});
            setCabinCount(cabinCount);
            List<PmsEntity> list = LocalDataManager.getInstance().cabinet.getPmsList();
            if(list.size() == 0 && cabinCount > 0){
                for (int i= 0; i<cabinCount; i++){
                    PmsEntity pmsEntity = new PmsEntity(i);
                    list.add(pmsEntity);
                }
            }
        }
        if (data.length > 29){
            byte[] bytes = new byte[12];
            System.arraycopy(data,18,bytes,0,12);
            setPid(NumberBytes.byte2String(bytes,12));
        }
        if (data.length > 43){
            byte[] bytes = new byte[14];
            System.arraycopy(data,30,bytes,0,14);
            setSn(NumberBytes.byte2String(bytes,14));
        }

    }
    boolean hasPartTowData;
    byte[] dataPartTow;
    public void setDataPartTow(byte[] data){
        hasPartTowData = true;
        LogUtil.i("ccu setdata 222");
        dataPartTow = data;
        if(data.length > 3){
            setDevState(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 4){
            byte b = data[4];
            setResetCount(b&3);
        }
        if(data.length > 6){
            setLcdTemp(data[6] -40);
        }
        if(data.length > 7){
            setChargerTemp(data[7] -40);
        }
        if(data.length > 9){
            int fireAlarmMask = NumberBytes.bytesToInt(new byte[]{data[8], data[8]});
            setFireAlarmMask(fireAlarmMask);
            //todo
        }
    }
    boolean hasPartThreeData;
    byte[] dataPartThree;
    public void setDataPartThree(byte[] data){
        hasPartThreeData = true;
        LogUtil.i("ccu setdata 333");
        dataPartThree = data;
        if(data.length > 1){
            setDeviceEnable(NumberBytes.bytesToInt(new byte[]{data[0], data[1]}));
        }
        if(data.length > 2){
            setFanPwrOnTemp(data[2]);
        }
        if(data.length > 3){
            setFanPwrOffTemp(data[3]);
        }
        if(data.length > 4){
            setMaxInputCurrent(data[4]);
        }
        if(data.length > 5){
            setFullSoc(data[5]);
        }
        if(data.length > 6){
            setHighTempAlarm(data[6]);
        }
        if(data.length > 7){
            setBatTempAlarm(data[7]);
        }
    }
    boolean hasPartFourData;
    byte[] dataPartFour;
    public void setDataPartFour(byte[] data){
        hasPartFourData = true;
        LogUtil.i("ccu setdata 444");
        dataPartFour = data;
        if(data.length > 0){
            setCcuCtrl(data[0]);
        }
        if(data.length > 3){
            setCcuCtrl(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 5){
            setOpStateCtrl(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
    }

    public int getCapacity0() {
        return capacity0;
    }

    public void setCapacity0(int capacity0) {
        this.capacity0 = capacity0;
    }

    public int getCapacity1() {
        return capacity1;
    }

    public void setCapacity1(int capacity1) {
        this.capacity1 = capacity1;
    }

    public int getCabinCount() {
        return cabinCount;
    }

    public void setCabinCount(int cabinCount) {
        this.cabinCount = cabinCount;
    }

    public int getDevState() {
        return devState;
    }

    public void setDevState(int devState) {
        this.devState = devState;
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
    }

    public int getLcdTemp() {
        return lcdTemp;
    }

    public void setLcdTemp(int lcdTemp) {
        this.lcdTemp = lcdTemp;
    }

    public int getChargerTemp() {
        return chargerTemp;
    }

    public void setChargerTemp(int chargerTemp) {
        this.chargerTemp = chargerTemp;
    }

    public int getFireAlarmMask() {
        return fireAlarmMask;
    }

    public void setFireAlarmMask(int fireAlarmMask) {
        this.fireAlarmMask = fireAlarmMask;
    }

    public int getDeviceEnable() {
        return deviceEnable;
    }

    public void setDeviceEnable(int deviceEnable) {
        this.deviceEnable = deviceEnable;
    }

    public int getFanPwrOnTemp() {
        return fanPwrOnTemp;
    }

    public void setFanPwrOnTemp(int fanPwrOnTemp) {
        this.fanPwrOnTemp = fanPwrOnTemp;
    }

    public int getFanPwrOffTemp() {
        return fanPwrOffTemp;
    }

    public void setFanPwrOffTemp(int fanPwrOffTemp) {
        this.fanPwrOffTemp = fanPwrOffTemp;
    }

    public int getMaxInputCurrent() {
        return maxInputCurrent;
    }

    public void setMaxInputCurrent(int maxInputCurrent) {
        this.maxInputCurrent = maxInputCurrent;
    }

    public int getFullSoc() {
        return fullSoc;
    }

    public void setFullSoc(int fullSoc) {
        this.fullSoc = fullSoc;
    }

    public int getHighTempAlarm() {
        return highTempAlarm;
    }

    public void setHighTempAlarm(int highTempAlarm) {
        this.highTempAlarm = highTempAlarm;
    }

    public int getBatTempAlarm() {
        return batTempAlarm;
    }

    public void setBatTempAlarm(int batTempAlarm) {
        this.batTempAlarm = batTempAlarm;
    }

    public int getCcuCtrl() {
        return ccuCtrl;
    }

    public void setCcuCtrl(int ccuCtrl) {
        this.ccuCtrl = ccuCtrl;
    }

    public int getAllPmsCtrl() {
        return allPmsCtrl;
    }

    public void setAllPmsCtrl(int allPmsCtrl) {
        this.allPmsCtrl = allPmsCtrl;
    }

    public int getOpStateCtrl() {
        return opStateCtrl;
    }

    public void setOpStateCtrl(int opStateCtrl) {
        this.opStateCtrl = opStateCtrl;
    }

    public int getDevFault() {
        return devFault;
    }

    public void setDevFault(int devFault) {
        this.devFault = devFault;
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
                .append("电表:").append((capacity0&(1<<0))!=0?"是":"否")
                .append(" ;柜门锁:").append((capacity0&(1<<1))!=0?"是":"否")
                .append(" ;柜内NTC:").append((capacity0&(1<<2))!=0?"是":"否")
                .append(" ;LCD区NTC:").append((capacity0&(1<<3))!=0?"是":"否")
                .append(" ;机柜进水传感器:").append((capacity0&(1<<4))!=0?"是":"否")
                .append(" ;支持PMS电源控制:").append((capacity0&(1<<5))!=0?"是":"否")
                .append(" ;支持PMS电源反馈:").append((capacity0&(1<<6))!=0?"是":"否")
                .append(" ;支持柜内风扇电源控制:").append((capacity0&(1<<7))!=0?"是":"否")
                .append(" ;支持柜内风扇电源反馈:").append((capacity0&(1<<8))!=0?"是":"否")
                .append(" ;支持仓内防盗锁:").append((capacity0&(1<<9))!=0?"是":"否")
                .append(" ;支持加热功能:").append((capacity0&(1<<10))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("CabinCount=").append(cabinCount).append("-")
                .append("仓位总数:").append(cabinCount).append("\n");

        stringBuffer.append("DevState=").append(devState).append("-")
                .append("机柜进水:").append((devState&(1<<0))!=0?"是":"否")
                .append(" ;风扇1启动:").append((devState&(1<<1))!=0?"是":"否")
                .append(" ;风扇2启动:").append((devState&(1<<2))!=0?"是":"否")
                .append(" ;PMS上电:").append((devState&(1<<3))!=0?"是":"否")
                .append(" ;电表在位:").append((devState&(1<<4))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("DevFault=").append(devFault).append("-")
                .append("风扇1故障:").append((devFault&(1<<0))!=0?"是":"否")
                .append(" ;风扇2故障:").append((devFault&(1<<1))!=0?"是":"否")
                .append("\n");

        stringBuffer.append("Lcd-Temp=").append(lcdTemp).append("-")
                .append("LCD区温度:").append(lcdTemp).append("\n");

        stringBuffer.append("Charger-Temp=").append(chargerTemp).append("-")
                .append("充电器区温度:").append(chargerTemp).append("\n");

        stringBuffer.append("FireAlarmMask=").append(fireAlarmMask).append("-")
                .append("火警:").append(fireAlarmMask!=0?"是":"否").append("\n");

        stringBuffer.append("DeviceEnable=").append(deviceEnable).append("-")
                .append("柜内上温度传感器使能:").append((deviceEnable&(1<<0))!=0?"是":"否")
                .append(" ;柜内下温度传感器使能:").append((deviceEnable&(1<<1))!=0?"是":"否").append("\n");

        stringBuffer.append("FanPwrOn_temp =").append(fanPwrOnTemp).append("-")
                .append("风扇启动温度:").append(fanPwrOnTemp).append("\n");

        stringBuffer.append("FanPwrOff_temp =").append(fanPwrOffTemp).append("-")
                .append("风扇停止温度:").append(fanPwrOffTemp).append("\n");

        stringBuffer.append("MaxInputCurrent =").append(maxInputCurrent).append("-")
                .append("柜子的最大输入电流:").append(maxInputCurrent).append("\n");

        stringBuffer.append("fullSoc =").append(fullSoc).append("-")
                .append("可换电池电量值:").append(fullSoc).append("\n");

        stringBuffer.append("HighTempAlarm =").append(highTempAlarm).append("-")
                .append("环境高温告警值:").append(highTempAlarm).append("\n");

        stringBuffer.append("BatTempAlarm =").append(batTempAlarm).append("-")
                .append("电池内部温度告警值:").append(batTempAlarm).append("\n");

        return stringBuffer.toString();
    }
}

package com.haipai.cabinet.manager;


import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.model.entity.Cabinet;
import com.haipai.cabinet.model.entity.PmsEntity;
import com.haipai.cabinet.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;


public class LocalDataManager {
    public static String imsi = "";
    public static String ccid = "";
    public static String imei = "";
    public static int dbm = 0;

    public static final int MAIN_ACTIVITY = 1;
    public static final int OTHER_ACTIVITY = 0;
    public static int currentActivity = MAIN_ACTIVITY;
    /**
     * 0,获取硬件初始信息，等待发送初始化给服务器
     * 1，已获取硬件初始信息，正在发送初始化给服务器
     * 2，已经发送初始化给服务器，收到应答了
     */
    public static int initStatus = 0;
    public static int slotNum = 12;   //插槽数
    public static String devId = "CHZD12GDTY200329110";


    public static int shouldEmptyPort = -1;

    public static int openSlot1 = -1;
    public static int openSlot2 = -1;
    public static BatteryInfo mBattery1 = null;
    public static BatteryInfo mBattery2 = null;

    public static BatteryInfo mBatteryGetOrBack = null;

    public static int outValidSoc = 90;      //可借出电池电量值，小于此的电量不能借出

    private static int batteryTotal = 0;

    //private static int batteryInOrder48 = 0;   //48V被预定电池数
    private static int batteryNum48 = 0;  //48V总电池数减掉被禁和仓门开的
    private static int realValidBatteryNum48 = 0;  //48V实际有效电池数，总电池数减掉不符合要求的电池数,可借出电池数


    //private static int batteryInOrder60 = 0;
    private static int batteryNum60 = 0;
    private static int realValidBatteryNum60 = 0;

   // private static int batteryInOrder72 = 0;
    private static int batteryNum72 = 0;
    private static int realValidBatteryNum72 = 0;
    public static void initOrderStatus(){
        OrderManager.currentOrder = null;
        openSlot1 = -1;
        openSlot2 = -1;
        mBattery1 = null;
        mBattery2 = null;
        mBatteryGetOrBack = null;
    }
    public static int getBatteryTotal(){
        return batteryTotal;
    }
    public static int getLogicValidBatteryNum(int vType){
        int num;
        if(vType == 0){
            num = realValidBatteryNum48;
        }else if(vType== 1){
            num = realValidBatteryNum60;
        }else if(vType== 2){
            num = realValidBatteryNum72;
        }else {
            num = 0;
        }
        if (num <0 ){
            num = 0;
        }
        return num;
    }
    public static int getLogicGetBatteryNum(int vType){
        int num;
        if(vType == 0){
            num = batteryNum48;
        }else if(vType== 1){
            num = batteryNum60;
        }else if(vType== 2){
            num = batteryNum72;
        }else {
            num = 0;
        }
        if (num <0 ){
            num = 0;
        }
        return num;
    }
    public static int getEmptyNum(){
        int num = slotNum - batteryTotal - getDisableNum();
        if (num < 0){
            num = 0;
        }
        return num;
    }
    public static int getLogicReturnBatteryNum(){
        int num = slotNum - batteryTotal - getDisableNum() -1;
        if (num < 0){
            num = 0;
        }
        return num;
    }
    public static int getDisableNum(){
        int num = 0;
        for (int i = 0 ; i < slotNum; i++){
            if (isPortDisable(i)){
                num ++;
            }
        }
        return num;
    }

    private LocalDataManager() {
    }
    private static LocalDataManager instance = null;

    public static LocalDataManager getInstance() {
        if (instance == null) {
            instance = new LocalDataManager();
        }
        return instance;
    }

    public Cabinet cabinet = new Cabinet();
    private List<BatteryInfo> batterys = new ArrayList<>();
    public  List<BatteryInfo> extraBatteries = new ArrayList<>();
    public List<BatteryInfo> getBatteriesClone(){
        List<BatteryInfo> rlt = new ArrayList<>();
        if(batterys!=null && batterys.size()>0){
            synchronized (batterys){
                for(int i = 0; i < batterys.size(); i++){
                    BatteryInfo info = new BatteryInfo();
                    info.clone(batterys.get(i));
                    rlt.add(info);
                }
            }
        }
        return rlt;
    }
    public List<BatteryInfo> getExtraBatteriesClone(){
        List<BatteryInfo> rlt = new ArrayList<>();
        if(extraBatteries !=null && extraBatteries.size()>0){
            synchronized (extraBatteries){
                for(int i = 0; i < extraBatteries.size(); i++){
                    BatteryInfo info = new BatteryInfo();
                    info.clone(extraBatteries.get(i));
                    rlt.add(info);
                }
            }
        }
        return rlt;
    }
    public List<BatteryInfo> getBatteriesAllClone(){
        List<BatteryInfo> rlt = new ArrayList<>();
        if(batterys!=null && batterys.size()>0){
            synchronized (batterys){
                for(int i = 0; i < batterys.size(); i++){
                    BatteryInfo info = new BatteryInfo();
                    info.clone(batterys.get(i));
                    rlt.add(info);
                }
            }
        }
        if(extraBatteries !=null && extraBatteries.size()>0){
            synchronized (extraBatteries){
                for(int i = 0; i < extraBatteries.size(); i++){
                    BatteryInfo info = new BatteryInfo();
                    info.clone(extraBatteries.get(i));
                    rlt.add(info);
                }
            }
        }
        return rlt;
    }
    public static final int MAX_STATUS_NUM = 16;  //表示状态的最大byte数
    public static boolean[] disableSlot = new boolean[MAX_STATUS_NUM]; //true 表示禁止使用
    public static boolean isPortDisable(int port){
        if (port > MAX_STATUS_NUM){
            return true;
        }else {
            return disableSlot[port];
        }
    }
    public static void setPortDisable(int port, boolean isDisable){
        if (port > MAX_STATUS_NUM || port < 0){
            return ;
        }else {
            disableSlot[port] = isDisable;
            if(PreferencesUtil.getInstance()!=null)
            PreferencesUtil.getInstance().setSlotDisable(port,isDisable);
        }
    }
    public boolean isPortClose(int port) {
        if (port >= 0 && port < cabinet.getPmsList().size()) {
            PmsEntity pms = cabinet.getPmsList().get(port);
            return !pms.isOpen();
        }
        return true;
    }
    public boolean isPortEmpty(int port) {
        //todo
        return true;
    }
    public static boolean checkBatteryNotOuted(BatteryInfo info){
        boolean isNoOuted = false;
        if (info.isOutValid() && info.getPort()!= LocalDataManager.shouldEmptyPort) {
            //被换出去的里面的不能算有效的
            isNoOuted = true;
        }
        return isNoOuted;
    }
    private void batteryClassify(List<BatteryInfo> batteries){
        List<BatteryInfo> tempBatteries = new ArrayList<>();
        List<BatteryInfo> tempExtras = new ArrayList<>();

        int num48 = 0;
        int num60 = 0;
        int num72 = 0;

        int valid48 = 0;
        int valid60 = 0;
        int valid72 = 0;

        int special48 = 0;
        int special60 = 0;
        int special72 = 0;
        for(int i = 0; i < batteries.size(); i++){
            BatteryInfo info = batteries.get(i);
            if(info.getType() == 0){
                num48++;
                if(isPortClose(info.getPort()) || !isPortDisable(info.getPort())){
                    if(checkBatteryNotOuted(info)){
                        valid48++;
                    }
                    tempBatteries.add(info);
                }else {
                    tempExtras.add(info);
                    special48++;
                }
            }else if(info.getType() == 1){
                num60++;
                if(isPortClose(info.getPort()) || !isPortDisable(info.getPort())){
                    if(checkBatteryNotOuted(info)){
                        valid60++;
                    }
                    tempBatteries.add(info);
                }else {
                    tempExtras.add(info);
                    special60++;
                }
            }else if(info.getType() == 2){
                num72++;
                if(isPortClose(info.getPort()) || !isPortDisable(info.getPort())){
                    if(checkBatteryNotOuted(info)){
                        valid72++;
                    }
                    tempBatteries.add(info);
                }else {
                    tempExtras.add(info);
                    special72++;
                }
            }
        }
        synchronized (batterys){
            batterys = tempBatteries;
        }
        synchronized (batterys){
            extraBatteries = tempExtras;
        }
        batteryTotal = num48 + num60 + num72;
        batteryNum48 = num48 - special48;
        realValidBatteryNum48 = valid48;
        batteryNum60 = num60 - special60;
        realValidBatteryNum60 = valid60;
        batteryNum72 = num72 - special72;
        realValidBatteryNum72 = valid72;
    }
    public void onPassSecond(){
        List<BatteryInfo> batteries = new ArrayList<>();
        synchronized (cabinet) {
            for (PmsEntity pms : cabinet.getPmsList()) {
                if (pms.hasBattery()) {
                    batteries.add(pms.getBatteryInfo());
                }
            }
        }
        batteryClassify(batteries);
    }
    public void getCcuDataPartOne(){

        SerialManager.getInstance().send04(1,0,22);
    }
    public void setCcuDataPartOne(byte[] data){

        cabinet.getCcu().setDataPartOne(data);

        List<PmsEntity> list = LocalDataManager.getInstance().cabinet.getPmsList();
        if(list.size() == 0 ){
            for (int i= 0; i< 12; i++){
                PmsEntity pmsEntity = new PmsEntity(i);
                list.add(pmsEntity);
            }
        }
    }
    public void getCcuDataPartTow(){

        SerialManager.getInstance().send04(1,49,5);
    }
    public void setCcuDataPartTow(byte[] data){

        cabinet.getCcu().setDataPartTow(data);
    }
    public void getCcuDataPartThree(){

        SerialManager.getInstance().send03(1,0,4);
    }
    public void setCcuDataPartThree(byte[] data){

        cabinet.getCcu().setDataPartThree(data);
    }
    public void getCcuDataPartFour(){

        SerialManager.getInstance().send03(1,99,3);
    }
    public void setCcuDataPartFour(byte[] data){

        cabinet.getCcu().setDataPartFour(data);
    }

    public void getPmsDataPartOne(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,0,21);
        }

    }
    public void setPmsDataPartOne(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).setDataPartOne(data);
        }
    }

    public void getPmsDataPartTow(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,99,10);
        }
    }
    public void setPmsDataPartTow(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).setDataPartTow(data);
        }
    }

    public void getPmsDataPartThree(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send03(port +4,0,3);
        }
    }
    public void setPmsDataPartThree(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).setDataPartThree(data);
        }
    }

    public void getPmsDataPartFour(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send03(port +4,99,2);
        }
    }
    public void setPmsDataPartFour(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).setDataPartFour(data);
        }
    }

    public void getChargerDataPartOne(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,499,14);
        }
    }
    public void setChargerDataPartOne(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getCharger().setDataPartOne(data);
        }
    }

    public void getChargerDataPartTow(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,599,11);
        }
    }
    public void setChargerDataPartTow(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getCharger().setDataPartTow(data);
        }
    }
    public void getChargerDataPartThree(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send03(port +4,499,4);
        }
    }
    public void setChargerDataPartThree(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getCharger().setDataPartThree(data);
        }
    }
    public void getChargerDataPartFour(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send03(port +4,599,5);
        }
    }
    public void setChargerDataPartFour(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getCharger().setDataPartFour(data);
        }
    }

    public void getBatteryDataPartOne(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,999,20);
        }
    }
    public void setBatteryDataPartOne(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getBattery().setDataPartOne(data);
        }
    }
    public void getBatteryDataPartTow(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,149,2);
        }
    }
    public void setBatteryDataPartTow(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getBattery().setDataPartTow(data);
        }
    }
    public void getMeterData(){

        SerialManager.getInstance().send04(2,0,10);
    }
    public void setMeterDat(byte[] data){
        cabinet.getMeter().setDataPart(data);
    }
   /* public void getBatteryDataPartThree(int port){

        if(cabinet.getPmsList().size() > port){
            SerialManager.getInstance().send04(port +4,1508,13);
        }
    }
    public void setBatteryDataPartThree(int port,byte[] data){

        if(cabinet.getPmsList().size() > port){
            cabinet.getPmsList().get(port).getBattery().setDataPartThree(data);
        }
    }*/

}

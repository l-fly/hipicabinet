package com.haipai.cabinet.model.entity;

import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;

public class BatteryEntity extends BaseEntity{
    int manufacturer;
    int model;
    int capacity;
    int nominalmAh;
    int productionDate;
    int clusterCount;
    int ntcCount;
   
    int fault;
    int voltage;
    int current;
    int residualmAh;
    int soc;
    int shortCount;
    int chgOcpCount;
    int dischgOcpCount;
    int cellOvpCount;
    int cellUvpCount;
    int chgHighTempCount;
    int chgLowTempCount;
    int dischgHighTempCount;
    int dischgLowTempCount;
    int packOvpCount;
    int packUvpCount;
    int resetCount;
    int cycle;
    Thread getDataThread;

    int port;
    public BatteryEntity(int mport){
        port = mport;
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {//升级时退出循环
                   /* if(!hasPartOneData){

                    }*/
                    LocalDataManager.getInstance().getBatteryDataPartOne(port);
                    LocalDataManager.getInstance().getBatteryDataPartTow(port);
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){}
                }
            }
        });
        getDataThread.start();
    }
    boolean hasPartOneData;
    byte[] dataPartOne;
    public void setDataPartOne(byte[] data){
        LogUtil.i("Battery setdata  111  " + port);
        hasPartOneData = true;
        dataPartOne = data;

        if (data.length > 2){
            setManufacturer(data[2]);
        }
        if (data.length > 3){
            setModel(data[3]);
        }
        if (data.length > 5){
            setCapacity(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
        if (data.length > 17){
            setHwMainVer(data[8]);
            setHwSubVer(data[9]);
            setFwMainVer(data[10]);
            setFwSubVer(data[11]);
            setFwMinorVer(data[12]);

            byte[] bytes = new byte[4];
            bytes[0] = data[14];
            bytes[1] = data[15];
            bytes[2] = data[16];
            bytes[3] = data[17];
            setFwBuildNum(NumberBytes.bytesToInt(bytes));
        }
        if (data.length > 31){
            byte[] bytes = new byte[14];
            System.arraycopy(data,18,bytes,0,14);
            setSn(NumberBytes.byte2String(bytes,14));
        }
        if (data.length > 33){
            setNominalmAh(NumberBytes.bytesToInt(new byte[]{data[32], data[33]}));
        }
        if (data.length > 35){
            setProductionDate(NumberBytes.bytesToInt(new byte[]{data[34], data[35]}));
        }
        if (data.length > 37){
            setClusterCount(NumberBytes.bytesToInt(new byte[]{data[36], data[37]}));
        }
        if (data.length > 39){
            setNtcCount(NumberBytes.bytesToInt(new byte[]{data[38], data[39]}));
        }
        if (data.length > 41){
            setCycle(NumberBytes.bytesToInt(new byte[]{data[40], data[41]}));
        }
    }
    boolean hasPartTowData;
    byte[] dataPartTow; //1500-1507
    public void setDataPartTow(byte[] data){
        LogUtil.i("Battery setdata  222  " + port);
        hasPartTowData = true;
        dataPartTow = data;
        if(data.length > 1){
            setFault(NumberBytes.bytesToInt(new byte[]{data[0], data[1]}));
        }
        if(data.length > 3){
            setVoltage(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 5){
            setCurrent(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
        if(data.length > 7){
            setResidualmAh(NumberBytes.bytesToInt(new byte[]{data[6], data[7]}));
        }
        if(data.length > 9){
            setSoc(NumberBytes.bytesToInt(new byte[]{data[8], data[9]}));
        }

        if(data.length > 19){
            setShortCount(NumberBytes.bytesToInt(new byte[]{data[18], data[19]}));
        }
        if(data.length > 21){
            setChgOcpCount(NumberBytes.bytesToInt(new byte[]{data[20], data[21]}));
        }
        if(data.length > 23){
            setDischgOcpCount(NumberBytes.bytesToInt(new byte[]{data[22], data[23]}));
        }
        if(data.length > 25){
            setCellOvpCount(NumberBytes.bytesToInt(new byte[]{data[24], data[25]}));
        }
        if(data.length > 27){
            setCellUvpCount(NumberBytes.bytesToInt(new byte[]{data[26], data[27]}));
        }
        if(data.length > 29){
            setChgHighTempCount(NumberBytes.bytesToInt(new byte[]{data[28], data[29]}));
        }
        if(data.length > 31){
            setChgLowTempCount(NumberBytes.bytesToInt(new byte[]{data[30], data[31]}));
        }
        if(data.length > 33){
            setDischgHighTempCount(NumberBytes.bytesToInt(new byte[]{data[32], data[33]}));
        }
        if(data.length > 35){
            setDischgLowTempCount(NumberBytes.bytesToInt(new byte[]{data[34], data[35]}));
        }
        if(data.length > 37){
            setPackOvpCount(NumberBytes.bytesToInt(new byte[]{data[36], data[37]}));
        }
        if(data.length > 39){
            setPackUvpCount(NumberBytes.bytesToInt(new byte[]{data[38], data[39]}));
        }
        if(data.length > 41){
            setPackUvpCount(NumberBytes.bytesToInt(new byte[]{data[40], data[41]}));
        }

    }
  /*  byte[] dataPartThree; //1508-1520
    public void setDataPartThree(byte[] data){
        dataPartThree = data;
        if(data.length > 3){
            setShortCount(NumberBytes.bytesToInt(new byte[]{data[2], data[3]}));
        }
        if(data.length > 5){
            setChgOcpCount(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
        if(data.length > 7){
            setDischgOcpCount(NumberBytes.bytesToInt(new byte[]{data[6], data[7]}));
        }
        if(data.length > 9){
            setCellOvpCount(NumberBytes.bytesToInt(new byte[]{data[8], data[9]}));
        }
        if(data.length > 11){
            setCellUvpCount(NumberBytes.bytesToInt(new byte[]{data[10], data[11]}));
        }
        if(data.length > 13){
            setChgHighTempCount(NumberBytes.bytesToInt(new byte[]{data[12], data[13]}));
        }
        if(data.length > 15){
            setChgLowTempCount(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if(data.length > 17){
            setDischgHighTempCount(NumberBytes.bytesToInt(new byte[]{data[16], data[17]}));
        }
        if(data.length > 19){
            setDischgLowTempCount(NumberBytes.bytesToInt(new byte[]{data[18], data[19]}));
        }
        if(data.length > 21){
            setPackOvpCount(NumberBytes.bytesToInt(new byte[]{data[20], data[21]}));
        }
        if(data.length > 23){
            setPackUvpCount(NumberBytes.bytesToInt(new byte[]{data[22], data[23]}));
        }
        if(data.length > 25){
            setResetCount(NumberBytes.bytesToInt(new byte[]{data[24], data[25]}));
        }
    }
*/
    public int getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(int manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNominalmAh() {
        return nominalmAh;
    }

    public void setNominalmAh(int nominalmAh) {
        this.nominalmAh = nominalmAh;
    }

    public int getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(int productionDate) {
        this.productionDate = productionDate;
    }

    public int getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        this.clusterCount = clusterCount;
    }

    public int getNtcCount() {
        return ntcCount;
    }

    public void setNtcCount(int ntcCount) {
        this.ntcCount = ntcCount;
    }


    public int getFault() {
        return fault;
    }

    public void setFault(int fault) {
        this.fault = fault;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getResidualmAh() {
        return residualmAh;
    }

    public void setResidualmAh(int residualmAh) {
        this.residualmAh = residualmAh;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getShortCount() {
        return shortCount;
    }

    public void setShortCount(int shortCount) {
        this.shortCount = shortCount;
    }

    public int getChgOcpCount() {
        return chgOcpCount;
    }

    public void setChgOcpCount(int chgOcpCount) {
        this.chgOcpCount = chgOcpCount;
    }

    public int getDischgOcpCount() {
        return dischgOcpCount;
    }

    public void setDischgOcpCount(int dischgOcpCount) {
        this.dischgOcpCount = dischgOcpCount;
    }

    public int getCellOvpCount() {
        return cellOvpCount;
    }

    public void setCellOvpCount(int cellOvpCount) {
        this.cellOvpCount = cellOvpCount;
    }

    public int getCellUvpCount() {
        return cellUvpCount;
    }

    public void setCellUvpCount(int cellUvpCount) {
        this.cellUvpCount = cellUvpCount;
    }

    public int getChgHighTempCount() {
        return chgHighTempCount;
    }

    public void setChgHighTempCount(int chgHighTempCount) {
        this.chgHighTempCount = chgHighTempCount;
    }

    public int getChgLowTempCount() {
        return chgLowTempCount;
    }

    public void setChgLowTempCount(int chgLowTempCount) {
        this.chgLowTempCount = chgLowTempCount;
    }

    public int getDischgHighTempCount() {
        return dischgHighTempCount;
    }

    public void setDischgHighTempCount(int dischgHighTempCount) {
        this.dischgHighTempCount = dischgHighTempCount;
    }

    public int getDischgLowTempCount() {
        return dischgLowTempCount;
    }

    public void setDischgLowTempCount(int dischgLowTempCount) {
        this.dischgLowTempCount = dischgLowTempCount;
    }

    public int getPackOvpCount() {
        return packOvpCount;
    }

    public void setPackOvpCount(int packOvpCount) {
        this.packOvpCount = packOvpCount;
    }

    public int getPackUvpCount() {
        return packUvpCount;
    }

    public void setPackUvpCount(int packUvpCount) {
        this.packUvpCount = packUvpCount;
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }
}

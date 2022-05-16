package com.haipai.cabinet.model.entity;

import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.NumberBytes;

public class MeterEntity {

    int manufacturer;
    int model;
    int devState;
    int totalWh;
    int nowWh;
    int voltage;
    int current;
    byte[] dataPart;
    Thread getDataThread;
    public MeterEntity(){
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {//升级时退出循环
                    try {
                        Thread.sleep(300000);
                    }catch (Exception e){}
                }
            }
        });
        getDataThread.start();
    }
    public void setDataPart(byte[] data){
        dataPart = data;
        if(data.length > 2){
            setManufacturer(data[2]);
        }
        if(data.length > 3){
            setModel(data[3]);
        }
        if(data.length > 5){
            setDevState(NumberBytes.bytesToInt(new byte[]{data[4], data[5]}));
        }
        if(data.length > 9){
            byte[] bytes = new byte[4];
            System.arraycopy(data,6,bytes,0,4);
            setTotalWh(NumberBytes.bytesToInt(bytes));

        }
        if(data.length > 13){
            byte[] bytes = new byte[4];
            System.arraycopy(data,10,bytes,0,4);
            setNowWh(NumberBytes.bytesToInt(bytes));
        }
        if(data.length > 15){
            setVoltage(NumberBytes.bytesToInt(new byte[]{data[14], data[15]}));
        }
        if(data.length > 19){
            byte[] bytes = new byte[4];
            System.arraycopy(data,16,bytes,0,4);
            setCurrent(NumberBytes.bytesToInt(bytes));
        }
    }


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

    public int getDevState() {
        return devState;
    }

    public void setDevState(int devState) {
        this.devState = devState;
    }

    public int getTotalWh() {
        return totalWh;
    }

    public void setTotalWh(int totalWh) {
        this.totalWh = totalWh;
    }

    public int getNowWh() {
        return nowWh;
    }

    public void setNowWh(int nowWh) {
        this.nowWh = nowWh;
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
}

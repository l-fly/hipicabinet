package com.haipai.cabinet.entity;


import com.haipai.cabinet.manager.LocalDataManager;

import java.io.Serializable;


public class BatteryInfo implements Serializable {
    String pId;
    String sn;
    int port;
    int fault;
    int voltage;
    int current;
    int residualmAh;
    int soc;
    int cycle;
    int type;

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

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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
    // 0-48,1-60,2-72
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 是否可还
     * @return
     */
    public boolean isInValid(){
        // todo
        return true;
    }

    /**
     * 是否可借出
     * @return
     */
    public boolean isOutValid(){
        return soc > LocalDataManager.outValidSoc;
    }

    public boolean isExchangeOutValid(){
        return soc > 30;
    }

    public BatteryInfo clone(BatteryInfo info){
        pId = info.pId;
        sn = info.sn;
        port = info.port;
        fault = info.fault;
        voltage = info.voltage;
        current = info.current;
        residualmAh = info.residualmAh;
        soc = info.soc;
        cycle = info.cycle;
        type = info.type;
        return this;
    }
}

package com.haipai.cabinet.model.entity;

import com.google.gson.Gson;

public class BaseEntity {
    //boolean init;

    /*public boolean isInit() {
        return init;
    }*/
    //int registerVersion;//寄存器版本号
    int hwMainVer;//硬件主版本
    int hwSubVer;//硬件子版本
    int fwMainVer;//固件主版本号
    int fwSubVer;//固件子版本号
    int fwMinorVer;//固件修订版本号
    //固件版本 BuildNum
    int fwBuildNum;


    String pid = "no data";
    String sn= "no data";



  /*  public int getRegisterVersion() {
        return registerVersion;
    }

    public void setRegisterVersion(int registerVersion) {
        this.registerVersion = registerVersion;
    }*/

    public int getHwMainVer() {
        return hwMainVer;
    }

    public void setHwMainVer(int hwMainVer) {
        this.hwMainVer = hwMainVer;
    }

    public int getHwSubVer() {
        return hwSubVer;
    }

    public void setHwSubVer(int hwSubVer) {
        this.hwSubVer = hwSubVer;
    }

    public int getFwMainVer() {
        return fwMainVer;
    }

    public void setFwMainVer(int fwMainVer) {
        this.fwMainVer = fwMainVer;
    }

    public int getFwSubVer() {
        return fwSubVer;
    }

    public void setFwSubVer(int fwSubVer) {
        this.fwSubVer = fwSubVer;
    }

    public int getFwMinorVer() {
        return fwMinorVer;
    }

    public void setFwMinorVer(int fwMinorVer) {
        this.fwMinorVer = fwMinorVer;
    }

    public int getFwBuildNum() {
        return fwBuildNum;
    }

    public void setFwBuildNum(int fwBuildNum) {
        this.fwBuildNum = fwBuildNum;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHwVersion(){
        return getHwMainVer() + "." +getFwMinorVer();
    }
    public String getFwVersion(){
        return getFwMainVer() + "." + getFwSubVer()+ "."  + getFwMinorVer()+ "." +getFwBuildNum();
    }

}

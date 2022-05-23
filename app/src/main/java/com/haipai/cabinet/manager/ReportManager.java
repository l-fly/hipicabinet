package com.haipai.cabinet.manager;

import com.haipai.cabinet.entity.AllPropertiesRequest;
import com.haipai.cabinet.entity.BaseResponse;
import com.haipai.cabinet.entity.BatteryInfo;
import com.haipai.cabinet.entity.InquiryRequest;
import com.haipai.cabinet.entity.LoginRequest;
import com.haipai.cabinet.entity.WarningRequest;
import com.haipai.cabinet.model.entity.PmsEntity;
import com.haipai.cabinet.tcp.ServerProtocolDefine;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ReportManager {

    public static void login(){
        LogUtil.i("TcpManager mina  login");
        LoginRequest request = new LoginRequest();
        request.setMsgType(110);
        request.setDevId(LocalDataManager.devId);
        request.setCabSta("1");
        request.setProtocolVersion("V2");
        request.setCcid(LocalDataManager.ccid);
        request.setImei(LocalDataManager.imei);
        request.setImsi(LocalDataManager.imsi);
        request.setDevType(2);
        request.setTxnNo(System.currentTimeMillis());
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }

    public static void inquiryExchange(String txnno,String bId){

        InquiryRequest request = new InquiryRequest();
        request.setMsgType(112);
        request.setDevId(LocalDataManager.devId);
        request.setTxnno(txnno);
        request.setBatteryId(bId);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }

    public static void baseResponse(int msgType,int result,String txnNo){
        BaseResponse baseRespone = new BaseResponse();
        baseRespone.setDevId(LocalDataManager.devId);
        baseRespone.setMsgType(msgType);
        baseRespone.setResult(result);
        baseRespone.setTxnNo(txnNo);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(baseRespone));
    }
    public static void boxOpenReport(int port){
        WarningRequest request = new WarningRequest();
        request.setMsgType(410);
        request.setDevId(LocalDataManager.devId);
        request.setTxnNo("" +System.currentTimeMillis());
        WarningRequest.AlarmListBean bean = new WarningRequest.AlarmListBean();
        bean.setId("boxFault");
        bean.setAlarmDesc("05opens"+ (port+1));
        bean.setAlarmTime("" + System.currentTimeMillis());

        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));
    }
    public static void switchFinishReport(int alarmDesc,int doorPort,
                                           BatteryInfo emptBat,
                                           BatteryInfo fullBat){
        WarningRequest request = new WarningRequest();
        request.setMsgType(410);
        request.setDevId(LocalDataManager.devId);
        List<WarningRequest.AlarmListBean> alarmList = new ArrayList<>();
        WarningRequest.AlarmListBean bean = new WarningRequest.AlarmListBean();
        bean.setId("switchFinish");
        if(LocalDataManager.mBatteryGet != null){
            bean.setBatteryId(LocalDataManager.mBatteryGet.getSn());
            bean.setFullDoorID(LocalDataManager.mBatteryGet.getPort()+1);
            bean.setFullBatteryId(LocalDataManager.mBatteryGet.getSn());
            bean.setFullBatsoc(LocalDataManager.mBatteryGet.getSoc());
        }
        if(LocalDataManager.mBatteryBack != null){
            bean.setBatteryId(LocalDataManager.mBatteryBack.getSn());
            bean.setEmptDoorID(LocalDataManager.mBatteryBack.getPort()+1);
            bean.setEmptBatteryId(LocalDataManager.mBatteryBack.getSn());
            bean.setEmptBatsoc(LocalDataManager.mBatteryBack.getSoc());
        }
        String txnNo ;
        if(OrderManager.currentOrder!=null){
            if (OrderManager.currentOrder.getUserId()!=null
                    && !OrderManager.currentOrder.getUserId().isEmpty()){
                bean.setUserId(OrderManager.currentOrder.getUserId());
            }
            txnNo = OrderManager.currentOrder.getTxnNo();
            request.setTxnNo(txnNo);
        }else {
            txnNo = "" + System.currentTimeMillis();
            request.setTxnNo(txnNo);
        }
        bean.setAlarmDesc("" + alarmDesc);
        bean.setDoorId(doorPort+1);
        bean.setAlarmTime("" + System.currentTimeMillis());
        if (emptBat != null){
            bean.setEmptDoorID(emptBat.getPort()+1);
            bean.setEmptBatteryId(emptBat.getSn());
            bean.setEmptBatsoc(emptBat.getSoc());
        }
        if (fullBat != null){
            bean.setFullDoorID(fullBat.getPort()+1);
            bean.setFullBatteryId(fullBat.getSn());
            bean.setFullBatsoc(fullBat.getSoc());
        }
        bean.setAlarmFlag(0);

        alarmList.add(bean);
        request.setAlarmList(alarmList);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request),""+ txnNo);
    }
    public static void messageAllReport(){
        AllPropertiesRequest request = new AllPropertiesRequest();
        request.setMsgType(310);
        request.setIsFull(1);
        request.setDevId(LocalDataManager.devId);
        request.setTxnNo("" + System.currentTimeMillis());

        List<AllPropertiesRequest.CabListBean> cabList = new ArrayList<>();
        AllPropertiesRequest.CabListBean cabBean = new AllPropertiesRequest.CabListBean();
        cabBean.setdBM("" + LocalDataManager.dbm);
        //电表数据
        cabBean.setCabVol("" + LocalDataManager.getInstance().cabinet.getMeter().getVoltage());
        cabBean.setCabCur("" + LocalDataManager.getInstance().cabinet.getMeter().getCurrent());
        cabBean.setEmKwh("" + LocalDataManager.getInstance().cabinet.getMeter().getTotalWh());

        cabBean.setCabT("" + LocalDataManager.getInstance().cabinet.getCcu().getLcdTemp());
        cabBean.setBatNum("" + LocalDataManager.getBatteryTotal());
        cabBean.setBatFullA("" + LocalDataManager.getLogicValidBatteryNum(0));
        cabBean.setBatFullB("" + LocalDataManager.getLogicValidBatteryNum(1));
        cabBean.setBatFullC("" + LocalDataManager.getLogicValidBatteryNum(2));

        //协议没弄明白
        List<String> undefinedFault = new ArrayList<>();
        undefinedFault.add("000");
        cabBean.setCabFault(undefinedFault);
        cabBean.setCabAlarm(undefinedFault);

        cabList.add(cabBean);

        List<AllPropertiesRequest.BoxListBean> boxList = new ArrayList<>();

        for (int i = 0; i<LocalDataManager.slotNum; i++){
            if(LocalDataManager.getInstance().cabinet.getPmsList().size()>i){
                PmsEntity pmsEntity = LocalDataManager.getInstance().cabinet.getPmsList().get(i);
                AllPropertiesRequest.BoxListBean boxBean  = new AllPropertiesRequest.BoxListBean();
                boxBean.setDoorSta("" + (CustomMethodUtil.isOpen(i)?1:0));
                boxBean.setDoorId("" + pmsEntity.getCabinID());
                boxBean.setBoxEnable("" + ((CustomMethodUtil.isPortDisable(pmsEntity.getPort())?0:1)));
                //boxBean.setChgTime();
               // boxBean.setBoxEnable("" + 1);
               // boxBean.setBoxChgSta("" + 2);
                //boxBean.setDoorSta("" + ((pmsEntity.getDevState()&1)==1?0:1));
                boxBean.setBoxChgSta("" + ((pmsEntity.getDevState()&2)!=0?1:2));
                int boxSta;
                if(pmsEntity.hasBattery()){
                    BatteryInfo info = pmsEntity.getBatteryInfo();
                    boxBean.setBatteryId(info.getSn());
                    boxSta = 7;
                    if (info.isOutValid()){
                        boxSta = 2;
                    }else {
                        if ((pmsEntity.getDevState()&2)!=0){
                            boxSta = 1;
                        }
                    }
                }else {
                    boxSta = 0;
                }
                boxBean.setBoxSta("" + boxSta);

                boxBean.setBoxFault(undefinedFault);
                boxBean.setBoxAlarm(undefinedFault);

                boxList.add(boxBean);
            }
        }

        List<AllPropertiesRequest.BatListBean> batList = new ArrayList<>();

        List<BatteryInfo> rlt = LocalDataManager.getInstance().getBatteriesAllClone();
        for (BatteryInfo info :rlt){
            AllPropertiesRequest.BatListBean batBean = new AllPropertiesRequest.BatListBean();
            batBean.setBatteryId(info.getSn());
           // batBean.setBmsT("" +37);
            batBean.setTotalAH("" + info.getResidualmAh());
            batBean.setSoc("" + info.getSoc());
            batBean.setChgCur("" + info.getCurrent());
            int batVol = 48;
            if(info.getType() == 1){
                batVol = 60;
            }else if (info.getType() == 2){
                batVol = 72;
            }
            batBean.setBatVol("" + batVol);
            batBean.setBatCycle("" + 1);
            batBean.setDoorId("" + (info.getPort()+1));
            batBean.setBmsFault(undefinedFault);
            batBean.setBmsAlarm(undefinedFault);
            batList.add(batBean);
        }

        request.setCabList(cabList);
        request.setBoxList(boxList);
        request.setBatList(batList);
        TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(request));

       // String str = "{\"devId\":\"PNA12A001HP21120066\",\"txnNo\":\"1652943801000\",\"boxList\":[{\"ChgTime\":\"2022-05-1915:03:18\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN211230564\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"1\"},{\"ChgTime\":\"2022-05-1914:36:25\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"doorSta\":\"0\",\"boxSta\":\"0\",\"doorId\":\"2\"},{\"ChgTime\":\"2022-05-1913:30:37\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"doorSta\":\"0\",\"boxSta\":\"0\",\"doorId\":\"3\"},{\"ChgTime\":\"2022-05-1915:03:18\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN220218016\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"4\"},{\"ChgTime\":\"2022-05-1915:03:18\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN220426304\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"5\"},{\"ChgTime\":\"2022-03-3120:48:42\",\"boxFault\":[\"000\"],\"boxEnable\":\"0\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"doorSta\":\"0\",\"boxSta\":\"0\",\"doorId\":\"6\"},{\"ChgTime\":\"2022-05-1915:03:18\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN220426223\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"7\"},{\"ChgTime\":\"2022-05-1915:03:19\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN220426062\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"8\"},{\"ChgTime\":\"2022-05-1915:03:19\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN211230518\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"9\"},{\"ChgTime\":\"2022-05-1915:01:15\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"0\",\"batteryId\":\"PN204805508DGPN211105010\",\"doorSta\":\"0\",\"boxSta\":\"2\",\"doorId\":\"10\"},{\"ChgTime\":\"2022-05-1915:03:20\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"0\",\"batteryId\":\"PN204805508DGPN220426295\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"11\"},{\"ChgTime\":\"2022-05-1915:03:20\",\"boxFault\":[\"000\"],\"boxEnable\":\"1\",\"boxAlarm\":[\"000\"],\"boxChgSta\":\"2\",\"batteryId\":\"PN204805508DGPN220426201\",\"doorSta\":\"0\",\"boxSta\":\"1\",\"doorId\":\"12\"}],\"msgType\":310,\"cabList\":[{\"cabEnable\":\"1\",\"cabSta\":\"1\",\"cabCur\":\"3.8\",\"cabFault\":[\"000\"],\"batFullC\":\"0\",\"batFullB\":\"0\",\"dBM\":\"-40\",\"batFullA\":\"6\",\"emKwh\":\"3277.2\",\"batNum\":\"9\",\"cabVol\":\"228.3\",\"cabT\":\"30.6\",\"cabAlarm\":[\"000\"]}],\"isFull\":1,\"batList\":[{\"bmsT\":\"37\",\"batteryId\":\"PN204805508DGPN211230564\",\"soc\":\"53\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"1\"},{\"bmsT\":\"33\",\"batteryId\":\"PN204805508DGPN220218016\",\"soc\":\"38\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"4\"},{\"bmsT\":\"34\",\"batteryId\":\"PN204805508DGPN220426304\",\"soc\":\"91\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"5\"},{\"bmsT\":\"37\",\"batteryId\":\"PN204805508DGPN220426223\",\"soc\":\"28\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"7\"},{\"bmsT\":\"38\",\"batteryId\":\"PN204805508DGPN220426062\",\"soc\":\"47\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"8\"},{\"bmsT\":\"35\",\"batteryId\":\"PN204805508DGPN211230518\",\"soc\":\"45\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"9\"},{\"bmsT\":\"37\",\"batteryId\":\"PN204805508DGPN211105010\",\"soc\":\"100\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"10\"},{\"bmsT\":\"33\",\"batteryId\":\"PN204805508DGPN220426295\",\"soc\":\"28\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"11\"},{\"bmsT\":\"34\",\"batteryId\":\"PN204805508DGPN220426201\",\"soc\":\"95\",\"totalAH\":\"55\",\"chgCur\":\"0.0\",\"bmsAlarm\":[\"000\"],\"batVol\":\"48\",\"bmsFault\":[\"000\"],\"batCycle\":\"1\",\"doorId\":\"12\"}]}";
        //TcpManager.getInstance().send(ServerProtocolDefine.makeDataBytes(str));
    }

}

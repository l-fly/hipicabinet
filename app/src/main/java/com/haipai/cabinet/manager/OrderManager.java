package com.haipai.cabinet.manager;


import com.haipai.cabinet.entity.OrderInfo;
import com.haipai.cabinet.entity.RemoteControlRequest;

public class OrderManager {
    public interface IOrderListener{
        void onReceive(String type);
    }
    private IOrderListener orderListener;
    /**
     * 设置监听
     * @param listener
     */
    public void setOrderListener(IOrderListener listener){
        if(!listener.equals(orderListener)) {
            orderListener = listener;
        }
    }

    /**
     * 取消监听
     * @param listener
     */
    public void removeOrderListener(IOrderListener listener){
        if(orderListener!=null&&orderListener.equals(listener)){
            orderListener = null;
        }
    }
    private static OrderManager _instance;
    private OrderManager(){
    }
    public static OrderManager getInstance(){
        if(_instance==null){
            _instance = new OrderManager();
        }
        return _instance;
    }
    public static OrderInfo currentOrder;
    public void receiveServerOrder(String txnNo,RemoteControlRequest.ParamListBean order){
        int result = 1;
        String value = order.getValue();
        String voltage = order.getVoltage();
        int type = 0;
        if(voltage!=null && !voltage.isEmpty()){
            if(voltage.equals("48")){
                type = 0;
            }else if(voltage.equals("60")){
                type = 1;
            }else if(voltage.equals("72")){
                type = 2;
            }
        }
        if (value.equals("01") /*|| value.equals("02") || value.equals("03")*/
                || value.equals("11") || value.equals("12")){
            if(LocalDataManager.currentActivity != LocalDataManager.MAIN_ACTIVITY || currentOrder !=null){
                result = 3;
            }
            if(!value.equals("11") && LocalDataManager.getEmptyNum() == 0){
                result = 4;
            }

            if(LocalDataManager.getLogicValidBatteryNum(type) == 0){
                result = 5;
            }
            if (UpgradeManager.update_status !=0 ){
                result = 16;
            }
            // todo 换电逻辑判断
            if (result == 1){
                currentOrder = new OrderInfo();
                currentOrder.setId(order.getId());
                currentOrder.setUserId(order.getUserId());
                currentOrder.setValue(order.getValue());
                currentOrder.setBatteryId(order.getBatteryId());
                currentOrder.setTxnNo(txnNo);
                currentOrder.setScanBattery(order.getScanBattery());
                if(orderListener!=null){
                    orderListener.onReceive(order.getValue());
                }
            }
        }else {
            result = 2;
        }
        ReportManager.baseResponse(501,result,txnNo);
    }

    public void receiveInquiryOrder(String result){
        if(orderListener!=null){
            orderListener.onReceive(result);
        }
    }
}

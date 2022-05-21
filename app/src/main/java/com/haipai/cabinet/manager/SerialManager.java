package com.haipai.cabinet.manager;

import com.haipai.cabinet.serialUtil.SerialPortUtil;
import com.haipai.cabinet.util.CrcUtil;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;
import java.util.ArrayList;
import java.util.List;


public class SerialManager {


    private static final boolean IS_SERIAL_LOG = true;

    private static final int MAX_DATA_LENGTH  = 248;   //内容最大长度
    private static final int NO_DATA_LENGTH = 5;       //空命令长度，是一帧数据的最小长度


    public interface IOnReceiveSerialDataListener{
        /**
         * 这里不带数据，数据去LocalDataManager取
         * @param cmd
         */
        void onReceive(byte cmd);

        /**
         * 这里带数据
         * @param cmd
         * @param data
         */
        void onReceive(byte devAddr ,byte cmd, byte[] data);

        void onError(byte [] err);
    }

    private IOnReceiveSerialDataListener serialListener;

    public void setSerialDataListener(IOnReceiveSerialDataListener listener){
        serialListener = listener;
    }

    public void removeSerialDataListener(IOnReceiveSerialDataListener listener){
        if(serialListener == listener){
            serialListener = null;
        }
    }

    public boolean enableSend = true;  //apk升级后就设置为false

    Thread mSendThread;
    class SendData {
        byte[] data;
        public SendData(byte[] data){
            this.data = data;
        }
    }
    List<SendData> sendArray = new ArrayList<>();
    private SerialManager(){
        mSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (enableSend) {//升级时退出循环
                    synchronized (sendArray) {

                        if(sendArray.size()>0){
                            sendCmdReal(sendArray.get(0).data);
                            sendArray.remove(0);
                        }
                    }
                    try {
                        Thread.sleep(10);
                    }catch (Exception e){}

                  /*  synchronized (sendArray) {
                        for (int i = 0; i < sendArray.size(); i++) {
                            sendCmdReal(sendArray.get(0).data);
                        }
                        sendArray.clear();
                    }
                    try {
                        Thread.sleep(50);
                    }catch (Exception e){}*/
                }
            }
        },"serialSendThread");
        mSendThread.start();
    }
    private static SerialManager _instance =  null;
    public static SerialManager getInstance(){
        if(_instance==null){
            _instance = new SerialManager();
        }
        return _instance;
    }


    public boolean send03(int devAddr, int mStart, int mAmount){
        byte[] startBytes = NumberBytes.intToBytes(mStart,2);
        byte[] amoutBytes = NumberBytes.intToBytes(mAmount,2);
        byte[] data = new byte[6];
        data[0] = (byte) devAddr;
        data[1] = (byte) 0x03;
        data[2] = startBytes[0];
        data[3] = startBytes[1];
        data[4] = amoutBytes[0];
        data[5] = amoutBytes[1];
        addSendData( data);
        return true;
    }
    public boolean send04(int devAddr, int mStart, int mAmount){
        byte[] startBytes = NumberBytes.intToBytes(mStart,2);
        byte[] amoutBytes = NumberBytes.intToBytes(mAmount,2);
        byte[] data = new byte[6];
        data[0] = (byte) devAddr;
        data[1] = (byte) 0x04;
        data[2] = startBytes[0];
        data[3] = startBytes[1];
        data[4] = amoutBytes[0];
        data[5] = amoutBytes[1];
        addSendData( data);
        return true;
    }
    public boolean send16(int devAddr, int mStart, byte[] send){
        if(send== null && send.length < 2){
            return false;
        }
        int mAmount = send.length/2;
        byte[] startBytes = NumberBytes.intToBytes(mStart,2);
        byte[] amoutBytes = NumberBytes.intToBytes(mAmount,2);
        byte[] data = new byte[7 + send.length];
        data[0] = (byte) devAddr;
        data[1] = (byte) 0x10;
        data[2] = startBytes[0];
        data[3] = startBytes[1];
        data[4] = amoutBytes[0];
        data[5] = amoutBytes[1];
        data[6] = (byte)send.length;
        System.arraycopy(send,0,data,7,send.length);
        //addSendData( data);
        LogUtil.d("serial send 16 " + NumberBytes.getHexString(data));
        return sendCmdReal(data);
    }

    private void addSendData(byte[] data){
        synchronized (sendArray){
            sendArray.add(new SendData(data));
        }
    }

    /**
     * 发送数据
     * @param data  拆分好的数据
     * @return
     */
    private boolean sendCmdReal(byte[] data){
        int length = data.length;
        if(length > MAX_DATA_LENGTH){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial send too much, need split");
            return false;
        }

        byte[] sendData = new byte[length + 4];
        sendData[0] = 0x7e;//Header
        if(length > 0){
            System.arraycopy(data, 0, sendData, 1, length);
        }

        int crc = CrcUtil.crc16(data);
        byte[] crcBytes = NumberBytes.intToBytes(crc,2);
        System.arraycopy(crcBytes,0, sendData, length + 1,2);
        sendData[length + 3] = (byte)0x7f;//Tail
        if(IS_SERIAL_LOG){
           // LogUtil.i("serial send Real"  + NumberBytes.getHexString(sendData));
        }

        return sendOneFrame(sendData);
    }

    /**
     * 发送一帧数据
     * @param frame  一帧原始数据
     * @return
     */
    private boolean sendOneFrame(byte[] frame){
        if(!SerialPortUtil.getInstance().isStart()){
            if(IS_SERIAL_LOG){
                LogUtil.d("serial port is not start");
            }
            return false;
        }

        byte[] convert = new byte[frame.length *2];
        convert[0] = frame[0];

        int i = 1, j = 1;
        for(i = 1; i < frame.length-1; i++){
            if(frame[i]==(byte)0x7e){
                convert[j] = (byte)0x8c;
                convert[j+1] = (byte)0x01;
                j+=2;
            }else if(frame[i]==(byte)0x7f){
                convert[j] = (byte)0x8c;
                convert[j+1] = 0x02;
                j+=2;
            }else if(frame[i]==(byte)0x8c){
                convert[j] = (byte)0x8c;
                convert[j+1] = 0x00;
                j+=2;
            }else{
                convert[j] = frame[i];
                j++;
            }
        }
        convert[j] = frame[i];
        byte[] sendData = new byte[j+1];

        System.arraycopy(convert, 0, sendData, 0, j+1);

        return SerialPortUtil.getInstance().sendBuffer(sendData);
    }


    private byte[] tempData = new byte[10240];
    private int tempLength = 0;

    /**
     *
     * @param originData 接收到的原始数据
     * @param size 数据大小
     */
    public synchronized void onReceiveData(byte[] originData, int size){

        if(tempLength+size> tempData.length){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial data is wrong size");
            tempLength = 0;
            return;
        }
        System.arraycopy(originData,0, tempData, tempLength, size);
        tempLength+=size;
        analysis();
        //应该是出错了
        if(tempLength>512){
            tempLength = 0;
        }
    }
    private void analysis(){
        int start = 0;
        int end = 0;
        while(start < tempLength){
            //先找到头
            if(tempData[start]!=0x7e){
                start++;
            }else{
                //找最近的尾
                for(int i = start+1; i < tempLength;i++){
                    if(tempData[i]==(byte)0x7f){
                        end = i;
                        break;  //跳出for循环
                    }
                }
                if(end > start){  //有头有尾，是一帧
//                    if(IS_SERIAL_LOG)
//                    LogUtil.d("serial find one frame start = "+start+", end = " + end);
                    byte[] oneFrame = new byte[end - start + 1];
                    System.arraycopy(tempData,start, oneFrame, 0, end - start + 1);
                    decodeOneFrame(oneFrame);
                    start = end+1;
                }else{  //最后一个有头没尾，不完全，也不用再找了
                    break;  //跳出while循环
                }
            }
        }
        if(end > 0){  //将处理过的移除
            System.arraycopy(tempData,end+1, tempData, 0, tempLength - end - 1);
            tempLength = tempLength - end - 1;
        }

    }

    /**
     * 解析一帧数据，主要是转码和校验
     * @param oneFrame  未转码之前的数据
     */
    private void decodeOneFrame(byte[] oneFrame){
       // if(IS_SERIAL_LOG)
        //LogUtil.d("serial decodeOneFrame" + NumberBytes.getHexString(oneFrame));
        if(oneFrame == null || oneFrame.length < NO_DATA_LENGTH ||
                oneFrame[0]!=0x7e || oneFrame[oneFrame.length-1]!=(byte)0x7f || //判断头尾
                oneFrame[oneFrame.length - 2]==(byte)0x8c){   //倒数第二个字符不能是转义
            if(IS_SERIAL_LOG)
            LogUtil.d("serial 解析帧失败，可能长度不够，首尾不对，或倒数第二帧byte是0x8c，不能转义");
            return;
        }
        byte[] convert = convertReceiveFrame(oneFrame);
        if(convert==null || convert.length < NO_DATA_LENGTH){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial 转义失败");
            return;
        }

       // LogUtil.d("serial convert" + NumberBytes.getHexString(convert));
        byte[] data = new byte[convert.length - 4];
        System.arraycopy(convert, 1, data, 0, data.length);
       // LogUtil.d("serial data " + NumberBytes.getHexString(data));
        int crcInit = CrcUtil.crc16(data);
        //LogUtil.d("serial crcInit " + crcInit);
       // byte[] crcInitBytes = NumberBytes.intToBytes(crcInit,2);
       // LogUtil.d("serial crcInitBytes " + NumberBytes.getHexString(crcInitBytes));
        byte[] crcGetBytes = new byte[]{convert[convert.length-2],convert[convert.length-3]};
       // LogUtil.d("serial crcGetBytes " + NumberBytes.getHexString(crcGetBytes));
        int crcGet = NumberBytes.bytesToInt(crcGetBytes);
        //LogUtil.d("serial crcGet " + crcGet);
        if(crcInit == crcGet){
            receiveOneIntactFrame(data);
        }else{
            if(IS_SERIAL_LOG) {
                LogUtil.d("serial 校验失败");
                LogUtil.d(NumberBytes.bytesToHexString(convert));
            }
        }
    }

    /**
     * 转义接收的数据，若转义失败，则返回null
     * @param receive
     * @return
     */
    private byte[] convertReceiveFrame(byte[] receive){
        byte[] data = new byte[receive.length];
        int j = 1;
        data[0] = receive[0];
        for(int i = 1; i < receive.length-1; i++){
            if(receive[i] == (byte)0x8c){
                if(receive[i+1]==(byte)0x01){
                    i++;
                    data[j] = 0x7e;
                    j++;
                }else if(receive[i+1] == (byte)0x02){
                    i++;
                    data[j] = (byte)0x7f;
                    j++;
                }else if(receive[i+1]==(byte)0x00){
                    i++;
                    data[j] = (byte)0x8c;
                    j++;
                }else{
                    return null;  //转义失败
                }
            }else{
                data[j] = receive[i];
                j++;
            }
        }
        data[j] = receive[receive.length-1];
        if(j == receive.length - 1) {  //没转义
            return data;
        }else{
            byte[] convert = new byte[j+1];
            System.arraycopy(data,0,convert,0,j+1);
            return convert;
        }
    }

    /**
     * 接收到一帧
     * @param oneFrame 一帧转义后校验过的有效数据
     */
    private void receiveOneIntactFrame(byte[] oneFrame){
       // if(IS_SERIAL_LOG)
            LogUtil.d("serial receive one frame " + NumberBytes.getHexString(oneFrame));

        if(oneFrame == null ){
            return;
        }
        byte devAddr = oneFrame[0];
        byte cmd = oneFrame[1];
        if(cmd == (byte)0x03 || cmd == (byte)0x04){
            int dataLength = NumberBytes.byteToInt(oneFrame[2]);
            byte [] realyData = new byte[dataLength];
            System.arraycopy(oneFrame,3,realyData,0,dataLength);
            LogUtil.d("serial realyData data size :" + realyData.length);
           // if(IS_SERIAL_LOG)
            LogUtil.d("serial receive realyData " + NumberBytes.getHexString(realyData));
            if(devAddr == (byte)0x01 && dataLength == 62){
                LocalDataManager.getInstance().setCcuDataPartOne(realyData);
            }
            if(devAddr == (byte)0x01 && dataLength == 12){
                LocalDataManager.getInstance().setCcuDataPartTow(realyData);
            }
            if(devAddr == (byte)0x01 && dataLength == 8){
                LocalDataManager.getInstance().setCcuDataPartThree(realyData);
            }
            if(devAddr == (byte)0x01 && dataLength == 6){
                LocalDataManager.getInstance().setCcuDataPartFour(realyData);
            }

            if(devAddr == (byte)0x02 && dataLength == 10){
                LocalDataManager.getInstance().setMeterDat(realyData);
            }

            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 42){
                LocalDataManager.getInstance().setPmsDataPartOne(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 20){
                LocalDataManager.getInstance().setPmsDataPartTow(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 8){
                LocalDataManager.getInstance().setPmsDataPartThree(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 4){
                LocalDataManager.getInstance().setPmsDataPartFour(devAddr - 0x04,realyData);
            }

            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 28){
                LocalDataManager.getInstance().setChargerDataPartOne(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 22){
                LocalDataManager.getInstance().setChargerDataPartTow(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 8){
                LocalDataManager.getInstance().setChargerDataPartThree(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 10){
                LocalDataManager.getInstance().setChargerDataPartFour(devAddr - 0x04,realyData);
            }

            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 72){
                LocalDataManager.getInstance().setBatteryDataPartOne(devAddr - 0x04,realyData);
            }
            if((devAddr >= (byte)0x04 & devAddr <= (byte)0x10) && dataLength == 46){
                LocalDataManager.getInstance().setBatteryDataPartTow(devAddr - 0x04,realyData);
            }



            if(serialListener!=null){
                serialListener.onReceive(devAddr,cmd, realyData);
            }
        }
        if(cmd == (byte) 0x83 || cmd == (byte)0x84){
            LogUtil.d("serial receive errorData " );
            if(serialListener!=null){
                serialListener.onError(new byte[]{oneFrame[1],oneFrame[2]});
            }
        }
    }




}

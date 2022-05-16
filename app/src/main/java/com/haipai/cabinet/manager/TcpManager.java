package com.haipai.cabinet.manager;




import com.haipai.cabinet.entity.RemoteControlRequest;
import com.haipai.cabinet.tcp.TcpReceiver;
import com.haipai.cabinet.tcp.mina.MinaClient;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.JsonUtils;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;
import com.haipai.cabinet.util.PreferencesUtil;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TcpManager {
    private static final boolean IS_TCP_LOG = true;
    MinaClient minaClient;
    private String SERVER_IP = "192.168.0.22";
    private int SERVER_PORT = 9988;


    private int RESENT_WAIT = 20000;    //等待超时时间 20s

    private TcpReceiver receiver = new TcpReceiver() {
        @Override
        public void connected() {

            LogUtil.i("TcpManager mina  connected");
            if(LocalDataManager.initStatus == 2){
                //ReportManager.login();
            }
        }

        @Override
        public void receive(byte[] buffer) {
            super.receive(buffer);
            lastReceiverDataTime = CustomMethodUtil.elapsedRealtime();;
            LogUtil.i("TcpManager mina  receive" + NumberBytes.getHexString(buffer));
            parseData(buffer);

        }

        @Override
        public void disConnect() {
            LogUtil.i("TcpManager mina  disConnect");
        }
    };

    public void connect(){
        if(minaClient == null){
            LogUtil.i("TcpManager mina start connect ip = "+SERVER_IP+", port = " + SERVER_PORT);
            minaClient = new MinaClient(SERVER_IP, SERVER_PORT, receiver);
            minaClient.start();
        }
    }

    long lastReceiverDataTime = 0;
    long lastReconnectTime = 0;
    public boolean reConnect(){
        long now = CustomMethodUtil.elapsedRealtime();
        if(now - lastReconnectTime < 30000){
            if(IS_TCP_LOG){
               // LogUtil.d("reconnect not not enough time");
            }
            return false;
        }
        lastReconnectTime = now;
        LogUtil.i("TcpManager reconnect start");
        if(minaClient!=null){
            minaClient.disconnect();
            try {
                minaClient.join();
            }catch (Exception e){
                e.printStackTrace();
            }
            minaClient = null;
            if(IS_TCP_LOG) {
                LogUtil.d("TcpManager reconnect destroy client");
            }
        }
        connect();
        return true;
    }
    private TcpManager(){
        int ip = PreferencesUtil.getInstance().getSwitchIp();
        switch (ip){
            case 1:
                //SERVER_IP = "192.168.0.22";
                //SERVER_PORT = 9988;
                SERVER_IP = "iot.jiabaida.com";
                SERVER_PORT = 1024;
                LogUtil.i("TcpManager mina  ip: " + 1);
                break;
            case 2:
                SERVER_IP = "192.168.0.22";
                SERVER_PORT = 9988;
                LogUtil.i("TcpManager mina  ip: " + 2);
                break;
            case 11:
                SERVER_IP = "120.25.72.44";
                SERVER_PORT = 8083;
                LogUtil.i("TcpManager mina  ip: " + 11);
                break;
            case 500:
                SERVER_IP = PreferencesUtil.getInstance().getServerIp();
                SERVER_PORT = PreferencesUtil.getInstance().getServerPort();
                LogUtil.i("TcpManager mina  ip: " + 11);
                break;
        }
    }

    private static TcpManager _instance;

    public static TcpManager getInstance(){
        if(_instance == null){
            _instance = new TcpManager();
        }
        return _instance;
    }


    /**
     * 经过一秒，查看超时
     */
    public void onPassSecond(){
        long curr = CustomMethodUtil.elapsedRealtime();
        if (curr - lastReceiverDataTime > 180000){
            reConnect();
            return;
        }
        if (minaClient != null && minaClient.isConnect()) {
            try {
                synchronized (waitList) {
                    for (int i = 0; i < waitList.size(); i++) {
                        if (waitList.get(i).time + RESENT_WAIT < curr) {
                            minaClient.writeBuf(waitList.get(i).data);
                            if(IS_TCP_LOG){
                                LogUtil.d("TcpManager no response resend ");
                            }
                            waitList.get(i).time = curr;
                        }
                    }
                }
            }catch (Exception e){
                LogUtil.d("TcpManager pass second exception: "+e.getMessage());
            }
        }
    }

    private LoginListener loginListener;   //管理员登录监听
    public interface LoginListener{
        void onReceiveLoginResult(byte[] data);
    }

    /**
     * 注册管理员登录监听
     * @param listener
     */
    public void addLoginListener(LoginListener listener){
        loginListener = listener;
    }

    /**
     * 注销管理员登录监听
     * @param listener
     */
    public void removeLoginListener(LoginListener listener){
        if(loginListener == listener){
            loginListener = null;
        }
    }
    public static final byte PROTOCOL_FRAME_HEAD = 0x7B;
    public static final byte PROTOCOL_FRAME_END = 0x7D;
    private byte[] tempBuffer = new byte[5120];  //缓存buff
    private int tempLength = 0;                  //缓存接收数据长度
    /**
     * @param data
     */
    private void parseData(byte[] data){
        if(IS_TCP_LOG) {
            LogUtil.d("TcpManager receive data length " + data.length );
        }
        LogUtil.d("TcpManager receive data" + NumberBytes.getHexString(data));
        LogUtil.d("TcpManager receive data" + new String(data));
        String strs = new String(data);
        List<String> list = parenThesisMatching(strs);
        for (String str : list){
            decodeOneFrame(str);
        }
    }


    /**
     * 解析json的字符串到jsonList
     * @param jsonString
     */
    public List<String> parenThesisMatching(String jsonString){
        List<String> jsonList  = new ArrayList<>();
        try {
            char[] jsonChars = jsonString.toCharArray();
            int charsTrimLength=jsonChars.length;
            Stack<Integer> subscriptStack=new Stack<>();
            int leftBraceSubscript=-1;
            int rightBraceSubscript=0;
            for(int i=0;i<charsTrimLength;i++){
                //如果当前字符是"{"
                if('{'==jsonChars[i]){
                    subscriptStack.push(i);
                    if(subscriptStack.size()==1){
                        leftBraceSubscript=i;
                    }
                }else if('}'== jsonChars[i] && subscriptStack.size() > 0){
                    subscriptStack.pop();
                    if(subscriptStack.size() == 0){
                        rightBraceSubscript = i;
                        jsonList.add(jsonString.substring(leftBraceSubscript,rightBraceSubscript+1));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonList;
    }

    /**
     * 处理一帧数据
     * @param jsonString
     */
    private void decodeOneFrame(String jsonString){
       // LogUtil.d("TcpManager receive oneFrame " + new String(oneFrame));
        try {
            //String jsonString  =  new String(oneFrame);
            JSONObject rootObject = new JSONObject(jsonString);
            int msgType = rootObject.getInt("msgType");
            String txnNo = rootObject.getString("txnNo");
            LogUtil.d("TcpManager decodeOneFrame msgType " + msgType);
            switch (msgType){
                case 111:
                    int result = rootObject.getInt("result");
                    if(result == 1){
                        //todo
                        LogUtil.i("TcpManager 登陸成功");
                    }
                    break;

                case 500:
                    RemoteControlRequest request = JsonUtils.jsonToBean(jsonString,RemoteControlRequest.class);
                    List<RemoteControlRequest.ParamListBean> paramList = request.getParamList();
                    for (RemoteControlRequest.ParamListBean bean : paramList){
                        String value = bean.getValue();
                        switch (bean.getId()){
                            case "switchControl":
                                if (value.equals("01") || value.equals("02") || value.equals("03")){
                                    OrderManager.getInstance().receiveServerOrder(txnNo,bean);
                                }else if(value.equals("04") ){
                                   int doorid =  bean.getDoorId();
                                   if(doorid>0 && doorid<LocalDataManager.slotNum){
                                       CustomMethodUtil.open(doorid - 1);
                                       ReportManager.baseResponse(501,1,txnNo);
                                   }else {
                                       ReportManager.baseResponse(501,2,txnNo);
                                   }
                                }else if(value.equals("06") ){
                                    int doorid =  bean.getDoorId();
                                    if(doorid>0 && doorid<LocalDataManager.slotNum){
                                        CustomMethodUtil.setPortDisable(doorid - 1,true);
                                        ReportManager.baseResponse(501,1,txnNo);
                                    }else {
                                        ReportManager.baseResponse(501,2,txnNo);
                                    }
                                }else if(value.equals("07") ){
                                    int doorid =  bean.getDoorId();
                                    if(doorid>0 && doorid<LocalDataManager.slotNum){
                                        CustomMethodUtil.setPortDisable(doorid - 1,false);
                                        ReportManager.baseResponse(501,1,txnNo);
                                    }else {
                                        ReportManager.baseResponse(501,2,txnNo);
                                    }
                                }else {
                                    ReportManager.baseResponse(501,2,txnNo);
                                }

                                break;
                            case "handle":
                                ReportManager.baseResponse(501,1,txnNo);
                                break;
                            case "swCabVolControl":
                                try {
                                    int audio = Integer.parseInt(value);
                                    CustomMethodUtil.setAudioSet(audio);
                                    ReportManager.baseResponse(501,1,txnNo);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    ReportManager.baseResponse(501,0,txnNo);
                                }

                                break;
                            case "swCabTempControl":
                                break;
                            case "swCabSocControl":
                                try {
                                    int cabSoc = Integer.parseInt(value);
                                    LocalDataManager.outValidSoc = cabSoc;
                                    ReportManager.baseResponse(501,1,txnNo);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    ReportManager.baseResponse(501,0,txnNo);
                                }
                                break;
                            case "swCabReset":
                                break;
                            case "swCabTcpPort":
                                try {
                                    String[] strs = value.split(",");
                                    if (strs.length == 2){
                                        int port = Integer.parseInt(strs[1]);
                                        PreferencesUtil.getInstance().setServerIp(strs[0]);
                                        PreferencesUtil.getInstance().setServerPort(port);
                                        PreferencesUtil.getInstance().setSwitchIp(500);
                                        ReportManager.baseResponse(501,1,txnNo);

                                        CustomMethodUtil.restartApp();
                                    }else {
                                        ReportManager.baseResponse(501,0,txnNo);
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                    ReportManager.baseResponse(501,0,txnNo);
                                }
                                break;
                            case "startHeat":
                                break;
                            case "stopHeat":
                                break;
                            case "maxChgCurrent":
                                break;
                            case "overTemp":
                                break;
                            case "recOverTemp":
                                break;
                            case "alarmTemp":
                                break;
                            case "recalarmTemp":
                                break;
                        }
                    }
                    break;
                case 411:
                    dropWait(txnNo);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    List<WaitData> waitList = new ArrayList<>();

    public void send(byte[]data){
        send(data, "", false);
    }
    public void send(byte[]data,String keycode){
        send(data, keycode, true);
    }
    /**
     *
     * @param data 将要发送到服务器的数据
     * @param keycode 标识
     * @param isWaitResp 是否需要等待回复
     */
    public void send(byte[] data, String keycode, boolean isWaitResp){
        if(IS_TCP_LOG) {
            LogUtil.d("TcpManager isConnect: " + minaClient.isConnect());
        }

        if (isWaitResp) {
            synchronized (waitList) {
                waitList.add(new WaitData(keycode, data, CustomMethodUtil.elapsedRealtime()));
            }
        }
        if(minaClient!=null && minaClient.isConnect()){
            try {
               // if(LocalDataManager.initStatus == 2 ){
                    if(!minaClient.writeBuf(data)){
                        if(IS_TCP_LOG) {
                            LogUtil.d("TcpManager send fail");
                        }
                        reConnect();
                    }else{
                        if(IS_TCP_LOG) {
                            LogUtil.d("TcpManager send cmd = "+new String(data));
                        }
                    }
                /*}else {
                    reConnect();
                }*/

            }catch (Exception e){
                if(IS_TCP_LOG) {
                    LogUtil.d("TcpManager nio write error: " + e.getMessage());
                }
                reConnect();
            }
        }else{
            reConnect();
        }
    }

    public synchronized void dropWait(String keycode){
        LogUtil.d("TcpManager dropWait keycode : " + keycode);
        synchronized (waitList){
            for (int i = waitList.size() - 1; i >= 0;i--){
                if(waitList.get(i).keycode.equals(keycode)){
                    waitList.remove(i);
                }
            }
        }
    }

    class WaitData{
        public byte[] data;
        public String keycode;
        public long time;
        public WaitData(String keycode, byte[] data, long time){
            this.keycode = keycode;
            this.data = data;
            this.time = time;
        }
    }

}

package com.haipai.cabinet.tcp;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.haipai.cabinet.util.JsonUtils;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;




public class ServerProtocolDefine {

    private static final boolean IS_DEBUG = true;


    /**
     * 通过type和json搭建发送给服务器的数据

     * @param obj 若为string则需要是json字符串，否则为其他对象
     * @return
     */
    public static byte[] makeDataBytes(Object obj){
        String json = null;
        if(obj != null) {
            if (obj instanceof String) {
                json = (String) obj;
            } else {
                json = JsonUtils.beanToJson(obj);
            }
        }
        byte[] jsonBytes = null;
       // short sendBodyLen = 0;
        if(!TextUtils.isEmpty(json)){
            jsonBytes = json.getBytes();
            //sendBodyLen = (short)jsonBytes.length;
        }
      /*  byte[] sendLenBytes = NumberBytes.shortToBytes(sendBodyLen);
        byte[] sendData = new byte[sendBodyLen+6];
        sendData[0] = PROTOCOL_FRAME_HEAD;
        sendData[1] = type;
        sendData[2] = 1;  //version
        System.arraycopy(sendLenBytes, 0, sendData, 3, 2);
        if(sendBodyLen>0){
            System.arraycopy(jsonBytes, 0, sendData, 5, sendBodyLen);
        }
        sendData[sendBodyLen+5] = PROTOCOL_FRAME_END;*/
        return jsonBytes;
    }


}

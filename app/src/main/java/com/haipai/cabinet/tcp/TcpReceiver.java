package com.haipai.cabinet.tcp;

public abstract class TcpReceiver {

    /**
     * 当建立连接是的回调
     */
    public void connected(){

    }

    /**
     * 当获取网络数据回调接口
     *
     * @param buffer 字节数据
     */
    public void receive(byte[] buffer){

    }

    /**
     * 当断开连接的回调
     */
    public void disConnect(){

    }
}

package com.haipai.cabinet.tcp;



import com.haipai.cabinet.util.LogUtil;

import java.util.Vector;

public class TCPSocketClient implements Runnable {

    private boolean needConnect = true;
    private boolean isConnect = false;
    private String host;
    private int port;
    private SSocket mSocket;
    private WriteThread mWriteThread;
    private TcpReceiver mReceiver;

    public TCPSocketClient(String host, int port, TcpReceiver receiver) {
        LogUtil.d("TCPSocketClient start host:"+host+", port:"+port);
        this.host = host;
        this.port = port;
        this.mReceiver = receiver;
        this.mSocket = new SSocket();
    }

    @Override
    public void run() {
        LogUtil.d("TCPSocketClient run");
        if (mReceiver == null) {
            LogUtil.d("TCPSocketClient receiver is null");
            return;
        }
        LogUtil.d("TCPSocketClient need connect = " + needConnect);
        while (needConnect) {
            LogUtil.d("TCPSocketClient is connect = " + isConnect);
            if(!isConnect) {
                synchronized (this) {
                    try {
                        isConnect = mSocket.connect(host, port);
                        LogUtil.d("TCPSocketClient connect "+isConnect);
                        if(!isConnect){
                            mSocket.disconnect();
                            try {
                                this.wait(6000);
                            } catch (InterruptedException e1) {
                            }
                            continue;
                        }else{
                            mWriteThread = new WriteThread(TCPSocketClient.this);
                            mReceiver.connected();
                        }
                    } catch (Exception e) {
                        LogUtil.d("TCPSocketClient error: " + e.getMessage());
                        try {
                            this.wait(6000);
                        } catch (InterruptedException e1) {
                        }
                        continue;
                    }
                }
            }
            try {
                mSocket.read(mReceiver);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("TCPSocketClient read error"+e.getMessage());
                onError();
            }
        }
    }

    public synchronized void onError(){
        mReceiver.disConnect();
        mWriteThread.close();
        mWriteThread = null;
        mSocket.disconnect();
        isConnect = false;
    }

    public void write(byte[] buffer) {
        if(buffer.length > 1 && buffer[1]!=2) {
            LogUtil.d("whj send a frame " + buffer[1]);
        }
        if (mWriteThread != null) {
            LogUtil.d("TCPSocketClient write buffer");
            mWriteThread.write(buffer);
        }
    }

    public synchronized void close() {
        needConnect = false;
        mWriteThread.close();
        mSocket.disconnect();
        this.notify();
    }

    private class WriteThread extends Thread {

        private boolean isWrite = true;
        private final Vector<byte[]> datas = new Vector<byte[]>();
        private SSocket mSocket;
        private TCPSocketClient client;
        private WriteThread(TCPSocketClient client) {
            this.client = client;
            this.mSocket = client.mSocket;
            this.start();
        }


        @Override
        public void run() {
            while (isWrite) {
                synchronized (this) {
                    if (datas.size() <= 0) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                }
                while (datas.size() > 0) {
                    LogUtil.d("WriteThread start write");
                    try {
                        byte[] buffer = datas.remove(0);
                        if (isWrite) {
                            mSocket.write(buffer);
                        } else {
                            this.notify();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.d("WriteThread write error:"+e.getMessage());
                        client.onError();
                    }
                }
            }
        }

        public synchronized void write(byte[] buffer) {
            datas.add(buffer);
            this.notify();
        }

        public synchronized void close() {
            isWrite = false;
            mSocket = null;
            this.notify();
        }
    }


//    public static void main(String[] args) throws Exception {
//        final StringBuilder sb = new StringBuilder("GET / HTTP/1.1\r\n");
//        sb.append("Connection: Keep-Alive").append("\r\n");
//        sb.append("Accept: */*").append("\r\n");
//        sb.append("Accept-Charset: UTF-8").append("\r\n");
//        sb.append("Accept-Language: zh-CN").append("\r\n");
//        sb.append("Host: www.baidu.com").append("\r\n");
//        sb.append("\r\n").append("\r\n");
//        System.out.println("init");
//        client = new TCPSocketClient("www.baidu.com", 80, new Receiver() {
//            @Override
//            public void receive(byte[] buffer) {
//                System.out.println("receive");
//                String s = new String(buffer);
//                System.out.println(s);
//                client.close();
//            }
//
//            @Override
//            public void connected() {
//                System.out.println("connected");
//                client.write(sb.toString().getBytes());
//            }
//
//            @Override
//            public void disconnect() {
//                System.out.println("disconnect");
//            }
//        });
//        new Thread(client).start();
//    }
}

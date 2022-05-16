package com.haipai.cabinet.tcp;



import com.haipai.cabinet.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Ashion on 2017/7/19.
 */

public class NioSocketClient extends Thread {

    private Selector selector = null;
    private SocketChannel client = null;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;
    private static final int RECONNECT_TIME = 30000;
    private static final int RECONNECT_TIME_SECOND = RECONNECT_TIME / 1000;

    private final byte CONNECT = 1;
    private final byte RUNNING = 2;
    private byte STATE = CONNECT;
    private boolean onWork;// 是否工作状态  

    private TcpReceiver mReceiver;

    static {
        System.setProperty("java.net.preferIPv6Addresses", "false");
    }

    private String ip = "127.0.0.1";
    private int port = 9527;

    private WriteThread mWriteThread = null;

    public NioSocketClient(String ip, int port, TcpReceiver receiver) {
        this.ip = ip;
        this.port = port;
        mReceiver = receiver;
        onWork = true;
    }

    public boolean isReady() {
        return STATE == RUNNING;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub  
        while (onWork) {
            switch (STATE) {
                case CONNECT:
                    connect();
                    break;
                case RUNNING:
                    running();
                    break;
                default:
                    break;
            }
        }
    }

    private synchronized void running() {
        SelectionKey key = null;
        try {
            while (selector.select(30000) > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    byte[] data = readBuf(key);
                    if (data != null){
                        LogUtil.d("nio read data length "+data.length);
                        if(mReceiver!=null){
                            mReceiver.receive(data);
                        }
                    }

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            LogUtil.d("nio read error: "+ e.getMessage());
//            LogUtil.f("nio read error: "+e.getMessage());
            closeKey(key);
        }
    }

    private final byte[] readBuf(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isReadable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            // 如果缓冲区过小的话那么信息流会分成多次接收  
            ByteArrayOutputStream bos = (ByteArrayOutputStream) selectionKey.attachment();
            ByteBuffer buffer = ByteBuffer.allocate(10240);// 10kb缓存  
            int actual = 0;
            while ((actual = client.read(buffer)) > 0) {
                buffer.flip();
                int limit = buffer.limit();
                byte b[] = new byte[limit];
                buffer.get(b);
                bos.write(b);
                buffer.clear();// 清空  
            }
            if (actual < 0) {
                // 出现异常  
                selectionKey.cancel();
                client.socket().close();
                client.close();
                throw new EOFException("nio Read EOF");
            }
            bos.flush();
            byte[] data = bos.toByteArray();
            bos.reset();
            return data;
        }
        return null;
    }

    public void writeBuf(byte[] data){
        if(data.length > 1 && data[1]!=2) {
            LogUtil.d("whj send a frame " + data[1]);
        }
        if(mWriteThread != null){
            mWriteThread.write(data);
        }
    }

//    public final boolean writeBuf(byte[] data) throws Exception {
//        if (client.isConnected()) {
//            if(data.length > 1 && data[1]!=2) {
//                LogUtil.d("nio send a frame " + data[1]);
//            }
//            ByteBuffer buffer = ByteBuffer.wrap(data);
//            int size = buffer.remaining();
//            // 此处需加中途断开逻辑，下次再继续发送数据包  
//            int actually = client.write(buffer);
//            if (actually == size)
//                return true;
//        }
//        return false;
//    }

    /**
     *  
     *      * 唤起连接线程重新连接 
     *      
     */
    protected synchronized void reconnect() {
        notify();
    }

    private synchronized void connect() {
        try {
//            LogUtil.f("nio start connect");
            selector = Selector.open();
            InetSocketAddress isa = new InetSocketAddress(ip, port);
            client = SocketChannel.open();
            // 设置连超时  
            client.socket().connect(isa, CONNECT_TIMEOUT);
            // 设置读超时  
            client.socket().setSoTimeout(READ_TIMEOUT);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ, new ByteArrayOutputStream());
            if (client.isConnected()) {
//                LogUtil.f("nio connected");
                // 连接成功开始监听服务端消息  
                // 发送一个验证数据包到服务器进行验证  
                STATE = RUNNING;
                LogUtil.d("nio 连接成功");
                if(mReceiver!=null){
                    mReceiver.connected();
                }
                mWriteThread = new WriteThread(this);
            } else {
                // 关闭通道过60S重新开始连接  
//                LogUtil.f("nio connect fail");
                StringBuffer buffer = new StringBuffer("nio 服务器连接失败");
                buffer.append(RECONNECT_TIME_SECOND);
                buffer.append("秒后再尝试连接");
                LogUtil.d(buffer.toString());
                close();// 关闭通道  
                Wait(RECONNECT_TIME);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block  
            // 有异常关闭通道过60S重新开始连接  
//            LogUtil.f("nio connect error: "+ e.getMessage());
            e.printStackTrace();
            StringBuffer buffer = new StringBuffer("nio 连接出错啦！");
            buffer.append(RECONNECT_TIME_SECOND);
            buffer.append("秒后再尝试连接");
            LogUtil.d(buffer.toString());
            close();// 关闭通道  
            Wait(RECONNECT_TIME);
        }
    }

    public synchronized void close() {
        STATE = CONNECT;
//        LogUtil.f("nio close");
        try {
            if(mReceiver!=null){
                mReceiver.disConnect();
            }
            if (client != null) {
                client.socket().close();
                client.close();
                client = null;
            }
            if (selector != null) {
                selector.close();
                selector = null;
            }
            if(mWriteThread!=null){
                mWriteThread.close();
                mWriteThread = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        Wait(RECONNECT_TIME);
    }

    private void closeKey(SelectionKey key) {
        if (key != null) {
            key.cancel();
            try {
                key.channel().close();
            } catch (Exception e) {
                // TODO Auto-generated catch block  
                e.printStackTrace();
                StringBuffer buffer = new StringBuffer("nio 连接断开啦！");
                buffer.append(RECONNECT_TIME_SECOND);
                buffer.append("秒后再尝试连接");
                LogUtil.d(buffer.toString());
//                Wait(RECONNECT_TIME);
            }
        }
        close();
    }

    private synchronized void Wait(long millis) {
        try {
            wait(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
    }


    private class WriteThread extends Thread{
        private NioSocketClient mClient;
        private boolean isWrite = true;
        private final Vector<byte[]> datas = new Vector<byte[]>();
        private WriteThread(NioSocketClient client){
            mClient = client;
            start();
        }

        @Override
        public void run(){
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
                        byte[] sendBuffer = datas.remove(0);
                        if (isWrite) {
                            if (mClient.client.isConnected()) {
                                if(sendBuffer.length > 1 && sendBuffer[1]!=2) {
                                    LogUtil.d("nio real send a frame " + sendBuffer[1]);
                                }
                                ByteBuffer buffer = ByteBuffer.wrap(sendBuffer);
                                int size = buffer.remaining();
                                // 此处需加中途断开逻辑，下次再继续发送数据包  
                                int actually = mClient.client.write(buffer);
                                if (actually == size) {
                                    continue;
                                }else{
                                    LogUtil.d("nio send size not equal");
//                                    LogUtil.f("nio send size not equal");
                                    mClient.close();
                                }
                            }else {
                                LogUtil.d("nio send not connect");
                                mClient.close();
                            }
                        } else {
                            this.notify();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.d("WriteThread write error:"+e.getMessage());
                        mClient.close();
                    }
                }
            }
        }
        public synchronized void write(byte[] buffer){
            datas.add(buffer);
            this.notify();
        }

        public synchronized void close(){
            isWrite = false;
            mClient = null;
            notify();
        }

    }

}

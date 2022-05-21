package com.haipai.cabinet.tcp.mina;



import com.haipai.cabinet.tcp.TcpReceiver;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.NumberBytes;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class MinaClient extends Thread {

    private static final boolean IS_MINA_LOG = true;

    private IoSession session = null;
    private IoConnector connector = null;

    private boolean isAutoConnect = true;  //是否自动重连

    private TcpReceiver mReceiver;
    private String ip = "127.0.0.1";
    private int port = 9527;

    static {
        System.setProperty("java.net.preferIPv6Addresses", "false");
    }

    /**
     *
     * @param ip 服务器地址
     * @param port 端口号
     * @param receiver 回调
     */
    public MinaClient(String ip, int port, TcpReceiver receiver) {
        this.ip = ip;
        this.port = port;
        this.mReceiver = receiver;
    }

    /**
     *
     * @param data
     * @return
     */
    public boolean writeBuf(byte[] data) {
        if (session != null) {
            session.write(IoBuffer.wrap(data));//关键，传递数组的关键

            return true;
        } else {
            if(IS_MINA_LOG ) {
                LogUtil.i("mina write出错 session = null");
            }
            return false;
        }
    }

    public boolean isConnect() {
        if (session == null) {
            return false;
        } else {
            return session.isConnected();
        }
    }

    public void run() {
        super.run();
        if(IS_MINA_LOG) {
            LogUtil.i("mina run");
        }
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(10000);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
        connector.setHandler(new MinaClientHandler());

        connector.setDefaultRemoteAddress(new InetSocketAddress(ip, port));

        connector.addListener(new IoListener() {
            @Override
            public void sessionDestroyed(IoSession arg0) throws Exception {
                // TODO Auto-generated method stub
                super.sessionDestroyed(arg0);
                if(IS_MINA_LOG) {
                    LogUtil.d("mina session destroyed");
                }
                if (mReceiver != null) {
                    mReceiver.disConnect();
                }
                int failCount = 0;
                while (isAutoConnect) {
                    try {
                        if(IS_MINA_LOG){
                            LogUtil.i("mina 断线 5秒后重连");
                            LogUtil.i(((InetSocketAddress) connector.getDefaultRemoteAddress()).getAddress()
                                    .getHostAddress());
                        }

                        Thread.sleep(5000);

                        ConnectFuture future = connector.connect();
                        future.awaitUninterruptibly();// 等待连接创建完成
                        session = future.getSession();// 获得session
                        if (session != null && session.isConnected()) {
                            if(IS_MINA_LOG ) {
                                LogUtil.i("mina 断线重连["
                                        + ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress()
                                        + ":" + ((InetSocketAddress) session.getRemoteAddress()).getPort() + "]成功");
                            }
                            if (mReceiver != null) {
                                mReceiver.connected();
                            }
                            break;
                        } else {
                            failCount++;
                            if(IS_MINA_LOG) {
                                LogUtil.i("mina 断线重连失败---->" + failCount + "次");
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        if(IS_MINA_LOG) {
                            LogUtil.i("mina 断线重连 error " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void sessionCreated(IoSession arg0) throws Exception {
                super.sessionCreated(arg0);
                if(IS_MINA_LOG) {
                    LogUtil.i("mina connect");
                }
                if (mReceiver != null) {
                    mReceiver.connected();
                }
            }
        });

        //开始连接
        try {
            ConnectFuture future = connector.connect();
            future.awaitUninterruptibly();// 等待连接创建完成
            session = future.getSession();// 获得session
            if (session != null && session.isConnected()) {
            } else {
                if(IS_MINA_LOG) {
                    LogUtil.d("写数据失败");
                }
            }
        } catch (Exception e) {
            if(IS_MINA_LOG ) {
                LogUtil.i("mina 客户端链接异常..." + e.getMessage());
            }
        }
        if (session != null && session.isConnected()) {
            session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
            if(IS_MINA_LOG) {
                LogUtil.i("mina 客户端断开...");
            }
            // connector.dispose();//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
        }
        if(IS_MINA_LOG) {
            LogUtil.d("mina run end");
        }

    }



    public void disconnect(){
        try {
            isAutoConnect = false;
            connector.dispose();
        }catch (Exception e){}
    }

    public class MinaClientHandler extends IoHandlerAdapter {

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            if(IS_MINA_LOG) {
                LogUtil.i("mina 客户端发生异常" + cause.getMessage());
            }
            super.exceptionCaught(session, cause);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            if (mReceiver != null) {
                byte[] bytes = (byte[]) message;
                mReceiver.receive(bytes);
            }
            super.messageReceived(session, message);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            // TODO Auto-generated method stub
            super.messageSent(session, message);
        }
    }


    public class IoListener implements IoServiceListener {

        @Override
        public void serviceActivated(IoService arg0) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void serviceDeactivated(IoService arg0) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void sessionClosed(IoSession arg0) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void sessionCreated(IoSession arg0) throws Exception {
            // TODO Auto-generated method stub

        }

        @Override
        public void sessionDestroyed(IoSession arg0) throws Exception {
            // TODO Auto-generated method stub

        }

    }


    //编码  
    public class ByteArrayEncoder extends ProtocolEncoderAdapter {
        @Override
        public void encode(IoSession session, Object message,
                           ProtocolEncoderOutput out) {
            out.write(message);
            out.flush();
        }
    }
    //解码  
  //  CumulativeProtocolDecoder
    public class ByteArrayDecoder extends CumulativeProtocolDecoder{

        @Override
        protected boolean doDecode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            if (in.remaining() > 4) {
                in.mark();
                int len = in.limit();
                byte[] bytes = new byte[len];
                in.get(bytes);
                if (in.get(len-1) == 0x7d
                        || in.get(len-1)  == 0X03){

                    out.write(bytes);

                    return true;
                }else {
                    in.reset();
                    return false;

                }
            }
            return false; // 断包，或者执行完，
        }

    }
   /* public class ByteArrayDecoder extends ProtocolDecoderAdapter {
        @Override
        public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
                throws Exception {
            int limit = in.limit();
            byte[] bytes = new byte[limit];
            in.get(bytes);
            LogUtil.i("TcpManager mina ByteArrayDecoder "  + NumberBytes.getHexString(bytes));
            out.write(bytes);
        }
    }*/

    //工厂  
    public class ByteArrayCodecFactory implements ProtocolCodecFactory {
        private ByteArrayDecoder decoder;
        private ByteArrayEncoder encoder;

        public ByteArrayCodecFactory() {
            encoder = new ByteArrayEncoder();
            decoder = new ByteArrayDecoder();
        }

        @Override
        public ProtocolDecoder getDecoder(IoSession session) throws Exception {
            return decoder;
        }

        @Override
        public ProtocolEncoder getEncoder(IoSession session) throws Exception {
            return encoder;
        }
    }


}
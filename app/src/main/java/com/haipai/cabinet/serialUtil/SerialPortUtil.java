package com.haipai.cabinet.serialUtil;



import android.serialport.SerialPort;


import com.haipai.cabinet.manager.SerialManager;
import com.haipai.cabinet.manager.ThreadManager;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 串口操作类
 * 
 * @author Jerome
 * 
 */
public class SerialPortUtil {
	private String TAG = "SerialPortUtil";
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	public String path = "/dev/ttyS3";
	public int baudRate = 9600;//波特率
	private static SerialPortUtil portUtil;
	private boolean isStop = false;
	private boolean isStart = false;

	private static final boolean IS_SERIAL_UTIL_LOG = true;

	private long readInterval = 200;

	public interface OnDataReceiveListener {
		public void onDataReceive(byte[] buffer, int size);
	}

	public static SerialPortUtil getInstance() {
		if (null == portUtil) {
			portUtil = new SerialPortUtil();
		}

//		if (portUtil.mSerialPort == null) {
//			portUtil.onCreate();
//		}
		return portUtil;
	}

    /**
     * 初始化串口信息
     * @param newPath
     */
	public void onCreate(String newPath) {
        path = newPath;
        start();
	}

	public void onCreate(String newPath, int baudrate){
		path = newPath;
		this.baudRate = baudrate;
		readInterval = 10;
		start();
	}

	public void start(){
        if(isStart()){
            return;
        }
        try {
            if(IS_SERIAL_UTIL_LOG)
                LogUtil.d("serial create  start");
			File device = new File(path);
			mSerialPort = SerialPort // 串口对象
					.newBuilder(device, baudRate) // 串口地址地址，波特率
					.dataBits(8) // 数据位,默认8；可选值为5~8
					.stopBits(1) // 停止位，默认1；1:1位停止位；2:2位停止位
					.parity(0) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
					.build();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();//读数据
            isStop = false;
            mReadThread.start();
            if(IS_SERIAL_UTIL_LOG)
                LogUtil.d("serial create success");
        } catch (Exception e) {
            e.printStackTrace();
            mSerialPort = null;
            if(mReadThread!=null){
                try {
                    mReadThread.join();
                }catch (Exception e1){
					LogUtil.d("mReadThread stop e:"+e1.getMessage());
                }
            }
            if(IS_SERIAL_UTIL_LOG)
                LogUtil.d("serial create fail: "+e.getMessage());
        }
    }

	public boolean isStart(){
		return isStart;
	}

	private long lastSendTime = 0;

	public long getLastReceiveTime() {
		return lastReceiveTime;
	}

	private long lastReceiveTime = 0;

    /**
     *
     * @param mBuffer 最终向串口发送的数据
     * @return
     */
	public boolean sendBuffer(byte[] mBuffer) {//写数据
		if(!isStart){
			if(IS_SERIAL_UTIL_LOG)
			LogUtil.d("serial not start");
			return false;
		}
		boolean result = true;
		byte[] mBufferTemp = new byte[mBuffer.length];
		System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
		if(IS_SERIAL_UTIL_LOG){
			String hex = Integer.toHexString(mBufferTemp[0] & 0xFF).toUpperCase(); 
			if (hex.length() == 1) {
				hex = '0' + hex; 
			}
			String byteStr = "[" + hex;
			for(int i = 1; i < mBufferTemp.length;i++){
				hex = Integer.toHexString(mBufferTemp[i] & 0xFF).toUpperCase(); 
				if (hex.length() == 1) { 
					hex = '0' + hex; 
				} 
				byteStr += "," + hex;
			}
			byteStr += "]";
            LogUtil.d("serial send buffer: " + byteStr);
		}
		try {
			if (mOutputStream != null) {
					mOutputStream.write(mBufferTemp);
					mOutputStream.flush();
					lastSendTime = CustomMethodUtil.elapsedRealtime();
					if(lastReceiveTime!=0 && lastSendTime - lastReceiveTime > 60000){
						LogUtil.d("已经超过1分钟没收到数据了");
						lastReceiveTime = lastSendTime;
						ThreadManager.execute(new Runnable() {
							@Override
							public void run() {
								mReadThread.interrupt();
								try{
									Thread.sleep(1000);
								}catch (Exception e){}
								mReadThread = new ReadThread();
								mReadThread.run();
							}
						});

					}
				if(IS_SERIAL_UTIL_LOG)
				LogUtil.d("serial send success");
			} else {
				result = false;
				if(IS_SERIAL_UTIL_LOG)
				LogUtil.d("serial send fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(IS_SERIAL_UTIL_LOG)
			LogUtil.d("serial send wrong!!\n" + e.getMessage());
			result = false;
			closeSerialPort();
		}
		return result;
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			byte[] buffer = new byte[4096];
			if(IS_SERIAL_UTIL_LOG)
			LogUtil.d("serial start read thread");
			isStart = true;
			while (!isStop && !isInterrupted()) {
				int size;
				try {
					if (mInputStream == null){
						if(IS_SERIAL_UTIL_LOG)
						LogUtil.d("serial inputStream is null");
						isStart = false;
						return;
					}
					size = mInputStream.read(buffer);
					if (size > 0) {
						if(IS_SERIAL_UTIL_LOG){
							String hex = Integer.toHexString(buffer[0] & 0xFF).toUpperCase(); 
							if (hex.length() == 1) {
								hex = '0' + hex; 
							}
							String str = "[" + hex;
							for(int i = 1; i < size; i++){
								hex = Integer.toHexString(buffer[i] & 0xFF).toUpperCase(); 
								if (hex.length() == 1) { 
									hex = '0' + hex; 
								} 
								str += ","+hex;
							}
							str +="]";
							LogUtil.d("serial receive data is:" + str);
						}
						lastReceiveTime = CustomMethodUtil.elapsedRealtime();
						SerialManager.getInstance().onReceiveData(buffer,size);
					}
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
					if(IS_SERIAL_UTIL_LOG)
					LogUtil.d("serial read fail by "+e.getMessage());
				}
			}
			isStart = false;
		}
	}

	/**
	 *关闭串口
	 */
	public void closeSerialPort() {
		isStop = true;
		if (mReadThread != null) {
			try {
				mReadThread.join();
			}catch (Exception e){
				LogUtil.d("mReadThread join e:"+e.getMessage());
			}
			mReadThread = null;
		}
		if (mSerialPort != null) {
			mSerialPort.close();
			if(IS_SERIAL_UTIL_LOG)
			LogUtil.d("serial serialPortClose") ;
			mSerialPort = null;
		}
		mInputStream = null;
		mOutputStream = null;
	}
	
}
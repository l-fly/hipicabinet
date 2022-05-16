package com.haipai.cabinet.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对数字和字节进行转换。<br>
 * 基础知识：<br>
 * 假设数据存储是以大端模式存储的：<br>
 * byte: 字节类型 占8位二进制 00000000<br>
 * char: 字符类型 占2个字节 16位二进制 byte[0] byte[1]<br>
 * int : 整数类型 占4个字节 32位二进制 byte[0] byte[1] byte[2] byte[3]<br>
 * long: 长整数类型 占8个字节 64位二进制 byte[0] byte[1] byte[2] byte[3] byte[4] byte[5]
 * byte[6] byte[7]<br>
 * float: 浮点数(小数) 占4个字节 32位二进制 byte[0] byte[1] byte[2] byte[3]<br>
 * double: 双精度浮点数(小数) 占8个字节 64位二进制 byte[0] byte[1] byte[2] byte[3] byte[4]
 * byte[5] byte[6] byte[7]<br>
 */
public class NumberBytes {

    /**
     * 将一个2位字节数组转换为char字符。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return char字符
     */
    public static char bytesToChar(byte[] b) {
        char c = (char) ((b[0] << 8) & 0xFF00L);
        c |= (char) (b[1] & 0xFFL);
        return c;
    }

    /**
     * 将一个8位字节数组转换为双精度浮点数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return 双精度浮点数
     */
    public static double bytesToDouble(byte[] b) {
        return Double.longBitsToDouble(bytesToLongBig(b));
    }

    /**
     * 将一个4位字节数组转换为浮点数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return 浮点数
     */
//    public static float bytesToFloat(byte[] b) {
//        return Float.intBitsToFloat(bytesToInt(b));
//    }


    /**
     * 将字节转换为整数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节
     * @return 整数
     */
    public static int byteToInt(byte b) {
        return (b & 0xFF);//负数补码 = 除符号位取反+1--->原码
    }

    /**
     * 将字节数组转换为整数。<br>
     * 高位在前
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return 整数
     */
    public static int bytesToIntBig(byte[] b) {
        int len = b.length;
        int result = 0;
        for(int i=0; i<len; i++){
            result += (b[i] & 0xFF) << (8 * (len - i - 1));
        }
        return result;
    }

    /**
     * 低位在前
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b){
        int len = b.length;
        int result = 0;
        for(int i = 0; i < len; i++){
            result += (b[i]&0xff) << (8 * i);
        }
        return result;
    }

    /**
     * 将一个8位字节数组转换为长整数。<br>
     * 注意，函数中不会对字节数组长度进行判断，请自行保证传入参数的正确性。
     *
     * @param b 字节数组
     * @return 长整数
     */
    public static long bytesToLongBig(byte[] b) {
        long l = ((long) b[0] << 56) & 0xFF00000000000000L;
        // 如果不强制转换为long，那么默认会当作int，导致最高32位丢失
        l |= ((long) b[1] << 48) & 0xFF000000000000L;
        l |= ((long) b[2] << 40) & 0xFF0000000000L;
        l |= ((long) b[3] << 32) & 0xFF00000000L;
        l |= ((long) b[4] << 24) & 0xFF000000L;
        l |= ((long) b[5] << 16) & 0xFF0000L;
        l |= ((long) b[6] << 8) & 0xFF00L;
        l |= (long) b[7] & 0xFFL;
        return l;
    }

    public static long bytesToLong(byte[] b){
        long l = 0;
        for(int i = 0; i < b.length;i++){
            l += ((long)(b[i]&0xff))<<(8*i);
        }
        return l;
    }

    /**
     * 将一个char字符转换位字节数组（2个字节），b[0]存储高位字符，大端
     *
     * @param c 字符（java char 2个字节）
     * @return 代表字符的字节数组
     */
    public static byte[] charToBytes(char c) {
        byte[] b = new byte[8];
        b[0] = (byte) (c >>> 8);
        b[1] = (byte) c;
        return b;
    }

    /**
     * 将一个双精度浮点数转换位字节数组（8个字节），b[0]存储高位字符，大端
     *
     * @param d 双精度浮点数
     * @return 代表双精度浮点数的字节数组
     */
    public static byte[] doubleToBytes(double d) {
        return longToBytesBig(Double.doubleToLongBits(d));
    }

    /**
     * 将一个浮点数转换为字节数组（4个字节），b[0]存储高位字符，大端
     *
     * @param f 浮点数
     * @return 代表浮点数的字节数组
     */
    public static byte[] floatToBytesBig(float f) {
        return intToBytesBig(Float.floatToIntBits(f));
    }

    public static byte[] intToBytesBig(int i){
        byte[] b = new byte[4];
        for(int j = 0; j < 4; j++){
            b[3 - j] = (byte)(i>>>(8*j));
        }
        return b;
    }

    /**
     * 将一个整数转换位字节数组(4个字节)，b[0]存储低位字符 小端
     * 低位在前
     * @param i 整数
     * @return 代表整数的字节数组
     */
    public static byte[] intToBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) i;
        b[1] = (byte) (i >>> 8);
        b[2] = (byte) (i >>> 16);
        b[3] = (byte) (i >>> 24);
        return b;
    }

    /**
     * 低位在前
     * @param value
     * @param size
     * @return
     */
    public static byte[] intToBytes(int value, int size){
        byte[] b = new byte[size];
        for(int i = 0; i < size; i++){
            b[i] = (byte) (value >>>(8*i));
        }
        return b;
    }

    public static byte[] shortToBytesBig(short i){
        byte[] b = new byte[2];
        b[1] = (byte)i;
        b[0] = (byte)(i>>>8);
        return b;
    }

    /**
     * 低位在前
     * @param i
     * @return
     */
    public static byte[] shortToBytes(short i){
        byte[] b = new byte[2];
        b[0] = (byte)i;
        b[1] = (byte)(i>>>8);
        return b;
    }

    /**
     * 将一个长整数转换位字节数组(8个字节)，b[0]存储高位字符，大端
     *
     * @param l 长整数
     * @return 代表长整数的字节数组
     */
    public static byte[] longToBytesBig(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }

    public static byte[] longToBytes(long l){
        byte[] b = new byte[8];
        for(int i = 0; i < 8;i++){
            b[i] = (byte)(l >>> (8*i));
        }
        return b;
    }


    /**
     * 将一个字节数组(4个字节)，b[0]存储高位字符，大端
     *
     * @param b 长整数
     * @return 代表长整数的字节数组
     */
    public static float byte2intFloatBig(byte b[]) {
        int byteLen=0;
        for(int i=0;i<b.length;i++){
            if(b[i]==0x00){
                byteLen++;
            }
        }
        if(b.length==byteLen){
            return (float)0.0;
        }
        int bits = b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16
                | (b[3] & 0xff) << 24;

        int sign = ((bits & 0x80000000) == 0) ? 1 : -1;
        int exponent = ((bits & 0x7f800000) >> 23);
        int mantissa = (bits & 0x007fffff);

        mantissa |= 0x00800000;
        // Calculate the result:
        float f = (float) (sign * mantissa * Math.pow(2, exponent - 150));

        return f;
    }


    /****
     * @author luxingsong 把字节数组转换成16进制字符串
     * @param bArray 字节数组
     * @return String 转换成的字符串
     */
    public static final String bytesToHexString(byte[] bArray) {
        int len = bArray.length;
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        // int v_len = bArray.length;
        int last = len - 1;
        //sb.append("0x");
        for (int i = 0; i < len; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append("0");
            sb.append(sTemp.toUpperCase());
            if (i != last)
                sb.append(" ");
        }
        return sb.toString();
    }


    public static String byte2String(byte[] buff, int size) {
        StringBuffer sbuf = new StringBuffer();
        // for (int i = 0; i < buff.length; i++)
        for (int i = 0; i < size && i < buff.length; i++) {
            int tmp = buff[i] & 0XFF;
            String str = Integer.toHexString(tmp);
            if (str.length() == 1) {
                sbuf.append("0" + str);
            } else {
                sbuf.append(str);
            }
        }

        return sbuf.toString();
    }


    public static void convertBytes(List<Byte> dataList){

        int len = dataList.size();
        int index = dataList.indexOf((byte)0x8C);
        if (index < 0){
            return;
        }
        List<Byte> newDataList = new ArrayList<>();
        boolean skipNext = false;
        for (int i=0; i<len-1; i++){
            byte currentByte = dataList.get(i);
            if (currentByte == (byte)0x8C){
                byte nextByte = dataList.get(i + 1);
                if (nextByte == (byte) 0x81){
                    newDataList.add((byte)0x7E);
                }else if (nextByte == (byte) 0x00){
                    newDataList.add((byte)0xFF);
                }else if (nextByte == (byte) 0x73){
                    newDataList.add((byte)0x8C);
                }
                skipNext = true;
            }else {
                if (!skipNext){
                    newDataList.add(currentByte);
                }else {
                    skipNext = false;
                }
            }
        }
        newDataList.add(dataList.get(len - 1));
        dataList.clear();
        dataList.addAll(newDataList);
    }


    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" +(byte)((b >> 7) & 0x1) +
                (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) +
                (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) +
                (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) +
                (byte)((b >> 0) & 0x1);
    }
    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    public static String asciiToString(String value){
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static String asciiToString(byte[] value){
        StringBuffer sbu = new StringBuffer();
        for (int i = 0;i< value.length; i++) {
            sbu.append((char) NumberBytes.byteToInt(value[i]));
        }
        return sbu.toString();
    }

    public static byte[] stringToAscii(String value){
        int len = value.length();
        byte[] result = new byte[len];
        char[] chars = value.toCharArray();
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (chars[i]);
        }
        return result;
    }

    /**
     * 将0x16转换成16这样,需要确定个位小于10，否则会出错，如1A会转换成20
     * @param hex
     * @return
     */
    public static int hexSameToInt(byte hex){
        int a = (hex&255);
        a = a / 16 * 10 + a % 16;
        return a;
    }

    public static byte[] hexString2Byte(String str){
        try{
            if(TextUtils.isEmpty(str)|| str.length()%2==1){
                return null;
            }
            byte[] result = new byte[str.length()/2];
            String temp;
            for(int i = 0; i < str.length()/2;i++){
                temp = str.substring(2*i, 2*i+2);
               // result[i] = Byte.parseByte(temp,16);    byte的范围是-128~127,不在范围会报 NumberFormatException，例如9D会报错
                result[i] =  (byte) Integer.parseInt(temp, 16);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String getHexString(byte data[]){
        String hex = Integer.toHexString(data[0] & 0xFF).toUpperCase();
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        String str = "[" + hex;
        for(int i = 1; i < data.length; i++){
            hex = Integer.toHexString(data[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str += ","+hex;
        }
        str +="]";
        return  str;
    }
    public static String getSnString(String hexStr){
        String snStr = "";
        try {
            hexStr =  hexStr .replace("[","");
            hexStr =  hexStr .replace("]","");
            hexStr =  hexStr .replace(",","");
            byte[] snByte = hexString2Byte(hexStr);
            snStr= new String (snByte);
        }catch (Exception e){
            e.printStackTrace();
        }
        return snStr;
    }

//
//    public static final void main(String[] args){
//
//        /*byte[] b = floatToBytes(4.5f);
//        System.out.println(bytesToHexString(b));
//        byte[] tmpByte = {*//*(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00*//*,*//* (byte)0x41,(byte)0x8A};
////        float f = byte2intFloat(tmpByte);
//        int n=bytesToInt(tmpByte);
//        System.out.println("Test byte2int......."+n);
//        int i = bytesToInt(tmpByte);
//        byte[] bytes = longToBytes(103);
//        System.out.println(Integer.toBinaryString(103));
//        String valueBin = "1";
//        byte[] valueByte = valueBin.getBytes();
//        System.out.println(bytesToHexString(valueByte));
//
//        byte[] xx = intToBytes(128);
//
//        System.out.println(bytesToHexString(xx));*/
//
//        byte[] bytes = new byte[]{(byte)0x30,(byte)0x31,(byte)0x32, (byte)0x38, (byte)0x33, (byte)0x32, (byte)0x31};
//
//        byte[] pairCode = new byte[4];
//        System.arraycopy(bytes, 3, pairCode, 0, 4);
//
//        System.out.println(asciiToString(pairCode));
//
//        System.out.println(stringToAscii("4321"));
//
//        //System.out.println(StringUtils.charToByte('4'));
//        //byte[] bytes = new byte[]{StringUtils.charToByte('4')};
//        String str = NumberBytes.bytesToHexString(stringToAscii("4321"));
//
//        System.out.println(str);
//
//    }


    public static byte[] convertToBytes(short[] sdata) {
        int byteCount = sdata.length * 2;
        byte[] data = new byte[byteCount];

        for(int i = 0; i < sdata.length; ++i) {
            data[i * 2] = (byte)(255 & sdata[i] >> 8);
            data[i * 2 + 1] = (byte)(255 & sdata[i]);
        }

        return data;
    }

    public static short[] convertToShorts(byte[] data) {
        short[] sdata = new short[data.length / 2];

        for(int i = 0; i < sdata.length; ++i) {
            sdata[i] = toShort(data[i * 2], data[i * 2 + 1]);
        }

        return sdata;
    }
    private static short toShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xff));
    }
}
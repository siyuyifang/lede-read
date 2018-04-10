package com.lede.blockchain.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private static final String MYKEY = "10661630";

    public static void main(String[] args) {
        try {
            String key = createKey();
            System.out.println("key:" + key);

            String msg = "dfsdasfdasf";

            System.out.println(msg);
            System.out.println(new String(AES.decryptBase64(AES.encryptBase64(msg.getBytes(), key), key)));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printByte(byte[] b) {
        for (byte i : b) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    /**
     * 生成一个AES的密钥
     *
     * @return
     */
    public static String createKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");  // 获取密匙生成器
            kg.init(128);  // 初始化
            // DES算法必须是56位
            // DESede算法可以是112位或168位
            // AES算法可以是128、192、256位
            SecretKey key = kg.generateKey();  // 生成密匙，可用多种方法来保存密匙
            byte[] keyByte = key.getEncoded();
            return byte2hex(keyByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param src 数据源
     * @param kv  密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] kv) throws Exception {
        SecretKey key = new SecretKeySpec(kv, "AES");

        //加密：
        Cipher cp = Cipher.getInstance("AES");  // 创建密码器
        cp.init(Cipher.ENCRYPT_MODE, key);  // 初始化
        byte[] ctext = cp.doFinal(src);  // 加密
        return ctext;
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param kv  密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] kv) throws Exception {
        SecretKey key = new SecretKeySpec(kv, "AES");
        Cipher cp = Cipher.getInstance("AES");  // 创建密码器
        cp.init(Cipher.DECRYPT_MODE, key);  // 初始化
        byte[] ptext = cp.doFinal(src);  // 解密
        return ptext;
    }

    /**
     * 密码解密
     *
     * @param data
     * @param key  只取8位
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data, String key) {
        try {
            return new String(decrypt(hex2byte(data), hex2byte(key)), "utf-8");
        } catch (Exception e) {
        }
        return "";
    }

    public final static byte[] decryptBase64(byte[] data, String key) {
        try {
            return Base64.decode(decrypt(data, hex2byte(key)));
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 密码加密
     *
     * @param password
     * @param key      只取8位
     * @return
     * @throws Exception
     */
    public final static String encrypt(String password, String key) {
        try {
            return byte2hex(encrypt(password.toString().getBytes("utf-8"), hex2byte(key)));
        } catch (Exception e) {
        }
        return "";
    }


    public final static byte[] encryptBase64(byte[] password, String key) {
        try {
            return encrypt(Base64.encode(password), hex2byte(key));
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 数组转十六进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        return bytesToUpperHexStr(b);
    }

    /**
     * 十六进制字符串转数组
     *
     * @param s
     * @return
     */
    public static byte[] hex2byte(String s) {
        byte[] b = s.getBytes();
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static String byte2lowHex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toLowerCase();
    }

    /**
     * 将字节数组转换为16进制字符串的形式.
     */
    private static String bytesToUpperHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);

        for (int i = 0; i < bcd.length; i++) {
            s.append(upBcdLookup[(bcd[i] >>> 4) & 0x0f]);
            s.append(upBcdLookup[bcd[i] & 0x0f]);
        }

        return s.toString();
    }

    private static final char[] upBcdLookup =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

}


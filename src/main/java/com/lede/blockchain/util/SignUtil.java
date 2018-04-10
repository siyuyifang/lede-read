package com.lede.blockchain.util;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * @author bjyiguoqiang on 2018/4/4.
 */
@Service
public class SignUtil {
    @Value("${app.key}")
    private String salt;


    public boolean validSign(List<String> paramValueList, String sign) {
        String generateSign = generateSign(paramValueList);
        return sign.equals(generateSign);
    }

    /**
     * 按参数值的字典顺序加密生成秘钥
     *
     * @param paramValueList 参数的值集合
     * @return 秘钥
     */
    public String generateSign(List<String> paramValueList) {
        //按字典顺序对参数值进行排序
        Collections.sort(paramValueList);
        //将所有参数组成字符串
        StringBuffer sb = new StringBuffer();
        for (String paramValue : paramValueList) {
            sb.append(paramValue);
        }
        //添加盐
        salt = salt == null ? "Zgyx@2Kyh#3" : salt;
        sb.append(salt);
        System.out.println("salt=" + salt);
        String sign = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            digest.update(sb.toString().getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hex = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0XFF);
                if (shaHex.length() < 2) {
                    hex.append(0);
                }
                hex.append(shaHex);
            }
            sign = hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign;
    }

    public static void main(String[] args) {
        List<String> s = Lists.newArrayList();
        s.add("110");
//        s.add("xiaosan");
        s.add("123");
        SignUtil instance = new SignUtil();
        String sign = instance.generateSign(s);
        System.out.println(sign);
        System.out.println(sign.length());
        boolean pass = instance.validSign(s, "1bd6f5cb1b7bc30147bfa8235b31ff82e880cc41");
        System.out.println(pass);
    }
}

package com.lede.blockchain.util;

import com.google.common.base.Strings;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author bjyiguoqiang on 2018/4/9.
 */
public class TokenUtil {

    /**
     * 60分钟
     */
    private static final long EXPIRATIONTIME = 3600000;

    /**
     * 根据用户ID生成token
     *
     * @param cellphone
     * @return
     */
    public static String getToken(String cellphone) {
        if (Strings.isNullOrEmpty(cellphone)) {
            return "";
        }
        String jwtToken = Jwts.builder().setSubject(cellphone).claim("roles", "user").setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME)).compact();
        return jwtToken;
    }
}

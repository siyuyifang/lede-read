package com.lede.blockchain.util;

import java.time.Clock;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
public class IdGenerateUtil {

    public static final String ARTICLE = "artice";

    public static final String COMMENT = "comment";

    public static final String ENROLL = "enroll";

    public static String generateEnrollId() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(DateUtil.getCurrentDateTime()).append(ENROLL).append(Clock.systemDefaultZone().millis());
        return buffer.toString();
    }

    public static String generateArticleId() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(DateUtil.getCurrentDateTime()).append(ARTICLE).append(Clock.systemDefaultZone().millis());
        return buffer.toString();
    }

    public static String generateCommentId() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(DateUtil.getCurrentDateTime()).append(COMMENT).append(Clock.systemDefaultZone().millis());
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateEnrollId());
        System.out.println(generateArticleId());
        System.out.println(generateCommentId());
    }
}

package blockchain.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.Clock;

/**
 * @author bjyiguoqiang on 2018/4/3.
 *         <p>
 *         日期帮助类
 */
public class DateUtil {

    /**
     * 获取当前期数，如 201804
     *
     * @return
     */
    public static String getCurrentPeriod() {
        return getPeriod(Clock.systemDefaultZone().millis());
    }

    /**
     * 指定时间戳获取当前期数
     *
     * @param timestamp 传入的时间戳
     * @return
     */
    public static String getPeriod(Long timestamp) {
        DateTime dateTime = new DateTime(timestamp);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMM");
        return dateTime.toString(formatter);
    }

    /**
     * 获取当前的格式化时间，如 2018040315
     *
     * @return
     */
    public static String getCurrentDateTime() {
        DateTime dateTime = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHH");
        return dateTime.toString(formatter);
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDateTime());
        System.out.println(getCurrentPeriod());
    }
}

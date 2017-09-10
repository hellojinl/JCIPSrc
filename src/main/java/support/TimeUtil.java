package support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public final class TimeUtil {

    private TimeUtil() {
    }

    /**
     * 每个线程都持有自己内部的Map<String, DateFormat>，其映射关系为: 日期字符串格式 -> SimpleDateFormat
     */
    private static final ThreadLocal< Map< String, SimpleDateFormat > > PATTERN_TO_DATE_FORMAT_THREAD_LOCAL = new ThreadLocal< Map< String, SimpleDateFormat > >();

    /**
     * 将日期date转化为pattern指定格式的字符串
     * 
     * @param date
     *            日期
     * @param pattern
     *            日期格式
     * @return 格式化后的字符串
     * 
     * @exception NullPointerException
     *                如果参数date为null 或 参数pattern为null
     * @exception IllegalArgumentException
     *                如果参数pattern无效
     */
    public static String format(Date date, String pattern) {

        if (date == null) {
            throw new NullPointerException( "参数date不能为空" );
        }

        return simpleDateFormatInCurrentThread( pattern ).format( date );
    }

    /**
     * 将pattern指定格式的字符串dateStr转化成 <code>Date</code>
     * 
     * @param dateStr
     *            日期字符串
     * @param pattern
     *            日期格式
     * @return <code>Date</code>
     * 
     * @exception NullPointerException
     *                如果参数dateStr为null 或 参数pattern为null
     * @exception IllegalArgumentException
     *                如果参数pattern无效
     * @exception ParseException
     *                如果转化失败
     */
    public static Date parse(String dateStr, String pattern) {

        if (dateStr == null) {
            throw new NullPointerException( "参数dateStr不能为空" );
        }

        try {
            return simpleDateFormatInCurrentThread( pattern ).parse( dateStr );
        } catch ( ParseException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * 得到当前线程独占的、格式为pattern的SimpleDateFormat
     * 
     * @param pattern
     *            日期格式
     * @return 当前线程独占的、格式为pattern的 <code>SimpleDateFormat</code>
     * 
     * @exception NullPointerException
     *                如果参数pattern为null
     * @exception IllegalArgumentException
     *                如果参数pattern无效
     */
    private static DateFormat simpleDateFormatInCurrentThread(String pattern) {

        if (pattern == null) {
            throw new NullPointerException( "参数pattern不能为null" );
        }

        Map< String, SimpleDateFormat > map = PATTERN_TO_DATE_FORMAT_THREAD_LOCAL.get();
        if (map == null) {
            SimpleDateFormat df = new SimpleDateFormat( pattern );
            map = new HashMap< String, SimpleDateFormat >();
            map.put( pattern, df );
            PATTERN_TO_DATE_FORMAT_THREAD_LOCAL.set( map );
            return df;
        }

        if (map.containsKey( pattern )) {
            return map.get( pattern );
        } else {
            SimpleDateFormat df = new SimpleDateFormat( pattern );
            map.put( pattern, df );
            return df;
        }
    }

    public static String defaultNow() {
        return format( new Date(), "yyyy-MM-dd HH:mm:ss" );
    }
}

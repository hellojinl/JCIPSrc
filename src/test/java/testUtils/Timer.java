package testUtils;

import java.util.concurrent.TimeUnit;

/**
 * 统计活动执行的时间
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public final class Timer {

    private Timer() {
    }

    /**
     * 计时
     * 
     * @param action
     *            待计时活动
     * @return 活动执行时间，单位为NANOSECONDS
     */
    public static long time(TimeRunnable action) {
        return time( action, TimeUnit.NANOSECONDS );
    }

    /**
     * 计时
     * 
     * @param action
     *            待计时活动
     * @return 活动执行时间，单位为MILLISECONDS
     */
    public static long timeMillis(TimeRunnable action) {
        return time( action, TimeUnit.MILLISECONDS );
    }

    /**
     * 计时
     * 
     * @param action
     *            待计时活动
     * @param unit
     *            时间单位
     * @return 活动执行的时间
     */
    public static long time(TimeRunnable action, TimeUnit unit) {
        if (action == null) {
            throw new NullPointerException( "action == null" );
        }
        if (unit == null) {
            throw new NullPointerException( "unit == null" );
        }

        try {
            long startTime = System.nanoTime();
            action.run();
            long duration = System.nanoTime() - startTime;
            return unit.convert( duration, TimeUnit.NANOSECONDS );
        } catch ( Throwable t ) {
            throw new RuntimeException( t );
        }

    }

}

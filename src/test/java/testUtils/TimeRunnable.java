package testUtils;

/**
 * 要计时的活动
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@FunctionalInterface
public interface TimeRunnable {

    /**
     * 运行
     */
    void run() throws Throwable;
}

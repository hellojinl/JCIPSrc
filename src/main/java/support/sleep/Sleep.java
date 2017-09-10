package support.sleep;

import java.util.concurrent.TimeUnit;

/**
 * 睡眠工具，目的是统一处理InterruptedException（快速失败）
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public final class Sleep {

    private Sleep() {

    }

    /**
     * 不可中断sleep，如果被中断了就立即失败
     * 
     * @throws RuntimeException
     *             if interrupted
     */
    public static void sleepUninterruptibly(long timeout, TimeUnit unit) {
        if (unit != null) {
            try {
                unit.sleep( timeout );
            } catch ( InterruptedException e ) {
                throw new RuntimeException( e ); // 快速失败
            }
        }
    }

}

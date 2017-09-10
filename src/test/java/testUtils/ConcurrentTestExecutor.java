package testUtils;

/**
 * 并发测试执行器
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public final class ConcurrentTestExecutor {

    private ConcurrentTestExecutor() {
    }

    /**
     * 反复的执行并发测试，直到其中有一次成功或者执行了指定次数为止
     * 
     * @param callback
     *            并发测试回调
     * @return true - 测试成功, false - 测试失败
     */
    public static boolean repeatedExecute(ConcurrentTestCallback callback) {
        if (callback == null) {
            throw new NullPointerException( "并发测试回调不能为null" );
        }

        final int count = callback.maximumExecutionTimes();
        if (count < 1) {
            throw new IllegalStateException( "执行次数，必须大于等于1" );
        }
        for (int i = 0; i < count; i++) {
            try {
                callback.setUp();

                if (callback.doConcurrentTest()) {
                    return true;
                }
            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally {
                try {
                    callback.tearDown();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 并发测试回调
     *
     * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
     *         Andals and the Rhoynar and the First Men, Lord of the Seven
     *         Kingdoms, Protector of the Realm, Caho of the Great Grass Sea,
     *         Breaker of Shackles, Father of Dragons.
     */
    @FunctionalInterface
    public static interface ConcurrentTestCallback {

        /**
         * 一次并发测试, 返回结果是你期待的结果有没有发生，而不是某次测试是否正确。 例如：
         * 你设计了一个场景，提前假设了一个结果，用于测试某个类是线程不安全的。 该结果并不是每次都出现，但是只要出现了，就能证明该类是线程不安全的。
         * 如果该结果在该次测试中出现，即得到了期望的结果，就应该返回true。 如果没出现，就返回false。
         * 
         * 
         * @return true - 得到期望的结果， false - 未得到期望的结果
         */
        boolean doConcurrentTest() throws Exception;

        /**
         * 最大执行次数
         * 
         * @return 最大执行次数，必须大于等于1
         */
        default int maximumExecutionTimes() {
            return 1000;
        }

        /**
         * 每次调用doConcurrentTest()前都执行
         */
        default void setUp() throws Exception {

        }

        /**
         * 每次调用doConcurrentTest()后都执行
         */
        default void tearDown() throws Exception {

        }
    }

}

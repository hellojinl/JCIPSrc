package C7_Cancellation_and_Shutdown.Ch7_25_UncaughtExceptionHandler_that_logs_the_exception;

import org.junit.Test;

/**
 * 直接为单个线程或所有线程设置UncaughtExceptionHandler
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see <a href="http://peirenlei.iteye.com/blog/305079">如何防止线程意外中止</a>
 * @see ThreadFactoryTest 演示为线程池的所有线程设置一个UncaughtExceptionHandler
 */
public class UEHLoggerTest {

    @Test
    public void singleThreadSetHandler() throws InterruptedException {
        ThreadA threadA = new ThreadA();
        threadA.setUncaughtExceptionHandler( new UEHLogger() ); // 单个线程设置未捕获异常处理器
        threadA.start();
        threadA.join();
    }

    @Test
    public void setDefaultUncaughtExceptionHandler() throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler( new UEHLogger() ); // 所有线程设置默认的未捕获异常处理器
        ThreadA threadA = new ThreadA();
        threadA.start();
        threadA.join();
    }

    class ThreadA extends Thread {

        public ThreadA() {
        }

        public void run() {
            double i = 12 / 0;// 抛出异常的地方
        }

    }
}

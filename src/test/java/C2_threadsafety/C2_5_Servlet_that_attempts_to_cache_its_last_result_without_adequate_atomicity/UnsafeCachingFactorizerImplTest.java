package C2_threadsafety.C2_5_Servlet_that_attempts_to_cache_its_last_result_without_adequate_atomicity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.junit.Test;

import support.servlet.ServletRequestImpl;
import support.servlet.ServletResponseImpl;
import testUtils.ConcurrentTestExecutor;

/**
 * UnsafeCachingFactorizer测试，
 * 其中使用UnsafeCachingFactorizerImpl来测试，UnsafeCachingFactorizerImpl针对测试进行了改造
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class UnsafeCachingFactorizerImplTest {

    private static final long timeout = 5;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final int threadCount = 2;

    @Test
    public void test_in_single_thread() throws ServletException, IOException {
        Servlet servlet = new UnsafeCachingFactorizerImpl();
        // create a request
        ServletRequest req = new ServletRequestImpl();
        req.setAttribute( UnsafeCachingFactorizerImpl.REQUEST_KEY, BigInteger.valueOf( 60 ) );
        // create a response
        ServletResponseImpl res = new ServletResponseImpl();

        servlet.service( req, res );

        BigInteger[] result = (BigInteger[]) res.getAttribute( UnsafeCachingFactorizerImpl.RESPONSE_KEY );
        assertTrue( eq( BigInteger.valueOf( 60 ), result ) );
    }

    @Test
    public void test_in_multithread() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {

            final CyclicBarrier barrier = new CyclicBarrier( threadCount + 1 );
            final Servlet servlet = new UnsafeCachingFactorizerImpl();
            final List< Future< Boolean > > futureList = new ArrayList< Future< Boolean > >();

            for (int i = 0; i < threadCount; i++) {
                Future< Boolean > future = executor.submit( new Callable< Boolean >() {

                    @Override
                    public Boolean call() throws Exception {
                        // req, res都是线程独占的，不存在竞争
                        ServletRequest req = new ServletRequestImpl();
                        req.setAttribute( UnsafeCachingFactorizerImpl.REQUEST_KEY, BigInteger.valueOf( 60 ) );
                        ServletResponseImpl res = new ServletResponseImpl();

                        barrier.await(); // 为了让线程同时开始，当barrier.await()的个数等于threadCount
                                         // + 1时，各个线程将同时往下运行

                        // 共享资源，存在竞争
                        // 用同一个数（这里是60）测试，将会出现如下并发问题：
                        // UnsafeCachingFactorizerImpl的状态由lastNumber和lastFactors属性构成，
                        // 虽然它们每个都是原子类型的数据类型，但是这并不足以保证UnsafeCachingFactorizerImpl的线程安全性，因为
                        // lastNumber和lastFactors是有关系的，即它们两个必须满足一致性条件（这里是lastNumber=lastFactors[0]*lastFactors[1]...*lastFactors[length-1],
                        // 例如60=2*2*3*5）
                        // 然而UnsafeCachingFactorizerImpl并没有对这个一致性条件采取任何保障措施，故UnsafeCachingFactorizerImpl将是线程不安全的
                        // 可能的错误执行顺序为：
                        // thread1： lastNumber.set(60);<此时lastNumber.get()=60,
                        // lastFactors.get()=null>
                        // |------------------------------------------------------------------------------------------------------->
                        // | lastFactors.set( factors
                        // );<此时factors=[2,2,3,5]>|STOP|
                        // thread2:
                        // ---------------------------------------------------------------------|
                        // if ( i.equals( lastNumber.get() )
                        // );<此时i=60,lastNumber.get()=60,
                        // if条件为true，返回lastFactors.get()=null>|STOP|
                        // thread2的结果是错的factor(60)=null, （应该为[2,2,3,5]）
                        servlet.service( req, res );

                        // 不存在竞争，结果的判定并不存在并发问题
                        BigInteger[] result = (BigInteger[]) res
                                .getAttribute( UnsafeCachingFactorizerImpl.RESPONSE_KEY );
                        return eq( BigInteger.valueOf( 60 ), result );
                    }

                } );

                futureList.add( future );
            }

            barrier.await();

            boolean hasError = false;
            for (Future< Boolean > future : futureList) {
                Boolean result = false;
                result = future.get( timeout, timeUnit );
                if (!result) {
                    hasError = true; // 因为UnsafeCachingFactorizerImpl是线程不安全，所以出现期望的错误代表测试正确
                }
            }

            return hasError;

        } ) );
    }

    private boolean eq(BigInteger num, BigInteger[] factors) {
        if (num == null || factors == null) {
            return false;
        }
        int expected = num.intValue();
        int actual = 1;
        for (int i = 0; i < factors.length; i++) {
            actual *= factors[i].intValue();
        }
        return expected == actual;
    }

    @Test
    public void test_factor() {
        // 测试因式分解的功能，是否满足测试需要

        UnsafeCachingFactorizerImpl impl = new UnsafeCachingFactorizerImpl();
        BigInteger[] result = impl.factor( BigInteger.ONE );
        assertEquals( 1, result.length );
        assertEquals( BigInteger.ONE, result[0] );

        result = impl.factor( BigInteger.valueOf( 2 ) );
        assertEquals( 1, result.length );
        assertEquals( BigInteger.valueOf( 2 ), result[0] );

        result = impl.factor( BigInteger.valueOf( 4 ) );
        assertEquals( 2, result.length );
        assertEquals( BigInteger.valueOf( 2 ), result[0] );
        assertEquals( BigInteger.valueOf( 2 ), result[1] );

        result = impl.factor( BigInteger.valueOf( 60 ) ); // 2 * 2 * 3 * 5
        assertEquals( 4, result.length );
        assertEquals( BigInteger.valueOf( 2 ), result[0] );
        assertEquals( BigInteger.valueOf( 2 ), result[1] );
        assertEquals( BigInteger.valueOf( 3 ), result[2] );
        assertEquals( BigInteger.valueOf( 5 ), result[3] );
    }

}

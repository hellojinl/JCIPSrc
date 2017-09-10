package java8newfeatures.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import support.sleep.Sleep;

/**
 * 参考文档<a href="http://www.jianshu.com/p/6f3ee90ab7d3">CompletableFuture 详解</a>
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CompletableFutureTest {

    @After
    public void tearDown() throws Exception {
        System.out.println();
    }

    /**
     * test_supplyAsync中的代码运行在ForkJoinPool.commonPool中
     */
    @Test
    public void test_supplyAsync() {
        System.out.println( "(test_supplyAsync)" );

        System.out.println( "test_supply runs in " + Thread.currentThread() );
        Thread thread = Thread.currentThread();

        Thread supplyAsyncThread = CompletableFuture.supplyAsync( () -> {
            System.out.println( "supplyAsync runs in " + Thread.currentThread() ); // 运行在ForkJoinPool.commonPool
            return Thread.currentThread();
        } ).join();

        assertTrue( thread != supplyAsyncThread );
    }

    /**
     * thenApply在前面的运行阶段完成之后执行有返回值，前面的运行结果会做为参数传递给它，在这里s的值为"hello"
     */
    @Test
    public void test_thenApply() {
        System.out.println( "(test_thenApply)" );

        CompletableFuture< String > future = CompletableFuture.supplyAsync( () -> "hello" )
                .thenApply( s -> s + " world" );
        String reuslt1 = future.join();

        String result2 = null;
        try {
            result2 = future.get(); // 同样的效果get方式显得比较笨重
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( ExecutionException e ) {
            e.printStackTrace();
        }

        System.out.println( "result = " + reuslt1 );
        assertEquals( reuslt1, result2 );
    }

    /**
     * thenAccept在前面的运行阶段完成后执行，没有返回值，前面的运行结果会做为参数传递给它，在这里a的值为"hello world"
     */
    @Test
    public void test_thenAccept() {
        System.out.println( "(test_thenAccept)" );

        CompletableFuture.supplyAsync( () -> "hello world" ).thenAccept( System.out::println );

        // 另一种写法
        CompletableFuture.supplyAsync( () -> "hello world" ).thenAccept( a -> System.out.println( a ) );
    }

    /**
     * thenRun在前面的运行阶段完成后执行，没有参数，没有返回值
     */
    @Test
    public void test_thenRun() {
        System.out.println( "(test_thenRun)" );

        CompletableFuture.supplyAsync( () -> 1234 ).thenRun( () -> {
            System.out.println( "hello world" );
        } );
    }

    /**
     * 等待前两个阶段完成后，接受它们的结果作为参数，执行操作并返回结果
     */
    @Test
    public void test_thenCombine() {
        System.out.println( "(test_thenCombine)" );

        CompletableFuture< String > other = CompletableFuture.supplyAsync( () -> "world" );
        String result = CompletableFuture.supplyAsync( () -> "hello" ).thenCombine( other, (s1, s2) -> s1 + " " + s2 )
                .join();
        System.out.println( "result=" + result );
        assertEquals( "hello world", result );
    }

    /**
     * 等待前两个阶段完成后，接受它们的结果作为参数，执行操作且没有返回结果
     */
    @Test
    public void test_thenAcceptBoth() {
        System.out.println( "(test_thenAcceptBoth)" );

        CompletableFuture< String > other = CompletableFuture.supplyAsync( () -> "world" );
        CompletableFuture.supplyAsync( () -> "hello" ).thenAcceptBoth( other,
                (s1, s2) -> System.out.println( s1 + " " + s2 ) );
    }

    /**
     * 等待前两个阶段完成后，执行操作，与前面两个阶段的执行结果没有关系
     */
    @Test
    public void test_runAfterBoth() {
        System.out.println( "(test_runAfterBoth)" );

        CompletableFuture< String > other = CompletableFuture.supplyAsync( () -> "world" );
        CompletableFuture.supplyAsync( () -> "hello" ).runAfterBoth( other,
                () -> System.out.println( "no paramters" ) );
    }

    /**
     * 等待前面两个任务完成比较快的那个结束，并用它的结果做为参数进行操作，然后返回结果
     */
    @Test
    public void test_applyToEither() {
        System.out.println( "(test_applyToEither)" );

        CompletableFuture< String > a = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 300, TimeUnit.MILLISECONDS );
            return "a";
        } );
        CompletableFuture< String > b = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 200, TimeUnit.MILLISECONDS );
            return "b";
        } );
        String result = a.applyToEither( b, r -> {
            return r;
        } ).join();
        System.out.println( "result = " + result );
        assertEquals( "b", result );
    }

    /**
     * 等待前面两个任务完成比较快的那个结束，并用它的结果做为参数进行操作，没有返回结果
     */
    @Test
    public void test_acceptEither() {
        System.out.println( "(test_acceptEither)" );

        CompletableFuture< String > a = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
            return "a";
        } );
        CompletableFuture< String > b = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 200, TimeUnit.MILLISECONDS );
            return "b";
        } );
        CompletableFuture< Void > c = a.acceptEither( b, r -> {
            System.out.println( "result = " + r );
        } );

        Void result = c.join();
        assertNull( result );
    }

    /**
     * 前两个比较快的那个完成后，执行下一步操作，不接受前面的参数，没有返回值
     */
    @Test
    public void test_runAfterEither() {
        System.out.println( "(test_acceptEither)" );

        CompletableFuture< String > a = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
            return "a";
        } );
        CompletableFuture< String > b = CompletableFuture.supplyAsync( () -> {
            Sleep.sleepUninterruptibly( 200, TimeUnit.MILLISECONDS );
            return "b";
        } );
        a.runAfterEither( b, () -> {
            System.out.println( "123456" );
        } ).join(); // join不是必须的，只是为了让测试结束前能够执行println

    }

    /**
     * 异常发生时进行一些操作，并且返回值
     */
    @SuppressWarnings("unused")
    @Test
    public void test_exceptionally() {
        System.out.println( "(test_exceptionally)" );

        String result = CompletableFuture.supplyAsync( () -> {
            if (true) {
                throw new RuntimeException( "发生了异常" );
            }
            return "123";
        } ).exceptionally( e -> {
            String str = e.getMessage();
            return str;
        } ).join();
        System.out.println( result );
    }

    /**
     * 当前面的阶段完成后执行操作，已前面阶段的结果和异常做为参数，并且自身没有返回值
     */
    @Test
    public void test_whenComplete() {
        System.out.println( "(test_whenComplete)" );

        @SuppressWarnings("unused")
        String result = CompletableFuture.supplyAsync( () -> {
            if (true) {
                throw new RuntimeException( "发生了异常" );
            }
            return "123";
        } ).whenComplete( (s, t) -> {
            System.out.println( "[whenComplete] s = " + s );
            System.out.println( "[whenComplete] t = " + t.getMessage() );
        } ).exceptionally( e -> {
            System.out.println( "[exceptionally] e = " + e.getMessage() );
            return "hello world";
        } ).join();

        System.out.println( "result = " + result );
        assertEquals( "hello world", result );
    }

    /**
     * 运行完成时对结果处理
     */
    @Test
    public void test_handle() {
        System.out.println( "(test_handle)" );

        String result = CompletableFuture.supplyAsync( () -> {
            return "123";
        } ).handle( (s, t) -> {
            return s;
        } ).join();

        System.out.println( "result = " + result );
        assertEquals( "123", result );
    }
}

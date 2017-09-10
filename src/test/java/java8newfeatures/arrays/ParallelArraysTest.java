package java8newfeatures.arrays;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import testUtils.Timer;

/**
 * {@link ParallelArrays}示例演示了基本的语法，我在它的基础上改写了一下，让它更清楚
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see ParallelArrays
 */
public class ParallelArraysTest {

    /**
     * 演示了并发排序 （实际上在数组大小不大的情况下parallelSort使用和Sort是一样的快速排序，但是当超过某个值时，它将自动使用并发排序）
     */
    @Test
    public void invoke_parallelSort() {
        System.out.println( "(parallelSort)" );
        // 初始化数组，赋值为一组随机数
        int[] datas = new int[ 10 ];
        Arrays.parallelSetAll( datas, i -> ThreadLocalRandom.current().nextInt( 100 ) ); // i为数组下标
        // 输出结果
        printArray( "init:", datas );

        // 并发排序
        Arrays.parallelSort( datas );
        // 输出结果
        printArray( "sorted:", datas );
        System.out.println();
    }

    /**
     * 演示了排序
     */
    @Test
    public void invoke_sort() {
        System.out.println( "(sort)" );
        // 初始化数组，赋值为一组随机数
        int[] datas = new int[ 10 ];
        Arrays.parallelSetAll( datas, index -> ThreadLocalRandom.current().nextInt( 100 ) ); // index为数组下标
        // 输出结果
        printArray( "init:", datas );

        // 排序
        Arrays.sort( datas );
        // 输出结果
        printArray( "sorted:", datas );
        System.out.println();
    }

    /**
     * 比较（一次）sort和parallelSort的效率
     */
    @Test
    public void compare_sort_and_parallelSort() {
        final int size = 1 << 14; // 1<<14用于确保parallelSort会启动并发算法，具体参考源代码Arrays.MIN_ARRAY_SORT_GRAN的值
        System.out.println( "(compare) datas.length = " + size );
        int[] datas1 = new int[ size ];
        Arrays.parallelSetAll( datas1, index -> ThreadLocalRandom.current().nextInt( 100000 ) );
        int[] datas2 = Arrays.copyOf( datas1, datas1.length );

        long sortTimeNs = Timer.time( () -> {
            Arrays.sort( datas1 );
        } );

        long parallelSortTimeNs = Timer.time( () -> {
            Arrays.parallelSort( datas2 );
        } );

        System.out.println( String.format( "%20s = %dns", "sortTime", sortTimeNs ) );
        System.out.println( String.format( "%20s = %dns", "parallelSortTime", parallelSortTimeNs ) );
        System.out.println();

        assertTrue( sortTimeNs > parallelSortTimeNs );

    }

    private void printArray(String prefix, int[] datas) {
        System.out.print( String.format( "%8s", prefix ) );
        Arrays.stream( datas ).forEach( element -> System.out.print( String.format( "%4d", element ) ) ); // 注意没有这样的操作datas.forEach
        System.out.println();
    }
}

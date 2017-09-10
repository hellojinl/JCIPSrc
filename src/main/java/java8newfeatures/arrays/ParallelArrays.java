package java8newfeatures.arrays;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import java8newfeatures.arrays.ParallelArraysTest;

/**
 * @author javacodegeeks
 * @see ParallelArraysTest 更清晰的示例
 * @see <a href="http://www.importnew.com/11908.html#parallelArrays">数组并发排序</a>
 */
public class ParallelArrays {

    public static void main(String[] args) {
        long[] arrayOfLong = new long[ 20000 ];
        Arrays.parallelSetAll( arrayOfLong, index -> ThreadLocalRandom.current().nextInt( 1000000 ) );
        Arrays.stream( arrayOfLong ).limit( 10 ).forEach( i -> System.out.print( i + " " ) );
        System.out.println();

        Arrays.parallelSort( arrayOfLong );
        Arrays.stream( arrayOfLong ).limit( 10 ).forEach( i -> System.out.print( i + " " ) );
        System.out.println();
    }
}

package C8_Applying_Thread_Pools.Ch8_11_Transforming_sequential_tail_recursion_into_parallelized_recursion;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C8_Applying_Thread_Pools.Ch8_11_Transforming_sequential_tail_recursion_into_parallelized_recursion.TransformingSequential.Node;
import support.RandomUtil;
import support.sleep.Sleep;
import testUtils.Timer;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TransformingSequentialTest {

    @Test
    public void test() throws InterruptedException {
        long timeSeq = time_sequentialRecursive();
        long timePar = time_parallelRecursive();
        assertTrue( timeSeq > timePar );
    }

    public long time_sequentialRecursive() {
        List< Node< Integer > > roots = generateNodeList();
        Queue< Integer > results = new ConcurrentLinkedQueue< Integer >();

        TransformingSequential t = new TransformingSequentialImpl();
        long time = Timer.timeMillis( () -> t.sequentialRecursive( roots, results ) );
        System.out.println( "It spent " + time + " millis (sequentialRecursive)" );
        return time;
    }

    public long time_parallelRecursive() throws InterruptedException {
        List< Node< Integer > > roots = generateNodeList();

        ExecutorService exec = Executors.newCachedThreadPool();
        Queue< Integer > results = new ConcurrentLinkedQueue< Integer >();

        TransformingSequential t = new TransformingSequentialImpl();
        long time = Timer.timeMillis( () -> {
            t.parallelRecursive( exec, roots, results );
            exec.shutdown();
            exec.awaitTermination( 5, TimeUnit.SECONDS );
        } );
        System.out.println( "It spent " + time + " millis (parallelRecursive)" );
        return time;
    }

    private List< Node< Integer > > generateNodeList() {
        List< Node< Integer > > roots = new ArrayList<>();

        RandomNode r1 = new RandomNode();
        List< Node< Integer > > children1 = new ArrayList<>();
        children1.add( new RandomNode() );
        children1.add( new RandomNode() );
        children1.add( new RandomNode() );
        r1.setChildren( children1 );

        RandomNode r2 = new RandomNode();
        List< Node< Integer > > children2 = new ArrayList<>();
        children2.add( new RandomNode() );
        children2.add( new RandomNode() );
        r2.setChildren( children2 );

        roots.add( r1 );
        roots.add( r2 );
        return roots;
    }

    class TransformingSequentialImpl extends TransformingSequential {

        @Override
        public void process(Element e) {

        }

    }

    class RandomNode implements Node< Integer > {

        private List< Node< Integer > > children = new ArrayList<>();

        RandomNode() {
        }

        RandomNode(List< Node< Integer > > children) {
            setChildren( children );
        }

        @Override
        public Integer compute() {
            int num = RandomUtil.get( 10, 1000 );
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS ); // 为了让并发发挥优势，需要增加计算任务的耗时（如果每个任务完成的速度都非常快，那么并发不一定比串行快，因为并发本身也是有开销的）
            return num;
        }

        @Override
        public List< Node< Integer > > getChildren() {
            return this.children;
        }

        public void setChildren(List< Node< Integer > > children) {
            if (children == null) {
                this.children = new ArrayList<>();
            } else {
                this.children = children;
            }
        }

    }
}

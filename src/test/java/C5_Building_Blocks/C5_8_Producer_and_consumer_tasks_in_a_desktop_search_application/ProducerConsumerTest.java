package C5_Building_Blocks.C5_8_Producer_and_consumer_tasks_in_a_desktop_search_application;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C5_Building_Blocks.C5_8_Producer_and_consumer_tasks_in_a_desktop_search_application.ProducerConsumer.FileCrawler;
import C5_Building_Blocks.C5_8_Producer_and_consumer_tasks_in_a_desktop_search_application.ProducerConsumer.Indexer;
import support.sleep.Sleep;

public class ProducerConsumerTest {

    private static final int BOUND = 10;
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

    @Test
    public void test() {

        String testClasses = Thread.currentThread().getContextClassLoader().getResource( "" ).getPath();

        File[] roots = new File[] { new File( testClasses ) };

        BlockingQueue< File > queue = new LinkedBlockingQueue< File >( BOUND );
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };

        for (File root : roots)
            new Thread( new FileCrawler( queue, filter, root ) ).start();

        for (int i = 0; i < N_CONSUMERS; i++)
            new Thread( new Indexer( queue ) ).start();

        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );
    }
}

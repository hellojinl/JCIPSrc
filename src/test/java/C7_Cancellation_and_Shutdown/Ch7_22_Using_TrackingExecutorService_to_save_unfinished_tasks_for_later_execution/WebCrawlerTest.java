package C7_Cancellation_and_Shutdown.Ch7_22_Using_TrackingExecutorService_to_save_unfinished_tasks_for_later_execution;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.RandomUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class WebCrawlerTest {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

    @Test
    public void test() throws MalformedURLException {
        URL url = new URL( "http://localhost:8080/" + RandomUtil.get( 0, 1000 ) );
        final SlowWebCrawler crawler = new SlowWebCrawler( url );

        cancelExec.schedule( new Runnable() {

            @Override
            public void run() {
                try {
                    System.err.println( "crawler.stop()" );
                    crawler.stop();

                    System.err.println( "crawler.start()" );
                    crawler.start();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

        }, 1, TimeUnit.SECONDS );

        cancelExec.schedule( new Runnable() {

            @Override
            public void run() {
                try {
                    System.err.println( "crawler.stop()" );
                    crawler.stop();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

        }, 3, TimeUnit.SECONDS );

        System.err.println( "crawler.start()" );
        crawler.start();

        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );
    }

    class SlowWebCrawler extends WebCrawler {

        public SlowWebCrawler(URL startUrl) {
            super( startUrl );
        }

        @Override
        protected List< URL > processPage(URL url) {
            List< URL > urls = new ArrayList< URL >();
            try {
                TimeUnit.MILLISECONDS.sleep( 500 );
                System.out.println( "processed page, url = " + url );
                urls.add( new URL( "http://localhost:8080/" + RandomUtil.get( 0, 1000 ) ) );
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
            return urls;

        }

    }
}

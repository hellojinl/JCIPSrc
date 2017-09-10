package C6_Task_Execution.Ch6_16_Fetching_an_advertisement_with_a_time_budget;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import C6_Task_Execution.Ch6_16_Fetching_an_advertisement_with_a_time_budget.RenderWithTimeBudgetTest.RenderWithTimeBudget.Page;

public class RenderWithTimeBudgetTest {

    @Test
    public void test() throws InterruptedException {
        RenderWithTimeBudget renderer = new RenderWithTimeBudget();
        Page page = renderer.renderPageWithAd();
        assertTrue( page.getAd() != null );
    }

    static class RenderWithTimeBudget {
        private static final Ad DEFAULT_AD = new Ad();
        private static final long TIME_BUDGET = 1000;
        private static final ExecutorService exec = Executors.newCachedThreadPool();

        Page renderPageWithAd() throws InterruptedException {
            long endNanos = System.nanoTime() + TIME_BUDGET;
            Future< Ad > f = exec.submit( new FetchAdTask() );
            // Render the page while waiting for the ad
            Page page = renderPageBody();
            Ad ad;
            try {
                // Only wait for the remaining time budget
                long timeLeft = endNanos - System.nanoTime();
                ad = f.get( timeLeft, NANOSECONDS );
            } catch ( ExecutionException e ) {
                ad = DEFAULT_AD;
            } catch ( TimeoutException e ) {
                ad = DEFAULT_AD;
                f.cancel( true );
            }
            page.setAd( ad );
            return page;
        }

        Page renderPageBody() {
            System.out.println( "render page body" );
            return new Page();
        }

        static class Ad {
        }

        static class Page {
            private Ad ad;

            public synchronized void setAd(Ad ad) {
                System.out.println( "set ad" );
                this.ad = ad;
            }

            public synchronized Ad getAd() {
                return this.ad;
            }
        }

        static class FetchAdTask implements Callable< Ad > {
            public Ad call() {
                System.out.println( "fetch ad" );
                return new Ad();
            }
        }

    }
}

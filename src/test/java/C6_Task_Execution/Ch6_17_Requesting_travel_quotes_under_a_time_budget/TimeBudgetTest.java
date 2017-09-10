package C6_Task_Execution.Ch6_17_Requesting_travel_quotes_under_a_time_budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import support.RandomUtil;
import support.sleep.Sleep;

/**
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TimeBudgetTest {

    private final static int MAX_COMPANY_COUNTS = 20;
    private final static int TIME = 100;
    private final static TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private TravelInfo travelInfo;
    private Set< TravelCompany > companies;
    private Comparator< TravelQuote > ranking;
    private long time;
    private TimeUnit unit;

    @Before
    public void setUp() throws Exception {
        travelInfo = new TravelInfoImpl( "beijing" );
        companies = new TreeSet< TravelCompany >();
        for (int i = 0; i < MAX_COMPANY_COUNTS; i++) {
            companies.add( new TravelCompanyImpl( "company " + i ) );
        }
        ranking = new PriceComparator();
        time = TIME;
        unit = TIME_UNIT;
    }

    @Test
    public void justRunIt() throws InterruptedException {
        TimeBudget budget = new TimeBudget();

        List< TravelQuote > quoteList = budget.getRankedTravelQuotes( travelInfo, companies, ranking, time, unit );

        // 打印出报价信息
        System.out.println( "获取如下旅游报价信息：" );
        final AtomicInteger count = new AtomicInteger();
        quoteList.forEach( new Consumer< TravelQuote >() {

            @Override
            public void accept(TravelQuote tq) {
                if (tq instanceof NormalTravelQuote) {
                    System.out.println( String.format( "%2d) %s", count.incrementAndGet(), tq ) );
                }
            }

        } );
        System.out.println( "在给定时间内，" + MAX_COMPANY_COUNTS + "家公司有" + count.get() + "家给出了报价" );
    }

    // 具体实现如下：

    static class TimeBudget {
        private static final ExecutorService exec = Executors.newCachedThreadPool();

        public List< TravelQuote > getRankedTravelQuotes(TravelInfo travelInfo, Set< TravelCompany > companies,
                Comparator< TravelQuote > ranking, long time, TimeUnit unit) throws InterruptedException {
            List< QuoteTask > tasks = new ArrayList< QuoteTask >();
            for (TravelCompany company : companies)
                tasks.add( new QuoteTask( company, travelInfo ) );

            List< Future< TravelQuote > > futures = exec.invokeAll( tasks, time, unit );

            List< TravelQuote > quotes = new ArrayList< TravelQuote >( tasks.size() );
            Iterator< QuoteTask > taskIter = tasks.iterator();
            for (Future< TravelQuote > f : futures) {
                QuoteTask task = taskIter.next();
                try {
                    quotes.add( f.get() );
                } catch ( ExecutionException e ) {
                    quotes.add( task.getFailureQuote( e.getCause() ) );
                } catch ( CancellationException e ) {
                    quotes.add( task.getTimeoutQuote( e ) );
                }
            }

            Collections.sort( quotes, ranking );
            return quotes;
        }

    }

    static class QuoteTask implements Callable< TravelQuote > {
        private final TravelCompany company;
        private final TravelInfo travelInfo;

        public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
            this.company = company;
            this.travelInfo = travelInfo;
        }

        TravelQuote getFailureQuote(Throwable t) {
            return FailureQuote.getInstance();
        }

        TravelQuote getTimeoutQuote(CancellationException e) {
            return TimeoutQuote.getInstance();
        }

        public TravelQuote call() throws Exception {
            return company.solicitQuote( travelInfo );
        }
    }

    /**
     * 旅游公司
     *
     */
    interface TravelCompany {

        TravelQuote solicitQuote(TravelInfo travelInfo) throws Exception;

        String getName();
    }

    class TravelCompanyImpl implements TravelCompany, Comparable< TravelCompanyImpl > {

        private final String name;

        TravelCompanyImpl(String name) {
            this.name = name;
        }

        @Override
        public TravelQuote solicitQuote(TravelInfo travelInfo) throws Exception {

            int sleepTime = RandomUtil.get( TIME - 50, TIME + 50 );
            Sleep.sleepUninterruptibly( sleepTime, TIME_UNIT ); // 使其有机会超时

            long price = RandomUtil.get( 50, 150 );
            int days = RandomUtil.get( 3, 30 );
            TravelQuote tq = new NormalTravelQuote( this, price, travelInfo.address(), days );
            return tq;
        }

        @Override
        public int compareTo(TravelCompanyImpl o) {
            return 1; // asc
        }

        @Override
        public String getName() {
            return name;
        }

    }

    /**
     * 旅行报价
     */
    interface TravelQuote {

        /**
         * 报价公司
         */
        TravelCompany company();

        /**
         * 价钱
         */
        long price();

        /**
         * 旅行地点
         */
        String address();

        /**
         * 天数
         */
        int days();
    }

    class NormalTravelQuote implements TravelQuote {

        private final TravelCompany company;
        private final long price;
        private final String address;
        private final int days;

        public NormalTravelQuote(TravelCompany company, long price, String address, int days) {
            super();
            this.company = company;
            this.price = price;
            this.address = address;
            this.days = days;
        }

        @Override
        public long price() {
            return this.price;
        }

        @Override
        public String address() {
            return this.address;
        }

        @Override
        public int days() {
            return this.days;
        }

        @Override
        public TravelCompany company() {
            return this.company;
        }

        @Override
        public String toString() {
            return String.format( "%10s - %3d dollars - %8s - %2d days", company.getName(), price, address, days );
        }

    }

    final static class FailureQuote implements TravelQuote {

        private final static FailureQuote instance = new FailureQuote();

        public static FailureQuote getInstance() {
            return instance;
        }

        private FailureQuote() {
        }

        @Override
        public final TravelCompany company() {
            throw new UnsupportedOperationException( "FailureQuote不支持该方法" );
        }

        @Override
        public final long price() {
            throw new UnsupportedOperationException( "FailureQuote不支持该方法" );
        }

        @Override
        public final String address() {
            throw new UnsupportedOperationException( "FailureQuote不支持该方法" );
        }

        @Override
        public final int days() {
            throw new UnsupportedOperationException( "FailureQuote不支持该方法" );
        }

        @Override
        public String toString() {
            return "failure quote";
        }

    }

    final static class TimeoutQuote implements TravelQuote {

        private final static TimeoutQuote instance = new TimeoutQuote();

        public static TimeoutQuote getInstance() {
            return instance;
        }

        private TimeoutQuote() {
        }

        @Override
        public final TravelCompany company() {
            throw new UnsupportedOperationException( "TimeoutQuote不支持该方法" );
        }

        @Override
        public final long price() {
            throw new UnsupportedOperationException( "TimeoutQuote不支持该方法" );
        }

        @Override
        public final String address() {
            throw new UnsupportedOperationException( "TimeoutQuote不支持该方法" );
        }

        @Override
        public final int days() {
            throw new UnsupportedOperationException( "TimeoutQuote不支持该方法" );
        }

        @Override
        public String toString() {
            return "timeout Quote";
        }

    }

    class PriceComparator implements Comparator< TravelQuote > {

        @Override
        public int compare(TravelQuote first, TravelQuote second) {
            if (isNormal( first ) && isNormal( second )) {
                long d = first.price() - second.price();
                return d == 0 ? 0 : (d > 0 ? 1 : -1);
            } else if (isNormal( first ) && !isNormal( second )) {
                return -1;
            } else if (!isNormal( first ) && isNormal( second )) {
                return 1;
            } else {
                return 0;
            }

        }

        private boolean isNormal(TravelQuote t) {
            return t instanceof NormalTravelQuote;
        }

    }

    /**
     * 旅行信息
     *
     */
    interface TravelInfo {

        /**
         * 旅行地点
         */
        String address();
    }

    class TravelInfoImpl implements TravelInfo {

        private final String address;

        public TravelInfoImpl(String address) {
            super();
            this.address = address;
        }

        @Override
        public String address() {
            return this.address;
        }
    }

}

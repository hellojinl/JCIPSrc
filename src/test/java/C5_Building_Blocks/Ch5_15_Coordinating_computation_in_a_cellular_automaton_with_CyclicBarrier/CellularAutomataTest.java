package C5_Building_Blocks.Ch5_15_Coordinating_computation_in_a_cellular_automaton_with_CyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import C5_Building_Blocks.Ch5_15_Coordinating_computation_in_a_cellular_automaton_with_CyclicBarrier.CellularAutomataTest.CellularAutomata.Board;
import support.TimeUtil;
import support.annotations.NotThreadSafe;
import support.annotations.ThreadSafe;
import support.sleep.Sleep;

/**
 * CellularAutomata运行实例
 * <p>
 * 这里并没有实现一个真正的<a href=
 * "https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's_Game_of_Life</a>，而只是写了一个能够用于演示的mock程序
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's_Game_of_Life</a>
 */
public class CellularAutomataTest {

    private final static int MAX_COMMIT_COUNT = 5;
    private final static AtomicInteger commitCount = new AtomicInteger();

    @Test
    public void justRunIt() {
        int processorCount = Runtime.getRuntime().availableProcessors();
        int maxX = 10 * processorCount;
        int maxY = maxX;
        BoardValue[][] values = new BoardValue[ maxX ][ maxY ];
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                values[x][y] = new BoardValue();
            }
        }
        SimpleBoard board = new SimpleBoard( values, maxX, maxY );
        CellularAutomata automata = new CellularAutomata( board );
        automata.start();
        System.out.println( TimeUtil.defaultNow() + " <" + commitCount + "> It has Converged." );
    }

    static class CellularAutomata {
        private final Board mainBoard;
        private final CyclicBarrier barrier;
        private final Worker[] workers;

        public CellularAutomata(Board board) {
            this.mainBoard = board;
            int count = Runtime.getRuntime().availableProcessors();
            this.barrier = new CyclicBarrier( count,
                    // 此Runnable任务在CyclicBarrier的数目达到后，所有其它线程被唤醒前被执行。
                    new Runnable() {
                        public void run() {
                            try {
                                System.out.println(
                                        TimeUtil.defaultNow() + " <" + commitCount + "> commit new values start..." );
                                mainBoard.commitNewValues();
                                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS ); // 休眠期间，相关线程不会被唤醒直到该线程结束
                                System.out.println(
                                        TimeUtil.defaultNow() + " <" + commitCount + "> commit new values end" );
                            } finally {
                                commitCount.incrementAndGet();
                            }
                        }
                    } );
            this.workers = new Worker[ count ];
            for (int i = 0; i < count; i++)
                workers[i] = new Worker( mainBoard.getSubBoard( count, i ) );
        }

        private class Worker implements Runnable {
            private final Board board;

            public Worker(Board board) {
                this.board = board;
            }

            public void run() {
                while ( !board.hasConverged() ) {
                    System.out.println( TimeUtil.defaultNow() + " <" + commitCount + "> set values" );
                    for (int x = 0; x < board.getMaxX(); x++)
                        for (int y = 0; y < board.getMaxY(); y++)
                            board.setNewValue( x, y, computeValue( x, y ) );

                    try {
                        barrier.await();
                    } catch ( InterruptedException ex ) {
                        return;
                    } catch ( BrokenBarrierException ex ) {
                        return;
                    }
                }
            }

            private int computeValue(int x, int y) {
                // Compute the new value that goes in (x,y)
                return 0;
            }
        }

        public void start() {
            for (int i = 0; i < workers.length; i++)
                new Thread( workers[i] ).start();

            mainBoard.waitForConvergence();
        }

        interface Board {
            int getMaxX();

            int getMaxY();

            int getValue(int x, int y);

            int setNewValue(int x, int y, int value);

            void commitNewValues();

            boolean hasConverged();

            void waitForConvergence();

            Board getSubBoard(int numPartitions, int index);
        }
    }

    @NotThreadSafe
    static class SimpleBoard implements Board {

        private final BoardValue[][] commitedValues;
        private final BoardValue[][] valuesBuffer;
        private final int maxX;
        private final int maxY;

        SimpleBoard(BoardValue[][] values, int maxX, int maxY) {
            this.commitedValues = cloneBoardValues( values, maxX, maxY );
            this.valuesBuffer = cloneBoardValues( values, maxX, maxY );
            this.maxX = maxX;
            this.maxY = maxY;
        }

        SimpleBoard(BoardValue[][] values, BoardValue[][] valuesBuffer, int maxX, int maxY) {
            this.commitedValues = values;
            this.valuesBuffer = valuesBuffer;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        @Override
        public int getMaxX() {
            return maxX;
        }

        @Override
        public int getMaxY() {
            return maxY;
        }

        @Override
        public int getValue(int x, int y) {
            return commitedValues[x][y].getValue();
        }

        @Override
        public int setNewValue(int x, int y, int value) {
            valuesBuffer[x][y].setValue( value );
            return commitedValues[x][y].getValue();
        }

        @Override
        public void commitNewValues() {
            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    commitedValues[x][y].setValue( valuesBuffer[x][y].getValue() );
                }
            }
        }

        @Override
        public boolean hasConverged() {
            return commitCount.get() >= MAX_COMMIT_COUNT; // mock
        }

        @Override
        public void waitForConvergence() {
            while ( !hasConverged() ) {
                Sleep.sleepUninterruptibly( 500, TimeUnit.MILLISECONDS );
            }
        }

        @Override
        public Board getSubBoard(int numPartitions, int index) {
            // 只分割X，只满足演示需要不细究细节
            int len = this.maxX / numPartitions; // 必须满足this.maxX %
                                                 // numPartitions == 0
            int minX = index * len;
            int maxX = (index + 1) * len;
            BoardValue[][] values = shallowcCloneBoardValues( this.commitedValues, minX, maxX, 0, this.maxY );
            BoardValue[][] valuesBuffer = shallowcCloneBoardValues( this.valuesBuffer, minX, maxX, 0, this.maxY );

            return new SimpleBoard( values, valuesBuffer, len, this.maxY );
        }

        // clone >>

        private BoardValue[][] shallowcCloneBoardValues(BoardValue[][] values, int minX, int maxX, int minY, int maxY) {
            BoardValue[][] cloned = new BoardValue[ maxX - minX ][ maxY - minY ];
            for (int i = 0, x = minX; x < maxX; i++, x++) {
                for (int j = 0, y = minY; y < maxY; j++, y++) {
                    cloned[i][j] = values[x][y];
                }
            }
            return cloned;
        }

        private BoardValue[][] cloneBoardValues(BoardValue[][] values, int maxX, int maxY) {
            return cloneBoardValues( values, 0, maxX, 0, maxY );
        }

        private BoardValue[][] cloneBoardValues(BoardValue[][] values, int minX, int maxX, int minY, int maxY) {
            BoardValue[][] cloned = new BoardValue[ maxX - minX ][ maxY - minY ];
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    cloned[x][y] = new BoardValue( values[x][y] );
                }
            }
            return cloned;
        }

        // << clone
    }

    @ThreadSafe
    static class BoardValue {
        private final AtomicInteger value = new AtomicInteger( 1 );

        BoardValue() {
        }

        BoardValue(BoardValue other) {
            this.value.set( other.value.get() );
        }

        BoardValue(int value) {
            this.value.set( value );
        }

        public int getValue() {
            return value.get();
        }

        public void setValue(int value) {
            this.value.set( value );
        }

    }
}

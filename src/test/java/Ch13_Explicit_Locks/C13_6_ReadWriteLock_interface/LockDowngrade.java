package Ch13_Explicit_Locks.C13_6_ReadWriteLock_interface;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;

import org.junit.Test;

/**
 * 锁降级是指写锁降级到读锁，更具体一点： 1. 当前线程持有写锁，此时所有其他线程都不持有任何写锁、读锁；
 * 
 * 2.
 * 当前线程的所有写操作完成了并且还剩下一系列的读操作未完成，为了性能考虑，需要将当前的写锁降级为读锁，允许其他的读线程进入，但又想要确保当前线程在进行读操作之前相关数据不被其他线程修改
 * 注：是否一定要确保当前线程在执行一系列操作的时候不会有其他线程做修改？
 * 不是的，这取决于你的需求，你可以允许其他线程做修改，也可以不允许。但是这两种需求的代码实现是不同的）
 * 
 * 3.具体的锁降级步骤如下（这里不允许其他线程中途修改相关数据） a）持有写锁， b）进行全部的写操作和部分的读操作（如果有），
 * c）申请读锁，（只有当前线程能申请读锁成功，其他线程是无法申请读锁的） d）释放写锁（此时降级成功了，同时又不允许其他的线程持有写锁，但可以持有读锁），
 * e）进行读操作， f）释放读锁
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LockDowngrade {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    private final static int threadsCount = 5;
    private final static CyclicBarrier barrier = new CyclicBarrier( threadsCount + 1 );
    private final static int maxCount = 800;

    /**
     * 注：书上建议是随机数相加，这里简化成整数累加（每次+1），为了更清楚的查看打印结果
     */
    @Test
    public void testIncreaseAndPrint() throws InterruptedException, BrokenBarrierException {
        final MySimpleCounter counter = new MySimpleCounter();
        for (int i = 0; i < threadsCount; i++) {
            pool.execute( new CountRunnable( counter, maxCount ) );
        }
        barrier.await(); // 所有线程同时开始
        barrier.await(); // 所有线程同时结束

        assertEquals( threadsCount * maxCount, counter.get() );
    }

    class CountRunnable implements Runnable {

        private final MySimpleCounter counter;
        private final int count;

        CountRunnable(MySimpleCounter counter, int count) {
            this.counter = counter;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                barrier.await();

                for (int i = 0; i < count; i++) {
                    counter.increaseAndPrint();
                    Thread.yield();
                }

                barrier.await();
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            } catch ( BrokenBarrierException e ) {
                throw new RuntimeException( e ); // 快速失败，因为是测试没啥好恢复的
            }

        }

    }

}

class MySimpleCounter {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    private int count;

    MySimpleCounter() {

    }

    MySimpleCounter(int c) {
        this.count = c;
    }

    public void increaseAndPrint() {
        w.lock();
        try {
            this.count++;
        } finally {
            // 锁降级
            r.lock();
            w.unlock();
        }

        try {
            System.out.println( String.format( "Thread [%2d] count=%d", Thread.currentThread().getId(), this.count ) );
        } finally {
            r.unlock();
        }
    }

    public void set(int c) {
        lockedWrite( () -> this.count = c );
    }

    public int get() {
        return lockedRead( () -> this.count );
    }

    public void increase() {
        lockedWrite( () -> this.count++ );
    }

    public int increaseAndGet() {
        return lockedWrite( () -> ++this.count );
    }

    private int lockedWrite(IntSupplier updateSupplier) {
        return lockedOperate( w, updateSupplier );
    }

    private int lockedRead(IntSupplier readSupplier) {
        return lockedOperate( r, readSupplier );
    }

    private int lockedOperate(Lock lock, IntSupplier supplier) {
        lock.lock();
        try {
            return supplier.getAsInt();
        } finally {
            lock.unlock();
        }
    }
}

/*
 * 某次运行结果 Thread [13] count=1 Thread [10] count=2 Thread [10] count=3 Thread
 * [10] count=4 Thread [10] count=5 Thread [10] count=6 Thread [10] count=7
 * Thread [10] count=8 Thread [10] count=9 Thread [10] count=10 Thread [10]
 * count=11 Thread [10] count=12 Thread [10] count=13 Thread [10] count=14
 * Thread [10] count=15 Thread [10] count=16 Thread [10] count=17 Thread [10]
 * count=18 Thread [10] count=19 Thread [10] count=20 Thread [10] count=21
 * Thread [10] count=22 Thread [10] count=23 Thread [10] count=24 Thread [10]
 * count=25 Thread [10] count=26 Thread [10] count=27 Thread [10] count=28
 * Thread [10] count=29 Thread [10] count=30 Thread [10] count=31 Thread [10]
 * count=32 Thread [10] count=33 Thread [10] count=34 Thread [10] count=35
 * Thread [10] count=36 Thread [10] count=37 Thread [10] count=38 Thread [10]
 * count=39 Thread [10] count=40 Thread [10] count=41 Thread [10] count=42
 * Thread [10] count=43 Thread [10] count=44 Thread [10] count=45 Thread [10]
 * count=46 Thread [10] count=47 Thread [10] count=48 Thread [ 9] count=49
 * Thread [ 9] count=50 Thread [ 9] count=51 Thread [ 9] count=52 Thread [ 9]
 * count=53 Thread [ 9] count=54 Thread [ 9] count=55 Thread [ 9] count=56
 * Thread [ 9] count=57 Thread [ 9] count=58 Thread [ 9] count=59 Thread [ 9]
 * count=60 Thread [ 9] count=61 Thread [ 9] count=62 Thread [ 9] count=63
 * Thread [ 9] count=64 Thread [ 9] count=65 Thread [ 9] count=66 Thread [ 9]
 * count=67 Thread [ 9] count=68 Thread [ 9] count=69 Thread [ 9] count=70
 * Thread [ 9] count=71 Thread [11] count=72 Thread [11] count=73 Thread [11]
 * count=74 Thread [11] count=75 Thread [11] count=76 Thread [11] count=77
 * Thread [11] count=78 Thread [11] count=79 Thread [11] count=80 Thread [11]
 * count=81 Thread [11] count=82 Thread [11] count=83 Thread [11] count=84
 * Thread [11] count=85 Thread [11] count=86 Thread [11] count=87 Thread [11]
 * count=88 Thread [11] count=89 Thread [11] count=90 Thread [11] count=91
 * Thread [11] count=92 Thread [11] count=93 Thread [11] count=94 Thread [11]
 * count=95 Thread [11] count=96 Thread [11] count=97 Thread [11] count=98
 * Thread [11] count=99 Thread [11] count=100 Thread [11] count=101 Thread [11]
 * count=102 Thread [11] count=103 Thread [11] count=104 Thread [11] count=105
 * Thread [11] count=106 Thread [11] count=107 Thread [11] count=108 Thread [11]
 * count=109 Thread [11] count=110 Thread [11] count=111 Thread [11] count=112
 * Thread [11] count=113 Thread [11] count=114 Thread [11] count=115 Thread [11]
 * count=116 Thread [11] count=117 Thread [11] count=118 Thread [11] count=119
 * Thread [11] count=120 Thread [11] count=121 Thread [11] count=122 Thread [11]
 * count=123 Thread [11] count=124 Thread [11] count=125 Thread [11] count=126
 * Thread [11] count=127 Thread [11] count=128 Thread [11] count=129 Thread [11]
 * count=130 Thread [11] count=131 Thread [11] count=132 Thread [11] count=133
 * Thread [11] count=134 Thread [11] count=135 Thread [11] count=136 Thread [11]
 * count=137 Thread [11] count=138 Thread [11] count=139 Thread [11] count=140
 * Thread [12] count=141 Thread [12] count=142 Thread [12] count=143 Thread [12]
 * count=144 Thread [12] count=145 Thread [12] count=146 Thread [12] count=147
 * Thread [12] count=148 Thread [13] count=149 Thread [13] count=150 Thread [13]
 * count=151 Thread [13] count=152 Thread [13] count=153 Thread [13] count=154
 * Thread [13] count=155 Thread [13] count=156 Thread [13] count=157 Thread [13]
 * count=158 Thread [13] count=159 Thread [13] count=160 Thread [13] count=161
 * Thread [13] count=162 Thread [13] count=163 Thread [13] count=164 Thread [13]
 * count=165 Thread [13] count=166 Thread [13] count=167 Thread [13] count=168
 * Thread [13] count=169 Thread [13] count=170 Thread [13] count=171 Thread [13]
 * count=172 Thread [13] count=173 Thread [13] count=174 Thread [13] count=175
 * Thread [13] count=176 Thread [13] count=177 Thread [13] count=178 Thread [13]
 * count=179 Thread [13] count=180 Thread [13] count=181 Thread [13] count=182
 * Thread [13] count=183 Thread [13] count=184 Thread [13] count=185 Thread [13]
 * count=186 Thread [13] count=187 Thread [13] count=188 Thread [13] count=189
 * Thread [13] count=190 Thread [10] count=191 Thread [10] count=192 Thread [10]
 * count=193 Thread [10] count=194 Thread [10] count=195 Thread [10] count=196
 * Thread [10] count=197 Thread [10] count=198 Thread [10] count=199 Thread [10]
 * count=200 Thread [ 9] count=201 Thread [ 9] count=202 Thread [ 9] count=203
 * Thread [ 9] count=204 Thread [ 9] count=205 Thread [ 9] count=206 Thread [ 9]
 * count=207 Thread [ 9] count=208 Thread [ 9] count=209 Thread [ 9] count=210
 * Thread [ 9] count=211 Thread [ 9] count=212 Thread [ 9] count=213 Thread [ 9]
 * count=214 Thread [ 9] count=215 Thread [ 9] count=216 Thread [ 9] count=217
 * Thread [ 9] count=218 Thread [11] count=219 Thread [11] count=220 Thread [11]
 * count=221 Thread [11] count=222 Thread [11] count=223 Thread [11] count=224
 * Thread [11] count=225 Thread [11] count=226 Thread [11] count=227 Thread [11]
 * count=228 Thread [11] count=229 Thread [11] count=230 Thread [11] count=231
 * Thread [11] count=232 Thread [11] count=233 Thread [11] count=234 Thread [11]
 * count=235 Thread [11] count=236 Thread [11] count=237 Thread [11] count=238
 * Thread [11] count=239 Thread [11] count=240 Thread [11] count=241 Thread [11]
 * count=242 Thread [11] count=243 Thread [11] count=244 Thread [12] count=245
 * Thread [12] count=246 Thread [12] count=247 Thread [12] count=248 Thread [12]
 * count=249 Thread [12] count=250 Thread [12] count=251 Thread [12] count=252
 * Thread [12] count=253 Thread [12] count=254 Thread [12] count=255 Thread [12]
 * count=256 Thread [12] count=257 Thread [12] count=258 Thread [12] count=259
 * Thread [12] count=260 Thread [12] count=261 Thread [12] count=262 Thread [12]
 * count=263 Thread [12] count=264 Thread [12] count=265 Thread [12] count=266
 * Thread [12] count=267 Thread [12] count=268 Thread [12] count=269 Thread [12]
 * count=270 Thread [12] count=271 Thread [12] count=272 Thread [12] count=273
 * Thread [12] count=274 Thread [12] count=275 Thread [12] count=276 Thread [12]
 * count=277 Thread [12] count=278 Thread [12] count=279 Thread [12] count=280
 * Thread [12] count=281 Thread [12] count=282 Thread [12] count=283 Thread [12]
 * count=284 Thread [12] count=285 Thread [12] count=286 Thread [12] count=287
 * Thread [12] count=288 Thread [12] count=289 Thread [12] count=290 Thread [12]
 * count=291 Thread [12] count=292 Thread [12] count=293 Thread [12] count=294
 * Thread [12] count=295 Thread [12] count=296 Thread [12] count=297 Thread [12]
 * count=298 Thread [12] count=299 Thread [12] count=300 Thread [12] count=301
 * Thread [12] count=302 Thread [12] count=303 Thread [12] count=304 Thread [12]
 * count=305 Thread [12] count=306 Thread [12] count=307 Thread [12] count=308
 * Thread [12] count=309 Thread [12] count=310 Thread [12] count=311 Thread [12]
 * count=312 Thread [12] count=313 Thread [12] count=314 Thread [12] count=315
 * Thread [12] count=316 Thread [12] count=317 Thread [12] count=318 Thread [12]
 * count=319 Thread [12] count=320 Thread [12] count=321 Thread [12] count=322
 * Thread [12] count=323 Thread [12] count=324 Thread [12] count=325 Thread [12]
 * count=326 Thread [12] count=327 Thread [12] count=328 Thread [12] count=329
 * Thread [12] count=330 Thread [12] count=331 Thread [12] count=332 Thread [12]
 * count=333 Thread [12] count=334 Thread [12] count=335 Thread [12] count=336
 * Thread [12] count=337 Thread [12] count=338 Thread [12] count=339 Thread [12]
 * count=340 Thread [12] count=341 Thread [12] count=342 Thread [12] count=343
 * Thread [12] count=344 Thread [12] count=345 Thread [12] count=346 Thread [12]
 * count=347 Thread [13] count=348 Thread [13] count=349 Thread [13] count=350
 * Thread [13] count=351 Thread [13] count=352 Thread [13] count=353 Thread [13]
 * count=354 Thread [13] count=355 Thread [13] count=356 Thread [13] count=357
 * Thread [13] count=358 Thread [13] count=359 Thread [13] count=360 Thread [13]
 * count=361 Thread [13] count=362 Thread [13] count=363 Thread [13] count=364
 * Thread [13] count=365 Thread [13] count=366 Thread [13] count=367 Thread [13]
 * count=368 Thread [13] count=369 Thread [13] count=370 Thread [13] count=371
 * Thread [10] count=372 Thread [10] count=373 Thread [10] count=374 Thread [10]
 * count=375 Thread [ 9] count=376 Thread [ 9] count=377 Thread [ 9] count=378
 * Thread [ 9] count=379 Thread [ 9] count=380 Thread [ 9] count=381 Thread [ 9]
 * count=382 Thread [ 9] count=383 Thread [ 9] count=384 Thread [ 9] count=385
 * Thread [ 9] count=386 Thread [ 9] count=387 Thread [ 9] count=388 Thread [ 9]
 * count=389 Thread [ 9] count=390 Thread [11] count=391 Thread [11] count=392
 * Thread [11] count=393 Thread [11] count=394 Thread [11] count=395 Thread [11]
 * count=396 Thread [11] count=397 Thread [11] count=398 Thread [11] count=399
 * Thread [11] count=400 Thread [11] count=401 Thread [11] count=402 Thread [11]
 * count=403 Thread [11] count=404 Thread [11] count=405 Thread [11] count=406
 * Thread [11] count=407 Thread [11] count=408 Thread [11] count=409 Thread [11]
 * count=410 Thread [11] count=411 Thread [11] count=412 Thread [11] count=413
 * Thread [11] count=414 Thread [11] count=415 Thread [11] count=416 Thread [11]
 * count=417 Thread [11] count=418 Thread [11] count=419 Thread [11] count=420
 * Thread [11] count=421 Thread [11] count=422 Thread [11] count=423 Thread [11]
 * count=424 Thread [11] count=425 Thread [11] count=426 Thread [11] count=427
 * Thread [11] count=428 Thread [11] count=429 Thread [11] count=430 Thread [11]
 * count=431 Thread [11] count=432 Thread [11] count=433 Thread [11] count=434
 * Thread [11] count=435 Thread [11] count=436 Thread [12] count=437 Thread [12]
 * count=438 Thread [12] count=439 Thread [12] count=440 Thread [12] count=441
 * Thread [12] count=442 Thread [12] count=443 Thread [12] count=444 Thread [12]
 * count=445 Thread [12] count=446 Thread [12] count=447 Thread [12] count=448
 * Thread [12] count=449 Thread [12] count=450 Thread [12] count=451 Thread [12]
 * count=452 Thread [12] count=453 Thread [12] count=454 Thread [12] count=455
 * Thread [12] count=456 Thread [12] count=457 Thread [12] count=458 Thread [12]
 * count=459 Thread [12] count=460 Thread [12] count=461 Thread [12] count=462
 * Thread [12] count=463 Thread [12] count=464 Thread [12] count=465 Thread [12]
 * count=466 Thread [12] count=467 Thread [12] count=468 Thread [12] count=469
 * Thread [12] count=470 Thread [12] count=471 Thread [12] count=472 Thread [12]
 * count=473 Thread [12] count=474 Thread [12] count=475 Thread [12] count=476
 * Thread [12] count=477 Thread [12] count=478 Thread [12] count=479 Thread [12]
 * count=480 Thread [12] count=481 Thread [12] count=482 Thread [12] count=483
 * Thread [12] count=484 Thread [12] count=485 Thread [12] count=486 Thread [12]
 * count=487 Thread [12] count=488 Thread [12] count=489 Thread [12] count=490
 * Thread [12] count=491 Thread [12] count=492 Thread [12] count=493 Thread [12]
 * count=494 Thread [13] count=495 Thread [10] count=496 Thread [10] count=497
 * Thread [10] count=498 Thread [10] count=499 Thread [10] count=500 Thread [10]
 * count=501 Thread [10] count=502 Thread [10] count=503 Thread [10] count=504
 * Thread [10] count=505 Thread [10] count=506 Thread [10] count=507 Thread [10]
 * count=508 Thread [10] count=509 Thread [10] count=510 Thread [10] count=511
 * Thread [10] count=512 Thread [10] count=513 Thread [10] count=514 Thread [10]
 * count=515 Thread [10] count=516 Thread [10] count=517 Thread [10] count=518
 * Thread [10] count=519 Thread [10] count=520 Thread [10] count=521 Thread [10]
 * count=522 Thread [10] count=523 Thread [10] count=524 Thread [10] count=525
 * Thread [10] count=526 Thread [10] count=527 Thread [10] count=528 Thread [10]
 * count=529 Thread [10] count=530 Thread [ 9] count=531 Thread [10] count=532
 * Thread [10] count=533 Thread [10] count=534 Thread [10] count=535 Thread [10]
 * count=536 Thread [10] count=537 Thread [10] count=538 Thread [10] count=539
 * Thread [10] count=540 Thread [10] count=541 Thread [10] count=542 Thread [10]
 * count=543 Thread [10] count=544 Thread [10] count=545 Thread [10] count=546
 * Thread [10] count=547 Thread [10] count=548 Thread [10] count=549 Thread [10]
 * count=550 Thread [10] count=551 Thread [10] count=552 Thread [10] count=553
 * Thread [10] count=554 Thread [10] count=555 Thread [10] count=556 Thread [10]
 * count=557 Thread [10] count=558 Thread [10] count=559 Thread [10] count=560
 * Thread [10] count=561 Thread [10] count=562 Thread [10] count=563 Thread [10]
 * count=564 Thread [10] count=565 Thread [10] count=566 Thread [10] count=567
 * Thread [10] count=568 Thread [10] count=569 Thread [10] count=570 Thread [10]
 * count=571 Thread [10] count=572 Thread [10] count=573 Thread [10] count=574
 * Thread [10] count=575 Thread [10] count=576 Thread [10] count=577 Thread [10]
 * count=578 Thread [10] count=579 Thread [10] count=580 Thread [10] count=581
 * Thread [10] count=582 Thread [10] count=583 Thread [10] count=584 Thread [10]
 * count=585 Thread [10] count=586 Thread [10] count=587 Thread [10] count=588
 * Thread [10] count=589 Thread [10] count=590 Thread [10] count=591 Thread [10]
 * count=592 Thread [10] count=593 Thread [10] count=594 Thread [10] count=595
 * Thread [10] count=596 Thread [10] count=597 Thread [10] count=598 Thread [10]
 * count=599 Thread [10] count=600 Thread [10] count=601 Thread [10] count=602
 * Thread [10] count=603 Thread [10] count=604 Thread [10] count=605 Thread [10]
 * count=606 Thread [10] count=607 Thread [10] count=608 Thread [10] count=609
 * Thread [10] count=610 Thread [10] count=611 Thread [10] count=612 Thread [10]
 * count=613 Thread [10] count=614 Thread [10] count=615 Thread [10] count=616
 * Thread [10] count=617 Thread [10] count=618 Thread [10] count=619 Thread [10]
 * count=620 Thread [10] count=621 Thread [10] count=622 Thread [10] count=623
 * Thread [10] count=624 Thread [10] count=625 Thread [10] count=626 Thread [10]
 * count=627 Thread [10] count=628 Thread [10] count=629 Thread [10] count=630
 * Thread [10] count=631 Thread [10] count=632 Thread [10] count=633 Thread [10]
 * count=634 Thread [10] count=635 Thread [10] count=636 Thread [10] count=637
 * Thread [10] count=638 Thread [10] count=639 Thread [10] count=640 Thread [10]
 * count=641 Thread [10] count=642 Thread [10] count=643 Thread [10] count=644
 * Thread [10] count=645 Thread [10] count=646 Thread [10] count=647 Thread [10]
 * count=648 Thread [10] count=649 Thread [10] count=650 Thread [10] count=651
 * Thread [10] count=652 Thread [10] count=653 Thread [10] count=654 Thread [10]
 * count=655 Thread [10] count=656 Thread [10] count=657 Thread [10] count=658
 * Thread [10] count=659 Thread [10] count=660 Thread [10] count=661 Thread [10]
 * count=662 Thread [10] count=663 Thread [10] count=664 Thread [10] count=665
 * Thread [10] count=666 Thread [10] count=667 Thread [10] count=668 Thread [10]
 * count=669 Thread [10] count=670 Thread [10] count=671 Thread [10] count=672
 * Thread [10] count=673 Thread [10] count=674 Thread [10] count=675 Thread [10]
 * count=676 Thread [10] count=677 Thread [10] count=678 Thread [10] count=679
 * Thread [10] count=680 Thread [10] count=681 Thread [10] count=682 Thread [10]
 * count=683 Thread [10] count=684 Thread [10] count=685 Thread [10] count=686
 * Thread [10] count=687 Thread [10] count=688 Thread [10] count=689 Thread [10]
 * count=690 Thread [10] count=691 Thread [10] count=692 Thread [10] count=693
 * Thread [10] count=694 Thread [10] count=695 Thread [10] count=696 Thread [10]
 * count=697 Thread [10] count=698 Thread [10] count=699 Thread [10] count=700
 * Thread [10] count=701 Thread [10] count=702 Thread [10] count=703 Thread [10]
 * count=704 Thread [10] count=705 Thread [10] count=706 Thread [10] count=707
 * Thread [10] count=708 Thread [10] count=709 Thread [10] count=710 Thread [10]
 * count=711 Thread [10] count=712 Thread [10] count=713 Thread [10] count=714
 * Thread [10] count=715 Thread [10] count=716 Thread [10] count=717 Thread [10]
 * count=718 Thread [10] count=719 Thread [10] count=720 Thread [10] count=721
 * Thread [10] count=722 Thread [10] count=723 Thread [10] count=724 Thread [10]
 * count=725 Thread [10] count=726 Thread [10] count=727 Thread [10] count=728
 * Thread [10] count=729 Thread [10] count=730 Thread [10] count=731 Thread [10]
 * count=732 Thread [10] count=733 Thread [10] count=734 Thread [10] count=735
 * Thread [10] count=736 Thread [10] count=737 Thread [10] count=738 Thread [10]
 * count=739 Thread [10] count=740 Thread [10] count=741 Thread [11] count=742
 * Thread [11] count=743 Thread [11] count=744 Thread [11] count=745 Thread [11]
 * count=746 Thread [11] count=747 Thread [11] count=748 Thread [11] count=749
 * Thread [11] count=750 Thread [11] count=751 Thread [11] count=752 Thread [11]
 * count=753 Thread [11] count=754 Thread [11] count=755 Thread [11] count=756
 * Thread [11] count=757 Thread [11] count=758 Thread [11] count=759 Thread [11]
 * count=760 Thread [11] count=761 Thread [11] count=762 Thread [11] count=763
 * Thread [11] count=764 Thread [11] count=765 Thread [11] count=766 Thread [11]
 * count=767 Thread [11] count=768 Thread [11] count=769 Thread [11] count=770
 * Thread [11] count=771 Thread [11] count=772 Thread [11] count=773 Thread [11]
 * count=774 Thread [11] count=775 Thread [11] count=776 Thread [11] count=777
 * Thread [11] count=778 Thread [11] count=779 Thread [11] count=780 Thread [11]
 * count=781 Thread [11] count=782 Thread [11] count=783 Thread [11] count=784
 * Thread [11] count=785 Thread [11] count=786 Thread [11] count=787 Thread [11]
 * count=788 Thread [11] count=789 Thread [11] count=790 Thread [11] count=791
 * Thread [11] count=792 Thread [11] count=793 Thread [11] count=794 Thread [11]
 * count=795 Thread [11] count=796 Thread [11] count=797 Thread [11] count=798
 * Thread [11] count=799 Thread [11] count=800 Thread [11] count=801 Thread [12]
 * count=802 Thread [12] count=803 Thread [11] count=804 Thread [12] count=805
 * Thread [11] count=806 Thread [12] count=807 Thread [11] count=808 Thread [12]
 * count=809 Thread [13] count=810 Thread [13] count=811 Thread [13] count=812
 * Thread [13] count=813 Thread [13] count=814 Thread [13] count=815 Thread [13]
 * count=816 Thread [13] count=817 Thread [13] count=818 Thread [13] count=819
 * Thread [13] count=820 Thread [13] count=821 Thread [13] count=822 Thread [13]
 * count=823 Thread [13] count=824 Thread [13] count=825 Thread [13] count=826
 * Thread [13] count=827 Thread [13] count=828 Thread [13] count=829 Thread [13]
 * count=830 Thread [13] count=831 Thread [13] count=832 Thread [13] count=833
 * Thread [13] count=834 Thread [13] count=835 Thread [13] count=836 Thread [13]
 * count=837 Thread [13] count=838 Thread [13] count=839 Thread [13] count=840
 * Thread [13] count=841 Thread [13] count=842 Thread [13] count=843 Thread [13]
 * count=844 Thread [13] count=845 Thread [13] count=846 Thread [13] count=847
 * Thread [13] count=848 Thread [13] count=849 Thread [13] count=850 Thread [13]
 * count=851 Thread [13] count=852 Thread [13] count=853 Thread [13] count=854
 * Thread [13] count=855 Thread [13] count=856 Thread [13] count=857 Thread [13]
 * count=858 Thread [13] count=859 Thread [13] count=860 Thread [13] count=861
 * Thread [13] count=862 Thread [13] count=863 Thread [13] count=864 Thread [13]
 * count=865 Thread [13] count=866 Thread [13] count=867 Thread [13] count=868
 * Thread [13] count=869 Thread [13] count=870 Thread [13] count=871 Thread [13]
 * count=872 Thread [13] count=873 Thread [13] count=874 Thread [13] count=875
 * Thread [13] count=876 Thread [13] count=877 Thread [13] count=878 Thread [13]
 * count=879 Thread [13] count=880 Thread [13] count=881 Thread [13] count=882
 * Thread [13] count=883 Thread [13] count=884 Thread [13] count=885 Thread [13]
 * count=886 Thread [13] count=887 Thread [13] count=888 Thread [13] count=889
 * Thread [13] count=890 Thread [13] count=891 Thread [13] count=892 Thread [13]
 * count=893 Thread [13] count=894 Thread [13] count=895 Thread [13] count=896
 * Thread [13] count=897 Thread [13] count=898 Thread [13] count=899 Thread [13]
 * count=900 Thread [13] count=901 Thread [13] count=902 Thread [13] count=903
 * Thread [13] count=904 Thread [13] count=905 Thread [13] count=906 Thread [ 9]
 * count=907 Thread [ 9] count=908 Thread [ 9] count=909 Thread [ 9] count=910
 * Thread [ 9] count=911 Thread [ 9] count=912 Thread [ 9] count=913 Thread [ 9]
 * count=914 Thread [ 9] count=915 Thread [ 9] count=916 Thread [ 9] count=917
 * Thread [ 9] count=918 Thread [ 9] count=919 Thread [10] count=920 Thread [10]
 * count=921 Thread [10] count=922 Thread [10] count=923 Thread [10] count=924
 * Thread [10] count=925 Thread [10] count=926 Thread [10] count=927 Thread [10]
 * count=928 Thread [10] count=929 Thread [10] count=930 Thread [10] count=931
 * Thread [10] count=932 Thread [10] count=933 Thread [10] count=934 Thread [10]
 * count=935 Thread [10] count=936 Thread [10] count=937 Thread [10] count=938
 * Thread [10] count=939 Thread [10] count=940 Thread [10] count=941 Thread [10]
 * count=942 Thread [10] count=943 Thread [10] count=944 Thread [10] count=945
 * Thread [10] count=946 Thread [10] count=947 Thread [10] count=948 Thread [10]
 * count=949 Thread [10] count=950 Thread [10] count=951 Thread [10] count=952
 * Thread [11] count=953 Thread [11] count=954 Thread [12] count=955 Thread [11]
 * count=956 Thread [12] count=957 Thread [12] count=958 Thread [12] count=959
 * Thread [12] count=960 Thread [12] count=961 Thread [12] count=962 Thread [12]
 * count=963 Thread [12] count=964 Thread [12] count=965 Thread [12] count=966
 * Thread [12] count=967 Thread [13] count=968 Thread [13] count=969 Thread [13]
 * count=970 Thread [13] count=971 Thread [13] count=972 Thread [13] count=973
 * Thread [13] count=974 Thread [13] count=975 Thread [13] count=976 Thread [13]
 * count=977 Thread [13] count=978 Thread [13] count=979 Thread [13] count=980
 * Thread [ 9] count=981 Thread [ 9] count=982 Thread [ 9] count=983 Thread [ 9]
 * count=984 Thread [ 9] count=985 Thread [ 9] count=986 Thread [ 9] count=987
 * Thread [ 9] count=988 Thread [ 9] count=989 Thread [ 9] count=990 Thread [ 9]
 * count=991 Thread [ 9] count=992 Thread [ 9] count=993 Thread [ 9] count=994
 * Thread [ 9] count=995 Thread [ 9] count=996 Thread [ 9] count=997 Thread [ 9]
 * count=998 Thread [ 9] count=999 Thread [ 9] count=1000 Thread [ 9] count=1001
 * Thread [ 9] count=1002 Thread [ 9] count=1003 Thread [ 9] count=1004 Thread [
 * 9] count=1005 Thread [ 9] count=1006 Thread [ 9] count=1007 Thread [ 9]
 * count=1008 Thread [ 9] count=1009 Thread [ 9] count=1010 Thread [ 9]
 * count=1011 Thread [ 9] count=1012 Thread [ 9] count=1013 Thread [ 9]
 * count=1014 Thread [ 9] count=1015 Thread [ 9] count=1016 Thread [ 9]
 * count=1017 Thread [ 9] count=1018 Thread [ 9] count=1019 Thread [ 9]
 * count=1020 Thread [ 9] count=1021 Thread [10] count=1022 Thread [10]
 * count=1023 Thread [10] count=1024 Thread [10] count=1025 Thread [10]
 * count=1026 Thread [10] count=1027 Thread [10] count=1028 Thread [10]
 * count=1029 Thread [10] count=1030 Thread [10] count=1031 Thread [10]
 * count=1032 Thread [10] count=1033 Thread [10] count=1034 Thread [10]
 * count=1035 Thread [10] count=1036 Thread [10] count=1037 Thread [10]
 * count=1038 Thread [10] count=1039 Thread [10] count=1040 Thread [10]
 * count=1041 Thread [10] count=1042 Thread [10] count=1043 Thread [10]
 * count=1044 Thread [10] count=1045 Thread [10] count=1046 Thread [10]
 * count=1047 Thread [10] count=1048 Thread [10] count=1049 Thread [10]
 * count=1050 Thread [10] count=1051 Thread [10] count=1052 Thread [10]
 * count=1053 Thread [10] count=1054 Thread [10] count=1055 Thread [10]
 * count=1056 Thread [10] count=1057 Thread [10] count=1058 Thread [10]
 * count=1059 Thread [10] count=1060 Thread [10] count=1061 Thread [10]
 * count=1062 Thread [10] count=1063 Thread [10] count=1064 Thread [10]
 * count=1065 Thread [10] count=1066 Thread [11] count=1067 Thread [11]
 * count=1068 Thread [11] count=1069 Thread [11] count=1070 Thread [11]
 * count=1071 Thread [11] count=1072 Thread [11] count=1073 Thread [11]
 * count=1074 Thread [11] count=1075 Thread [11] count=1076 Thread [11]
 * count=1077 Thread [11] count=1078 Thread [11] count=1079 Thread [11]
 * count=1080 Thread [11] count=1081 Thread [11] count=1082 Thread [11]
 * count=1083 Thread [11] count=1084 Thread [11] count=1085 Thread [11]
 * count=1086 Thread [11] count=1087 Thread [11] count=1088 Thread [11]
 * count=1089 Thread [11] count=1090 Thread [11] count=1091 Thread [11]
 * count=1092 Thread [11] count=1093 Thread [11] count=1094 Thread [11]
 * count=1095 Thread [11] count=1096 Thread [11] count=1097 Thread [11]
 * count=1098 Thread [11] count=1099 Thread [11] count=1100 Thread [11]
 * count=1101 Thread [11] count=1102 Thread [11] count=1103 Thread [11]
 * count=1104 Thread [11] count=1105 Thread [11] count=1106 Thread [11]
 * count=1107 Thread [11] count=1108 Thread [11] count=1109 Thread [11]
 * count=1110 Thread [11] count=1111 Thread [11] count=1112 Thread [11]
 * count=1113 Thread [11] count=1114 Thread [11] count=1115 Thread [11]
 * count=1116 Thread [11] count=1117 Thread [11] count=1118 Thread [11]
 * count=1119 Thread [11] count=1120 Thread [11] count=1121 Thread [11]
 * count=1122 Thread [11] count=1123 Thread [11] count=1124 Thread [11]
 * count=1125 Thread [11] count=1126 Thread [11] count=1127 Thread [11]
 * count=1128 Thread [11] count=1129 Thread [11] count=1130 Thread [11]
 * count=1131 Thread [11] count=1132 Thread [11] count=1133 Thread [11]
 * count=1134 Thread [11] count=1135 Thread [11] count=1136 Thread [11]
 * count=1137 Thread [11] count=1138 Thread [11] count=1139 Thread [11]
 * count=1140 Thread [11] count=1141 Thread [11] count=1142 Thread [11]
 * count=1143 Thread [11] count=1144 Thread [12] count=1145 Thread [12]
 * count=1146 Thread [12] count=1147 Thread [12] count=1148 Thread [12]
 * count=1149 Thread [12] count=1150 Thread [12] count=1151 Thread [12]
 * count=1152 Thread [12] count=1153 Thread [12] count=1154 Thread [12]
 * count=1155 Thread [12] count=1156 Thread [12] count=1157 Thread [12]
 * count=1158 Thread [12] count=1159 Thread [12] count=1160 Thread [12]
 * count=1161 Thread [12] count=1162 Thread [12] count=1163 Thread [12]
 * count=1164 Thread [12] count=1165 Thread [12] count=1166 Thread [12]
 * count=1167 Thread [12] count=1168 Thread [12] count=1169 Thread [12]
 * count=1170 Thread [13] count=1171 Thread [13] count=1172 Thread [13]
 * count=1173 Thread [13] count=1174 Thread [13] count=1175 Thread [13]
 * count=1176 Thread [13] count=1177 Thread [13] count=1178 Thread [13]
 * count=1179 Thread [13] count=1180 Thread [13] count=1181 Thread [13]
 * count=1182 Thread [13] count=1183 Thread [13] count=1184 Thread [13]
 * count=1185 Thread [13] count=1186 Thread [13] count=1187 Thread [13]
 * count=1188 Thread [13] count=1189 Thread [13] count=1190 Thread [13]
 * count=1191 Thread [13] count=1192 Thread [13] count=1193 Thread [13]
 * count=1194 Thread [13] count=1195 Thread [13] count=1196 Thread [13]
 * count=1197 Thread [13] count=1198 Thread [13] count=1199 Thread [13]
 * count=1200 Thread [13] count=1201 Thread [13] count=1202 Thread [13]
 * count=1203 Thread [13] count=1204 Thread [13] count=1205 Thread [13]
 * count=1206 Thread [13] count=1207 Thread [13] count=1208 Thread [13]
 * count=1209 Thread [13] count=1210 Thread [13] count=1211 Thread [ 9]
 * count=1212 Thread [ 9] count=1213 Thread [ 9] count=1214 Thread [ 9]
 * count=1215 Thread [ 9] count=1216 Thread [ 9] count=1217 Thread [ 9]
 * count=1218 Thread [ 9] count=1219 Thread [ 9] count=1220 Thread [ 9]
 * count=1221 Thread [ 9] count=1222 Thread [ 9] count=1223 Thread [ 9]
 * count=1224 Thread [ 9] count=1225 Thread [ 9] count=1226 Thread [ 9]
 * count=1227 Thread [ 9] count=1228 Thread [ 9] count=1229 Thread [ 9]
 * count=1230 Thread [ 9] count=1231 Thread [ 9] count=1232 Thread [ 9]
 * count=1233 Thread [ 9] count=1234 Thread [ 9] count=1235 Thread [ 9]
 * count=1236 Thread [ 9] count=1237 Thread [ 9] count=1238 Thread [ 9]
 * count=1239 Thread [ 9] count=1240 Thread [ 9] count=1241 Thread [ 9]
 * count=1242 Thread [ 9] count=1243 Thread [ 9] count=1244 Thread [ 9]
 * count=1245 Thread [ 9] count=1246 Thread [ 9] count=1247 Thread [ 9]
 * count=1248 Thread [ 9] count=1249 Thread [ 9] count=1250 Thread [ 9]
 * count=1251 Thread [ 9] count=1252 Thread [ 9] count=1253 Thread [ 9]
 * count=1254 Thread [ 9] count=1255 Thread [ 9] count=1256 Thread [ 9]
 * count=1257 Thread [ 9] count=1258 Thread [ 9] count=1259 Thread [ 9]
 * count=1260 Thread [ 9] count=1261 Thread [ 9] count=1262 Thread [ 9]
 * count=1263 Thread [ 9] count=1264 Thread [ 9] count=1265 Thread [ 9]
 * count=1266 Thread [ 9] count=1267 Thread [ 9] count=1268 Thread [ 9]
 * count=1269 Thread [ 9] count=1270 Thread [ 9] count=1271 Thread [ 9]
 * count=1272 Thread [ 9] count=1273 Thread [ 9] count=1274 Thread [ 9]
 * count=1275 Thread [ 9] count=1276 Thread [ 9] count=1277 Thread [ 9]
 * count=1278 Thread [ 9] count=1279 Thread [ 9] count=1280 Thread [ 9]
 * count=1281 Thread [ 9] count=1282 Thread [ 9] count=1283 Thread [ 9]
 * count=1284 Thread [ 9] count=1285 Thread [ 9] count=1286 Thread [ 9]
 * count=1287 Thread [ 9] count=1288 Thread [ 9] count=1289 Thread [ 9]
 * count=1290 Thread [ 9] count=1291 Thread [ 9] count=1292 Thread [ 9]
 * count=1293 Thread [ 9] count=1294 Thread [ 9] count=1295 Thread [ 9]
 * count=1296 Thread [ 9] count=1297 Thread [ 9] count=1298 Thread [ 9]
 * count=1299 Thread [ 9] count=1300 Thread [ 9] count=1301 Thread [ 9]
 * count=1302 Thread [ 9] count=1303 Thread [ 9] count=1304 Thread [ 9]
 * count=1305 Thread [ 9] count=1306 Thread [ 9] count=1307 Thread [ 9]
 * count=1308 Thread [ 9] count=1309 Thread [ 9] count=1310 Thread [ 9]
 * count=1311 Thread [ 9] count=1312 Thread [ 9] count=1313 Thread [ 9]
 * count=1314 Thread [ 9] count=1315 Thread [ 9] count=1316 Thread [ 9]
 * count=1317 Thread [ 9] count=1318 Thread [ 9] count=1319 Thread [ 9]
 * count=1320 Thread [ 9] count=1321 Thread [ 9] count=1322 Thread [ 9]
 * count=1323 Thread [ 9] count=1324 Thread [ 9] count=1325 Thread [ 9]
 * count=1326 Thread [ 9] count=1327 Thread [ 9] count=1328 Thread [10]
 * count=1329 Thread [10] count=1330 Thread [10] count=1331 Thread [10]
 * count=1332 Thread [10] count=1333 Thread [10] count=1334 Thread [10]
 * count=1335 Thread [10] count=1336 Thread [10] count=1337 Thread [10]
 * count=1338 Thread [10] count=1339 Thread [10] count=1340 Thread [10]
 * count=1341 Thread [10] count=1342 Thread [10] count=1343 Thread [10]
 * count=1344 Thread [10] count=1345 Thread [10] count=1346 Thread [10]
 * count=1347 Thread [10] count=1348 Thread [10] count=1349 Thread [11]
 * count=1350 Thread [11] count=1351 Thread [11] count=1352 Thread [11]
 * count=1353 Thread [11] count=1354 Thread [11] count=1355 Thread [11]
 * count=1356 Thread [11] count=1357 Thread [11] count=1358 Thread [11]
 * count=1359 Thread [11] count=1360 Thread [11] count=1361 Thread [11]
 * count=1362 Thread [11] count=1363 Thread [11] count=1364 Thread [11]
 * count=1365 Thread [11] count=1366 Thread [11] count=1367 Thread [11]
 * count=1368 Thread [11] count=1369 Thread [11] count=1370 Thread [11]
 * count=1371 Thread [11] count=1372 Thread [11] count=1373 Thread [11]
 * count=1374 Thread [12] count=1375 Thread [12] count=1376 Thread [12]
 * count=1377 Thread [12] count=1378 Thread [12] count=1379 Thread [12]
 * count=1380 Thread [12] count=1381 Thread [12] count=1382 Thread [12]
 * count=1383 Thread [12] count=1384 Thread [12] count=1385 Thread [12]
 * count=1386 Thread [12] count=1387 Thread [12] count=1388 Thread [12]
 * count=1389 Thread [12] count=1390 Thread [12] count=1391 Thread [12]
 * count=1392 Thread [12] count=1393 Thread [12] count=1394 Thread [12]
 * count=1395 Thread [13] count=1396 Thread [13] count=1397 Thread [13]
 * count=1398 Thread [13] count=1399 Thread [13] count=1400 Thread [13]
 * count=1401 Thread [13] count=1402 Thread [ 9] count=1403 Thread [13]
 * count=1404 Thread [13] count=1405 Thread [13] count=1406 Thread [13]
 * count=1407 Thread [13] count=1408 Thread [13] count=1409 Thread [13]
 * count=1410 Thread [13] count=1411 Thread [13] count=1412 Thread [10]
 * count=1413 Thread [10] count=1414 Thread [10] count=1415 Thread [10]
 * count=1416 Thread [10] count=1417 Thread [10] count=1418 Thread [10]
 * count=1419 Thread [10] count=1420 Thread [10] count=1421 Thread [10]
 * count=1422 Thread [10] count=1423 Thread [10] count=1424 Thread [10]
 * count=1425 Thread [10] count=1426 Thread [10] count=1427 Thread [10]
 * count=1428 Thread [10] count=1429 Thread [10] count=1430 Thread [10]
 * count=1431 Thread [10] count=1432 Thread [10] count=1433 Thread [10]
 * count=1434 Thread [10] count=1435 Thread [10] count=1436 Thread [10]
 * count=1437 Thread [10] count=1438 Thread [10] count=1439 Thread [10]
 * count=1440 Thread [10] count=1441 Thread [10] count=1442 Thread [10]
 * count=1443 Thread [10] count=1444 Thread [10] count=1445 Thread [10]
 * count=1446 Thread [10] count=1447 Thread [10] count=1448 Thread [10]
 * count=1449 Thread [10] count=1450 Thread [10] count=1451 Thread [10]
 * count=1452 Thread [10] count=1453 Thread [10] count=1454 Thread [10]
 * count=1455 Thread [10] count=1456 Thread [10] count=1457 Thread [10]
 * count=1458 Thread [10] count=1459 Thread [10] count=1460 Thread [10]
 * count=1461 Thread [10] count=1462 Thread [10] count=1463 Thread [10]
 * count=1464 Thread [10] count=1465 Thread [10] count=1466 Thread [10]
 * count=1467 Thread [10] count=1468 Thread [10] count=1469 Thread [10]
 * count=1470 Thread [10] count=1471 Thread [10] count=1472 Thread [10]
 * count=1473 Thread [10] count=1474 Thread [10] count=1475 Thread [10]
 * count=1476 Thread [10] count=1477 Thread [10] count=1478 Thread [10]
 * count=1479 Thread [10] count=1480 Thread [10] count=1481 Thread [10]
 * count=1482 Thread [10] count=1483 Thread [10] count=1484 Thread [10]
 * count=1485 Thread [10] count=1486 Thread [10] count=1487 Thread [10]
 * count=1488 Thread [10] count=1489 Thread [10] count=1490 Thread [10]
 * count=1491 Thread [10] count=1492 Thread [10] count=1493 Thread [10]
 * count=1494 Thread [10] count=1495 Thread [10] count=1496 Thread [10]
 * count=1497 Thread [10] count=1498 Thread [10] count=1499 Thread [10]
 * count=1500 Thread [11] count=1501 Thread [11] count=1502 Thread [11]
 * count=1503 Thread [11] count=1504 Thread [11] count=1505 Thread [11]
 * count=1506 Thread [11] count=1507 Thread [11] count=1508 Thread [11]
 * count=1509 Thread [11] count=1510 Thread [11] count=1511 Thread [11]
 * count=1512 Thread [11] count=1513 Thread [11] count=1514 Thread [11]
 * count=1515 Thread [11] count=1516 Thread [11] count=1517 Thread [11]
 * count=1518 Thread [11] count=1519 Thread [11] count=1520 Thread [11]
 * count=1521 Thread [11] count=1522 Thread [11] count=1523 Thread [11]
 * count=1524 Thread [11] count=1525 Thread [11] count=1526 Thread [11]
 * count=1527 Thread [11] count=1528 Thread [11] count=1529 Thread [11]
 * count=1530 Thread [11] count=1531 Thread [11] count=1532 Thread [11]
 * count=1533 Thread [11] count=1534 Thread [12] count=1535 Thread [12]
 * count=1536 Thread [12] count=1537 Thread [12] count=1538 Thread [12]
 * count=1539 Thread [12] count=1540 Thread [12] count=1541 Thread [12]
 * count=1542 Thread [12] count=1543 Thread [12] count=1544 Thread [12]
 * count=1545 Thread [12] count=1546 Thread [12] count=1547 Thread [12]
 * count=1548 Thread [12] count=1549 Thread [12] count=1550 Thread [12]
 * count=1551 Thread [12] count=1552 Thread [12] count=1553 Thread [12]
 * count=1554 Thread [12] count=1555 Thread [12] count=1556 Thread [12]
 * count=1557 Thread [12] count=1558 Thread [ 9] count=1559 Thread [13]
 * count=1560 Thread [ 9] count=1561 Thread [13] count=1562 Thread [13]
 * count=1563 Thread [13] count=1564 Thread [13] count=1565 Thread [13]
 * count=1566 Thread [13] count=1567 Thread [13] count=1568 Thread [13]
 * count=1569 Thread [13] count=1570 Thread [13] count=1571 Thread [13]
 * count=1572 Thread [13] count=1573 Thread [13] count=1574 Thread [13]
 * count=1575 Thread [13] count=1576 Thread [13] count=1577 Thread [13]
 * count=1578 Thread [13] count=1579 Thread [13] count=1580 Thread [13]
 * count=1581 Thread [13] count=1582 Thread [13] count=1583 Thread [13]
 * count=1584 Thread [13] count=1585 Thread [13] count=1586 Thread [13]
 * count=1587 Thread [13] count=1588 Thread [13] count=1589 Thread [13]
 * count=1590 Thread [13] count=1591 Thread [13] count=1592 Thread [13]
 * count=1593 Thread [13] count=1594 Thread [13] count=1595 Thread [13]
 * count=1596 Thread [13] count=1597 Thread [13] count=1598 Thread [13]
 * count=1599 Thread [13] count=1600 Thread [13] count=1601 Thread [13]
 * count=1602 Thread [13] count=1603 Thread [13] count=1604 Thread [13]
 * count=1605 Thread [13] count=1606 Thread [13] count=1607 Thread [13]
 * count=1608 Thread [13] count=1609 Thread [13] count=1610 Thread [13]
 * count=1611 Thread [13] count=1612 Thread [13] count=1613 Thread [13]
 * count=1614 Thread [13] count=1615 Thread [13] count=1616 Thread [13]
 * count=1617 Thread [13] count=1618 Thread [13] count=1619 Thread [13]
 * count=1620 Thread [13] count=1621 Thread [13] count=1622 Thread [13]
 * count=1623 Thread [13] count=1624 Thread [13] count=1625 Thread [13]
 * count=1626 Thread [13] count=1627 Thread [13] count=1628 Thread [13]
 * count=1629 Thread [13] count=1630 Thread [13] count=1631 Thread [13]
 * count=1632 Thread [13] count=1633 Thread [13] count=1634 Thread [13]
 * count=1635 Thread [13] count=1636 Thread [13] count=1637 Thread [13]
 * count=1638 Thread [13] count=1639 Thread [13] count=1640 Thread [13]
 * count=1641 Thread [13] count=1642 Thread [13] count=1643 Thread [13]
 * count=1644 Thread [13] count=1645 Thread [13] count=1646 Thread [13]
 * count=1647 Thread [13] count=1648 Thread [13] count=1649 Thread [13]
 * count=1650 Thread [13] count=1651 Thread [13] count=1652 Thread [13]
 * count=1653 Thread [13] count=1654 Thread [13] count=1655 Thread [13]
 * count=1656 Thread [13] count=1657 Thread [13] count=1658 Thread [13]
 * count=1659 Thread [13] count=1660 Thread [13] count=1661 Thread [13]
 * count=1662 Thread [13] count=1663 Thread [13] count=1664 Thread [13]
 * count=1665 Thread [13] count=1666 Thread [13] count=1667 Thread [13]
 * count=1668 Thread [13] count=1669 Thread [13] count=1670 Thread [13]
 * count=1671 Thread [13] count=1672 Thread [13] count=1673 Thread [13]
 * count=1674 Thread [13] count=1675 Thread [13] count=1676 Thread [13]
 * count=1677 Thread [13] count=1678 Thread [13] count=1679 Thread [13]
 * count=1680 Thread [13] count=1681 Thread [13] count=1682 Thread [13]
 * count=1683 Thread [13] count=1684 Thread [13] count=1685 Thread [13]
 * count=1686 Thread [13] count=1687 Thread [13] count=1688 Thread [13]
 * count=1689 Thread [13] count=1690 Thread [13] count=1691 Thread [13]
 * count=1692 Thread [13] count=1693 Thread [13] count=1694 Thread [13]
 * count=1695 Thread [13] count=1696 Thread [13] count=1697 Thread [13]
 * count=1698 Thread [13] count=1699 Thread [13] count=1700 Thread [13]
 * count=1701 Thread [13] count=1702 Thread [13] count=1703 Thread [13]
 * count=1704 Thread [13] count=1705 Thread [13] count=1706 Thread [13]
 * count=1707 Thread [13] count=1708 Thread [13] count=1709 Thread [13]
 * count=1710 Thread [13] count=1711 Thread [13] count=1712 Thread [13]
 * count=1713 Thread [13] count=1714 Thread [13] count=1715 Thread [13]
 * count=1716 Thread [13] count=1717 Thread [13] count=1718 Thread [13]
 * count=1719 Thread [13] count=1720 Thread [13] count=1721 Thread [13]
 * count=1722 Thread [13] count=1723 Thread [13] count=1724 Thread [13]
 * count=1725 Thread [13] count=1726 Thread [13] count=1727 Thread [13]
 * count=1728 Thread [13] count=1729 Thread [13] count=1730 Thread [13]
 * count=1731 Thread [13] count=1732 Thread [13] count=1733 Thread [13]
 * count=1734 Thread [13] count=1735 Thread [13] count=1736 Thread [11]
 * count=1737 Thread [11] count=1738 Thread [11] count=1739 Thread [11]
 * count=1740 Thread [11] count=1741 Thread [11] count=1742 Thread [11]
 * count=1743 Thread [11] count=1744 Thread [11] count=1745 Thread [11]
 * count=1746 Thread [11] count=1747 Thread [11] count=1748 Thread [11]
 * count=1749 Thread [11] count=1750 Thread [11] count=1751 Thread [11]
 * count=1752 Thread [11] count=1753 Thread [11] count=1754 Thread [11]
 * count=1755 Thread [11] count=1756 Thread [11] count=1757 Thread [11]
 * count=1758 Thread [11] count=1759 Thread [11] count=1760 Thread [11]
 * count=1761 Thread [11] count=1762 Thread [11] count=1763 Thread [11]
 * count=1764 Thread [11] count=1765 Thread [11] count=1766 Thread [11]
 * count=1767 Thread [11] count=1768 Thread [11] count=1769 Thread [11]
 * count=1770 Thread [11] count=1771 Thread [11] count=1772 Thread [11]
 * count=1773 Thread [11] count=1774 Thread [11] count=1775 Thread [11]
 * count=1776 Thread [11] count=1777 Thread [11] count=1778 Thread [11]
 * count=1779 Thread [11] count=1780 Thread [11] count=1781 Thread [11]
 * count=1782 Thread [11] count=1783 Thread [11] count=1784 Thread [11]
 * count=1785 Thread [11] count=1786 Thread [11] count=1787 Thread [11]
 * count=1788 Thread [11] count=1789 Thread [11] count=1790 Thread [11]
 * count=1791 Thread [11] count=1792 Thread [11] count=1793 Thread [11]
 * count=1794 Thread [11] count=1795 Thread [11] count=1796 Thread [11]
 * count=1797 Thread [11] count=1798 Thread [11] count=1799 Thread [11]
 * count=1800 Thread [11] count=1801 Thread [11] count=1802 Thread [11]
 * count=1803 Thread [11] count=1804 Thread [11] count=1805 Thread [11]
 * count=1806 Thread [11] count=1807 Thread [11] count=1808 Thread [11]
 * count=1809 Thread [11] count=1810 Thread [11] count=1811 Thread [11]
 * count=1812 Thread [11] count=1813 Thread [11] count=1814 Thread [11]
 * count=1815 Thread [11] count=1816 Thread [11] count=1817 Thread [11]
 * count=1818 Thread [11] count=1819 Thread [11] count=1820 Thread [11]
 * count=1821 Thread [11] count=1822 Thread [11] count=1823 Thread [11]
 * count=1824 Thread [11] count=1825 Thread [11] count=1826 Thread [11]
 * count=1827 Thread [11] count=1828 Thread [11] count=1829 Thread [11]
 * count=1830 Thread [11] count=1831 Thread [11] count=1832 Thread [11]
 * count=1833 Thread [11] count=1834 Thread [11] count=1835 Thread [11]
 * count=1836 Thread [11] count=1837 Thread [11] count=1838 Thread [11]
 * count=1839 Thread [11] count=1840 Thread [11] count=1841 Thread [11]
 * count=1842 Thread [11] count=1843 Thread [11] count=1844 Thread [11]
 * count=1845 Thread [11] count=1846 Thread [11] count=1847 Thread [11]
 * count=1848 Thread [11] count=1849 Thread [11] count=1850 Thread [11]
 * count=1851 Thread [11] count=1852 Thread [11] count=1853 Thread [11]
 * count=1854 Thread [11] count=1855 Thread [11] count=1856 Thread [11]
 * count=1857 Thread [11] count=1858 Thread [11] count=1859 Thread [11]
 * count=1860 Thread [11] count=1861 Thread [11] count=1862 Thread [11]
 * count=1863 Thread [11] count=1864 Thread [11] count=1865 Thread [11]
 * count=1866 Thread [11] count=1867 Thread [11] count=1868 Thread [11]
 * count=1869 Thread [11] count=1870 Thread [11] count=1871 Thread [11]
 * count=1872 Thread [11] count=1873 Thread [11] count=1874 Thread [11]
 * count=1875 Thread [12] count=1876 Thread [12] count=1877 Thread [12]
 * count=1878 Thread [12] count=1879 Thread [12] count=1880 Thread [12]
 * count=1881 Thread [12] count=1882 Thread [12] count=1883 Thread [12]
 * count=1884 Thread [12] count=1885 Thread [12] count=1886 Thread [12]
 * count=1887 Thread [12] count=1888 Thread [12] count=1889 Thread [12]
 * count=1890 Thread [12] count=1891 Thread [12] count=1892 Thread [12]
 * count=1893 Thread [12] count=1894 Thread [12] count=1895 Thread [12]
 * count=1896 Thread [12] count=1897 Thread [12] count=1898 Thread [12]
 * count=1899 Thread [12] count=1900 Thread [12] count=1901 Thread [12]
 * count=1902 Thread [12] count=1903 Thread [12] count=1904 Thread [12]
 * count=1905 Thread [12] count=1906 Thread [12] count=1907 Thread [12]
 * count=1908 Thread [12] count=1909 Thread [12] count=1910 Thread [12]
 * count=1911 Thread [12] count=1912 Thread [12] count=1913 Thread [12]
 * count=1914 Thread [12] count=1915 Thread [12] count=1916 Thread [ 9]
 * count=1917 Thread [ 9] count=1918 Thread [ 9] count=1919 Thread [ 9]
 * count=1920 Thread [ 9] count=1921 Thread [ 9] count=1922 Thread [ 9]
 * count=1923 Thread [ 9] count=1924 Thread [ 9] count=1925 Thread [ 9]
 * count=1926 Thread [ 9] count=1927 Thread [ 9] count=1928 Thread [ 9]
 * count=1929 Thread [ 9] count=1930 Thread [ 9] count=1931 Thread [ 9]
 * count=1932 Thread [10] count=1933 Thread [10] count=1934 Thread [10]
 * count=1935 Thread [10] count=1936 Thread [10] count=1937 Thread [10]
 * count=1938 Thread [10] count=1939 Thread [10] count=1940 Thread [10]
 * count=1941 Thread [10] count=1942 Thread [10] count=1943 Thread [10]
 * count=1944 Thread [10] count=1945 Thread [10] count=1946 Thread [10]
 * count=1947 Thread [10] count=1948 Thread [10] count=1949 Thread [10]
 * count=1950 Thread [10] count=1951 Thread [10] count=1952 Thread [10]
 * count=1953 Thread [10] count=1954 Thread [10] count=1955 Thread [10]
 * count=1956 Thread [10] count=1957 Thread [10] count=1958 Thread [10]
 * count=1959 Thread [10] count=1960 Thread [10] count=1961 Thread [10]
 * count=1962 Thread [10] count=1963 Thread [10] count=1964 Thread [10]
 * count=1965 Thread [10] count=1966 Thread [10] count=1967 Thread [10]
 * count=1968 Thread [10] count=1969 Thread [10] count=1970 Thread [10]
 * count=1971 Thread [10] count=1972 Thread [10] count=1973 Thread [10]
 * count=1974 Thread [10] count=1975 Thread [10] count=1976 Thread [10]
 * count=1977 Thread [10] count=1978 Thread [10] count=1979 Thread [10]
 * count=1980 Thread [10] count=1981 Thread [10] count=1982 Thread [10]
 * count=1983 Thread [10] count=1984 Thread [10] count=1985 Thread [10]
 * count=1986 Thread [10] count=1987 Thread [10] count=1988 Thread [10]
 * count=1989 Thread [10] count=1990 Thread [10] count=1991 Thread [10]
 * count=1992 Thread [10] count=1993 Thread [10] count=1994 Thread [10]
 * count=1995 Thread [10] count=1996 Thread [10] count=1997 Thread [10]
 * count=1998 Thread [10] count=1999 Thread [10] count=2000 Thread [10]
 * count=2001 Thread [10] count=2002 Thread [10] count=2003 Thread [10]
 * count=2004 Thread [10] count=2005 Thread [10] count=2006 Thread [10]
 * count=2007 Thread [10] count=2008 Thread [10] count=2009 Thread [10]
 * count=2010 Thread [10] count=2011 Thread [10] count=2012 Thread [10]
 * count=2013 Thread [10] count=2014 Thread [10] count=2015 Thread [10]
 * count=2016 Thread [10] count=2017 Thread [10] count=2018 Thread [10]
 * count=2019 Thread [10] count=2020 Thread [10] count=2021 Thread [10]
 * count=2022 Thread [10] count=2023 Thread [10] count=2024 Thread [10]
 * count=2025 Thread [10] count=2026 Thread [10] count=2027 Thread [10]
 * count=2028 Thread [10] count=2029 Thread [10] count=2030 Thread [10]
 * count=2031 Thread [10] count=2032 Thread [10] count=2033 Thread [10]
 * count=2034 Thread [10] count=2035 Thread [10] count=2036 Thread [10]
 * count=2037 Thread [10] count=2038 Thread [10] count=2039 Thread [10]
 * count=2040 Thread [10] count=2041 Thread [10] count=2042 Thread [10]
 * count=2043 Thread [10] count=2044 Thread [10] count=2045 Thread [10]
 * count=2046 Thread [10] count=2047 Thread [10] count=2048 Thread [10]
 * count=2049 Thread [10] count=2050 Thread [10] count=2051 Thread [10]
 * count=2052 Thread [10] count=2053 Thread [10] count=2054 Thread [10]
 * count=2055 Thread [10] count=2056 Thread [10] count=2057 Thread [10]
 * count=2058 Thread [10] count=2059 Thread [10] count=2060 Thread [10]
 * count=2061 Thread [10] count=2062 Thread [10] count=2063 Thread [10]
 * count=2064 Thread [10] count=2065 Thread [10] count=2066 Thread [10]
 * count=2067 Thread [10] count=2068 Thread [10] count=2069 Thread [10]
 * count=2070 Thread [10] count=2071 Thread [10] count=2072 Thread [10]
 * count=2073 Thread [10] count=2074 Thread [10] count=2075 Thread [10]
 * count=2076 Thread [10] count=2077 Thread [10] count=2078 Thread [10]
 * count=2079 Thread [10] count=2080 Thread [10] count=2081 Thread [10]
 * count=2082 Thread [10] count=2083 Thread [10] count=2084 Thread [10]
 * count=2085 Thread [10] count=2086 Thread [10] count=2087 Thread [10]
 * count=2088 Thread [10] count=2089 Thread [10] count=2090 Thread [10]
 * count=2091 Thread [10] count=2092 Thread [10] count=2093 Thread [10]
 * count=2094 Thread [10] count=2095 Thread [10] count=2096 Thread [10]
 * count=2097 Thread [10] count=2098 Thread [10] count=2099 Thread [10]
 * count=2100 Thread [10] count=2101 Thread [10] count=2102 Thread [10]
 * count=2103 Thread [10] count=2104 Thread [10] count=2105 Thread [10]
 * count=2106 Thread [10] count=2107 Thread [10] count=2108 Thread [10]
 * count=2109 Thread [10] count=2110 Thread [10] count=2111 Thread [10]
 * count=2112 Thread [10] count=2113 Thread [10] count=2114 Thread [10]
 * count=2115 Thread [10] count=2116 Thread [10] count=2117 Thread [10]
 * count=2118 Thread [10] count=2119 Thread [10] count=2120 Thread [10]
 * count=2121 Thread [10] count=2122 Thread [10] count=2123 Thread [10]
 * count=2124 Thread [10] count=2125 Thread [10] count=2126 Thread [10]
 * count=2127 Thread [10] count=2128 Thread [10] count=2129 Thread [10]
 * count=2130 Thread [10] count=2131 Thread [10] count=2132 Thread [10]
 * count=2133 Thread [10] count=2134 Thread [10] count=2135 Thread [10]
 * count=2136 Thread [10] count=2137 Thread [10] count=2138 Thread [10]
 * count=2139 Thread [10] count=2140 Thread [10] count=2141 Thread [10]
 * count=2142 Thread [10] count=2143 Thread [10] count=2144 Thread [10]
 * count=2145 Thread [10] count=2146 Thread [10] count=2147 Thread [10]
 * count=2148 Thread [10] count=2149 Thread [10] count=2150 Thread [10]
 * count=2151 Thread [10] count=2152 Thread [10] count=2153 Thread [10]
 * count=2154 Thread [10] count=2155 Thread [10] count=2156 Thread [10]
 * count=2157 Thread [10] count=2158 Thread [10] count=2159 Thread [10]
 * count=2160 Thread [10] count=2161 Thread [10] count=2162 Thread [10]
 * count=2163 Thread [10] count=2164 Thread [10] count=2165 Thread [10]
 * count=2166 Thread [10] count=2167 Thread [10] count=2168 Thread [10]
 * count=2169 Thread [10] count=2170 Thread [10] count=2171 Thread [10]
 * count=2172 Thread [10] count=2173 Thread [10] count=2174 Thread [10]
 * count=2175 Thread [10] count=2176 Thread [10] count=2177 Thread [10]
 * count=2178 Thread [10] count=2179 Thread [10] count=2180 Thread [10]
 * count=2181 Thread [10] count=2182 Thread [10] count=2183 Thread [10]
 * count=2184 Thread [10] count=2185 Thread [10] count=2186 Thread [10]
 * count=2187 Thread [10] count=2188 Thread [10] count=2189 Thread [10]
 * count=2190 Thread [10] count=2191 Thread [10] count=2192 Thread [10]
 * count=2193 Thread [10] count=2194 Thread [10] count=2195 Thread [10]
 * count=2196 Thread [10] count=2197 Thread [10] count=2198 Thread [10]
 * count=2199 Thread [10] count=2200 Thread [10] count=2201 Thread [10]
 * count=2202 Thread [10] count=2203 Thread [10] count=2204 Thread [10]
 * count=2205 Thread [10] count=2206 Thread [10] count=2207 Thread [10]
 * count=2208 Thread [10] count=2209 Thread [10] count=2210 Thread [10]
 * count=2211 Thread [10] count=2212 Thread [10] count=2213 Thread [10]
 * count=2214 Thread [10] count=2215 Thread [10] count=2216 Thread [10]
 * count=2217 Thread [10] count=2218 Thread [10] count=2219 Thread [10]
 * count=2220 Thread [10] count=2221 Thread [10] count=2222 Thread [10]
 * count=2223 Thread [10] count=2224 Thread [10] count=2225 Thread [10]
 * count=2226 Thread [10] count=2227 Thread [10] count=2228 Thread [10]
 * count=2229 Thread [10] count=2230 Thread [10] count=2231 Thread [10]
 * count=2232 Thread [10] count=2233 Thread [10] count=2234 Thread [10]
 * count=2235 Thread [10] count=2236 Thread [13] count=2237 Thread [13]
 * count=2238 Thread [13] count=2239 Thread [11] count=2240 Thread [11]
 * count=2241 Thread [11] count=2242 Thread [11] count=2243 Thread [11]
 * count=2244 Thread [11] count=2245 Thread [11] count=2246 Thread [11]
 * count=2247 Thread [11] count=2248 Thread [11] count=2249 Thread [11]
 * count=2250 Thread [11] count=2251 Thread [11] count=2252 Thread [11]
 * count=2253 Thread [11] count=2254 Thread [11] count=2255 Thread [11]
 * count=2256 Thread [11] count=2257 Thread [11] count=2258 Thread [11]
 * count=2259 Thread [11] count=2260 Thread [11] count=2261 Thread [11]
 * count=2262 Thread [11] count=2263 Thread [11] count=2264 Thread [11]
 * count=2265 Thread [11] count=2266 Thread [11] count=2267 Thread [11]
 * count=2268 Thread [11] count=2269 Thread [11] count=2270 Thread [11]
 * count=2271 Thread [11] count=2272 Thread [11] count=2273 Thread [11]
 * count=2274 Thread [11] count=2275 Thread [11] count=2276 Thread [11]
 * count=2277 Thread [11] count=2278 Thread [11] count=2279 Thread [12]
 * count=2280 Thread [12] count=2281 Thread [12] count=2282 Thread [12]
 * count=2283 Thread [12] count=2284 Thread [12] count=2285 Thread [12]
 * count=2286 Thread [12] count=2287 Thread [12] count=2288 Thread [12]
 * count=2289 Thread [12] count=2290 Thread [12] count=2291 Thread [12]
 * count=2292 Thread [12] count=2293 Thread [12] count=2294 Thread [12]
 * count=2295 Thread [12] count=2296 Thread [12] count=2297 Thread [12]
 * count=2298 Thread [12] count=2299 Thread [12] count=2300 Thread [12]
 * count=2301 Thread [12] count=2302 Thread [12] count=2303 Thread [12]
 * count=2304 Thread [12] count=2305 Thread [12] count=2306 Thread [12]
 * count=2307 Thread [12] count=2308 Thread [12] count=2309 Thread [12]
 * count=2310 Thread [12] count=2311 Thread [ 9] count=2312 Thread [ 9]
 * count=2313 Thread [ 9] count=2314 Thread [ 9] count=2315 Thread [10]
 * count=2316 Thread [10] count=2317 Thread [10] count=2318 Thread [13]
 * count=2319 Thread [13] count=2320 Thread [13] count=2321 Thread [13]
 * count=2322 Thread [13] count=2323 Thread [13] count=2324 Thread [13]
 * count=2325 Thread [13] count=2326 Thread [13] count=2327 Thread [13]
 * count=2328 Thread [13] count=2329 Thread [13] count=2330 Thread [13]
 * count=2331 Thread [13] count=2332 Thread [13] count=2333 Thread [13]
 * count=2334 Thread [13] count=2335 Thread [13] count=2336 Thread [13]
 * count=2337 Thread [13] count=2338 Thread [13] count=2339 Thread [13]
 * count=2340 Thread [13] count=2341 Thread [13] count=2342 Thread [13]
 * count=2343 Thread [13] count=2344 Thread [13] count=2345 Thread [13]
 * count=2346 Thread [13] count=2347 Thread [13] count=2348 Thread [13]
 * count=2349 Thread [13] count=2350 Thread [13] count=2351 Thread [13]
 * count=2352 Thread [13] count=2353 Thread [13] count=2354 Thread [13]
 * count=2355 Thread [13] count=2356 Thread [13] count=2357 Thread [13]
 * count=2358 Thread [13] count=2359 Thread [13] count=2360 Thread [13]
 * count=2361 Thread [13] count=2362 Thread [13] count=2363 Thread [13]
 * count=2364 Thread [13] count=2365 Thread [13] count=2366 Thread [13]
 * count=2367 Thread [13] count=2368 Thread [13] count=2369 Thread [13]
 * count=2370 Thread [13] count=2371 Thread [13] count=2372 Thread [13]
 * count=2373 Thread [13] count=2374 Thread [13] count=2375 Thread [13]
 * count=2376 Thread [13] count=2377 Thread [13] count=2378 Thread [13]
 * count=2379 Thread [13] count=2380 Thread [13] count=2381 Thread [13]
 * count=2382 Thread [13] count=2383 Thread [13] count=2384 Thread [13]
 * count=2385 Thread [13] count=2386 Thread [13] count=2387 Thread [13]
 * count=2388 Thread [13] count=2389 Thread [13] count=2390 Thread [13]
 * count=2391 Thread [13] count=2392 Thread [13] count=2393 Thread [13]
 * count=2394 Thread [13] count=2395 Thread [13] count=2396 Thread [13]
 * count=2397 Thread [13] count=2398 Thread [13] count=2399 Thread [13]
 * count=2400 Thread [13] count=2401 Thread [13] count=2402 Thread [13]
 * count=2403 Thread [13] count=2404 Thread [13] count=2405 Thread [13]
 * count=2406 Thread [13] count=2407 Thread [13] count=2408 Thread [13]
 * count=2409 Thread [13] count=2410 Thread [13] count=2411 Thread [13]
 * count=2412 Thread [13] count=2413 Thread [13] count=2414 Thread [13]
 * count=2415 Thread [13] count=2416 Thread [13] count=2417 Thread [13]
 * count=2418 Thread [13] count=2419 Thread [13] count=2420 Thread [13]
 * count=2421 Thread [13] count=2422 Thread [13] count=2423 Thread [13]
 * count=2424 Thread [13] count=2425 Thread [13] count=2426 Thread [13]
 * count=2427 Thread [13] count=2428 Thread [13] count=2429 Thread [13]
 * count=2430 Thread [13] count=2431 Thread [13] count=2432 Thread [13]
 * count=2433 Thread [13] count=2434 Thread [13] count=2435 Thread [13]
 * count=2436 Thread [13] count=2437 Thread [13] count=2438 Thread [13]
 * count=2439 Thread [13] count=2440 Thread [13] count=2441 Thread [13]
 * count=2442 Thread [13] count=2443 Thread [13] count=2444 Thread [13]
 * count=2445 Thread [13] count=2446 Thread [13] count=2447 Thread [13]
 * count=2448 Thread [13] count=2449 Thread [13] count=2450 Thread [13]
 * count=2451 Thread [13] count=2452 Thread [13] count=2453 Thread [13]
 * count=2454 Thread [13] count=2455 Thread [13] count=2456 Thread [13]
 * count=2457 Thread [13] count=2458 Thread [13] count=2459 Thread [13]
 * count=2460 Thread [13] count=2461 Thread [13] count=2462 Thread [13]
 * count=2463 Thread [13] count=2464 Thread [13] count=2465 Thread [13]
 * count=2466 Thread [13] count=2467 Thread [13] count=2468 Thread [13]
 * count=2469 Thread [13] count=2470 Thread [13] count=2471 Thread [13]
 * count=2472 Thread [13] count=2473 Thread [13] count=2474 Thread [13]
 * count=2475 Thread [13] count=2476 Thread [13] count=2477 Thread [13]
 * count=2478 Thread [13] count=2479 Thread [13] count=2480 Thread [13]
 * count=2481 Thread [13] count=2482 Thread [13] count=2483 Thread [13]
 * count=2484 Thread [13] count=2485 Thread [13] count=2486 Thread [13]
 * count=2487 Thread [13] count=2488 Thread [13] count=2489 Thread [13]
 * count=2490 Thread [13] count=2491 Thread [13] count=2492 Thread [13]
 * count=2493 Thread [13] count=2494 Thread [13] count=2495 Thread [13]
 * count=2496 Thread [13] count=2497 Thread [13] count=2498 Thread [13]
 * count=2499 Thread [13] count=2500 Thread [13] count=2501 Thread [13]
 * count=2502 Thread [13] count=2503 Thread [13] count=2504 Thread [13]
 * count=2505 Thread [13] count=2506 Thread [13] count=2507 Thread [13]
 * count=2508 Thread [13] count=2509 Thread [13] count=2510 Thread [13]
 * count=2511 Thread [13] count=2512 Thread [13] count=2513 Thread [13]
 * count=2514 Thread [13] count=2515 Thread [13] count=2516 Thread [13]
 * count=2517 Thread [13] count=2518 Thread [13] count=2519 Thread [13]
 * count=2520 Thread [13] count=2521 Thread [13] count=2522 Thread [13]
 * count=2523 Thread [13] count=2524 Thread [13] count=2525 Thread [13]
 * count=2526 Thread [13] count=2527 Thread [13] count=2528 Thread [13]
 * count=2529 Thread [13] count=2530 Thread [13] count=2531 Thread [13]
 * count=2532 Thread [13] count=2533 Thread [13] count=2534 Thread [13]
 * count=2535 Thread [13] count=2536 Thread [13] count=2537 Thread [13]
 * count=2538 Thread [13] count=2539 Thread [13] count=2540 Thread [13]
 * count=2541 Thread [13] count=2542 Thread [13] count=2543 Thread [13]
 * count=2544 Thread [13] count=2545 Thread [13] count=2546 Thread [13]
 * count=2547 Thread [13] count=2548 Thread [13] count=2549 Thread [13]
 * count=2550 Thread [13] count=2551 Thread [13] count=2552 Thread [13]
 * count=2553 Thread [13] count=2554 Thread [13] count=2555 Thread [13]
 * count=2556 Thread [13] count=2557 Thread [13] count=2558 Thread [13]
 * count=2559 Thread [13] count=2560 Thread [13] count=2561 Thread [13]
 * count=2562 Thread [13] count=2563 Thread [13] count=2564 Thread [13]
 * count=2565 Thread [13] count=2566 Thread [13] count=2567 Thread [13]
 * count=2568 Thread [11] count=2569 Thread [11] count=2570 Thread [11]
 * count=2571 Thread [11] count=2572 Thread [11] count=2573 Thread [11]
 * count=2574 Thread [11] count=2575 Thread [11] count=2576 Thread [11]
 * count=2577 Thread [11] count=2578 Thread [11] count=2579 Thread [11]
 * count=2580 Thread [11] count=2581 Thread [11] count=2582 Thread [11]
 * count=2583 Thread [11] count=2584 Thread [11] count=2585 Thread [11]
 * count=2586 Thread [11] count=2587 Thread [11] count=2588 Thread [11]
 * count=2589 Thread [11] count=2590 Thread [11] count=2591 Thread [11]
 * count=2592 Thread [11] count=2593 Thread [11] count=2594 Thread [11]
 * count=2595 Thread [11] count=2596 Thread [11] count=2597 Thread [11]
 * count=2598 Thread [11] count=2599 Thread [11] count=2600 Thread [11]
 * count=2601 Thread [11] count=2602 Thread [11] count=2603 Thread [11]
 * count=2604 Thread [11] count=2605 Thread [11] count=2606 Thread [11]
 * count=2607 Thread [11] count=2608 Thread [11] count=2609 Thread [11]
 * count=2610 Thread [11] count=2611 Thread [11] count=2612 Thread [11]
 * count=2613 Thread [11] count=2614 Thread [11] count=2615 Thread [11]
 * count=2616 Thread [11] count=2617 Thread [11] count=2618 Thread [11]
 * count=2619 Thread [11] count=2620 Thread [11] count=2621 Thread [11]
 * count=2622 Thread [11] count=2623 Thread [11] count=2624 Thread [11]
 * count=2625 Thread [11] count=2626 Thread [11] count=2627 Thread [11]
 * count=2628 Thread [11] count=2629 Thread [11] count=2630 Thread [11]
 * count=2631 Thread [11] count=2632 Thread [11] count=2633 Thread [11]
 * count=2634 Thread [11] count=2635 Thread [11] count=2636 Thread [11]
 * count=2637 Thread [11] count=2638 Thread [11] count=2639 Thread [11]
 * count=2640 Thread [11] count=2641 Thread [11] count=2642 Thread [11]
 * count=2643 Thread [11] count=2644 Thread [11] count=2645 Thread [11]
 * count=2646 Thread [11] count=2647 Thread [11] count=2648 Thread [11]
 * count=2649 Thread [11] count=2650 Thread [11] count=2651 Thread [11]
 * count=2652 Thread [11] count=2653 Thread [11] count=2654 Thread [11]
 * count=2655 Thread [11] count=2656 Thread [11] count=2657 Thread [11]
 * count=2658 Thread [11] count=2659 Thread [11] count=2660 Thread [11]
 * count=2661 Thread [11] count=2662 Thread [11] count=2663 Thread [11]
 * count=2664 Thread [11] count=2665 Thread [11] count=2666 Thread [11]
 * count=2667 Thread [11] count=2668 Thread [11] count=2669 Thread [11]
 * count=2670 Thread [11] count=2671 Thread [11] count=2672 Thread [11]
 * count=2673 Thread [11] count=2674 Thread [11] count=2675 Thread [11]
 * count=2676 Thread [11] count=2677 Thread [11] count=2678 Thread [11]
 * count=2679 Thread [11] count=2680 Thread [11] count=2681 Thread [11]
 * count=2682 Thread [11] count=2683 Thread [11] count=2684 Thread [11]
 * count=2685 Thread [11] count=2686 Thread [11] count=2687 Thread [11]
 * count=2688 Thread [11] count=2689 Thread [11] count=2690 Thread [11]
 * count=2691 Thread [11] count=2692 Thread [11] count=2693 Thread [11]
 * count=2694 Thread [11] count=2695 Thread [11] count=2696 Thread [11]
 * count=2697 Thread [11] count=2698 Thread [11] count=2699 Thread [11]
 * count=2700 Thread [11] count=2701 Thread [11] count=2702 Thread [11]
 * count=2703 Thread [11] count=2704 Thread [11] count=2705 Thread [11]
 * count=2706 Thread [11] count=2707 Thread [11] count=2708 Thread [11]
 * count=2709 Thread [11] count=2710 Thread [11] count=2711 Thread [11]
 * count=2712 Thread [11] count=2713 Thread [11] count=2714 Thread [11]
 * count=2715 Thread [11] count=2716 Thread [11] count=2717 Thread [11]
 * count=2718 Thread [11] count=2719 Thread [11] count=2720 Thread [11]
 * count=2721 Thread [11] count=2722 Thread [11] count=2723 Thread [11]
 * count=2724 Thread [11] count=2725 Thread [11] count=2726 Thread [11]
 * count=2727 Thread [11] count=2728 Thread [11] count=2729 Thread [11]
 * count=2730 Thread [11] count=2731 Thread [11] count=2732 Thread [11]
 * count=2733 Thread [11] count=2734 Thread [11] count=2735 Thread [11]
 * count=2736 Thread [11] count=2737 Thread [11] count=2738 Thread [11]
 * count=2739 Thread [11] count=2740 Thread [11] count=2741 Thread [11]
 * count=2742 Thread [11] count=2743 Thread [11] count=2744 Thread [11]
 * count=2745 Thread [11] count=2746 Thread [11] count=2747 Thread [11]
 * count=2748 Thread [11] count=2749 Thread [11] count=2750 Thread [11]
 * count=2751 Thread [11] count=2752 Thread [12] count=2753 Thread [12]
 * count=2754 Thread [12] count=2755 Thread [12] count=2756 Thread [12]
 * count=2757 Thread [ 9] count=2758 Thread [ 9] count=2759 Thread [ 9]
 * count=2760 Thread [ 9] count=2761 Thread [ 9] count=2762 Thread [ 9]
 * count=2763 Thread [ 9] count=2764 Thread [ 9] count=2765 Thread [ 9]
 * count=2766 Thread [ 9] count=2767 Thread [ 9] count=2768 Thread [ 9]
 * count=2769 Thread [ 9] count=2770 Thread [ 9] count=2771 Thread [ 9]
 * count=2772 Thread [ 9] count=2773 Thread [ 9] count=2774 Thread [ 9]
 * count=2775 Thread [ 9] count=2776 Thread [ 9] count=2777 Thread [ 9]
 * count=2778 Thread [ 9] count=2779 Thread [ 9] count=2780 Thread [ 9]
 * count=2781 Thread [ 9] count=2782 Thread [ 9] count=2783 Thread [ 9]
 * count=2784 Thread [ 9] count=2785 Thread [ 9] count=2786 Thread [ 9]
 * count=2787 Thread [ 9] count=2788 Thread [ 9] count=2789 Thread [ 9]
 * count=2790 Thread [ 9] count=2791 Thread [ 9] count=2792 Thread [ 9]
 * count=2793 Thread [ 9] count=2794 Thread [ 9] count=2795 Thread [ 9]
 * count=2796 Thread [ 9] count=2797 Thread [ 9] count=2798 Thread [ 9]
 * count=2799 Thread [ 9] count=2800 Thread [ 9] count=2801 Thread [ 9]
 * count=2802 Thread [ 9] count=2803 Thread [ 9] count=2804 Thread [ 9]
 * count=2805 Thread [ 9] count=2806 Thread [ 9] count=2807 Thread [ 9]
 * count=2808 Thread [ 9] count=2809 Thread [ 9] count=2810 Thread [ 9]
 * count=2811 Thread [ 9] count=2812 Thread [ 9] count=2813 Thread [ 9]
 * count=2814 Thread [ 9] count=2815 Thread [ 9] count=2816 Thread [ 9]
 * count=2817 Thread [ 9] count=2818 Thread [ 9] count=2819 Thread [ 9]
 * count=2820 Thread [ 9] count=2821 Thread [ 9] count=2822 Thread [ 9]
 * count=2823 Thread [ 9] count=2824 Thread [ 9] count=2825 Thread [ 9]
 * count=2826 Thread [ 9] count=2827 Thread [ 9] count=2828 Thread [ 9]
 * count=2829 Thread [ 9] count=2830 Thread [ 9] count=2831 Thread [ 9]
 * count=2832 Thread [ 9] count=2833 Thread [ 9] count=2834 Thread [ 9]
 * count=2835 Thread [ 9] count=2836 Thread [ 9] count=2837 Thread [ 9]
 * count=2838 Thread [ 9] count=2839 Thread [ 9] count=2840 Thread [ 9]
 * count=2841 Thread [ 9] count=2842 Thread [ 9] count=2843 Thread [ 9]
 * count=2844 Thread [ 9] count=2845 Thread [ 9] count=2846 Thread [ 9]
 * count=2847 Thread [ 9] count=2848 Thread [ 9] count=2849 Thread [ 9]
 * count=2850 Thread [ 9] count=2851 Thread [ 9] count=2852 Thread [ 9]
 * count=2853 Thread [ 9] count=2854 Thread [ 9] count=2855 Thread [ 9]
 * count=2856 Thread [ 9] count=2857 Thread [ 9] count=2858 Thread [ 9]
 * count=2859 Thread [ 9] count=2860 Thread [ 9] count=2861 Thread [ 9]
 * count=2862 Thread [ 9] count=2863 Thread [ 9] count=2864 Thread [ 9]
 * count=2865 Thread [ 9] count=2866 Thread [ 9] count=2867 Thread [ 9]
 * count=2868 Thread [ 9] count=2869 Thread [ 9] count=2870 Thread [ 9]
 * count=2871 Thread [ 9] count=2872 Thread [ 9] count=2873 Thread [ 9]
 * count=2874 Thread [ 9] count=2875 Thread [ 9] count=2876 Thread [ 9]
 * count=2877 Thread [ 9] count=2878 Thread [ 9] count=2879 Thread [ 9]
 * count=2880 Thread [ 9] count=2881 Thread [ 9] count=2882 Thread [ 9]
 * count=2883 Thread [ 9] count=2884 Thread [ 9] count=2885 Thread [ 9]
 * count=2886 Thread [ 9] count=2887 Thread [ 9] count=2888 Thread [ 9]
 * count=2889 Thread [ 9] count=2890 Thread [ 9] count=2891 Thread [ 9]
 * count=2892 Thread [ 9] count=2893 Thread [ 9] count=2894 Thread [ 9]
 * count=2895 Thread [ 9] count=2896 Thread [ 9] count=2897 Thread [ 9]
 * count=2898 Thread [ 9] count=2899 Thread [ 9] count=2900 Thread [ 9]
 * count=2901 Thread [ 9] count=2902 Thread [ 9] count=2903 Thread [ 9]
 * count=2904 Thread [ 9] count=2905 Thread [13] count=2906 Thread [13]
 * count=2907 Thread [13] count=2908 Thread [13] count=2909 Thread [13]
 * count=2910 Thread [13] count=2911 Thread [13] count=2912 Thread [13]
 * count=2913 Thread [13] count=2914 Thread [13] count=2915 Thread [13]
 * count=2916 Thread [13] count=2917 Thread [13] count=2918 Thread [13]
 * count=2919 Thread [13] count=2920 Thread [13] count=2921 Thread [13]
 * count=2922 Thread [13] count=2923 Thread [13] count=2924 Thread [13]
 * count=2925 Thread [13] count=2926 Thread [13] count=2927 Thread [13]
 * count=2928 Thread [13] count=2929 Thread [13] count=2930 Thread [13]
 * count=2931 Thread [13] count=2932 Thread [13] count=2933 Thread [13]
 * count=2934 Thread [13] count=2935 Thread [13] count=2936 Thread [13]
 * count=2937 Thread [13] count=2938 Thread [13] count=2939 Thread [13]
 * count=2940 Thread [13] count=2941 Thread [13] count=2942 Thread [13]
 * count=2943 Thread [13] count=2944 Thread [13] count=2945 Thread [13]
 * count=2946 Thread [13] count=2947 Thread [13] count=2948 Thread [13]
 * count=2949 Thread [13] count=2950 Thread [13] count=2951 Thread [13]
 * count=2952 Thread [13] count=2953 Thread [13] count=2954 Thread [13]
 * count=2955 Thread [13] count=2956 Thread [13] count=2957 Thread [13]
 * count=2958 Thread [13] count=2959 Thread [13] count=2960 Thread [13]
 * count=2961 Thread [13] count=2962 Thread [13] count=2963 Thread [13]
 * count=2964 Thread [13] count=2965 Thread [13] count=2966 Thread [13]
 * count=2967 Thread [13] count=2968 Thread [13] count=2969 Thread [13]
 * count=2970 Thread [13] count=2971 Thread [13] count=2972 Thread [13]
 * count=2973 Thread [13] count=2974 Thread [13] count=2975 Thread [13]
 * count=2976 Thread [13] count=2977 Thread [13] count=2978 Thread [13]
 * count=2979 Thread [13] count=2980 Thread [13] count=2981 Thread [13]
 * count=2982 Thread [13] count=2983 Thread [13] count=2984 Thread [13]
 * count=2985 Thread [13] count=2986 Thread [13] count=2987 Thread [13]
 * count=2988 Thread [13] count=2989 Thread [13] count=2990 Thread [13]
 * count=2991 Thread [13] count=2992 Thread [13] count=2993 Thread [13]
 * count=2994 Thread [13] count=2995 Thread [13] count=2996 Thread [13]
 * count=2997 Thread [13] count=2998 Thread [13] count=2999 Thread [13]
 * count=3000 Thread [13] count=3001 Thread [13] count=3002 Thread [13]
 * count=3003 Thread [13] count=3004 Thread [13] count=3005 Thread [13]
 * count=3006 Thread [13] count=3007 Thread [13] count=3008 Thread [13]
 * count=3009 Thread [13] count=3010 Thread [13] count=3011 Thread [13]
 * count=3012 Thread [13] count=3013 Thread [13] count=3014 Thread [13]
 * count=3015 Thread [13] count=3016 Thread [13] count=3017 Thread [13]
 * count=3018 Thread [13] count=3019 Thread [13] count=3020 Thread [13]
 * count=3021 Thread [13] count=3022 Thread [13] count=3023 Thread [13]
 * count=3024 Thread [13] count=3025 Thread [13] count=3026 Thread [13]
 * count=3027 Thread [13] count=3028 Thread [13] count=3029 Thread [13]
 * count=3030 Thread [13] count=3031 Thread [13] count=3032 Thread [13]
 * count=3033 Thread [13] count=3034 Thread [13] count=3035 Thread [13]
 * count=3036 Thread [13] count=3037 Thread [13] count=3038 Thread [13]
 * count=3039 Thread [13] count=3040 Thread [13] count=3041 Thread [11]
 * count=3042 Thread [11] count=3043 Thread [11] count=3044 Thread [11]
 * count=3045 Thread [11] count=3046 Thread [11] count=3047 Thread [11]
 * count=3048 Thread [12] count=3049 Thread [ 9] count=3050 Thread [ 9]
 * count=3051 Thread [ 9] count=3052 Thread [ 9] count=3053 Thread [ 9]
 * count=3054 Thread [ 9] count=3055 Thread [ 9] count=3056 Thread [ 9]
 * count=3057 Thread [ 9] count=3058 Thread [ 9] count=3059 Thread [ 9]
 * count=3060 Thread [ 9] count=3061 Thread [ 9] count=3062 Thread [ 9]
 * count=3063 Thread [ 9] count=3064 Thread [ 9] count=3065 Thread [ 9]
 * count=3066 Thread [ 9] count=3067 Thread [ 9] count=3068 Thread [ 9]
 * count=3069 Thread [ 9] count=3070 Thread [ 9] count=3071 Thread [ 9]
 * count=3072 Thread [ 9] count=3073 Thread [ 9] count=3074 Thread [ 9]
 * count=3075 Thread [ 9] count=3076 Thread [ 9] count=3077 Thread [ 9]
 * count=3078 Thread [ 9] count=3079 Thread [ 9] count=3080 Thread [ 9]
 * count=3081 Thread [ 9] count=3082 Thread [ 9] count=3083 Thread [ 9]
 * count=3084 Thread [ 9] count=3085 Thread [ 9] count=3086 Thread [ 9]
 * count=3087 Thread [ 9] count=3088 Thread [ 9] count=3089 Thread [ 9]
 * count=3090 Thread [ 9] count=3091 Thread [ 9] count=3092 Thread [ 9]
 * count=3093 Thread [ 9] count=3094 Thread [ 9] count=3095 Thread [ 9]
 * count=3096 Thread [ 9] count=3097 Thread [ 9] count=3098 Thread [ 9]
 * count=3099 Thread [ 9] count=3100 Thread [ 9] count=3101 Thread [ 9]
 * count=3102 Thread [ 9] count=3103 Thread [ 9] count=3104 Thread [ 9]
 * count=3105 Thread [ 9] count=3106 Thread [ 9] count=3107 Thread [ 9]
 * count=3108 Thread [ 9] count=3109 Thread [ 9] count=3110 Thread [ 9]
 * count=3111 Thread [ 9] count=3112 Thread [ 9] count=3113 Thread [ 9]
 * count=3114 Thread [ 9] count=3115 Thread [ 9] count=3116 Thread [ 9]
 * count=3117 Thread [ 9] count=3118 Thread [ 9] count=3119 Thread [ 9]
 * count=3120 Thread [ 9] count=3121 Thread [ 9] count=3122 Thread [ 9]
 * count=3123 Thread [ 9] count=3124 Thread [ 9] count=3125 Thread [ 9]
 * count=3126 Thread [ 9] count=3127 Thread [ 9] count=3128 Thread [ 9]
 * count=3129 Thread [ 9] count=3130 Thread [ 9] count=3131 Thread [ 9]
 * count=3132 Thread [ 9] count=3133 Thread [ 9] count=3134 Thread [ 9]
 * count=3135 Thread [ 9] count=3136 Thread [ 9] count=3137 Thread [ 9]
 * count=3138 Thread [ 9] count=3139 Thread [ 9] count=3140 Thread [ 9]
 * count=3141 Thread [ 9] count=3142 Thread [ 9] count=3143 Thread [ 9]
 * count=3144 Thread [ 9] count=3145 Thread [ 9] count=3146 Thread [ 9]
 * count=3147 Thread [ 9] count=3148 Thread [ 9] count=3149 Thread [ 9]
 * count=3150 Thread [ 9] count=3151 Thread [ 9] count=3152 Thread [ 9]
 * count=3153 Thread [ 9] count=3154 Thread [ 9] count=3155 Thread [ 9]
 * count=3156 Thread [ 9] count=3157 Thread [ 9] count=3158 Thread [ 9]
 * count=3159 Thread [ 9] count=3160 Thread [ 9] count=3161 Thread [ 9]
 * count=3162 Thread [ 9] count=3163 Thread [ 9] count=3164 Thread [ 9]
 * count=3165 Thread [ 9] count=3166 Thread [ 9] count=3167 Thread [ 9]
 * count=3168 Thread [ 9] count=3169 Thread [ 9] count=3170 Thread [ 9]
 * count=3171 Thread [ 9] count=3172 Thread [ 9] count=3173 Thread [ 9]
 * count=3174 Thread [ 9] count=3175 Thread [ 9] count=3176 Thread [ 9]
 * count=3177 Thread [ 9] count=3178 Thread [ 9] count=3179 Thread [ 9]
 * count=3180 Thread [ 9] count=3181 Thread [ 9] count=3182 Thread [ 9]
 * count=3183 Thread [ 9] count=3184 Thread [ 9] count=3185 Thread [ 9]
 * count=3186 Thread [ 9] count=3187 Thread [ 9] count=3188 Thread [ 9]
 * count=3189 Thread [ 9] count=3190 Thread [ 9] count=3191 Thread [ 9]
 * count=3192 Thread [ 9] count=3193 Thread [ 9] count=3194 Thread [ 9]
 * count=3195 Thread [ 9] count=3196 Thread [ 9] count=3197 Thread [ 9]
 * count=3198 Thread [ 9] count=3199 Thread [ 9] count=3200 Thread [ 9]
 * count=3201 Thread [ 9] count=3202 Thread [ 9] count=3203 Thread [ 9]
 * count=3204 Thread [ 9] count=3205 Thread [ 9] count=3206 Thread [ 9]
 * count=3207 Thread [ 9] count=3208 Thread [ 9] count=3209 Thread [ 9]
 * count=3210 Thread [ 9] count=3211 Thread [ 9] count=3212 Thread [ 9]
 * count=3213 Thread [ 9] count=3214 Thread [ 9] count=3215 Thread [ 9]
 * count=3216 Thread [ 9] count=3217 Thread [ 9] count=3218 Thread [ 9]
 * count=3219 Thread [ 9] count=3220 Thread [ 9] count=3221 Thread [ 9]
 * count=3222 Thread [ 9] count=3223 Thread [ 9] count=3224 Thread [ 9]
 * count=3225 Thread [ 9] count=3226 Thread [ 9] count=3227 Thread [ 9]
 * count=3228 Thread [ 9] count=3229 Thread [ 9] count=3230 Thread [ 9]
 * count=3231 Thread [ 9] count=3232 Thread [ 9] count=3233 Thread [ 9]
 * count=3234 Thread [ 9] count=3235 Thread [ 9] count=3236 Thread [ 9]
 * count=3237 Thread [ 9] count=3238 Thread [ 9] count=3239 Thread [ 9]
 * count=3240 Thread [ 9] count=3241 Thread [ 9] count=3242 Thread [ 9]
 * count=3243 Thread [ 9] count=3244 Thread [ 9] count=3245 Thread [ 9]
 * count=3246 Thread [ 9] count=3247 Thread [ 9] count=3248 Thread [ 9]
 * count=3249 Thread [ 9] count=3250 Thread [ 9] count=3251 Thread [ 9]
 * count=3252 Thread [ 9] count=3253 Thread [ 9] count=3254 Thread [ 9]
 * count=3255 Thread [ 9] count=3256 Thread [ 9] count=3257 Thread [ 9]
 * count=3258 Thread [ 9] count=3259 Thread [ 9] count=3260 Thread [ 9]
 * count=3261 Thread [ 9] count=3262 Thread [ 9] count=3263 Thread [ 9]
 * count=3264 Thread [ 9] count=3265 Thread [ 9] count=3266 Thread [ 9]
 * count=3267 Thread [ 9] count=3268 Thread [ 9] count=3269 Thread [ 9]
 * count=3270 Thread [ 9] count=3271 Thread [ 9] count=3272 Thread [ 9]
 * count=3273 Thread [ 9] count=3274 Thread [ 9] count=3275 Thread [ 9]
 * count=3276 Thread [ 9] count=3277 Thread [ 9] count=3278 Thread [ 9]
 * count=3279 Thread [ 9] count=3280 Thread [ 9] count=3281 Thread [ 9]
 * count=3282 Thread [ 9] count=3283 Thread [ 9] count=3284 Thread [ 9]
 * count=3285 Thread [ 9] count=3286 Thread [ 9] count=3287 Thread [ 9]
 * count=3288 Thread [ 9] count=3289 Thread [ 9] count=3290 Thread [ 9]
 * count=3291 Thread [ 9] count=3292 Thread [ 9] count=3293 Thread [ 9]
 * count=3294 Thread [ 9] count=3295 Thread [ 9] count=3296 Thread [ 9]
 * count=3297 Thread [ 9] count=3298 Thread [ 9] count=3299 Thread [ 9]
 * count=3300 Thread [ 9] count=3301 Thread [ 9] count=3302 Thread [ 9]
 * count=3303 Thread [ 9] count=3304 Thread [ 9] count=3305 Thread [ 9]
 * count=3306 Thread [ 9] count=3307 Thread [ 9] count=3308 Thread [ 9]
 * count=3309 Thread [ 9] count=3310 Thread [ 9] count=3311 Thread [ 9]
 * count=3312 Thread [ 9] count=3313 Thread [ 9] count=3314 Thread [ 9]
 * count=3315 Thread [ 9] count=3316 Thread [ 9] count=3317 Thread [ 9]
 * count=3318 Thread [ 9] count=3319 Thread [ 9] count=3320 Thread [ 9]
 * count=3321 Thread [ 9] count=3322 Thread [ 9] count=3323 Thread [ 9]
 * count=3324 Thread [ 9] count=3325 Thread [ 9] count=3326 Thread [ 9]
 * count=3327 Thread [ 9] count=3328 Thread [ 9] count=3329 Thread [ 9]
 * count=3330 Thread [ 9] count=3331 Thread [ 9] count=3332 Thread [ 9]
 * count=3333 Thread [ 9] count=3334 Thread [ 9] count=3335 Thread [ 9]
 * count=3336 Thread [ 9] count=3337 Thread [ 9] count=3338 Thread [ 9]
 * count=3339 Thread [ 9] count=3340 Thread [ 9] count=3341 Thread [ 9]
 * count=3342 Thread [ 9] count=3343 Thread [ 9] count=3344 Thread [ 9]
 * count=3345 Thread [ 9] count=3346 Thread [ 9] count=3347 Thread [ 9]
 * count=3348 Thread [ 9] count=3349 Thread [ 9] count=3350 Thread [ 9]
 * count=3351 Thread [ 9] count=3352 Thread [ 9] count=3353 Thread [ 9]
 * count=3354 Thread [ 9] count=3355 Thread [ 9] count=3356 Thread [ 9]
 * count=3357 Thread [ 9] count=3358 Thread [ 9] count=3359 Thread [ 9]
 * count=3360 Thread [ 9] count=3361 Thread [ 9] count=3362 Thread [ 9]
 * count=3363 Thread [ 9] count=3364 Thread [ 9] count=3365 Thread [ 9]
 * count=3366 Thread [ 9] count=3367 Thread [ 9] count=3368 Thread [ 9]
 * count=3369 Thread [ 9] count=3370 Thread [ 9] count=3371 Thread [ 9]
 * count=3372 Thread [ 9] count=3373 Thread [ 9] count=3374 Thread [ 9]
 * count=3375 Thread [ 9] count=3376 Thread [ 9] count=3377 Thread [ 9]
 * count=3378 Thread [ 9] count=3379 Thread [ 9] count=3380 Thread [ 9]
 * count=3381 Thread [ 9] count=3382 Thread [ 9] count=3383 Thread [ 9]
 * count=3384 Thread [ 9] count=3385 Thread [ 9] count=3386 Thread [ 9]
 * count=3387 Thread [ 9] count=3388 Thread [ 9] count=3389 Thread [ 9]
 * count=3390 Thread [ 9] count=3391 Thread [ 9] count=3392 Thread [ 9]
 * count=3393 Thread [ 9] count=3394 Thread [ 9] count=3395 Thread [ 9]
 * count=3396 Thread [ 9] count=3397 Thread [ 9] count=3398 Thread [ 9]
 * count=3399 Thread [ 9] count=3400 Thread [ 9] count=3401 Thread [ 9]
 * count=3402 Thread [ 9] count=3403 Thread [ 9] count=3404 Thread [ 9]
 * count=3405 Thread [ 9] count=3406 Thread [ 9] count=3407 Thread [ 9]
 * count=3408 Thread [ 9] count=3409 Thread [ 9] count=3410 Thread [ 9]
 * count=3411 Thread [ 9] count=3412 Thread [ 9] count=3413 Thread [ 9]
 * count=3414 Thread [ 9] count=3415 Thread [ 9] count=3416 Thread [ 9]
 * count=3417 Thread [ 9] count=3418 Thread [ 9] count=3419 Thread [ 9]
 * count=3420 Thread [ 9] count=3421 Thread [ 9] count=3422 Thread [ 9]
 * count=3423 Thread [ 9] count=3424 Thread [ 9] count=3425 Thread [ 9]
 * count=3426 Thread [ 9] count=3427 Thread [ 9] count=3428 Thread [ 9]
 * count=3429 Thread [ 9] count=3430 Thread [ 9] count=3431 Thread [ 9]
 * count=3432 Thread [ 9] count=3433 Thread [ 9] count=3434 Thread [ 9]
 * count=3435 Thread [11] count=3436 Thread [11] count=3437 Thread [11]
 * count=3438 Thread [11] count=3439 Thread [11] count=3440 Thread [11]
 * count=3441 Thread [11] count=3442 Thread [11] count=3443 Thread [11]
 * count=3444 Thread [11] count=3445 Thread [11] count=3446 Thread [11]
 * count=3447 Thread [11] count=3448 Thread [12] count=3449 Thread [ 9]
 * count=3450 Thread [ 9] count=3451 Thread [ 9] count=3452 Thread [ 9]
 * count=3453 Thread [ 9] count=3454 Thread [ 9] count=3455 Thread [ 9]
 * count=3456 Thread [ 9] count=3457 Thread [ 9] count=3458 Thread [ 9]
 * count=3459 Thread [ 9] count=3460 Thread [ 9] count=3461 Thread [ 9]
 * count=3462 Thread [ 9] count=3463 Thread [ 9] count=3464 Thread [11]
 * count=3465 Thread [12] count=3466 Thread [12] count=3467 Thread [12]
 * count=3468 Thread [12] count=3469 Thread [12] count=3470 Thread [12]
 * count=3471 Thread [12] count=3472 Thread [12] count=3473 Thread [12]
 * count=3474 Thread [12] count=3475 Thread [12] count=3476 Thread [12]
 * count=3477 Thread [12] count=3478 Thread [12] count=3479 Thread [12]
 * count=3480 Thread [12] count=3481 Thread [12] count=3482 Thread [12]
 * count=3483 Thread [12] count=3484 Thread [12] count=3485 Thread [12]
 * count=3486 Thread [12] count=3487 Thread [12] count=3488 Thread [12]
 * count=3489 Thread [12] count=3490 Thread [12] count=3491 Thread [12]
 * count=3492 Thread [12] count=3493 Thread [12] count=3494 Thread [12]
 * count=3495 Thread [12] count=3496 Thread [12] count=3497 Thread [12]
 * count=3498 Thread [12] count=3499 Thread [12] count=3500 Thread [12]
 * count=3501 Thread [12] count=3502 Thread [12] count=3503 Thread [12]
 * count=3504 Thread [12] count=3505 Thread [12] count=3506 Thread [12]
 * count=3507 Thread [12] count=3508 Thread [12] count=3509 Thread [12]
 * count=3510 Thread [12] count=3511 Thread [12] count=3512 Thread [12]
 * count=3513 Thread [12] count=3514 Thread [12] count=3515 Thread [12]
 * count=3516 Thread [12] count=3517 Thread [12] count=3518 Thread [12]
 * count=3519 Thread [12] count=3520 Thread [12] count=3521 Thread [12]
 * count=3522 Thread [12] count=3523 Thread [12] count=3524 Thread [12]
 * count=3525 Thread [12] count=3526 Thread [12] count=3527 Thread [12]
 * count=3528 Thread [12] count=3529 Thread [12] count=3530 Thread [12]
 * count=3531 Thread [12] count=3532 Thread [12] count=3533 Thread [12]
 * count=3534 Thread [12] count=3535 Thread [12] count=3536 Thread [12]
 * count=3537 Thread [12] count=3538 Thread [12] count=3539 Thread [12]
 * count=3540 Thread [12] count=3541 Thread [12] count=3542 Thread [12]
 * count=3543 Thread [12] count=3544 Thread [12] count=3545 Thread [12]
 * count=3546 Thread [12] count=3547 Thread [12] count=3548 Thread [12]
 * count=3549 Thread [12] count=3550 Thread [12] count=3551 Thread [12]
 * count=3552 Thread [12] count=3553 Thread [12] count=3554 Thread [12]
 * count=3555 Thread [12] count=3556 Thread [12] count=3557 Thread [12]
 * count=3558 Thread [12] count=3559 Thread [12] count=3560 Thread [12]
 * count=3561 Thread [12] count=3562 Thread [12] count=3563 Thread [12]
 * count=3564 Thread [12] count=3565 Thread [12] count=3566 Thread [12]
 * count=3567 Thread [12] count=3568 Thread [12] count=3569 Thread [12]
 * count=3570 Thread [12] count=3571 Thread [12] count=3572 Thread [12]
 * count=3573 Thread [12] count=3574 Thread [12] count=3575 Thread [12]
 * count=3576 Thread [12] count=3577 Thread [12] count=3578 Thread [12]
 * count=3579 Thread [12] count=3580 Thread [12] count=3581 Thread [12]
 * count=3582 Thread [12] count=3583 Thread [12] count=3584 Thread [12]
 * count=3585 Thread [12] count=3586 Thread [12] count=3587 Thread [12]
 * count=3588 Thread [12] count=3589 Thread [12] count=3590 Thread [12]
 * count=3591 Thread [12] count=3592 Thread [12] count=3593 Thread [12]
 * count=3594 Thread [12] count=3595 Thread [12] count=3596 Thread [12]
 * count=3597 Thread [12] count=3598 Thread [12] count=3599 Thread [12]
 * count=3600 Thread [12] count=3601 Thread [12] count=3602 Thread [12]
 * count=3603 Thread [12] count=3604 Thread [12] count=3605 Thread [12]
 * count=3606 Thread [12] count=3607 Thread [12] count=3608 Thread [12]
 * count=3609 Thread [12] count=3610 Thread [12] count=3611 Thread [12]
 * count=3612 Thread [12] count=3613 Thread [12] count=3614 Thread [12]
 * count=3615 Thread [12] count=3616 Thread [12] count=3617 Thread [12]
 * count=3618 Thread [12] count=3619 Thread [12] count=3620 Thread [12]
 * count=3621 Thread [12] count=3622 Thread [12] count=3623 Thread [12]
 * count=3624 Thread [12] count=3625 Thread [12] count=3626 Thread [12]
 * count=3627 Thread [11] count=3628 Thread [11] count=3629 Thread [11]
 * count=3630 Thread [11] count=3631 Thread [11] count=3632 Thread [11]
 * count=3633 Thread [11] count=3634 Thread [11] count=3635 Thread [11]
 * count=3636 Thread [11] count=3637 Thread [11] count=3638 Thread [11]
 * count=3639 Thread [11] count=3640 Thread [11] count=3641 Thread [11]
 * count=3642 Thread [11] count=3643 Thread [11] count=3644 Thread [11]
 * count=3645 Thread [11] count=3646 Thread [11] count=3647 Thread [11]
 * count=3648 Thread [11] count=3649 Thread [11] count=3650 Thread [11]
 * count=3651 Thread [11] count=3652 Thread [11] count=3653 Thread [11]
 * count=3654 Thread [11] count=3655 Thread [11] count=3656 Thread [11]
 * count=3657 Thread [11] count=3658 Thread [11] count=3659 Thread [11]
 * count=3660 Thread [11] count=3661 Thread [11] count=3662 Thread [11]
 * count=3663 Thread [11] count=3664 Thread [11] count=3665 Thread [11]
 * count=3666 Thread [11] count=3667 Thread [11] count=3668 Thread [11]
 * count=3669 Thread [11] count=3670 Thread [11] count=3671 Thread [11]
 * count=3672 Thread [11] count=3673 Thread [11] count=3674 Thread [11]
 * count=3675 Thread [12] count=3676 Thread [12] count=3677 Thread [12]
 * count=3678 Thread [12] count=3679 Thread [12] count=3680 Thread [12]
 * count=3681 Thread [12] count=3682 Thread [12] count=3683 Thread [12]
 * count=3684 Thread [12] count=3685 Thread [12] count=3686 Thread [12]
 * count=3687 Thread [12] count=3688 Thread [12] count=3689 Thread [12]
 * count=3690 Thread [12] count=3691 Thread [12] count=3692 Thread [12]
 * count=3693 Thread [12] count=3694 Thread [12] count=3695 Thread [12]
 * count=3696 Thread [12] count=3697 Thread [12] count=3698 Thread [12]
 * count=3699 Thread [12] count=3700 Thread [12] count=3701 Thread [12]
 * count=3702 Thread [12] count=3703 Thread [12] count=3704 Thread [12]
 * count=3705 Thread [12] count=3706 Thread [12] count=3707 Thread [12]
 * count=3708 Thread [12] count=3709 Thread [12] count=3710 Thread [12]
 * count=3711 Thread [12] count=3712 Thread [12] count=3713 Thread [12]
 * count=3714 Thread [12] count=3715 Thread [12] count=3716 Thread [11]
 * count=3717 Thread [11] count=3718 Thread [11] count=3719 Thread [11]
 * count=3720 Thread [11] count=3721 Thread [11] count=3722 Thread [11]
 * count=3723 Thread [11] count=3724 Thread [11] count=3725 Thread [11]
 * count=3726 Thread [11] count=3727 Thread [11] count=3728 Thread [11]
 * count=3729 Thread [11] count=3730 Thread [11] count=3731 Thread [11]
 * count=3732 Thread [11] count=3733 Thread [11] count=3734 Thread [11]
 * count=3735 Thread [11] count=3736 Thread [11] count=3737 Thread [11]
 * count=3738 Thread [11] count=3739 Thread [11] count=3740 Thread [12]
 * count=3741 Thread [12] count=3742 Thread [12] count=3743 Thread [12]
 * count=3744 Thread [12] count=3745 Thread [12] count=3746 Thread [12]
 * count=3747 Thread [12] count=3748 Thread [12] count=3749 Thread [12]
 * count=3750 Thread [12] count=3751 Thread [12] count=3752 Thread [12]
 * count=3753 Thread [12] count=3754 Thread [12] count=3755 Thread [12]
 * count=3756 Thread [12] count=3757 Thread [12] count=3758 Thread [12]
 * count=3759 Thread [12] count=3760 Thread [12] count=3761 Thread [12]
 * count=3762 Thread [12] count=3763 Thread [12] count=3764 Thread [12]
 * count=3765 Thread [12] count=3766 Thread [12] count=3767 Thread [12]
 * count=3768 Thread [12] count=3769 Thread [12] count=3770 Thread [12]
 * count=3771 Thread [12] count=3772 Thread [12] count=3773 Thread [12]
 * count=3774 Thread [12] count=3775 Thread [12] count=3776 Thread [12]
 * count=3777 Thread [12] count=3778 Thread [12] count=3779 Thread [12]
 * count=3780 Thread [12] count=3781 Thread [12] count=3782 Thread [12]
 * count=3783 Thread [12] count=3784 Thread [12] count=3785 Thread [12]
 * count=3786 Thread [12] count=3787 Thread [12] count=3788 Thread [12]
 * count=3789 Thread [12] count=3790 Thread [12] count=3791 Thread [12]
 * count=3792 Thread [12] count=3793 Thread [12] count=3794 Thread [12]
 * count=3795 Thread [12] count=3796 Thread [12] count=3797 Thread [12]
 * count=3798 Thread [12] count=3799 Thread [12] count=3800 Thread [12]
 * count=3801 Thread [12] count=3802 Thread [12] count=3803 Thread [12]
 * count=3804 Thread [12] count=3805 Thread [12] count=3806 Thread [12]
 * count=3807 Thread [12] count=3808 Thread [12] count=3809 Thread [12]
 * count=3810 Thread [12] count=3811 Thread [12] count=3812 Thread [12]
 * count=3813 Thread [12] count=3814 Thread [12] count=3815 Thread [12]
 * count=3816 Thread [12] count=3817 Thread [12] count=3818 Thread [12]
 * count=3819 Thread [12] count=3820 Thread [12] count=3821 Thread [12]
 * count=3822 Thread [12] count=3823 Thread [12] count=3824 Thread [12]
 * count=3825 Thread [12] count=3826 Thread [12] count=3827 Thread [12]
 * count=3828 Thread [12] count=3829 Thread [12] count=3830 Thread [12]
 * count=3831 Thread [12] count=3832 Thread [12] count=3833 Thread [12]
 * count=3834 Thread [12] count=3835 Thread [12] count=3836 Thread [12]
 * count=3837 Thread [12] count=3838 Thread [12] count=3839 Thread [12]
 * count=3840 Thread [12] count=3841 Thread [12] count=3842 Thread [12]
 * count=3843 Thread [12] count=3844 Thread [12] count=3845 Thread [12]
 * count=3846 Thread [12] count=3847 Thread [12] count=3848 Thread [12]
 * count=3849 Thread [12] count=3850 Thread [12] count=3851 Thread [12]
 * count=3852 Thread [12] count=3853 Thread [12] count=3854 Thread [12]
 * count=3855 Thread [12] count=3856 Thread [12] count=3857 Thread [12]
 * count=3858 Thread [12] count=3859 Thread [12] count=3860 Thread [12]
 * count=3861 Thread [12] count=3862 Thread [12] count=3863 Thread [12]
 * count=3864 Thread [12] count=3865 Thread [12] count=3866 Thread [12]
 * count=3867 Thread [12] count=3868 Thread [12] count=3869 Thread [12]
 * count=3870 Thread [12] count=3871 Thread [12] count=3872 Thread [12]
 * count=3873 Thread [12] count=3874 Thread [12] count=3875 Thread [12]
 * count=3876 Thread [12] count=3877 Thread [12] count=3878 Thread [12]
 * count=3879 Thread [12] count=3880 Thread [12] count=3881 Thread [12]
 * count=3882 Thread [12] count=3883 Thread [12] count=3884 Thread [12]
 * count=3885 Thread [12] count=3886 Thread [12] count=3887 Thread [12]
 * count=3888 Thread [12] count=3889 Thread [12] count=3890 Thread [12]
 * count=3891 Thread [12] count=3892 Thread [12] count=3893 Thread [12]
 * count=3894 Thread [12] count=3895 Thread [12] count=3896 Thread [12]
 * count=3897 Thread [12] count=3898 Thread [12] count=3899 Thread [12]
 * count=3900 Thread [12] count=3901 Thread [12] count=3902 Thread [12]
 * count=3903 Thread [12] count=3904 Thread [12] count=3905 Thread [12]
 * count=3906 Thread [12] count=3907 Thread [12] count=3908 Thread [12]
 * count=3909 Thread [12] count=3910 Thread [12] count=3911 Thread [12]
 * count=3912 Thread [12] count=3913 Thread [12] count=3914 Thread [12]
 * count=3915 Thread [12] count=3916 Thread [12] count=3917 Thread [12]
 * count=3918 Thread [12] count=3919 Thread [12] count=3920 Thread [12]
 * count=3921 Thread [12] count=3922 Thread [12] count=3923 Thread [12]
 * count=3924 Thread [12] count=3925 Thread [12] count=3926 Thread [12]
 * count=3927 Thread [12] count=3928 Thread [12] count=3929 Thread [12]
 * count=3930 Thread [12] count=3931 Thread [12] count=3932 Thread [12]
 * count=3933 Thread [12] count=3934 Thread [12] count=3935 Thread [12]
 * count=3936 Thread [12] count=3937 Thread [12] count=3938 Thread [12]
 * count=3939 Thread [12] count=3940 Thread [12] count=3941 Thread [12]
 * count=3942 Thread [12] count=3943 Thread [12] count=3944 Thread [12]
 * count=3945 Thread [12] count=3946 Thread [12] count=3947 Thread [12]
 * count=3948 Thread [12] count=3949 Thread [12] count=3950 Thread [12]
 * count=3951 Thread [12] count=3952 Thread [12] count=3953 Thread [12]
 * count=3954 Thread [12] count=3955 Thread [12] count=3956 Thread [12]
 * count=3957 Thread [12] count=3958 Thread [12] count=3959 Thread [12]
 * count=3960 Thread [12] count=3961 Thread [12] count=3962 Thread [12]
 * count=3963 Thread [12] count=3964 Thread [12] count=3965 Thread [12]
 * count=3966 Thread [12] count=3967 Thread [12] count=3968 Thread [12]
 * count=3969 Thread [12] count=3970 Thread [12] count=3971 Thread [12]
 * count=3972 Thread [12] count=3973 Thread [12] count=3974 Thread [12]
 * count=3975 Thread [12] count=3976 Thread [12] count=3977 Thread [12]
 * count=3978 Thread [12] count=3979 Thread [12] count=3980 Thread [12]
 * count=3981 Thread [12] count=3982 Thread [12] count=3983 Thread [12]
 * count=3984 Thread [12] count=3985 Thread [12] count=3986 Thread [12]
 * count=3987 Thread [12] count=3988 Thread [12] count=3989 Thread [12]
 * count=3990 Thread [12] count=3991 Thread [12] count=3992 Thread [12]
 * count=3993 Thread [12] count=3994 Thread [12] count=3995 Thread [12]
 * count=3996 Thread [12] count=3997 Thread [12] count=3998 Thread [12]
 * count=3999 Thread [12] count=4000
 */

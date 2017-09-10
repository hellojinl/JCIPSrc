package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_6_Nonblocking_stack_using_Treiber_s_algorithm;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ConcurrentStackTest {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    final ConcurrentStack< Integer > stack = new ConcurrentStack<>();

    final AtomicLong expected = new AtomicLong();
    final AtomicLong result = new AtomicLong();

    @Test
    public void test() throws InterruptedException {
        for (int i = 1; i <= 800; i++) {
            stack.push( i );
            expected.addAndGet( i );
        }
        for (int i = 0; i < 5; i++) {
            pool.execute( new PopRunnable() );
        }
        pool.shutdown();
        pool.awaitTermination( 2, TimeUnit.SECONDS );

        assertEquals( expected.get(), result.get() );
    }

    class PopRunnable implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 160; i++) {
                int pop = stack.pop();
                result.addAndGet( pop );
                System.out.println( String.format( "Thread[%2d] pop=%d", Thread.currentThread().getId(), pop ) );
            }
        }

    }

}

/*
 * 某次运行结果（并非降序） Thread[ 9] pop=800 Thread[10] pop=799 Thread[12] pop=798
 * Thread[10] pop=796 Thread[12] pop=795 Thread[10] pop=794 Thread[12] pop=793
 * Thread[10] pop=792 Thread[12] pop=791 Thread[10] pop=790 Thread[12] pop=789
 * Thread[10] pop=788 Thread[11] pop=787 Thread[10] pop=785 Thread[11] pop=784
 * Thread[12] pop=786 Thread[10] pop=783 Thread[11] pop=782 Thread[12] pop=781
 * Thread[10] pop=780 Thread[11] pop=779 Thread[12] pop=778 Thread[10] pop=777
 * Thread[11] pop=776 Thread[12] pop=775 Thread[10] pop=774 Thread[11] pop=773
 * Thread[12] pop=772 Thread[10] pop=771 Thread[11] pop=770 Thread[12] pop=769
 * Thread[10] pop=768 Thread[11] pop=767 Thread[12] pop=766 Thread[10] pop=765
 * Thread[11] pop=764 Thread[12] pop=763 Thread[10] pop=762 Thread[11] pop=761
 * Thread[12] pop=760 Thread[10] pop=759 Thread[11] pop=758 Thread[12] pop=757
 * Thread[10] pop=756 Thread[11] pop=755 Thread[12] pop=754 Thread[10] pop=753
 * Thread[11] pop=752 Thread[12] pop=751 Thread[10] pop=750 Thread[12] pop=748
 * Thread[10] pop=747 Thread[12] pop=746 Thread[10] pop=745 Thread[12] pop=744
 * Thread[10] pop=743 Thread[11] pop=749 Thread[10] pop=741 Thread[11] pop=740
 * Thread[12] pop=742 Thread[11] pop=738 Thread[12] pop=737 Thread[11] pop=736
 * Thread[12] pop=735 Thread[11] pop=734 Thread[12] pop=733 Thread[11] pop=732
 * Thread[12] pop=731 Thread[11] pop=730 Thread[12] pop=729 Thread[11] pop=728
 * Thread[12] pop=727 Thread[11] pop=726 Thread[10] pop=739 Thread[11] pop=724
 * Thread[10] pop=723 Thread[11] pop=722 Thread[10] pop=721 Thread[11] pop=720
 * Thread[10] pop=719 Thread[11] pop=718 Thread[11] pop=716 Thread[11] pop=715
 * Thread[12] pop=725 Thread[11] pop=714 Thread[12] pop=713 Thread[11] pop=712
 * Thread[12] pop=711 Thread[11] pop=710 Thread[12] pop=709 Thread[10] pop=717
 * Thread[12] pop=707 Thread[10] pop=706 Thread[12] pop=705 Thread[10] pop=704
 * Thread[11] pop=708 Thread[10] pop=702 Thread[10] pop=700 Thread[12] pop=703
 * Thread[10] pop=699 Thread[12] pop=698 Thread[10] pop=697 Thread[12] pop=696
 * Thread[10] pop=695 Thread[11] pop=701 Thread[10] pop=693 Thread[12] pop=694
 * Thread[10] pop=691 Thread[12] pop=690 Thread[10] pop=689 Thread[12] pop=688
 * Thread[10] pop=687 Thread[11] pop=692 Thread[10] pop=685 Thread[12] pop=686
 * Thread[11] pop=684 Thread[12] pop=682 Thread[11] pop=681 Thread[ 9] pop=797
 * Thread[12] pop=680 Thread[11] pop=679 Thread[ 9] pop=678 Thread[12] pop=677
 * Thread[ 9] pop=675 Thread[12] pop=674 Thread[ 9] pop=673 Thread[12] pop=672
 * Thread[ 9] pop=671 Thread[12] pop=670 Thread[10] pop=683 Thread[12] pop=668
 * Thread[10] pop=667 Thread[12] pop=666 Thread[10] pop=665 Thread[12] pop=664
 * Thread[10] pop=663 Thread[12] pop=662 Thread[10] pop=661 Thread[10] pop=659
 * Thread[10] pop=658 Thread[10] pop=657 Thread[ 9] pop=669 Thread[11] pop=676
 * Thread[ 9] pop=654 Thread[11] pop=653 Thread[ 9] pop=652 Thread[11] pop=651
 * Thread[ 9] pop=650 Thread[11] pop=649 Thread[ 9] pop=648 Thread[11] pop=647
 * Thread[ 9] pop=646 Thread[11] pop=645 Thread[ 9] pop=644 Thread[11] pop=643
 * Thread[ 9] pop=642 Thread[11] pop=641 Thread[10] pop=655 Thread[11] pop=639
 * Thread[10] pop=638 Thread[11] pop=637 Thread[10] pop=636 Thread[11] pop=635
 * Thread[10] pop=634 Thread[11] pop=633 Thread[13] pop=656 Thread[12] pop=660
 * Thread[13] pop=630 Thread[12] pop=629 Thread[11] pop=631 Thread[10] pop=632
 * Thread[11] pop=626 Thread[ 9] pop=640 Thread[11] pop=624 Thread[ 9] pop=623
 * Thread[11] pop=622 Thread[ 9] pop=621 Thread[11] pop=620 Thread[ 9] pop=619
 * Thread[11] pop=618 Thread[10] pop=625 Thread[11] pop=616 Thread[10] pop=615
 * Thread[11] pop=614 Thread[10] pop=613 Thread[11] pop=612 Thread[10] pop=611
 * Thread[11] pop=610 Thread[10] pop=609 Thread[11] pop=608 Thread[12] pop=627
 * Thread[13] pop=628 Thread[12] pop=605 Thread[13] pop=604 Thread[11] pop=606
 * Thread[13] pop=602 Thread[11] pop=601 Thread[13] pop=600 Thread[11] pop=599
 * Thread[13] pop=598 Thread[11] pop=597 Thread[13] pop=596 Thread[11] pop=595
 * Thread[13] pop=594 Thread[11] pop=593 Thread[13] pop=592 Thread[11] pop=591
 * Thread[13] pop=590 Thread[10] pop=607 Thread[ 9] pop=617 Thread[13] pop=588
 * Thread[11] pop=589 Thread[13] pop=585 Thread[11] pop=584 Thread[13] pop=583
 * Thread[11] pop=582 Thread[13] pop=581 Thread[11] pop=580 Thread[13] pop=579
 * Thread[11] pop=578 Thread[13] pop=577 Thread[ 9] pop=586 Thread[13] pop=575
 * Thread[ 9] pop=574 Thread[13] pop=573 Thread[ 9] pop=572 Thread[13] pop=571
 * Thread[ 9] pop=570 Thread[12] pop=603 Thread[ 9] pop=568 Thread[13] pop=569
 * Thread[ 9] pop=566 Thread[11] pop=576 Thread[10] pop=587 Thread[11] pop=563
 * Thread[10] pop=562 Thread[11] pop=561 Thread[10] pop=560 Thread[11] pop=559
 * Thread[10] pop=558 Thread[11] pop=557 Thread[ 9] pop=564 Thread[13] pop=565
 * Thread[12] pop=567 Thread[13] pop=553 Thread[12] pop=552 Thread[13] pop=551
 * Thread[12] pop=550 Thread[13] pop=549 Thread[12] pop=548 Thread[13] pop=547
 * Thread[12] pop=546 Thread[13] pop=545 Thread[12] pop=544 Thread[13] pop=543
 * Thread[12] pop=542 Thread[ 9] pop=554 Thread[11] pop=555 Thread[10] pop=556
 * Thread[ 9] pop=539 Thread[10] pop=537 Thread[ 9] pop=536 Thread[12] pop=540
 * Thread[ 9] pop=534 Thread[12] pop=533 Thread[13] pop=541 Thread[12] pop=531
 * Thread[13] pop=530 Thread[12] pop=529 Thread[13] pop=528 Thread[ 9] pop=532
 * Thread[10] pop=535 Thread[11] pop=538 Thread[ 9] pop=525 Thread[11] pop=523
 * Thread[ 9] pop=522 Thread[13] pop=526 Thread[12] pop=527 Thread[13] pop=519
 * Thread[ 9] pop=520 Thread[13] pop=517 Thread[ 9] pop=516 Thread[11] pop=521
 * Thread[ 9] pop=514 Thread[11] pop=513 Thread[ 9] pop=512 Thread[11] pop=511
 * Thread[ 9] pop=510 Thread[11] pop=509 Thread[ 9] pop=508 Thread[10] pop=524
 * Thread[ 9] pop=506 Thread[10] pop=505 Thread[ 9] pop=504 Thread[11] pop=507
 * Thread[ 9] pop=502 Thread[11] pop=501 Thread[ 9] pop=500 Thread[11] pop=499
 * Thread[ 9] pop=498 Thread[11] pop=497 Thread[ 9] pop=496 Thread[11] pop=495
 * Thread[ 9] pop=494 Thread[ 9] pop=492 Thread[11] pop=493 Thread[ 9] pop=491
 * Thread[ 9] pop=489 Thread[ 9] pop=488 Thread[ 9] pop=487 Thread[ 9] pop=486
 * Thread[ 9] pop=485 Thread[ 9] pop=484 Thread[13] pop=515 Thread[12] pop=518
 * Thread[12] pop=481 Thread[ 9] pop=483 Thread[11] pop=490 Thread[ 9] pop=479
 * Thread[10] pop=503 Thread[ 9] pop=477 Thread[11] pop=478 Thread[ 9] pop=475
 * Thread[11] pop=474 Thread[ 9] pop=473 Thread[11] pop=472 Thread[ 9] pop=471
 * Thread[11] pop=470 Thread[ 9] pop=469 Thread[11] pop=468 Thread[ 9] pop=467
 * Thread[11] pop=466 Thread[ 9] pop=465 Thread[11] pop=464 Thread[ 9] pop=463
 * Thread[12] pop=480 Thread[ 9] pop=461 Thread[12] pop=460 Thread[ 9] pop=459
 * Thread[13] pop=482 Thread[ 9] pop=457 Thread[12] pop=458 Thread[11] pop=462
 * Thread[10] pop=476 Thread[11] pop=453 Thread[10] pop=452 Thread[11] pop=451
 * Thread[10] pop=450 Thread[11] pop=449 Thread[10] pop=448 Thread[12] pop=454
 * Thread[ 9] pop=455 Thread[12] pop=445 Thread[13] pop=456 Thread[12] pop=443
 * Thread[ 9] pop=444 Thread[10] pop=446 Thread[11] pop=447 Thread[ 9] pop=440
 * Thread[11] pop=438 Thread[12] pop=441 Thread[12] pop=435 Thread[13] pop=442
 * Thread[12] pop=434 Thread[ 9] pop=437 Thread[11] pop=436 Thread[10] pop=439
 * Thread[11] pop=430 Thread[ 9] pop=431 Thread[12] pop=432 Thread[13] pop=433
 * Thread[12] pop=426 Thread[13] pop=425 Thread[ 9] pop=427 Thread[11] pop=428
 * Thread[10] pop=429 Thread[11] pop=421 Thread[ 9] pop=422 Thread[13] pop=423
 * Thread[12] pop=424 Thread[13] pop=417 Thread[12] pop=416 Thread[13] pop=415
 * Thread[ 9] pop=418 Thread[11] pop=419 Thread[10] pop=420 Thread[11] pop=411
 * Thread[ 9] pop=412 Thread[13] pop=413 Thread[12] pop=414 Thread[13] pop=407
 * Thread[ 9] pop=408 Thread[11] pop=409 Thread[ 9] pop=404 Thread[11] pop=403
 * Thread[ 9] pop=402 Thread[11] pop=401 Thread[ 9] pop=400 Thread[11] pop=399
 * Thread[ 9] pop=398 Thread[11] pop=397 Thread[ 9] pop=396 Thread[11] pop=395
 * Thread[ 9] pop=394 Thread[11] pop=393 Thread[ 9] pop=392 Thread[11] pop=391
 * Thread[ 9] pop=390 Thread[10] pop=410 Thread[ 9] pop=388 Thread[10] pop=387
 * Thread[ 9] pop=386 Thread[10] pop=385 Thread[ 9] pop=384 Thread[10] pop=383
 * Thread[ 9] pop=382 Thread[10] pop=381 Thread[ 9] pop=380 Thread[11] pop=389
 * Thread[ 9] pop=378 Thread[11] pop=377 Thread[ 9] pop=376 Thread[11] pop=375
 * Thread[ 9] pop=374 Thread[13] pop=405 Thread[ 9] pop=372 Thread[13] pop=371
 * Thread[ 9] pop=370 Thread[13] pop=369 Thread[ 9] pop=368 Thread[13] pop=367
 * Thread[ 9] pop=366 Thread[13] pop=365 Thread[ 9] pop=364 Thread[13] pop=363
 * Thread[ 9] pop=362 Thread[13] pop=361 Thread[ 9] pop=360 Thread[13] pop=359
 * Thread[ 9] pop=358 Thread[12] pop=406 Thread[ 9] pop=356 Thread[12] pop=355
 * Thread[12] pop=353 Thread[13] pop=357 Thread[11] pop=373 Thread[10] pop=379
 * Thread[11] pop=350 Thread[13] pop=351 Thread[11] pop=348 Thread[13] pop=347
 * Thread[12] pop=352 Thread[13] pop=345 Thread[12] pop=344 Thread[13] pop=343
 * Thread[12] pop=342 Thread[ 9] pop=354 Thread[12] pop=340 Thread[ 9] pop=339
 * Thread[12] pop=338 Thread[13] pop=341 Thread[11] pop=346 Thread[13] pop=335
 * Thread[11] pop=334 Thread[13] pop=333 Thread[11] pop=332 Thread[13] pop=331
 * Thread[11] pop=330 Thread[13] pop=329 Thread[10] pop=349 Thread[13] pop=327
 * Thread[10] pop=326 Thread[13] pop=325 Thread[13] pop=323 Thread[11] pop=328
 * Thread[12] pop=336 Thread[12] pop=320 Thread[ 9] pop=337 Thread[12] pop=319
 * Thread[ 9] pop=318 Thread[12] pop=317 Thread[11] pop=321 Thread[13] pop=322
 * Thread[10] pop=324 Thread[13] pop=313 Thread[10] pop=312 Thread[11] pop=314
 * Thread[ 9] pop=316 Thread[11] pop=309 Thread[ 9] pop=308 Thread[11] pop=307
 * Thread[ 9] pop=306 Thread[11] pop=305 Thread[ 9] pop=304 Thread[ 9] pop=302
 * Thread[12] pop=315 Thread[ 9] pop=301 Thread[11] pop=303 Thread[10] pop=310
 * Thread[13] pop=311 Thread[11] pop=298 Thread[13] pop=296 Thread[11] pop=295
 * Thread[13] pop=294 Thread[11] pop=293 Thread[13] pop=292 Thread[11] pop=291
 * Thread[13] pop=290 Thread[11] pop=289 Thread[13] pop=288 Thread[11] pop=287
 * Thread[13] pop=286 Thread[11] pop=285 Thread[13] pop=284 Thread[11] pop=283
 * Thread[13] pop=282 Thread[11] pop=281 Thread[13] pop=280 Thread[11] pop=279
 * Thread[13] pop=278 Thread[11] pop=277 Thread[13] pop=276 Thread[11] pop=275
 * Thread[13] pop=274 Thread[11] pop=273 Thread[13] pop=272 Thread[11] pop=271
 * Thread[13] pop=270 Thread[11] pop=269 Thread[13] pop=268 Thread[11] pop=267
 * Thread[13] pop=266 Thread[11] pop=265 Thread[13] pop=264 Thread[11] pop=263
 * Thread[13] pop=262 Thread[10] pop=297 Thread[11] pop=261 Thread[10] pop=259
 * Thread[11] pop=258 Thread[10] pop=257 Thread[11] pop=256 Thread[10] pop=255
 * Thread[11] pop=254 Thread[11] pop=252 Thread[ 9] pop=299 Thread[11] pop=251
 * Thread[ 9] pop=250 Thread[11] pop=249 Thread[ 9] pop=248 Thread[ 9] pop=247
 * Thread[ 9] pop=246 Thread[12] pop=300 Thread[ 9] pop=245 Thread[ 9] pop=243
 * Thread[ 9] pop=242 Thread[ 9] pop=241 Thread[ 9] pop=240 Thread[13] pop=260
 * Thread[13] pop=238 Thread[10] pop=253 Thread[13] pop=237 Thread[ 9] pop=239
 * Thread[12] pop=244 Thread[ 9] pop=234 Thread[13] pop=235 Thread[10] pop=236
 * Thread[ 9] pop=232 Thread[13] pop=231 Thread[12] pop=233 Thread[13] pop=228
 * Thread[12] pop=227 Thread[13] pop=226 Thread[12] pop=225 Thread[13] pop=224
 * Thread[12] pop=223 Thread[ 9] pop=229 Thread[12] pop=221 Thread[ 9] pop=220
 * Thread[12] pop=219 Thread[ 9] pop=218 Thread[12] pop=217 Thread[ 9] pop=216
 * Thread[10] pop=230 Thread[ 9] pop=214 Thread[12] pop=215 Thread[ 9] pop=212
 * Thread[12] pop=211 Thread[ 9] pop=210 Thread[12] pop=209 Thread[ 9] pop=208
 * Thread[12] pop=207 Thread[13] pop=222 Thread[12] pop=205 Thread[ 9] pop=206
 * Thread[10] pop=213 Thread[ 9] pop=202 Thread[10] pop=201 Thread[ 9] pop=200
 * Thread[10] pop=199 Thread[12] pop=203 Thread[10] pop=197 Thread[12] pop=196
 * Thread[13] pop=204 Thread[12] pop=194 Thread[10] pop=195 Thread[12] pop=192
 * Thread[10] pop=191 Thread[12] pop=190 Thread[10] pop=189 Thread[12] pop=188
 * Thread[10] pop=187 Thread[12] pop=186 Thread[10] pop=185 Thread[12] pop=184
 * Thread[10] pop=183 Thread[12] pop=182 Thread[10] pop=181 Thread[12] pop=180
 * Thread[10] pop=179 Thread[12] pop=178 Thread[10] pop=177 Thread[12] pop=176
 * Thread[10] pop=175 Thread[12] pop=174 Thread[10] pop=173 Thread[12] pop=172
 * Thread[10] pop=171 Thread[12] pop=170 Thread[10] pop=169 Thread[12] pop=168
 * Thread[10] pop=167 Thread[12] pop=166 Thread[10] pop=165 Thread[12] pop=164
 * Thread[10] pop=163 Thread[12] pop=162 Thread[10] pop=161 Thread[12] pop=160
 * Thread[10] pop=159 Thread[12] pop=158 Thread[10] pop=157 Thread[12] pop=156
 * Thread[10] pop=155 Thread[12] pop=154 Thread[10] pop=153 Thread[12] pop=152
 * Thread[10] pop=151 Thread[12] pop=150 Thread[10] pop=149 Thread[12] pop=148
 * Thread[12] pop=146 Thread[ 9] pop=198 Thread[12] pop=145 Thread[10] pop=147
 * Thread[12] pop=143 Thread[10] pop=142 Thread[12] pop=141 Thread[10] pop=140
 * Thread[12] pop=139 Thread[10] pop=138 Thread[12] pop=137 Thread[10] pop=136
 * Thread[12] pop=135 Thread[10] pop=134 Thread[12] pop=133 Thread[13] pop=193
 * Thread[12] pop=131 Thread[13] pop=130 Thread[12] pop=129 Thread[13] pop=128
 * Thread[12] pop=127 Thread[13] pop=126 Thread[12] pop=125 Thread[13] pop=124
 * Thread[10] pop=132 Thread[ 9] pop=144 Thread[10] pop=121 Thread[ 9] pop=120
 * Thread[10] pop=119 Thread[ 9] pop=118 Thread[10] pop=117 Thread[ 9] pop=116
 * Thread[10] pop=115 Thread[ 9] pop=114 Thread[10] pop=113 Thread[ 9] pop=112
 * Thread[10] pop=111 Thread[ 9] pop=110 Thread[10] pop=109 Thread[ 9] pop=108
 * Thread[10] pop=107 Thread[ 9] pop=106 Thread[10] pop=105 Thread[ 9] pop=104
 * Thread[10] pop=103 Thread[13] pop=122 Thread[12] pop=123 Thread[13] pop=100
 * Thread[12] pop=99 Thread[13] pop=98 Thread[10] pop=101 Thread[13] pop=96
 * Thread[10] pop=95 Thread[13] pop=94 Thread[10] pop=93 Thread[13] pop=92
 * Thread[10] pop=91 Thread[13] pop=90 Thread[10] pop=89 Thread[10] pop=87
 * Thread[ 9] pop=102 Thread[10] pop=86 Thread[ 9] pop=85 Thread[10] pop=84
 * Thread[ 9] pop=83 Thread[10] pop=82 Thread[ 9] pop=81 Thread[ 9] pop=79
 * Thread[13] pop=88 Thread[12] pop=97 Thread[13] pop=77 Thread[ 9] pop=78
 * Thread[13] pop=75 Thread[10] pop=80 Thread[13] pop=73 Thread[ 9] pop=74
 * Thread[13] pop=71 Thread[ 9] pop=70 Thread[13] pop=69 Thread[12] pop=76
 * Thread[ 9] pop=68 Thread[13] pop=67 Thread[10] pop=72 Thread[13] pop=64
 * Thread[ 9] pop=65 Thread[12] pop=66 Thread[ 9] pop=61 Thread[13] pop=62
 * Thread[10] pop=63 Thread[13] pop=58 Thread[ 9] pop=59 Thread[12] pop=60
 * Thread[ 9] pop=55 Thread[13] pop=56 Thread[13] pop=53 Thread[10] pop=57
 * Thread[13] pop=52 Thread[10] pop=51 Thread[12] pop=54 Thread[10] pop=49
 * Thread[12] pop=48 Thread[10] pop=47 Thread[12] pop=46 Thread[10] pop=45
 * Thread[12] pop=44 Thread[10] pop=43 Thread[10] pop=41 Thread[12] pop=42
 * Thread[10] pop=40 Thread[12] pop=39 Thread[10] pop=38 Thread[13] pop=50
 * Thread[13] pop=37 Thread[13] pop=36 Thread[13] pop=35 Thread[13] pop=34
 * Thread[13] pop=33 Thread[13] pop=32 Thread[13] pop=31 Thread[13] pop=30
 * Thread[13] pop=29 Thread[13] pop=28 Thread[13] pop=27 Thread[13] pop=26
 * Thread[13] pop=25 Thread[13] pop=24 Thread[13] pop=23 Thread[13] pop=22
 * Thread[13] pop=21 Thread[13] pop=20 Thread[13] pop=19 Thread[13] pop=18
 * Thread[13] pop=17 Thread[13] pop=16 Thread[13] pop=15 Thread[13] pop=14
 * Thread[13] pop=13 Thread[13] pop=12 Thread[13] pop=11 Thread[13] pop=10
 * Thread[13] pop=9 Thread[13] pop=8 Thread[13] pop=7 Thread[13] pop=6
 * Thread[13] pop=5 Thread[13] pop=4 Thread[13] pop=3 Thread[13] pop=2
 * Thread[13] pop=1
 */

package C7_Cancellation_and_Shutdown.Ch7_17_Shutdown_with_poison_pill;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class IndexingServiceTest {

    @Test
    public void test() throws InterruptedException {
        String testClasses = Thread.currentThread().getContextClassLoader().getResource( "" ).getPath();

        File root = new File( testClasses );
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };
        IndexingService indexingService = new IndexingService( root, filter );
        indexingService.start();
        indexingService.awaitTermination();
    }

    static class IndexingService {
        private static final int CAPACITY = 1000;
        private static final File POISON = new File( "" );
        private final IndexerThread consumer = new IndexerThread();
        private final CrawlerThread producer = new CrawlerThread();
        private final BlockingQueue< File > queue;
        private final FileFilter fileFilter;
        private final File root;

        public IndexingService(File root, final FileFilter fileFilter) {
            this.root = root;
            this.queue = new LinkedBlockingQueue< File >( CAPACITY );
            this.fileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || fileFilter.accept( f );
                }
            };
        }

        private boolean alreadyIndexed(File f) {
            return false;
        }

        class CrawlerThread extends Thread {
            public void run() {
                try {
                    crawl( root );
                } catch ( InterruptedException e ) { /* fall through */
                } finally {
                    while ( true ) {
                        try {
                            System.out.println( "put POISON" );
                            queue.put( POISON );
                            break;
                        } catch ( InterruptedException e1 ) { /* retry */
                        }
                    }
                }
            }

            private void crawl(File root) throws InterruptedException {
                File[] entries = root.listFiles( fileFilter );
                if (entries != null) {
                    for (File entry : entries) {
                        if (entry.isDirectory())
                            crawl( entry );
                        else if (!alreadyIndexed( entry )) {
                            System.out.println( "put entry :" + entry.getName() );
                            queue.put( entry );
                        }
                    }
                }
            }
        }

        class IndexerThread extends Thread {
            public void run() {
                try {
                    while ( true ) {
                        File file = queue.take();
                        if (file == POISON) {
                            System.err.println( "indexer thread exit" );
                            break;
                        } else
                            indexFile( file );
                    }
                } catch ( InterruptedException consumed ) {
                }
            }

            public void indexFile(File file) {
                System.err.println( "index file " + file.getName() );
            };
        }

        public void start() {
            producer.start();
            consumer.start();
        }

        public void stop() {
            producer.interrupt();
        }

        public void awaitTermination() throws InterruptedException {
            consumer.join();
        }
    }
}

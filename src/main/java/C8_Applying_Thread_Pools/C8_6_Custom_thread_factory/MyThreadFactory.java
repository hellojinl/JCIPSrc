package C8_Applying_Thread_Pools.C8_6_Custom_thread_factory;

import java.util.concurrent.ThreadFactory;

import C8_Applying_Thread_Pools.C8_7_Custom_thread_base_class.MyAppThread;

/**
 * MyThreadFactory
 * <p/>
 * Custom thread factory
 *
 * @author Brian Goetz and Tim Peierls
 */
public class MyThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    public Thread newThread(Runnable runnable) {
        return new MyAppThread( runnable, poolName );
    }
}

package C2_threadsafety.C2_7_Code_that_would_deadlock_if_intrinsic_locks_were_not_reentrant;

import org.junit.Test;

public class NonreentrantDeadlockImplTest {

    @Test
    public void test_reentrant() {
        LoggingWidget loggingWidget = new LoggingWidget();
        loggingWidget.doSomething();

        // 测试能运行完成，就说明可重入，没有出现死锁
    }

}

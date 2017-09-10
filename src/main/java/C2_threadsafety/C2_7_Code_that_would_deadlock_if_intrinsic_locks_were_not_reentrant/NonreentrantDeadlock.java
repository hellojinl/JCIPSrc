package C2_threadsafety.C2_7_Code_that_would_deadlock_if_intrinsic_locks_were_not_reentrant;

/**
 * NonreentrantDeadlock
 * <p/>
 * Code that would deadlock if intrinsic locks were not reentrant
 *
 * @author Brian Goetz and Tim Peierls
 */

class Widget {
    public synchronized void doSomething() { // 锁对象是Widget
    }
}

class LoggingWidget extends Widget {
    public synchronized void doSomething() { // 锁对象是LoggingWidget
        System.out.println( toString() + ": calling doSomething" );
        super.doSomething(); // 锁对象是LoggingWidget，验证程序见SynchronizedTargetTest
    }
}

// 说明：
// intrinsic locks指内在的锁，这里指synchronized关键字（通常说的获取锁是谁获取锁？是执行代码的线程）
// 如果是非静态方法，那么
//
// public synchronized void doSomething() {
// }
// 等价于
// public void doSomething() {
// synchronized( this ) {
// }
// }
//
// reentrant指可重入
// Code that would deadlock if intrinsic locks were not reentrant
// 讲的是，内置的锁机制如果不可重入，那么代码将死锁
//
// 例如：
// 1.代码调用 LoggingWidget实例的doSomething()方法 | => 获得LoggingWidget实例作为锁
// 2.在1的内部调用super.doSomething() | => 因为内置锁不可重入（假设），那么super.doSomething()将等待
// LoggingWidget实例的doSomething()方法释放锁，才能继续运行，
// | 而LoggingWidget实例的doSomething()方法将等待super.doSomething()的完成才能释放锁，
// | 死锁！
//

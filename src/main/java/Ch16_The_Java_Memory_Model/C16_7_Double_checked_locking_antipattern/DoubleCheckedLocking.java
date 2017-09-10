package Ch16_The_Java_Memory_Model.C16_7_Double_checked_locking_antipattern;

import support.annotations.Evaluated;
import support.annotations.NotThreadSafe;

/**
 * DoubleCheckedLocking
 * <p/>
 * Double-checked-locking antipattern
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class DoubleCheckedLocking {
    private static Resource resource;

    @Evaluated(">_<，不要这么做")
    public static Resource getInstance() {
        if (resource == null) {
            synchronized ( DoubleCheckedLocking.class ) {
                if (resource == null)
                    resource = new Resource();
            }
        }
        return resource; // 问题出在这句话，它没有使用同步，那么它的发布就是不安全的，
                         // 有可能出现这种情况，调用线程能看到resource的引用， 但是resource的状态确是失效的
                         // ResourceFactory是更好的选择

    }

    static class Resource {

    }
}

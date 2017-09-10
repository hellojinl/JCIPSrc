package Ch16_The_Java_Memory_Model.C16_3_Unsafe_lazy_initialization;

import support.annotations.Evaluated;
import support.annotations.NotThreadSafe;

/**
 * UnsafeLazyInitialization
 * <p/>
 * Unsafe lazy initialization
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class UnsafeLazyInitialization {
    private static Resource resource;

    @Evaluated(">_<")
    public static Resource getInstance() {
        if (resource == null)
            resource = new Resource(); // unsafe publication
        return resource;
    }

    static class Resource {
    }
}

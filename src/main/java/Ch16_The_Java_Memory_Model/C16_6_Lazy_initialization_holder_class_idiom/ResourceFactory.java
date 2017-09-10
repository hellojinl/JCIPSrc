package Ch16_The_Java_Memory_Model.C16_6_Lazy_initialization_holder_class_idiom;

import support.annotations.ThreadSafe;

/**
 * ResourceFactory
 * <p/>
 * Lazy initialization holder class idiom
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ResourceFactory {
    private static class ResourceHolder { // 内部静态类延时加载，不用不加载
        public static Resource resource = new Resource();
    }

    public static Resource getResource() {
        return ResourceFactory.ResourceHolder.resource;
    }

    static class Resource {
    }
}

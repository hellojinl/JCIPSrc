package Ch16_The_Java_Memory_Model.C16_6_Lazy_initialization_holder_class_idiom;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import Ch16_The_Java_Memory_Model.C16_6_Lazy_initialization_holder_class_idiom.ResourceFactoryTest.ResourceFactory.Resource;

/**
 * 探讨下内部类的初始化顺序
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ResourceFactoryTest {

    @Test
    public void test_getResource() {
        System.out.println( "---- test_getResource ----" );
        Resource r1 = ResourceFactory.getResource();
        assertNotNull( r1 );

        // 运行结果表示，调用getResource之后，才开始初始化类Resource和类ResourceHolder
        // ResourceFactory clinit
        // ResourceFactory getResource
        // (inner static class) Resource clinit
        // (inner static class) ResourceHolder clinit
    }

    @Test
    public void test_doNothing() {
        System.out.println( "---- test_doNothing ----" );
        ResourceFactory2.doNothing();

        // 运行结果表明，不使用就不初始化内部静态类
        // ResourceFactory2 clinit
        // do nothing
    }

    static class ResourceFactory {

        private static class ResourceHolder { // 内部静态类延时加载，不用不加载
            public static Resource resource = new Resource();

            static {
                System.out.println( "(inner static class) ResourceHolder clinit" );
            }
        }

        static {
            System.out.println( "ResourceFactory clinit" );
        }

        public static Resource getResource() {
            System.out.println( "ResourceFactory getResource" );
            return ResourceFactory.ResourceHolder.resource;
        }

        static class Resource {
            static {
                System.out.println( "(inner static class) Resource clinit" );
            }

        }
    }

    static class ResourceFactory2 {

        private static class ResourceHolder2 { // 内部静态类延时加载，不用不加载
            public static Resource2 resource = new Resource2();

            static {
                System.out.println( "(inner static class) ResourceHolder2 clinit" );
            }
        }

        static {
            System.out.println( "ResourceFactory2 clinit" );
        }

        public static Resource2 getResource2() {
            System.out.println( "ResourceFactory2 getResource" );
            return ResourceFactory2.ResourceHolder2.resource;
        }

        public static void doNothing() {
            System.out.println( "do nothing" );
        }

        static class Resource2 {
            static {
                System.out.println( "(inner static class) Resource2 clinit" );
            }

        }
    }
}

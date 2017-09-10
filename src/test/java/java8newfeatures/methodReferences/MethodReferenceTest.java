package java8newfeatures.methodReferences;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

import org.junit.Test;

/**
 * 把{@link MethodReference}做了个简单的分解和补充，使其更清晰
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class MethodReferenceTest {

    /**
     * 方法引用::引用构造函数
     * <p>
     * 这几种方式是等价的
     */
    @Test
    public void test_new() {
        // 方法引用::引用构造函数
        PersonFactory factory = new PersonFactory( Person::new );
        Person person = factory.getPerson();
        person.setName( "No one" );

        // Lambda表达式一
        PersonFactory factory2 = new PersonFactory( () -> new Person() );
        Person person2 = factory2.getPerson();
        person2.setName( "No two" );

        // Lambda表达式二
        PersonFactory factory3 = new PersonFactory( () -> {
            return new Person();
        } );
        Person person3 = factory3.getPerson();
        person3.setName( "No three" );

        // 匿名类的方式
        PersonFactory factory4 = new PersonFactory( new Supplier< Person >() {
            @Override
            public Person get() {
                return new Person();
            }
        } );
        Person person4 = factory4.getPerson();
        person4.setName( "I'm Arya Stark" );
    }

    /**
     * 方法引用::引用静态方法
     * <p>
     * 这几种方式是等价的
     */
    @Test
    public void test_static_function() {
        System.out.println( "test_static_function:" );

        PersonFactory factory = new PersonFactory( Person::new );
        final Person one = factory.getPerson();
        one.setName( "one" );
        final Person two = factory.getPerson();
        two.setName( "two" );
        final Person three = factory.getPerson();
        three.setName( "three" );

        // 方法引用::引用静态方法
        Person[] people = { one, two, three };
        Arrays.parallelSort( people, MethodReference::myCompare );
        Arrays.stream( people ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: MethodReference::myCompare ) " );
        // 引用静态方法对参数列表是有要求的, 如下调用将提示编译错误
        // Arrays.parallelSort( people, MethodReference::printArray ); // The
        // type MethodReference does not define printArray(T, T) that is
        // applicable here

        // Lambda表达式
        Person[] people2 = { one, two, three };
        Arrays.parallelSort( people2, (x, y) -> MethodReference.myCompare( x, y ) );
        Arrays.stream( people2 ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: ( x, y ) -> MethodReference.myCompare( x, y ) ) " );

        // 匿名类的方式
        Person[] people3 = { one, two, three };
        Arrays.parallelSort( people3, new Comparator< Person >() {

            @Override
            public int compare(Person x, Person y) {
                return MethodReference.myCompare( x, y );
            }

        } );
        Arrays.stream( people3 ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: new Comparator<Person>() {..MethodReference.myCompare( x, y )...} ) " );
        System.out.println();

    }

    /**
     * 方法引用::用特定对象的实例方法
     * <p>
     * 这几种方式是等价的
     */
    @Test
    public void test_fixed_instance_function() {
        System.out.println( "test_fixed_instance_function:" );

        PersonFactory factory = new PersonFactory( Person::new );
        final Person one = factory.getPerson();
        one.setName( "one" );
        final Person two = factory.getPerson();
        two.setName( "two" );
        final Person three = factory.getPerson();
        three.setName( "three" );

        // 方法引用::用特定对象的实例方法
        Person[] people = { one, two, three };
        Arrays.parallelSort( people, one::compare );
        Arrays.stream( people ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: one::compare ) " );

        // Lambda表达式
        Person[] people2 = { one, two, three };
        Arrays.parallelSort( people2, (x, y) -> one.compare( x, y ) );
        Arrays.stream( people2 ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: ( x, y ) -> one.compare( x, y ) ) " );

        // 匿名类的方式
        Person[] people3 = { one, two, three };
        Arrays.parallelSort( people3, new Comparator< Person >() {

            @Override
            public int compare(Person x, Person y) {
                return one.compare( x, y );
            }

        } );
        Arrays.stream( people3 ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: new Comparator<Person>() {...one.compare( x, y )...} ) " );
        System.out.println();
    }

    /**
     * 方法引用::引用特定类型的任意对象的实例方法
     */
    @Test
    public void test_uncertain_instance_function() {
        System.out.println( "test_uncertain_instance_function:" );

        PersonFactory factory = new PersonFactory( Person::new );
        final Person one = factory.getPerson();
        one.setName( "one" );
        final Person two = factory.getPerson();
        two.setName( "two" );
        final Person three = factory.getPerson();
        three.setName( "three" );

        // 方法引用::引用特定类型的任意对象的实例方法
        Person[] people = { one, two, three };
        Arrays.sort( people, Person::compareTo );
        Arrays.stream( people ).forEach( p -> System.out.print( p.getName() + " " ) );
        System.out.println( "( use: Person::compareTo ) " );
        System.out.println();
    }
}

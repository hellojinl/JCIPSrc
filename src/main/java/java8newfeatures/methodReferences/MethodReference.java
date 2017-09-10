package java8newfeatures.methodReferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 8之方法引用介绍
 *
 * @author kimy
 * @see <a href="http://blog.csdn.net/kimylrong/article/details/47255123">Java
 *      8之方法引用(Method References)</a>
 */
public class MethodReference {
    public static void main(String[] args) {
        // 方法引用::引用构造函数
        PersonFactory factory = new PersonFactory( Person::new );

        List< Person > personList = new ArrayList< Person >();

        Person p1 = factory.getPerson();
        p1.setName( "Kobe" );
        personList.add( p1 );
        Person p2 = factory.getPerson();
        p2.setName( "James" );
        personList.add( p2 );
        Person p3 = factory.getPerson();
        p3.setName( "Paul" );
        personList.add( p3 );

        Person[] persons1 = personList.toArray( new Person[ personList.size() ] );
        System.out.print( "排序前: " );
        printArray( persons1 );

        // 方法引用::引用静态方法
        Arrays.sort( persons1, MethodReference::myCompare );
        System.out.print( "排序后: " );
        printArray( persons1 );
        System.out.println();

        Person[] persons2 = personList.toArray( new Person[ personList.size() ] );
        System.out.print( "排序前: " );
        printArray( persons2 );

        // 方法引用::用特定对象的实例方法
        Arrays.sort( persons2, p1::compare );
        System.out.print( "排序后: " );
        printArray( persons2 );
        System.out.println();

        Person[] persons3 = personList.toArray( new Person[ personList.size() ] );
        System.out.print( "排序前: " );
        printArray( persons3 );

        // 方法引用::引用特定类型的任意对象的实例方法
        Arrays.sort( persons3, Person::compareTo );
        System.out.print( "排序后: " );
        printArray( persons3 );
    }

    public static void printArray(Person[] persons) {
        for (Person p : persons) {
            System.out.print( p.getName() + "  " );
        }
        System.out.println();
    }

    public static int myCompare(Person p1, Person p2) {
        return p1.getName().compareTo( p2.getName() );
    }

}

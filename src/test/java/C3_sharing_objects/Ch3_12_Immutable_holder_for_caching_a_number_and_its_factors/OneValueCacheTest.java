package C3_sharing_objects.Ch3_12_Immutable_holder_for_caching_a_number_and_its_factors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

public class OneValueCacheTest {

    @Test
    public void test_Immutable() {
        BigInteger lastNumber = new BigInteger( "8" );
        BigInteger two = new BigInteger( "2" );
        BigInteger[] lastFactors = new BigInteger[] { two, two, two };
        OneValueCache oneValueCache = new OneValueCache( lastNumber, lastFactors );
        BigInteger[] returnedFactors = oneValueCache.getFactors( lastNumber );

        // OneValueCache构造函数中的Arrays.copyOf和getFactors中的Arrays.copyOf是缺一不可的
        // 因为final BigInteger[] lastFactors并不能保证对其元素的修改（比如lastFactors[0]=new
        // BigInteger( "100" )）
        assertTrue( lastFactors != returnedFactors );

        // 这里用的是==，为了说明Arrays.copyOf其实是个浅拷贝
        assertTrue( lastFactors[0] == returnedFactors[0] );
        assertTrue( lastFactors[1] == returnedFactors[1] );
        assertTrue( lastFactors[2] == returnedFactors[2] );
        assertTrue( two == returnedFactors[0] );

        // 由于BigInteger是不可变类，故即使是浅拷贝，也能保证OneValueCache的不可变性
        // 但如果BigInteger被替换为一个可变的类型（比如你自己写代码的时候），那么OneValueCache这种写法将无法保证不可变性
    }

    @Test
    public void test_Immutable_error() {
        Person[] persons = new Person[] { new Person( "a" ), new Person( "b" ), new Person( "c" ) };
        PersonCache cache = new PersonCache( persons );
        Person[] cachedPersons = cache.getPersons();

        // persons != cachedPersons似乎说明了PersonCache是不可变的
        assertTrue( persons != cachedPersons );
        assertTrue( persons[0] == cachedPersons[0] );
        assertTrue( persons[1] == cachedPersons[1] );
        assertTrue( persons[2] == cachedPersons[2] );

        // 然而PersonCache是可变的
        String oldName = cachedPersons[0].getName();
        cachedPersons[0].setName( "d" ); // 这个操作将修改PersonCache的内部状态。
        String cachedName = cache.getPersons()[0].getName();
        assertFalse( oldName.equals( cachedName ) );
        assertEquals( "d", cachedName ); // cache中的值被修改了

    }

    class Person {
        private String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class PersonCache {
        private final Person[] lastPersons;

        public PersonCache(Person[] persons) {
            this.lastPersons = Arrays.copyOf( persons, persons.length );
        }

        public Person[] getPersons() {
            return Arrays.copyOf( lastPersons, lastPersons.length );
        }
    }
}

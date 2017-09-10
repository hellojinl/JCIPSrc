package C3_sharing_objects.C3_9_Thread_confinement_of_local_primitive_and_reference_variables;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Animals
 * <p/>
 * Thread confinement of local primitive and reference variables
 *
 * @author Brian Goetz and Tim Peierls
 * @see <a href=
 *      "http://www.cnblogs.com/danbing/p/5023231.html">Java中堆内存（heap）和栈内存（stack）的区别</a>
 */
public class Animals {
    Ark ark;
    Species species;
    Gender gender;

    public int loadTheArk(Collection< Animal > candidates) {
        SortedSet< Animal > animals; // SortedSet的引用变量，存在于线程独有的Stack中，封闭在线程中
        int numPairs = 0; // 基本类型的局部变量，存在于线程独有的Stack中，封闭在线程中
        Animal candidate = null;

        // animals confined to method, don't let them escape!
        // TreeSet对象存在于非线程独有的Heap中，但是它的引用只有一个，即animals，
        // 只要不将TreeSet对象的引用从本方法里发布出去，那么TreeSet对象也是封闭在线程中的。
        // 因为当animals被回收的时候，TreeSet对象的引用数量减少为0，它将被垃圾回收机制回收，
        // 可以粗略的认为TreeSet对象的生命周期等于animals
        animals = new TreeSet< Animal >( new SpeciesGenderComparator() );
        animals.addAll( candidates );
        for (Animal a : animals) {
            if (candidate == null || !candidate.isPotentialMate( a ))
                candidate = a;
            else {
                ark.load( new AnimalPair( candidate, a ) );
                ++numPairs;
                candidate = null;
            }
        }
        return numPairs;
    }

    class Animal {
        Species species;
        Gender gender;

        public boolean isPotentialMate(Animal other) {
            return species == other.species && gender != other.gender;
        }
    }

    enum Species {
        AARDVARK, BENGAL_TIGER, CARIBOU, DINGO, ELEPHANT, FROG, GNU, HYENA, IGUANA, JAGUAR, KIWI, LEOPARD, MASTADON, NEWT, OCTOPUS, PIRANHA, QUETZAL, RHINOCEROS, SALAMANDER, THREE_TOED_SLOTH, UNICORN, VIPER, WEREWOLF, XANTHUS_HUMMINBIRD, YAK, ZEBRA
    }

    enum Gender {
        MALE, FEMALE
    }

    class AnimalPair {
        private final Animal one, two;

        public AnimalPair(Animal one, Animal two) {
            this.one = one;
            this.two = two;
        }
    }

    class SpeciesGenderComparator implements Comparator< Animal > {
        public int compare(Animal one, Animal two) {
            int speciesCompare = one.species.compareTo( two.species );
            return (speciesCompare != 0) ? speciesCompare : one.gender.compareTo( two.gender );
        }
    }

    class Ark {
        private final Set< AnimalPair > loadedAnimals = new HashSet< AnimalPair >();

        public void load(AnimalPair pair) {
            loadedAnimals.add( pair );
        }
    }
}

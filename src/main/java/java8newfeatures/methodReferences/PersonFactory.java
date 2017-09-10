package java8newfeatures.methodReferences;

import java.util.function.Supplier;

/**
 * 
 * @author kimy
 */
public class PersonFactory {
    private Supplier< Person > supplier;

    public PersonFactory(Supplier< Person > supplier) {
        this.supplier = supplier;
    }

    public Person getPerson() {
        return supplier.get();
    }
}

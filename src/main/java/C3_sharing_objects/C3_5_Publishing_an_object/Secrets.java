package C3_sharing_objects.C3_5_Publishing_an_object;

import java.util.HashSet;
import java.util.Set;

/**
 * Secrets
 *
 * Publishing an object
 *
 * @author Brian Goetz and Tim Peierls
 */
class Secrets {
    public static Set< Secret > knownSecrets;

    public void initialize() {
        knownSecrets = new HashSet< Secret >();
    }
}

class Secret {
}

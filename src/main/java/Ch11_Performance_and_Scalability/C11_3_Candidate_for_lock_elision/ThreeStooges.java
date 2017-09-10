package Ch11_Performance_and_Scalability.C11_3_Candidate_for_lock_elision;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import support.annotations.Immutable;

/**
 * ThreeStooges
 * <p/>
 * Immutable class built out of mutable underlying objects, demonstration of
 * candidate for lock elision
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public final class ThreeStooges {
    private final Set< String > stooges = new HashSet< String >();

    public ThreeStooges() {
        stooges.add( "Moe" );
        stooges.add( "Larry" );
        stooges.add( "Curly" );
    }

    public boolean isStooge(String name) {
        return stooges.contains( name );
    }

    public String getStoogeNames() {
        List< String > stooges = new Vector< String >();
        stooges.add( "Moe" );
        stooges.add( "Larry" );
        stooges.add( "Curly" );
        return stooges.toString();
    }
}

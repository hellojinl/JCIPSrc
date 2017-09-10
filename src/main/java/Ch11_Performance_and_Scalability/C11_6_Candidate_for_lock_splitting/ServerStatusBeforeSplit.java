package Ch11_Performance_and_Scalability.C11_6_Candidate_for_lock_splitting;

import java.util.HashSet;
import java.util.Set;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * ServerStatusBeforeSplit
 * <p/>
 * Candidate for lock splitting
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ServerStatusBeforeSplit {
    @GuardedBy("this")
    public final Set< String > users;
    @GuardedBy("this")
    public final Set< String > queries;

    public ServerStatusBeforeSplit() {
        users = new HashSet< String >();
        queries = new HashSet< String >();
    }

    public synchronized void addUser(String u) {
        users.add( u );
    }

    public synchronized void addQuery(String q) {
        queries.add( q );
    }

    public synchronized void removeUser(String u) {
        users.remove( u );
    }

    public synchronized void removeQuery(String q) {
        queries.remove( q );
    }
}

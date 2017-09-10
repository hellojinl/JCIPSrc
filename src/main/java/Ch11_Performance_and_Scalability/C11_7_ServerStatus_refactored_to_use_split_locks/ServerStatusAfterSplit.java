package Ch11_Performance_and_Scalability.C11_7_ServerStatus_refactored_to_use_split_locks;

import java.util.HashSet;
import java.util.Set;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * ServerStatusAfterSplit
 * <p/>
 * ServerStatus refactored to use split locks
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ServerStatusAfterSplit {
    @GuardedBy("users")
    public final Set< String > users;
    @GuardedBy("queries")
    public final Set< String > queries;

    public ServerStatusAfterSplit() {
        users = new HashSet< String >();
        queries = new HashSet< String >();
    }

    public void addUser(String u) {
        synchronized ( users ) {
            users.add( u );
        }
    }

    public void addQuery(String q) {
        synchronized ( queries ) {
            queries.add( q );
        }
    }

    public void removeUser(String u) {
        synchronized ( users ) {
            users.remove( u );
        }
    }

    public void removeQuery(String q) {
        synchronized ( users ) {
            queries.remove( q );
        }
    }
}

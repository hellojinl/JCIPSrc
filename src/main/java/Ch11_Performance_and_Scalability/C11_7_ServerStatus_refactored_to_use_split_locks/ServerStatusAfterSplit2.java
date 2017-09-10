package Ch11_Performance_and_Scalability.C11_7_ServerStatus_refactored_to_use_split_locks;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import support.annotations.ThreadSafe;

/**
 * 将用户状态和查询状态委托给一个线程安全的Set
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@ThreadSafe
public class ServerStatusAfterSplit2 {
    public final Set< String > users;
    public final Set< String > queries;

    public ServerStatusAfterSplit2() {
        users = new ConcurrentSkipListSet< String >();
        queries = new ConcurrentSkipListSet< String >();
    }

    public void addUser(String u) {
        users.add( u );
    }

    public void addQuery(String q) {
        queries.add( q );
    }

    public void removeUser(String u) {
        users.remove( u );
    }

    public void removeQuery(String q) {
        queries.remove( q );
    }
}

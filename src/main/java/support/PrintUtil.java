package support;

/**
 * 输出工具
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public final class PrintUtil {

    private PrintUtil() {
    }

    public static <T> void printlnArray(T... elements) {
        printArray( elements );
        println();
    }

    public static <T> void printArray(T... elements) {
        System.out.print( arrayToString( elements ) );
    }

    public static <T> String arrayToString(T... elements) {
        if (elements == null || elements.length < 1) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder( "[ " );
        sb.append( elements[0].toString() );
        for (int i = 1; i < elements.length; i++) {
            sb.append( ", " );
            sb.append( elements[i].toString() );
        }

        sb.append( " ]" );
        return sb.toString();
    }

    public static void println() {
        System.out.println();
    }

    public static void currentThreadPrintln(String message) {
        System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId() + "] " + message );
    }
}

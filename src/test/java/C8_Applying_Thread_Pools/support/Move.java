package C8_Applying_Thread_Pools.support;

/**
 * 移动
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public interface Move<P> {

    /**
     * 移动
     * 
     * @throws NullPointerException
     *             if from == null
     */
    P moveTo(P from);

    /**
     * 尝试执行移动
     * 
     * @return true - 可以到达，false - 不可以到达
     * @throws NullPointerException
     *             if from == null
     */
    default boolean attempt(P from) {
        return moveTo( from ) != PuzzlePoint.ILLEGAL_POINT;
    }

    /**
     * 移动之后的终点
     */
    P getDestination();

    /**
     * 移动名称
     */
    String description();
}

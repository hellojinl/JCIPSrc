package Ch14_Building_Custom_Synchronizers.C14_3_Bounded_buffer_that_balks_when_preconditions_are_not_met;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class GrumpyBoundedBufferTest {

    @Test(expected = BufferFullException.class)
    public void test_put() {
        GrumpyBoundedBuffer< String > buffer = new GrumpyBoundedBuffer<>( 1 );
        buffer.put( "I'm about to explode #_*" );
        buffer.put( "I'm about to explode $_$" );
    }

    @Test(expected = BufferEmptyException.class)
    public void test_take() {
        GrumpyBoundedBuffer< String > buffer = new GrumpyBoundedBuffer<>( 1 );
        buffer.take();
    }
}

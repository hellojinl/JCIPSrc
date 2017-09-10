package C7_Cancellation_and_Shutdown.C7_1_Using_a_volatile_field_to_hold_cancellation_state;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

public class PrimeGeneratorTest {

    @Test
    public void justRunIt() throws InterruptedException {
        List< BigInteger > primes = PrimeGenerator.aSecondOfPrimes();
        primes.forEach( p -> {
            System.out.println( p + ", " );
        } );
    }

}

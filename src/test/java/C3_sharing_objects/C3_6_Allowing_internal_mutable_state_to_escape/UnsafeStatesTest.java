package C3_sharing_objects.C3_6_Allowing_internal_mutable_state_to_escape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnsafeStatesTest {

    @Test
    public void test() {

        UnsafeStatesImpl unsafeStates = new UnsafeStatesImpl();
        String[] states = unsafeStates.getStates();
        states[0] = "BK";
        states[2] = "BO";
        states[4] = "BQ";

        String[] newStates = unsafeStates.getStates();
        assertTrue( states == newStates ); // 2次返回的States是同一个对象

        // 说明原本private的states变量，在对象外部被修改了
        assertEquals( "BK", newStates[0] );
        assertEquals( "BO", newStates[2] );
        assertEquals( "BQ", newStates[4] );
    }

    class UnsafeStatesImpl {
        private String[] states = new String[] { "AK", "AL", "AO", "AP", "AQ", "AR", };

        public String[] getStates() {
            return states;
        }
    }

}

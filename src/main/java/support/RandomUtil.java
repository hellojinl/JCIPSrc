package support;

import java.util.Random;

public final class RandomUtil {

    private final static Random random = new Random();

    private RandomUtil() {

    }

    public static int get(int min, int max) {
        return random.nextInt( max ) % (max - min + 1) + min;
    }

}

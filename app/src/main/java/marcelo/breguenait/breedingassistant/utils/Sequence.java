package marcelo.breguenait.breedingassistant.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marcelo on 01/03/2017.
 */

public class Sequence {

    private static final AtomicInteger counter = new AtomicInteger();

    public static int nextValue() {
        return counter.getAndIncrement();
    }
}
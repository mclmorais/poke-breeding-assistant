package marcelo.breguenait.breedingassistant.utils;

import android.support.annotation.IntDef;

/**
 * Created by Marcelo on 19/03/2017.
 */

public class Stats {

    public static final int HP = 0;
    private static final int ATK = 1;
    private static final int DEF = 2;
    private static final int SATK = 3;
    private static final int SDEF = 4;
    private static final int SPD = 5;

    @IntDef({HP, ATK, DEF, SATK, SDEF, SPD})
    public @interface StatFlag {}

}

package marcelo.breguenait.breedingassistant.utils;

import android.support.annotation.IntDef;

/**
 * Created by Marcelo on 19/03/2017.
 */

public class Stats {

    public static final int HP = 0, ATK = 1, DEF = 2, SATK = 3, SDEF = 4, SPD = 5;

    @IntDef({HP, ATK, DEF, SATK, SDEF, SPD})
    public @interface StatFlag {}

}

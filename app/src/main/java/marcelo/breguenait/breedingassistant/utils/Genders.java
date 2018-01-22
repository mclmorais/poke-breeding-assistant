package marcelo.breguenait.breedingassistant.utils;

import android.support.annotation.IntDef;


public class Genders {

    public static final int MALE = 0, FEMALE = 1000, GENDERLESS = 2000, DITTO = 3000;

    @IntDef({MALE, FEMALE, GENDERLESS, DITTO})
    public @interface GendersFlag {

    }

}

package marcelo.breguenait.breedingassistant.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

public class Utility {

    public static int calculateNumberOfColumns(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        int noOfColumns = (int) (dpWidth / px);
        return noOfColumns - 1;
    }


    public static Drawable getIcon(int id, Context context) {
        String iconId = "pkmn_link_" + String.format("%03d", id);
        return ContextCompat.getDrawable(
                context,
                context.getResources().getIdentifier(iconId, "drawable", context.getPackageName()));
    }

    public static int upTo(int desired, int max) {
        return desired > max ? max : desired;
    }

}
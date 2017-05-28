package eu.kalodiodev.customersnote;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/**
 * Test Helper
 *
 * Contains methods commonly used by all activities
 *
 * @author Athanasios Raptodimos
 */
public class TestHelper {

    /**
     * Change activity's orientation
     *
     * @param activity activity tested
     */
    public static void changeOrientation(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;

        switch (currentOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    /**
     * Check if screen is Large or XLarge
     *
     * @param activity activity tested
     * @return true if screen is large or xlarge, otherwise false
     */
    public static boolean isLargeScreen(Activity activity) {
        Configuration configuration = activity.getResources().getConfiguration();
        return ((configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) ||
                ((configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    }
}

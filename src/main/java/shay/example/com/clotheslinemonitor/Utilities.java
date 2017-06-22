package shay.example.com.clotheslinemonitor;

import android.app.Application;

/**
 * Created by Shay on 01/06/2017.
 */

public class Utilities extends Application
{
    private static int stateCounter;

    public void onCreate()
    {
        super.onCreate();
        stateCounter = 0;
    }

    /**
     * @return true if application is on background
     * */
    public static boolean isApplicationOnBackground()
    {
        return stateCounter == 0;
    }

    //to be called on each Activity onStart()
    public static void activityStarted()
    {
        stateCounter++;
    }

    //to be called on each Activity onStop()
    public static void activityStopped()
    {
        stateCounter--;
    }
}
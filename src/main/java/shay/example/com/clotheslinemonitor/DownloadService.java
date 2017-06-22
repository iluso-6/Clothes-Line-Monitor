package shay.example.com.clotheslinemonitor;

/**
 * Created by Shay on 21/05/2017.
 */


// This service is activated by the monitor switch in OptionsActivity

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static activities.OptionsActivity.REPEAT_MINUTES;


public class DownloadService extends Service {

    private static Timer timer;
    private Context ctx;
    int oneHour = 60000;
    int num_of_repeat_minutes;

    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        num_of_repeat_minutes = intent.getIntExtra(REPEAT_MINUTES, 20);
        Log.e("onHandleIntent", num_of_repeat_minutes + "");
        num_of_repeat_minutes = num_of_repeat_minutes * oneHour;
        Log.e("onHandleIntent", num_of_repeat_minutes + "");
        startService();
        return flags;
    }

    public void onCreate() {
        super.onCreate();
        ctx = this;
        timer = new Timer();


    }

    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, num_of_repeat_minutes);
    }


    private class mainTask extends TimerTask {
        public void run() {
            DoAsyncRefresh.refresh();
            checkForRain();
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Toast.makeText(this, "Service Stopped .DownloadService", Toast.LENGTH_SHORT).show();
    }

    private void checkForRain() {

        Integer[] precipProbability = UI_DataHolder.getPrecipProbability();
        int nextRainValue = precipProbability[1];// rain due in the next hour
        if(Utilities.isApplicationOnBackground())
        {
            //you should want to check here if your application is on background before possibly sending notification
            Log.e("Utilities","isApplicationOnBackground");
                if (nextRainValue > 0) {
                    // set notification now in the background
                    MyNotification.createNotification(this.getApplicationContext());
                    Log.e("MyNotification started", "DownloadService");

                }

        }


    }


    private final Handler toastHandler = new Handler() // use this workaround to display toast message on different thread
    {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "Timed DownloadService", Toast.LENGTH_SHORT).show();
        }
    };

}


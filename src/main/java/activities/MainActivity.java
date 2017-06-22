package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import shay.example.com.clotheslinemonitor.CalculateWeatherRating;
import shay.example.com.clotheslinemonitor.DoAsyncRefresh;
import shay.example.com.clotheslinemonitor.MyAdapter;
import shay.example.com.clotheslinemonitor.MyNotification;
import shay.example.com.clotheslinemonitor.R;
import shay.example.com.clotheslinemonitor.UI_DataHolder;
import shay.example.com.clotheslinemonitor.Utilities;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG_SETTINGS = "SETTINGS";
    private static final String TAG_REFRESH = "REFRESH";
    private static final String TAG_GPS = "GPS";
    public static MyAdapter myAdapter;
    public static FloatingActionMenu actionMenu;
    public static long[] weatherTime;
    public static String[] weatherInfo;
    private static MainActivity myActivity;
    ListView mListView;


    public static MainActivity getMyActivity() {
        return myActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Main", "onCreate");
        super.onCreate(savedInstanceState);
        MainActivity.myActivity = this;
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        int TOTAL_DATA_HOURS = 16;// pre load maximum data length available to display
        final int[] rainAlert = new int[TOTAL_DATA_HOURS];
        final int[] weatherImage = new int[TOTAL_DATA_HOURS];

        for (int i = 0; i < TOTAL_DATA_HOURS; i++) {
            weatherImage[i] = CalculateWeatherRating.getRating(i);
            rainAlert[i] = CalculateWeatherRating.getRainCondition(i);
        }

        weatherInfo = UI_DataHolder.getSummary();
        weatherTime = UI_DataHolder.getWeatherTime();
        mListView = (ListView) findViewById(R.id.listview);
        myAdapter = new MyAdapter(MainActivity.this, weatherInfo, weatherImage, rainAlert, weatherTime);
        mListView.setAdapter(myAdapter);

        ImageView fbIcon = new ImageView(this); // Create an fbIcon;
        boolean switchState = PreferenceHelper.getSharedPreferenceBoolean(this, OptionsActivity.MONITOR_SWITCH, OptionsActivity.defaultMonitorState);

        if (switchState) {               // getter in this class from shared prefs
            fbIcon.setImageResource(R.drawable.switch_on);
        } else {
            fbIcon.setImageResource(R.drawable.switch_off);
            PreferenceHelper.setSharedPreferenceBoolean(this, OptionsActivity.MONITOR_SWITCH, false);
        }

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(fbIcon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.drawable.gps);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon1).build();
        button1.setTag(TAG_GPS);

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.drawable.fbsettings);
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
        button2.setTag(TAG_SETTINGS);

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.drawable.fbrefresh);
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();
        button3.setTag(TAG_REFRESH);


        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .attachTo(actionButton)
                .build();

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        //addNotification();


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(MainActivity.this, DetailHourActivity.class);
                mIntent.putExtra("weatherInfo", weatherInfo[i]);
                mIntent.putExtra("weatherImage", weatherImage[i]);
                mIntent.putExtra("position", i);
                startActivity(mIntent);

                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        String[] positions = new String[2];
        if (v.getTag().equals(TAG_REFRESH)) {

            refreshMainDisplay();


        } else if (v.getTag().equals(TAG_SETTINGS)) {

            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
            this.finish();
        } else if (v.getTag().equals(TAG_GPS)) {
      // get new location for user
            MyNotification.createNotification(this);
        }
        }





    @Override
    public void onBackPressed() {// show alert dialog to check if user wants to exit the application
        showExitDialog();
    }

    private void showExitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to exit?");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("Exit Application");
        alertDialog.setPositiveButton("Yes", null);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                } // close the floating button if open
                MainActivity.this.finish(); // close the application
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (actionMenu.isOpen()) {
                    actionMenu.close(true);
                }
                // nothing else needed here .. same as cancel
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.create();
        alertDialog.show();

    }

    public void refreshMainDisplay() {
        DoAsyncRefresh.refresh();

        this.finish();
        startActivity(getIntent());
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Utilities.activityStarted();
    }

    @Override
    public void onStop()
    {
        Utilities.activityStopped();
    /*    if(Utilities.isApplicationOnBackground())
        {
            //you should want to check here if your application is on background
            Log.e("Utilities","isApplicationOnBackground");
        }*/
        super.onStop();
    }


}


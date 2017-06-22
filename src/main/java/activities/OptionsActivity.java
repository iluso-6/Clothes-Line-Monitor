package activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import shay.example.com.clotheslinemonitor.DownloadService;
import shay.example.com.clotheslinemonitor.R;
import shay.example.com.clotheslinemonitor.UI_DataHolder;
import shay.example.com.clotheslinemonitor.Utilities;

public class OptionsActivity extends AppCompatActivity {
    public static final String SETTINGS = "settings"; // parent key for sharedPrefs
    public static final String HOUR_BTN = "hourBtn"; // key for sharedPrefs
    public static final String UPDATE_BTN = "updateBtn";// key for sharedPrefs
    public static final String MONITOR_SWITCH = "switch";// another child key for sharedPrefs
    public static final String REPEAT_MINUTES = "REPEAT_MINUTES";
    public static final String SERVICE_IS_RUNNING = "SERVICE_IS_RUNNING";
    public static int defaultRadioHours = 1;
    public static int defaultUpdateMinutes = 3;
    public static boolean defaultMonitorState = false;
    public static boolean defaultServiceState = false;

    private boolean service_Is_Running_State;

    RadioButton btn_displayHours[] = new RadioButton[4];
    RadioButton btn_updateMinutes[] = new RadioButton[5];

    Intent downloadIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        getSupportActionBar().setTitle(R.string.options_title);
        TextView street = (TextView) findViewById(R.id.textViewStreetTitle);
        String name = PreferenceHelper.getSharedPreferenceString(this, Splash.STREET, "");
        street.setText(name);

        createRadioButtons();
        updateCurrentPreferences();

    }


    private void updateCurrentPreferences() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        int checkedHourBtn = PreferenceHelper.getSharedPreferenceInt(this, HOUR_BTN, defaultRadioHours);
        int checkedUpdateBtn = PreferenceHelper.getSharedPreferenceInt(this, UPDATE_BTN, defaultUpdateMinutes);

        btn_displayHours[checkedHourBtn].setChecked(true);
        btn_updateMinutes[checkedUpdateBtn].setChecked(true);


        final Switch monitorSwitch = (Switch) findViewById(R.id.monitorSwitch);
        boolean checkedMonitorSw = prefs.getBoolean(MONITOR_SWITCH, defaultMonitorState);
        service_Is_Running_State = prefs.getBoolean(SERVICE_IS_RUNNING, defaultServiceState);
        setSwitchVisualState(checkedMonitorSw);
        UI_DataHolder.setMonitorSwitchState(checkedMonitorSw);


        monitorSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean state = monitorSwitch.isChecked();

                setSwitchVisualState(state); // set the text and switch pos
                setSwitchPreferences(state); // store the state
            }


        });


    }


    private void setSwitchVisualState(boolean state) {
        final Switch monitorSwitch = (Switch) findViewById(R.id.monitorSwitch);
        if (state) {
            monitorSwitch.setText(R.string.switch_on);
            startDownLoadService();
        } else {
            monitorSwitch.setText(R.string.switch_off);
            stopDownLoadService();
        }
        monitorSwitch.setChecked(state);

        enable_disable_MinsRadioBtns(state);
    }

    private void enable_disable_MinsRadioBtns(boolean state) { // enable minutes radio button changes only when monitor is off
        for (int i = 0; i < 5; i++) {
            btn_updateMinutes[i].setEnabled(!state);
        }
    }

    private void setSwitchPreferences(boolean state) {
        PreferenceHelper.setSharedPreferenceBoolean(this, MONITOR_SWITCH, state);

    }

    private void setRadioPreferences(String key, int value) {
        PreferenceHelper.setSharedPreferenceInt(this, key, value);
    }


    private void createRadioButtons() {
        RadioGroup hoursGroup = (RadioGroup) findViewById(R.id.radio_group_hours);
        int[] num_radioHours = getResources().getIntArray(R.array.display_hours);
        int loopNum = num_radioHours.length;

        for (int i = 0; i < loopNum; i++) {
            btn_displayHours[i] = new RadioButton(this);
            btn_displayHours[i].setId(i);
            String text = getString(R.string.hours_options, num_radioHours[i]); // insert value in strings " %1$d "
            Log.e("setRadioPreferences", num_radioHours[i] + "");
            btn_displayHours[i].setText(text);
            hoursGroup.addView(btn_displayHours[i]);
        }


        hoursGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                setRadioPreferences(HOUR_BTN, checkedId); // set which button is true
                int[] num_displayHours = getResources().getIntArray(R.array.display_hours); // get the hours value from int array <item> in options_values.xml
                UI_DataHolder.setDisplayHours(num_displayHours[checkedId]);
            }
        });

        RadioGroup updateMinutesGroup = (RadioGroup) findViewById(R.id.radio_group_update);
        int[] num_updateMins = getResources().getIntArray(R.array.update_mins); // int array xml for minutes
        int loopLength = num_updateMins.length;

        for (int i = 0; i < loopLength; i++) {
            btn_updateMinutes[i] = new RadioButton(this);
            btn_updateMinutes[i].setId(i);
            String text = getString(R.string.update_options, num_updateMins[i]); // insert value in strings " %1$d "
            btn_updateMinutes[i].setText(text);
            updateMinutesGroup.addView(btn_updateMinutes[i]);
        }

        updateMinutesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup updateGroup, @IdRes int checkedId) {

                setRadioPreferences(UPDATE_BTN, checkedId); // set which button is true
                int[] repeatMinutesArray = getResources().getIntArray(R.array.update_mins); // int array xml for minutes
                int actualMINS = repeatMinutesArray[checkedId];
                PreferenceHelper.setSharedPreferenceInt(getApplicationContext(), REPEAT_MINUTES, actualMINS);
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        Utilities.activityStarted(); // make aware that the application is in the foreground
        super.onStart();

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(OptionsActivity.this,
                    "Download complete.",
                    Toast.LENGTH_LONG).show();

        }
    };


    public void startDownLoadService() {
        if (!service_Is_Running_State) {// check that the service is running

            downloadIntent = new Intent(this, DownloadService.class);
            int int_repeat_variable = PreferenceHelper.getSharedPreferenceInt(this, REPEAT_MINUTES, 20);
            downloadIntent.putExtra(REPEAT_MINUTES, int_repeat_variable);
            startService(downloadIntent);
            Toast.makeText(OptionsActivity.this,
                    "startDownLoadService OptionsActivity",
                    Toast.LENGTH_LONG).show();
            service_Is_Running_State = true;
        }
    }

    public void stopDownLoadService() {
        if (service_Is_Running_State) {
            stopService(downloadIntent);
            Toast.makeText(OptionsActivity.this,
                    "Service stopped",
                    Toast.LENGTH_LONG).show();
            service_Is_Running_State = false;
        }
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

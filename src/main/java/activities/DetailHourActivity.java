package activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import shay.example.com.clotheslinemonitor.R;
import shay.example.com.clotheslinemonitor.UI_DataHolder;
import shay.example.com.clotheslinemonitor.Utilities;

public class DetailHourActivity extends AppCompatActivity {

    Toolbar detailToolbar;
    ImageView detail_ImageView;
    TextView tempView;
    TextView windView;
    TextView humidView;
    TextView rainProbView;
    TextView dewPointView;
    TextView cloudCoverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_details);
        getSupportActionBar().hide();
        detailToolbar = (Toolbar) findViewById(R.id.detailToolbar);
        detail_ImageView = (ImageView) findViewById(R.id.detail_ImageView);
        tempView = (TextView) findViewById(R.id.textViewTemp);
        windView = (TextView) findViewById(R.id.textViewWind);
        humidView = (TextView) findViewById(R.id.textViewHumidity);
        rainProbView = (TextView) findViewById(R.id.textViewRain);
        cloudCoverView = (TextView) findViewById(R.id.textViewCloudCover);
        dewPointView = (TextView) findViewById(R.id.textViewDew);

        Bundle mBundle = getIntent().getExtras();
        int pos = mBundle.getInt("position");
        String temp = getString(R.string.temp) + UI_DataHolder.getTemperature()[pos] + getString(R.string.celcius);
        String wind = getString(R.string.wind) + UI_DataHolder.getWindSpeed()[pos] + getString(R.string.mph);
        String humidity = getString(R.string.humidity) + UI_DataHolder.getHumidity()[pos] + getString(R.string.percent);
        String rainProb = getString(R.string.rain) + UI_DataHolder.getPrecipProbability()[pos] + getString(R.string.percent);
        String cloudCover = getString(R.string.cloud) + UI_DataHolder.getCloudCover()[pos] + getString(R.string.percent);
        String dewPoint = getString(R.string.dew) + UI_DataHolder.getDewPoint()[pos] + getString(R.string.celcius);

        if (mBundle != null) {
            detailToolbar.setTitle(mBundle.getString("weatherInfo"));
            detailToolbar.setLogo(R.drawable.weather_stats);
            detail_ImageView.setImageResource(mBundle.getInt("weatherImage"));
            tempView.setText(temp);
            windView.setText(wind);
            humidView.setText(humidity);
            rainProbView.setText(rainProb);
            cloudCoverView.setText(cloudCover);
            dewPointView.setText(dewPoint);
        }

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

package activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import shay.example.com.clotheslinemonitor.Function;
import shay.example.com.clotheslinemonitor.R;
import shay.example.com.clotheslinemonitor.UI_DataHolder;


/**
 * Created by Shay on 25/02/2017.
 */

public class Splash extends AppCompatActivity {
    private static final String NONE = "NONE";
    private static final String MYLATITUDE = "latitude";
    private static final String MYLONGITUDE = "longitude";
    private LocationManager locationManager;
    private Location gpsLocation;
    private boolean permit = false;
    public static final String STREET = "STREET";
    private int minutes = 60000;
    private int repeatValue = 20; // 20 minutes default loop to download api JSON
    private String geocodeURL;
    private String geocodeAPI;

    public static String latitude;
    public static String longitude;

    private boolean firstLaunch = true;
    private MediaPlayer mp;
    private TextView splashInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.splash_activity);

        final ImageView splashImg = (ImageView) findViewById(R.id.imageViewSplash);
        final Animation animOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);
        final Animation animIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scaleback);

        splashImg.startAnimation(animOut);
        // --------------         animation for main logo       -------------
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImg.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImg.startAnimation(animOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // --------------         end animation        -------------
        splashInfo = (TextView) findViewById(R.id.textViewSplash);
        geocodeURL = getResources().getString(R.string.geocode_url);
        geocodeAPI = getResources().getString(R.string.geocode_api);

        String checkLocationSet = PreferenceHelper.getSharedPreferenceString(this, MYLATITUDE, NONE);
        if (checkLocationSet.equals(NONE)) { // no location previously stored

            setNewLocation();

        } else {// we already have set the location - get the stored values

            latitude = PreferenceHelper.getSharedPreferenceString(this, MYLATITUDE, "53.2662");
            longitude = PreferenceHelper.getSharedPreferenceString(this, MYLONGITUDE, "-6.14312");
        }

        getJSONgeocodeAddress(latitude, longitude); // get human readable address from lat, lon

        mp = MediaPlayer.create(this, R.raw.intro);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                animIn.cancel();
                animOut.cancel();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit); // slide animation
                Splash.this.finish();
                mp.release(); // finished with the audio
            }
        });

        getNumberDisplayHours(); // needed before main activity is loaded with list adapter

        beginAsyncDownLoad(latitude, longitude); // one time call


        if (firstLaunch) {       // call once only ... on complete begins new activity
            mp.start();
            firstLaunch = false;
        }

    }

    private void setNewLocation() {
        // get permissions and location()
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getPermissions();
        if (permit) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            latitude = gpsLocation.getLatitude() + "";
            longitude = gpsLocation.getLongitude() + "";
            PreferenceHelper.setSharedPreferenceString(this, MYLATITUDE, latitude);
            PreferenceHelper.setSharedPreferenceString(this, MYLONGITUDE, longitude);

        } else {
            Toast.makeText(this, "No permission granted", Toast.LENGTH_LONG).show();
        }

    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        permit = true;
        gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    private void getNumberDisplayHours() {

        int checkedHourBtn = PreferenceHelper.getSharedPreferenceInt(this, OptionsActivity.HOUR_BTN, OptionsActivity.defaultRadioHours); // get the checked radio in settings activity
        int[] num_radioHours = getResources().getIntArray(R.array.display_hours); // get the list array of choices

        int numberOfListHours = num_radioHours[checkedHourBtn]; // get the chosen value
        UI_DataHolder.setDisplayHours(numberOfListHours); // set the value needed for the adapter - now available when launched

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    private void beginAsyncDownLoad(String lat, String lon) {
        Toast.makeText(getApplicationContext(), "beginAsyncDownLoad", Toast.LENGTH_SHORT).show();
        Function.placeIdTask asyncTask = new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String[] temperature, Integer[] precipProbability, Integer[] cloudCover, String[] dewPoint, Integer[] humidity, String[] windSpeed, String[] summary, long[] weather_time) {

                UI_DataHolder.setTemperature(temperature);
                UI_DataHolder.setPecipProbability(precipProbability);
                UI_DataHolder.setCloudCover(cloudCover);
                UI_DataHolder.setDewPoint(dewPoint);
                UI_DataHolder.setHumidity(humidity);
                UI_DataHolder.setWindSpeed(windSpeed);
                UI_DataHolder.setSummary(summary);
                UI_DataHolder.setWeatherTime(weather_time);

            }


        });


        asyncTask.execute(lat, lon);

    }

    private void getJSONgeocodeAddress(String latitude, String longitude) {


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = geocodeURL + latitude + "," + longitude + "&key=" + geocodeAPI;
        //     Log.e("URL: ",url);
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    //       String address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address"); // returns town/City
                    String str = response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("long_name");
                    splashInfo.setText(str);
                    PreferenceHelper.setSharedPreferenceString(getApplicationContext(), STREET, str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error + "");
            }
        });
        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}

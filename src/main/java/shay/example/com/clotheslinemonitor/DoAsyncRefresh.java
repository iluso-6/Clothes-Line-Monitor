package shay.example.com.clotheslinemonitor;

import activities.Splash;


/**
 * Created by Shay on 12/04/2017.
 */

public class DoAsyncRefresh {

    public static void refresh() {
        String lat = Splash.latitude;
        String lon = Splash.longitude;

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

                // no further args needed here as had been in Splash

            }
        });


        asyncTask.execute(lat, lon);

    }


}

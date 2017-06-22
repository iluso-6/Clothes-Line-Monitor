package shay.example.com.clotheslinemonitor;

/**
 * Created by Shay on 18/03/2017.
 */


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Function {

    private static int TOTAL_DATA_HOURS = 16; // max data hours to be available after download
    private static final String API = "https://api.forecast.io/forecast/ca05a018bff6ebdd72951bb203fac735/%s" + "?units=uk";

    public interface AsyncResponse {

        void processFinish(String[] output1, Integer[] output2, Integer[] output3, String[] output4, Integer[] output5, String[] output6, String[] output7, long[] output8);

    }


    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {


        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor - we can access this in Splash for post exec
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //  Log.e("currentThread()",Thread.currentThread().getName());
            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            String[] temperature = new String[TOTAL_DATA_HOURS];
            Integer[] precipProbability = new Integer[TOTAL_DATA_HOURS];
            String[] dewPoint = new String[TOTAL_DATA_HOURS];
            Integer[] humidity = new Integer[TOTAL_DATA_HOURS];
            String[] windSpeed = new String[TOTAL_DATA_HOURS];
            String[] summary = new String[TOTAL_DATA_HOURS];
            Integer[] cloudCover = new Integer[TOTAL_DATA_HOURS];
            long[] weather_time = new long[TOTAL_DATA_HOURS];
            try {
                if (json != null) {


                    JSONObject hourly = new JSONObject(json.getString("hourly"));
                    String hourSummary = hourly.getString("summary");
                    Log.e("", hourSummary);
                    JSONArray array = hourly.getJSONArray("data");
                    for (int i = 0; i < TOTAL_DATA_HOURS; i++) {
                        JSONObject json_data = array.getJSONObject(i);

                        temperature[i] = json_data.getString("temperature");
                        Log.i("Temperature: ", temperature[i] + "");

                        double temp = json_data.getDouble("precipProbability");
                        temp = temp * 100;// get percentage value
                        precipProbability[i] = (int) (temp);
                        Log.i("precipProbability: ", precipProbability[i] + "");

                        double cld = json_data.getDouble("cloudCover");
                        cld = cld * 100;// get percentage value
                        cloudCover[i] = (int) cld; // clean cast to int without any decimal points...
                        Log.i("cloudCover: ", cloudCover[i] + "");

                        dewPoint[i] = json_data.getString("dewPoint");
                        Log.i("dewPoint: ", dewPoint[i]);

                        double humidTemp = json_data.getDouble("humidity");
                        humidTemp = humidTemp * 100; // get percentage value
                        humidity[i] = (int) (humidTemp);
                        Log.i("humidity: ", humidity[i] + "");

                        windSpeed[i] = json_data.getString("windSpeed");
                        Log.i("windSpeed: ", windSpeed[i]);

                        summary[i] = json_data.getString("summary");
                        Log.i("summary: ", summary[i]);

                        weather_time[i] = json_data.getLong("time");
                        Log.i("time: ", weather_time[i] + "");


                    }
                    // results now in Splash
                    delegate.processFinish(temperature, precipProbability, cloudCover, dewPoint, humidity, windSpeed, summary, weather_time);


                }
            } catch (JSONException e) {
                Log.e("", "Cannot process JSON results", e);
            }


        }
    }


    private static JSONObject getWeatherJSON(String lat, String lon) {
        String coord = lat + "," + lon;
        try {
            //coord = "40.7127,-74.0059";// replace %s
            URL url = new URL(String.format((API), coord));
            Log.e("getWeatherJSON", url + "");
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            //Log.e("getWeatherJSON",data+"");
            return data;
        } catch (Exception e) {
            Log.e("Exception", e + "");
            return null;
        }
    }


}

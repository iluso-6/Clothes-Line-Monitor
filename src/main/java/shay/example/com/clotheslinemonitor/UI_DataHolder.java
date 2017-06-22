package shay.example.com.clotheslinemonitor;


// Store all values using getters and setters -- this data only needed when application is running in foreground


// String[] temperature, String[] precipProbability, String[] dewPoint, String[] humidity, String[] windSpeed, String[] summary, long[] weatherTime

public class UI_DataHolder {


    // --------------- option variables to share from stored values ----------------------
    public static int displayHours = 8;

    public static int getDisplayHours() {
        return displayHours;
    }

    public static void setDisplayHours(int data) {
        UI_DataHolder.displayHours = data;
    }

    private static Boolean monitorSwitchState = false;

    public static Boolean getMonitorSwitchState() {
        return monitorSwitchState;
    }

    public static void setMonitorSwitchState(Boolean data) {
        UI_DataHolder.monitorSwitchState = data;
    }

    // --------------- weather variables to hold in state ----------------------
    private static String[] temperature;

    public static String[] getTemperature() {
        return temperature;
    }

    public static void setTemperature(String[] data) {
        UI_DataHolder.temperature = data;
    }

    private static Integer[] precipProbability;

    public static Integer[] getPrecipProbability() {
        return precipProbability;
    }

    public static void setPecipProbability(Integer[] data) {
        UI_DataHolder.precipProbability = data;
        //  UI_DataHolder.precipProbability[1]=16;
        //  UI_DataHolder.precipProbability[3]=56;
    }

    private static Integer[] cloudCover;

    public static Integer[] getCloudCover() {
        return cloudCover;
    }

    public static void setCloudCover(Integer[] data) {
        UI_DataHolder.cloudCover = data;
    }

    private static String[] dewPoint;

    public static String[] getDewPoint() {
        return dewPoint;
    }

    public static void setDewPoint(String[] data) {
        UI_DataHolder.dewPoint = data;
    }

    private static Integer[] humidity;

    public static Integer[] getHumidity() {
        return humidity;
    }

    public static void setHumidity(Integer[] data) {
        UI_DataHolder.humidity = data;
        //  UI_DataHolder.humidity[1] = 12;
    }

    private static String[] windSpeed;

    public static String[] getWindSpeed() {
        return windSpeed;
    }

    public static void setWindSpeed(String[] data) {
        UI_DataHolder.windSpeed = data;
    }

    private static String[] summary;

    public static String[] getSummary() {
        return summary;
    }

    public static void setSummary(String[] data) {
        UI_DataHolder.summary = data;
    }

    private static long[] weatherTime;

    public static long[] getWeatherTime() {
        return weatherTime;
    }

    public static void setWeatherTime(long[] data) {
        UI_DataHolder.weatherTime = data;
    }


}
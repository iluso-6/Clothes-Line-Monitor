package shay.example.com.clotheslinemonitor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shay on 06/04/2017.
 */

public class Converter {

    private static String time;

    public static String convertTime(long unixTime) {

        unixTime = unixTime + (60 * 60); // 60 seconds * 60 = GMT + 1 hour =  Eire time
        String EventTimeFormatter = new SimpleDateFormat("HH:mm").format(new Date(unixTime * 1000L));
        Converter.time = EventTimeFormatter;
        //  Log.e("Converter.time: ",Converter.time);
        return Converter.time;
    }


}

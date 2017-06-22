package shay.example.com.clotheslinemonitor;

import android.util.Log;

/**
 * Created by Shay on 10/04/2017.
 */

public class CalculateWeatherRating {

    public static int getRainCondition(int i) {
        Integer rainPercentage = UI_DataHolder.getPrecipProbability()[i];

        int rtn;
        if (rainPercentage > 0 && rainPercentage < 15) {
            rtn = R.drawable.red1;
        } else if (rainPercentage >= 15 && rainPercentage < 30) {
            rtn = R.drawable.red2;
        } else if (rainPercentage >= 30 && rainPercentage < 50) {
            rtn = R.drawable.red3;
        } else if (rainPercentage >= 50 && rainPercentage < 70) {
            rtn = R.drawable.red4;
        } else if (rainPercentage >= 70) {
            rtn = R.drawable.red5;
        } else {
            rtn = R.drawable.blank;
        }
        return rtn;
    }


    private static int[] rating;

    public static int getRating(int i) {

        double rating = 0;

        int[] weatherImageRating = {
                R.drawable.clothesitem0,
                R.drawable.clothesitem1,
                R.drawable.clothesitem2,
                R.drawable.clothesitem3,
                R.drawable.clothesitem4,
                R.drawable.clothesitem5
        };

        final Integer[] humidity = UI_DataHolder.getHumidity();
        String[] windSp = UI_DataHolder.getWindSpeed();
        String[] tempC = UI_DataHolder.getTemperature();
        String[] dew = UI_DataHolder.getDewPoint();
        Integer[] cloud = UI_DataHolder.getCloudCover();

        double cloudCoverPercent = cloud[i];
        double wS = Double.parseDouble(windSp[i]); // windspeed in mph
        double tC = Double.parseDouble(tempC[i]);
        double dewPt = Double.parseDouble(dew[i]);
        double dewPointDiff = tC - dewPt;

        dewPointDiff = Math.round(dewPointDiff);

        int maxTemp = 25;  // get a scale from 0 - 25 C
        int maxWind = 20;  // get a scale from 0 - 20 mph
        int maxHum = 130; // adjust for inverse value .. outdoor humidity is never 100%

        double cloudCoverInv = (100 - cloudCoverPercent); // inverse percentage higher=good with 90 @max
        int rhInverse = (maxHum - humidity[i]); // inverse percentage higher=good with 90 @max
        //    rhInverse =  (rhInverse>maxHum) ? maxHum : rhInverse; // cap the limit to 90 == saturation
        //   rhInverse =  (rhInverse<1) ? 1 : rhInverse; // cap the limit to 90 == saturation

        tC = ((tC / maxTemp) * 100);
        tC = (tC > 100) ? 100 : tC; // cap the limit to 100
        tC = (tC < 1) ? 1 : tC; // cap the min to 1
        wS = ((wS / maxWind) * 100);
        wS = (wS > 100) ? 100 : wS;
        wS = (wS < 1) ? 1 : wS; // cap the min to 1

        rating = (tC + wS + rhInverse + cloudCoverInv) / 4;  // rating percent %

        //   Log.e(" Rating: ",rating+" temporary: "+tC+" wind: "+wS+ " RhInv: "+ rhInverse + " cloudCoverInv: "+ cloudCoverInv);
        double finalRating = ((rating / 100) * 5);
        finalRating = Math.floor(finalRating + 0.5);

        int rate = (int) finalRating;
        Log.e(" Rating : ", rate + "");

/* This is a more advanced formula ... unnecessarily complicated


        final Integer[] humidity = UI_DataHolder.getHumidity();
        String[] windSp = UI_DataHolder.getWindSpeed();
        String[] tempC = UI_DataHolder.getTemperature();

        double wS = 1;//Double.parseDouble(windSp[i]); // windspeed in mph
        double tC = Double.parseDouble( tempC[i]);
        double tF = 54;//tC +33.38;
        int rh = 10;//humidity[i];
//notes:
// 192 max wS@30, tF@60, rh@1
//  5.7 result from wS@30, tF@60, rh@90
// 1.62 result from wS@30, tF@34, rh@90
//  0.11 result from wS@1, tF@34, rh@90
//  1.72 result from wS@1, tF@54, rh@10


/* vapor pressure in pascal vp=610.78 × exp(t/(t+238.3) × 17.2694) × rh
* kPa 7.50061 = mmHg


    //                      7.4PA(0.447W)^0.78
 //  Evaporation =       __________________________________________
  //                              T+459.67

    E = Evaporation Rate (Gallons/Day)
    A = Pool Surface Area (ft2)
    W = Wind Speed Above Pool (mph)
    P = Water's Vapor Pressure (mmHG) at Ambient Temperature
    T = Temperature (°F)

        tC = tF-33.38;

        double vP = (610.78 * Math.exp(tC/(tC+238.3)*17.2694)/rh) * 7.50061; // vP now in mmHg
        double eV1 = Math.pow((7.4 * 1 * vP *(0.447*wS)),0.78); //tF+459.67;

        double ev = eV1/(tF+459.67);
        Log.e("Evaporation: ",ev+"");

        */
        //     final Random rand = new Random();
        //     int randomRoll = rand.nextInt(5);

        int temporary = weatherImageRating[rate];
        return temporary;
    }


}

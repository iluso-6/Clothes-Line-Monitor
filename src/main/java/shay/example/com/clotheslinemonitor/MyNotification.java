package shay.example.com.clotheslinemonitor;

/**
 * Created by Shay on 06/05/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import activities.MainActivity;

public class MyNotification {

    @SuppressWarnings("deprecation")
    public static void createNotification(Context ctx) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.rain)
                        .setContentTitle("Rain predicted")
                        .setContentText("Rain is expected in the coming hour");
        Uri alarmSound = (Uri.parse("android.resource://"
                + ctx.getPackageName() + "/" + R.raw.splash));
        builder.setSound(alarmSound);
        builder.setAutoCancel(true);

        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }
}
package com.theglorious.re4der.cantreye;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationCreator {

    private final String notChannelId = "1";

    public void createNotification(String[] characters, int inactiveCount, Context context){
        //prepare resources
        int notificationID = 1;
        String title = "";
        String text = "";
        if(characters[0]==null){
            title = context.getResources().getString(R.string.no_inactive_notification);
        }
        else {
            title = context.getResources().getString(R.string.inactive_chars_notification)+" "+inactiveCount;
            for (int i = 0; i < 15; i++) {
                if (i==inactiveCount-1 && inactiveCount>1) {
                    text += " " + context.getResources().getString(R.string.and) + " ";
                }
                else if (characters[i] != null && i != 0) {
                    text += ", ";
                }
                if(characters[i]!=null) {
                    text += characters[i];
                }
            }
        }

        //create intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.cantr.net//"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //create notification


        int drawable = R.drawable.notification_eye;

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder =
                    new Notification.Builder(context, notChannelId)
                            .setSmallIcon(drawable)
                            .setContentTitle(title)
                            .setContentIntent(pendingIntent)
                            .setContentText(text);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //create channel
            mNotificationManager.createNotificationChannel(new NotificationChannel(notChannelId, context.getResources().getString(R.string.not_channel_name), NotificationManager.IMPORTANCE_DEFAULT));

            //notify
            mNotificationManager.notify(notificationID, builder.build());
        }
        else{
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(drawable)
                            .setContentTitle(title)
                            .setContentIntent(pendingIntent)
                            .setContentText(text);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //notify
            mNotificationManager.notify(notificationID, builder.build());
        }
    }

}

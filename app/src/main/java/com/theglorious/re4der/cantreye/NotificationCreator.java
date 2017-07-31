package com.theglorious.re4der.cantreye;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class NotificationCreator {

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

        /*
        switch(inactiveCount) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:

                break;
            case 8:

                break;
            case 9:

                break;
            case 10:

                break;
            case 11:

                break;
            case 12:

                break;
            case 13:

                break;
            case 14:

                break;
            case 15:

                break;
            default:

                break;
        }*/

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(drawable)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        .setContentText(text);

        //notify
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationID, builder.build());
    }

}

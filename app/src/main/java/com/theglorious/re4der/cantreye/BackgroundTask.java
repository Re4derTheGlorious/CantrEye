package com.theglorious.re4der.cantreye;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class BackgroundTask extends IntentService {

    public BackgroundTask() {
        this(BackgroundTask.class.getName());
    }

    private String[] characters = new String[15];
    private int inactiveCount = 0;
    private String request;

    public BackgroundTask(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        request = intent.getStringExtra("com.theglorious.re4der.MESSAGE");

        for(int i = 0; i<15; i++){
            characters[i] = null;
        }

        try {
            URLConnection connection = new URL(request).openConnection();
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);

            int index = -1;
            do{
                if(index!=-1) {
                    characters[index] = scanner.nextLine();
                }
                else{
                    scanner.nextLine();
                }
                index++;
            }while(scanner.hasNextLine());
        } catch (IOException argh) {
            argh.printStackTrace();
        }
        for(int i = 0; i<15; i++){
            if(characters[i]!=null){
                inactiveCount++;
            }
        }
        createNotification();
    }

    private void createNotification(){
        //prepare resources
        int notificationID = 1;
        String title = "";
        String text = "";
        if(characters[0]==null){
            title = getResources().getString(R.string.no_inactive_notification);
        }
        else {
            title = inactiveCount+" "+getResources().getString(R.string.inactive_chars_notification);
            for (int i = 0; i < 15; i++) {
                if (i < 14) {
                    if (characters[i + 1] == null && characters[i] != null) {
                        text += " " + getResources().getString(R.string.and) + " ";
                    } else if (characters[i] != null && i != 0) {
                        text += ", ";
                    }
                }

                if (characters[i] != null) {
                    text += characters[i];
                }
            }
        }

        //create intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.cantr.net//"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //create notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.eye_icon)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        .setContentText(text);


        //notify
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationID, builder.build());
    }
}

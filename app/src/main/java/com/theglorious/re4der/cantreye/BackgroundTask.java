package com.theglorious.re4der.cantreye;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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
        inactiveCount = 0;
        for(int i = 0; i<15; i++){
            if(characters[i]!=null){
                inactiveCount++;
            }
        }
        NotificationCreator notCreator = new NotificationCreator();
        notCreator.createNotification(characters, inactiveCount, this);
    }
}

package com.theglorious.re4der.cantreye;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class CharacterListActivity extends AppCompatActivity {

    private String[] characters = new String[15];
    int inactiveCount = 0;

    private final int alarmID = 1234;

    private String request;
    private String ID;
    private AlarmManager alarm;

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        //get the message
        Intent creatorIntent = getIntent();
        request = creatorIntent.getStringExtra("com.theglorious.re4der.MESSAGE");
        ID = creatorIntent.getStringExtra("com.theglorious.re4der.MESSAGE2");

        //set stuff
        TextView view = (TextView) findViewById(R.id.id_label);
        view.setText(ID);

        context = this;
    }

    private void startBackgroundService(){
        int refreshInterval = 60000;

        Intent intent = new Intent(this, BackgroundTask.class);
        intent.putExtra("com.theglorious.re4der.MESSAGE", request);
        PendingIntent pending = PendingIntent.getService(this, alarmID, intent, 0);

        alarm.setInexactRepeating(AlarmManager.RTC,
                SystemClock.elapsedRealtime()+5000,
                refreshInterval, pending);
    }

    public void stopNotificationsAction(View view){
        stopBackgroundService();
        Toast toast = Toast.makeText(this, getResources().getString(R.string.notifications_off_toast).toString(), Toast.LENGTH_LONG);
        toast.show();
    }

    public void startNotificationsAction(View view){
        startBackgroundService();
        Toast toast = Toast.makeText(this, getResources().getString(R.string.notifications_on_toast).toString(), Toast.LENGTH_LONG);
        toast.show();
    }

    public void refreshNow(View view){
        new refreshNowClass().execute(null, null, null);
    }

    private void stopBackgroundService(){
        Intent intent = new Intent(this, BackgroundTask.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, alarmID, intent, 0);
        alarm.cancel(pendingIntent);

    }

    public void goToGame(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.cantr.net//"));
        startActivity(intent);
    }

    public void aboutButton(View view){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void logoff(View view){
        stopBackgroundService();
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences SPStorage = getSharedPreferences(getResources().getString(R.string.storage_adress), 0);
        SharedPreferences.Editor editor = SPStorage.edit();
        editor.remove("credential");
        editor.remove("ID");
        editor.commit();
        startActivity(intent);
        this.finish();
    }

    private class refreshNowClass extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
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
            notCreator.createNotification(characters, inactiveCount, context);
            return null;
        }
    }
}

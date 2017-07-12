package com.theglorious.re4der.cantreye;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class CharacterListActivity extends AppCompatActivity {

    private String request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        //get the message
        Intent creatorIntent = getIntent();
        request = creatorIntent.getStringExtra("com.theglorious.re4der.MESSAGE");

        //start service
        startBackgroundService();
    }

    private void startBackgroundService(){
        int refreshInterval = 60000;
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, BackgroundTask.class);
        intent.putExtra("com.theglorious.re4der.MESSAGE", request);
        PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime()+5000,
                refreshInterval, pending);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences SPStorage = getSharedPreferences(getResources().getString(R.string.storage_adress), 0);
        SharedPreferences.Editor editor = SPStorage.edit();
        editor.remove("credential");
        editor.commit();
        startActivity(intent);
    }
}

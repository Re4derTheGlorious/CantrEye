package com.theglorious.re4der.cantreye;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CharacterListActivity extends AppCompatActivity {

    private final int alarmID = 1234;

    private String request;
    private String ID;
    private AlarmManager alarm;

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

    public void notificationToggle(View view){
        ToggleButton toggle = (ToggleButton) view;
        if(toggle.isChecked()){
            startBackgroundService();
        }
        else{
            stopBackgroundService();
        }
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

    public void logoff(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences SPStorage = getSharedPreferences(getResources().getString(R.string.storage_adress), 0);
        SharedPreferences.Editor editor = SPStorage.edit();
        editor.remove("credential");
        editor.remove("ID");
        editor.commit();
        startActivity(intent);
        this.finish();
    }
}

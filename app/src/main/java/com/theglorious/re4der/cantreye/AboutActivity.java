package com.theglorious.re4der.cantreye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void donate(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.paypal.me/re4der"));
        startActivity(intent);
    }

    public void goToPolishPortal(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://cantr.pl/"));
        startActivity(intent);
    }

    public void goToTicks(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://joo.freehostia.com/cantr/ticks/"));
        startActivity(intent);
    }

    public void goToWiki(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://wiki.cantr.net/"));
        startActivity(intent);
    }
}

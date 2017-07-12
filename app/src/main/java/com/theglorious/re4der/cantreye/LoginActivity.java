package com.theglorious.re4der.cantreye;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

class Key{
    String m;
    String e;
    String max;
}

public class LoginActivity extends AppCompatActivity {

    private final String API_VERSION = "1.1.0.";
    public Key key = new Key();
    private String serverResponse = "";
    private String m;
    private String e;
    private String max;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //check if logged in
        SharedPreferences SPStorage = getSharedPreferences(getResources().getString(R.string.storage_adress), 0);
        String url = SPStorage.getString("credential", "ERROR");
        String id = SPStorage.getString("ID", "ERROR");
        if(!url.equals("ERROR")) {
            autolog(url, id);
        }
    }

    private boolean waitForResponse(){
        int timePassed = 0;
        int breakTreshold = 10000;
        int stepSize = 500;
        while(timePassed<breakTreshold && serverResponse.equals("")){
            try {
                Thread.sleep(stepSize);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            timePassed+=stepSize;
        }
        if(timePassed>=breakTreshold){
            return false;
        }
        else{
            return true;
        }
    }

    private void displayError(String errorText){
        Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_LONG);
        toast.show();
        setLoader(false);
    }

    private void setLoader(boolean running){
        View loadingAnimation = findViewById(R.id.login_progress);
        View loginForms = findViewById(R.id.login_forms);
        if(running) {
            loginForms.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);
        }
        else{
            loginForms.setVisibility(View.VISIBLE);
            loadingAnimation.setVisibility(View.GONE);
        }
    }

    public boolean attemptLogin(View view) {
        //start loading animation
        setLoader(true);

        //clear
        serverResponse = "";
        key = new Key();

        //prepare forms
        String rawText = "";
        EditText idForm = (EditText) findViewById(R.id.ID_form);
        EditText passForm = (EditText) findViewById(R.id.password_form);

        //check for empty forms
        rawText = idForm.getText().toString();
        if(rawText.length()==0){
            displayError(getResources().getString(R.string.error_field_required));
            return false;
        }
        rawText = passForm.getText().toString();
        if(rawText.length()==0){
            displayError(getResources().getString(R.string.error_field_required));
            return false;
        }

        //assemble url
        rawText = idForm.getText().toString();
        String url = "https://cantr.net/app.getevents2.php?id=" + rawText + "&requestkey=1&ver=" + API_VERSION;

        //request key
        KeyRequest request = new KeyRequest();
        request.execute(url);
        if(!waitForResponse()) {
            displayError(getResources().getString(R.string.error_timed_out));
            return false;
        }

        //handle errors
        if(serverResponse.equals("ERROR Hacking attempt")){
            displayError(getResources().getString(R.string.error_incorrect_credentials));
            return false;
        }
        else if(serverResponse.equals("ERROR Wrong version")){
            displayError(getResources().getString(R.string.error_old_API));
            return false;
        }
        else if(serverResponse.startsWith("ERROR")){
            displayError(getResources().getString(R.string.error_unknown));
            return false;
        }

        //encrypt password
        rawText = passForm.getText().toString();
        jCryption encryptor = new jCryption();
        String encryptedPass = encryptor.encrypt(key.e, key.m, key.max, rawText);


        //assemble new url
        rawText = idForm.getText().toString();
        url = "https://cantr.net/app.getevents2.php?id="+rawText+"&pass="+encryptedPass+"&ver="+API_VERSION;


        //authenticate
        serverResponse = "";
        Authentication auth = new Authentication();
        auth.execute(url);
        if(!waitForResponse()){
            displayError(getResources().getString(R.string.error_timed_out));
            return false;
        }

        //handle errors 2
        if(serverResponse.equals("ERROR Hacking attempt")){
            displayError(getResources().getString(R.string.error_incorrect_credentials));
            return false;
        }
        else if(serverResponse.equals("ERROR Wrong version")){
            displayError(getResources().getString(R.string.error_old_API));
            return false;
        }
        else if(serverResponse.equals("GAME LOCKED")){
            displayError(getResources().getString(R.string.error_game_locked));
            return false;
        }
        else if(serverResponse.equals("BAD LOGIN")){
            displayError(getResources().getString(R.string.error_incorrect_credentials));
            return false;
        }
        else if(serverResponse.startsWith("ERROR")){
            displayError(getResources().getString(R.string.error_unknown));
            return false;
        }

        //remember credentials
        saveCredentials(url, idForm.getText().toString());

        //move forward
        Intent intent = new Intent(this, CharacterListActivity.class);
        intent.putExtra("com.theglorious.re4der.MESSAGE", url);
        intent.putExtra("com.theglorious.re4der.MESSAGE2", idForm.getText().toString());
        startActivity(intent);
        this.finish();
        return true;
    }

    private boolean autolog(String url, String id){
        //authenticate
        serverResponse = "";
        Authentication auth = new Authentication();
        auth.execute(url);
        if(!waitForResponse()){
            displayError(getResources().getString(R.string.error_timed_out));
            return false;
        }

        //handle errors 2
        if(serverResponse.equals("ERROR Hacking attempt")){
            displayError(getResources().getString(R.string.error_incorrect_credentials));
            return false;
        }
        else if(serverResponse.equals("ERROR Wrong version")){
            displayError(getResources().getString(R.string.error_old_API));
            return false;
        }
        else if(serverResponse.equals("GAME LOCKED")){
            displayError(getResources().getString(R.string.error_game_locked));
            return false;
        }
        else if(serverResponse.equals("BAD LOGIN")){
            displayError(getResources().getString(R.string.error_incorrect_credentials));
            return false;
        }
        else if(serverResponse.startsWith("ERROR")){
            displayError(getResources().getString(R.string.error_unknown));
            return false;
        }
        Intent intent = new Intent(this, CharacterListActivity.class);
        intent.putExtra("com.theglorious.re4der.MESSAGE", url);
        intent.putExtra("com.theglorious.re4der.MESSAGE2", id);
        startActivity(intent);
        this.finish();
        return true;
    }

    private void saveCredentials(String value, String ID){
        SharedPreferences SPStorage = getSharedPreferences(getResources().getString(R.string.storage_adress), 0);
        SharedPreferences.Editor editor = SPStorage.edit();
        editor.putString("credential", value);
        editor.putString("ID", ID);
        editor.commit();
    }


    class Authentication extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... url) {
            try {
                URLConnection connection = new URL(url[0]).openConnection();
                InputStream response = connection.getInputStream();

                Scanner scanner = new Scanner(response);
                serverResponse = scanner.nextLine();
                if(serverResponse.startsWith("OK")){
                    serverResponse = "OK";
                }

                scanner.close();
            }catch(IOException argh){
                argh.printStackTrace();
            }
            return null;
        }
    }

    class KeyRequest extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... url) {
            try {
                URLConnection connection = new URL(url[0]).openConnection();
                InputStream response = connection.getInputStream();

                Scanner scanner = new Scanner(response);
                String responseBody = scanner.useDelimiter("\\s").next();

                if(responseBody.equals("ERROR")){
                    serverResponse = responseBody+scanner.nextLine();
                    return null;
                }
                else{
                    serverResponse = "OK";
                }

                key.e = responseBody;

                responseBody = scanner.useDelimiter("\\s").next();
                key.m = responseBody;

                responseBody = scanner.useDelimiter("\\s").next();
                key.max = responseBody;

                scanner.close();
            } catch (IOException argh) {
                argh.printStackTrace();
            }
            return null;
        }
    }
}
package com.usman.treasurehuntgame.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.usman.treasurehuntgame.Classes.JsonFileReader;
import com.usman.treasurehuntgame.Classes.StaticVariables;
import com.usman.treasurehuntgame.Firebase.FirebaseHandler;
import com.usman.treasurehuntgame.R;

import spencerstudios.com.bungeelib.Bungee;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = " BaseActivity";
    FirebaseHandler firebaseHandle;
    JsonFileReader jsonFileReader;
    BroadcastReceiver broadCastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        jsonFileReader = new JsonFileReader(this);
        firebaseHandle = new FirebaseHandler(this);
        firebaseHandle.getScoreBoardData();
        StaticVariables.isInternetConnected = isNetworkConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncPlayerDataInFirebase();
        Log.d(TAG, "onResume: ");
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    void checkPlayerDataPresent(){
        if(!jsonFileReader.isFilePresent(this,getString(R.string.player_data))){
            startActivity(new Intent(BaseActivity.this, CreateNewPlayerActivity.class));
            finish();
            Bungee.diagonal(this);
        }else{
            try {
                Log.d(TAG, "checkPlayerDataPresent: reading player data from file");
                StaticVariables.myName = jsonFileReader.readPlayerDataFromFile().getString("name");
                StaticVariables.myScore = Integer.parseInt(jsonFileReader.readPlayerDataFromFile().getString("score"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void syncPlayerDataInFirebase(){
        Log.d(TAG, "syncPlayerDataInFirebase: StaticVariables.isInternetConnected:"+StaticVariables.isInternetConnected);
        if(StaticVariables.isInternetConnected && jsonFileReader.isFilePresent(this,getString(R.string.player_data))) {
            firebaseHandle.savePlayerData();
            Log.d(TAG, "syncPlayerDataInFirebase: ");
        }
    }

    public Boolean isInternetConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

}

package com.example.chu.chatroomapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/*
TruTel Communications - August 2014
Created by Benjamin Chu - Simple G2Sky Chat Application w/ XMPP Connection to eJabberd server
 */
public class LobbyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Log.i("LobbyApplication", "Entered onCreate()");
        Intent intent = new Intent(this, LocalService.class);
        Log.i("LobbyApplication", "Starting service from Application onCreate" + startService(intent));
    }

    public void onTerminate() {

    }
}

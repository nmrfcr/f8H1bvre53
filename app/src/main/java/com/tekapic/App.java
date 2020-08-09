package com.tekapic;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

//import androidx.multidex.MultiDex;

/**
 * Created by LEV on 28/07/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
//        MultiDex.install(this);
    }
}

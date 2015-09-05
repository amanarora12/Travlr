package aman.com.travlr.application;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import aman.com.travlr.R;

/**
 * Created by Aman on 19-08-2015.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    public Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public void startTracking(){
        if(mTracker==null){
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker=ga.newTracker(R.xml.app_tracker);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }
    public Tracker getTracker(){
        startTracking();
        return mTracker;
    }
    public static MyApplication getInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}

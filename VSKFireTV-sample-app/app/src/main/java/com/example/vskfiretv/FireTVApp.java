package com.example.vskfiretv;

import android.app.Activity;
import android.app.Application;

public class FireTVApp extends Application {
    //This base class has a reference in the manifest and is used to preserve a reference to the current activity
    //We need this to access from within broadcast receivers where there otherwise is no meaningful context
    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity(){

        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}

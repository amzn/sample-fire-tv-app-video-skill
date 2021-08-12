/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class FireTVApp extends Application {
    private static final String TAG = FireTVApp.class.getSimpleName();

    //This base class has a reference in the manifest and is used to preserve a reference to the current activity
    //We need this to access from within broadcast receivers where there otherwise is no meaningful context
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        Log.d(TAG, "Application initialized");
    }

    private Activity mCurrentActivity = null;
    private static FireTVApp application;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(final Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public static FireTVApp getInstance() {
        return application;
    }
}

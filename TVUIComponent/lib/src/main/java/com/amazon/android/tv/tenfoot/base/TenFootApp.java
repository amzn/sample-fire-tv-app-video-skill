/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazon.android.tv.tenfoot.base;

import com.amazon.android.configuration.ConfigurationManager;
import com.amazon.android.contentbrowser.app.ContentBrowserApplication;
import com.amazon.android.contentbrowser.constants.ConfigurationConstants;
import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.tv.tenfoot.ui.activities.ContentBrowseActivity;
import com.amazon.android.tv.tenfoot.ui.activities.ContentDetailsActivity;
import com.amazon.android.tv.tenfoot.ui.activities.ContentSearchActivity;
import com.amazon.android.tv.tenfoot.ui.activities.FullContentBrowseActivity;
import com.amazon.android.tv.tenfoot.ui.activities.SplashActivity;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.amazon.android.uamp.ui.PlaybackActivity;
import com.amazon.analytics.AnalyticsTags;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.device.messaging.ADM;
import com.amazon.device.messaging.development.ADMManifest;


import java.util.ArrayList;
import java.util.List;

/**
 * TenFoot Application class.
 */
public class TenFootApp extends ContentBrowserApplication implements Application.ActivityLifecycleCallbacks {

    /**
     * Debug TAG.
     */
    private static final String TAG = ContentBrowserApplication.class.getSimpleName();

    private Activity currentActivity;

    private static final String ALEXA_SKILL_ID = "amzn1.ask.skill.544641fd-b4b3-40ab-a9f6-c9757bda68b6";

    @Override
    public void onCreate() {

        super.onCreate();

        try {
            /**
             * Get the default values and set them to Configuration manager.
             */
            ConfigurationManager
                    .getInstance(this)
                    .setBooleanValue(ConfigurationConstants.CONFIG_HIDE_MORE_ACTIONS,
                                     getResources().getBoolean(R.bool.hide_more_options))
                    .setIntegerValue(com.amazon.android.ui.constants.ConfigurationConstants
                                             .CONFIG_SPINNER_ALPHA_COLOR,
                                     getResources().getInteger(R.integer.spinner_alpha))
                    .setIntegerValue(com.amazon.android.ui.constants.ConfigurationConstants
                                             .CONFIG_SPINNER_COLOR,
                                     ContextCompat.getColor(this, R.color.spinner_color))
                    .setIntegerValue(ConfigurationConstants.CONFIG_TIME_TO_RELOAD_FEED,
                                     getResources().getInteger(R.integer.time_to_reload_content));
        }
        catch (Resources.NotFoundException exception) {
            Log.e(TAG, "Resources not found", exception);
        }

        // Initialize the Alexa Video Skills Client Library at the end of
        // application creation step.
        initializeAlexaClientLibrary();

        // Initialize the ADM.
        initializeAdm();

        // Add analytics constant of embedded activities.
        mAnalyticsManager.addAnalyticsConstantForActivity(SplashActivity.class.getSimpleName(),
                                                          AnalyticsTags.SCREEN_SPLASH)
                         .addAnalyticsConstantForActivity(ContentBrowseActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_BROWSE)
                         .addAnalyticsConstantForActivity(FullContentBrowseActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_BROWSE)
                         .addAnalyticsConstantForActivity(ContentSearchActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_SEARCH)
                         .addAnalyticsConstantForActivity(ContentDetailsActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_DETAILS)
                         .addAnalyticsConstantForActivity(PlaybackActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_PLAYBACK);

        registerActivityLifecycleCallbacks(this);

    }



    private void initializeAlexaClientLibrary() {
        // Retrieve the shared instance of the AlexaClientManager
        AlexaClientManager clientManager = AlexaClientManager.getSharedInstance();


        // Create a list of supported capabilities in your skill.
        List<String> capabilities = new ArrayList<>();
        capabilities.add(AlexaClientManager.CAPABILITY_CHANNEL_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_REMOTE_VIDEO_PLAYER);
        capabilities.add(AlexaClientManager.CAPABILITY_PLAY_BACK_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_SEEK_CONTROLLER);

        clientManager.initialize(getApplicationContext(),
                ALEXA_SKILL_ID,
                AlexaClientManager.SKILL_STAGE_LIVE,
                capabilities);

        // (Optional) Enable the VSK client library so that VSK start auto-pairing in background
        // immediately which will enable your user use Voice service ASAP.
        // You can delay this step until active user signed-in to your application.
        //clientManager.setAlexaEnabled(true);
    }


    private void initializeAdm() {
        try {
            final ADM adm = new ADM(this);
            if (adm.isSupported()) {
                if (adm.getRegistrationId() == null) {
                    // ADM is not ready now. You have to start ADM registration by calling
                    // startRegister() API. ADM will call onRegister() API on your ADM
                    // handler service when ADM registration was completed with registered ADM id.
                    adm.startRegister();
                } else {
                    // [IMPORTANT]
                    // ADM down-channel is already available. This is a common case that your
                    // application restarted. ADM manager on your Fire TV caches the previous
                    // ADM registration info and provides it immediately when your application
                    // is identified as restarted.
                    //
                    // You have to provide the retrieved ADM registration Id to the VSK Client library.
                    final String admRegistrationId = adm.getRegistrationId();
                    Log.i(TAG, "ADM registration Id:" + admRegistrationId);

                    // Provide the acquired ADM registration ID.
                    final AlexaClientManager alexaClientManager = AlexaClientManager.getSharedInstance();
                    alexaClientManager.setDownChannelReady(true, admRegistrationId);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "ADM initialization is failed with exception", ex);
        }
    }


    @Override
    public void onActivityStarted(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // Nothing to do here
    }

    @Override
    public void onActivityCreated(Activity var1, Bundle var2) {
        // Nothing to do here
    }

    @Override
    public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
        // Nothing to do here
    }

    @Override
    public void onActivityDestroyed(Activity var1) {
        // Nothing to do here
    }

    /**
     * @return Current running Activity in the application
     */
    public Activity getCurrentActivity() {
        return currentActivity;
    }

}

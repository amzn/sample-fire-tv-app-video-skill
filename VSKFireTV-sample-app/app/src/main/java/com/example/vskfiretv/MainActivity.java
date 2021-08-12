/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.alexa.vsk.clientlib.capability.AlexaChannelControllerCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaKeyPadControllerCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaMediaDetailsNavigatorCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaPlaybackControllerCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaRemoteVideoPlayerCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaSeekController;
import com.amazon.alexa.vsk.clientlib.capability.AlexaUIControllerCapability;
import com.amazon.alexa.vsk.clientlib.capability.AlexaVideoCapability;
import com.amazon.alexa.vsk.clientlib.capability.configuration.MediaDetailsNavigatorConfiguration;
import com.amazon.alexa.vsk.clientlib.capability.operation.KeyPadControllerOperation;
import com.amazon.alexa.vsk.clientlib.capability.operation.PlaybackControllerOperation;
import com.amazon.alexa.vsk.clientlib.capability.operation.UIControllerOperation;
import com.amazon.alexa.vsk.clientlib.capability.property.UIControllerSupportedProperty;
import com.amazon.alexauicontroller.EntityType;
import com.amazon.alexauicontroller.UIAction;
import com.amazon.device.messaging.ADM;
import com.example.vskfiretv.utils.UIElementUtil;
import com.google.gson.JsonElement;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.vskfiretv.utils.Constants.ACTION_ON_MEDIA_DETAILS;
import static com.example.vskfiretv.utils.Constants.ACTION_ON_UI_ELEMENT;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected FireTVApp myFireTVApp;
    public UIElementAction uiElementAction;
    private MediaDetailsAction mediaDetailsAction;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        myFireTVApp = (FireTVApp) this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [IMPORTANT]
        // initialize the VSK client library.
        initializeAlexaClient();

        // Initialize the ADM.
        initializeAdm();

        // (Optional) Enable the VSK client library so that VSK start auto-pairing in background
        // immediately which will enable your user use Voice service ASAP.
        // You can delay this step until active user signed-in to your application.
        AlexaClientManager.getSharedInstance().setAlexaEnabled(true);
        Log.d(TAG, "MainActivity initialized");
    }

    private void initializeAlexaClient() {
        // Retrieve the shared instance of the AlexaClientManager
        final AlexaClientManager clientManager = AlexaClientManager.getSharedInstance();

        // Gather your Skill ID
        final String alexaSkillId = "<insert skill id>";

        // Create a list of supported capabilities in your skill.
        final List<AlexaVideoCapability> supportedCapabilities = new ArrayList<>();
        supportedCapabilities.add(getAlexaChannelControllerCapability());
        supportedCapabilities.add(getAlexaPlaybackControllerCapability());
        supportedCapabilities.add(getAlexaRemoteVideoPlayerCapability());
        supportedCapabilities.add(getAlexaSeekControllerCapability());
        supportedCapabilities.add(getAlexaKeypadControllerCapability());
        supportedCapabilities.add(getAlexaUIControllerCapability());
        supportedCapabilities.add(getMediaDetailsNavigatorCapability());

        // Initialize the client library by calling initialize().
        clientManager.initializeClient(getApplicationContext(),
                alexaSkillId,
                AlexaClientManager.SKILL_STAGE_DEVELOPMENT,
                supportedCapabilities);
    }

    private AlexaVideoCapability getAlexaChannelControllerCapability() {
        return new AlexaChannelControllerCapability("1.0", null);
    }

    private AlexaVideoCapability getAlexaPlaybackControllerCapability() {
        final List<PlaybackControllerOperation> playBackOperationsSupported = new ArrayList<>();
        playBackOperationsSupported.add(PlaybackControllerOperation.PLAY);
        playBackOperationsSupported.add(PlaybackControllerOperation.PAUSE);
        playBackOperationsSupported.add(PlaybackControllerOperation.STOP);
        playBackOperationsSupported.add(PlaybackControllerOperation.START_OVER);
        playBackOperationsSupported.add(PlaybackControllerOperation.REWIND);
        playBackOperationsSupported.add(PlaybackControllerOperation.FAST_FORWARD);

        return new AlexaPlaybackControllerCapability("3", playBackOperationsSupported);
    }

    private AlexaVideoCapability getAlexaRemoteVideoPlayerCapability() {
        return new AlexaRemoteVideoPlayerCapability("1.0");
    }

    private AlexaVideoCapability getAlexaSeekControllerCapability() {
        return new AlexaSeekController("1.0");
    }

    private AlexaVideoCapability getAlexaKeypadControllerCapability() {
        return new AlexaKeyPadControllerCapability("3", Arrays.asList(KeyPadControllerOperation.values()));
    }

    private AlexaVideoCapability getAlexaUIControllerCapability() {
        return new AlexaUIControllerCapability("3.0", Arrays.asList(UIControllerOperation.values()),
                Arrays.asList(UIControllerSupportedProperty.values()));
    }

    private AlexaVideoCapability getMediaDetailsNavigatorCapability() {
        return new AlexaMediaDetailsNavigatorCapability("3.0", Arrays.asList(MediaDetailsNavigatorConfiguration.VIDEO,
                MediaDetailsNavigatorConfiguration.APP));
    }

    private void initializeAdm() {
        try {
            final ADM adm = new ADM(this);
            if (adm.isSupported()) {
                if (adm.getRegistrationId() == null) {
                    // ADM is not ready now. You have to start ADM registration by calling
                    // startRegister() API. ADM will calls onRegister() API on your ADM
                    // handler service when ADM registration was completed with registered ADM id.
                    adm.startRegister();
                } else {
                    // [IMPORTANT]
                    // ADM down-channel is already available. This is a common case that your
                    // application restarted. ADM manager on your Fire TV caches the previous
                    // ADM registration info and provides it immediately when your application
                    // is identified as restarted.

                    // You have to provide the retrieved ADM registration Id to the Alexa Client library.
                    final String admRegistrationId = adm.getRegistrationId();
                    Log.d("VSKFireTVApp", MessageFormat.format("ADM registration Id: {0}", admRegistrationId));
                    // Provide the acquired ADM registration ID.
                    AlexaClientManager.getSharedInstance().setDownChannelReady(true, admRegistrationId);
                }
            }
        } catch (Exception ex) {
            Log.e("VSKFireTVApp", "ADM initialization is failed with exception", ex);
        }
    }

    public void ShowResults(final String searchTerm, final JsonElement searchPayload){
        final MainFragment fragmentDemo = (MainFragment) getFragmentManager().findFragmentById(R.id.main_browse_fragment);
        fragmentDemo.displaySearchResults( searchTerm,  searchPayload);
    }

    protected void onResume() {
        super.onResume();
        myFireTVApp.setCurrentActivity(this);
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        final Activity currActivity = myFireTVApp.getCurrentActivity();
        if (this.equals(currActivity))
            myFireTVApp.setCurrentActivity(null);
    }

    @Override
    public void onAttachFragment(@NonNull final Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "MainActivity fragment attached");

        final Intent intent = getIntent();
        if (intent != null) {
            onNewIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        Log.d(TAG, MessageFormat.format("Received a new Intent {0}", intent));
        super.onNewIntent(intent);

        final String intentAction = intent.getAction();
        if (ACTION_ON_UI_ELEMENT.equals(intentAction)) {
            Log.d(TAG, "creating bundle for UIController intent");
            uiElementAction.onUIElementAction(UIElementUtil.getUIControllerBundleFromIntent(intent));
        } else if (ACTION_ON_MEDIA_DETAILS.equals(intentAction)) {
            Log.d(TAG, "creating bundle for MediaDetailsNavigator intent");
            mediaDetailsAction.onMediaDetailsAction(UIElementUtil.getMediaDetailsNavigatorBundleFromIntent(intent));
        } else {
            Log.d(TAG, "Unknown Intent received");
        }
        Log.d(TAG, "Finished processing the received intent.");
    }

    public void setUiElementAction(final UIElementAction uiElementAction) {
        this.uiElementAction = uiElementAction;
    }

    public void setMediaDetailsAction(final MediaDetailsAction mediaDetailsAction) {
        this.mediaDetailsAction = mediaDetailsAction;
    }

    public interface UIElementAction {
        void onUIElementAction(final Bundle bundle);
    }

    public interface MediaDetailsAction {
        void onMediaDetailsAction(final Bundle bundle);
    }
}

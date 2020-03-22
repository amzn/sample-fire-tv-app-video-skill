/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.vskfiretv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.device.messaging.ADM;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    protected FireTVApp myFireTVApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    }

    private void initializeAlexaClient() {
        // Retrieve the shared instance of the AlexaClientManager
        AlexaClientManager clientManager = AlexaClientManager.getSharedInstance();

        // Gather your Skill ID
        final String alexaSkillId = "amzn1.ask.skill.12345678-abcd-1234-efgh-123456789";

        // Create a list of supported capabilities in your skill.
        List capabilities = new ArrayList<>();
        capabilities.add(AlexaClientManager.CAPABILITY_CHANNEL_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_PLAY_BACK_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_REMOTE_VIDEO_PLAYER);
        capabilities.add(AlexaClientManager.CAPABILITY_SEEK_CONTROLLER);
        capabilities.add(AlexaClientManager.CAPABILITY_KEYPAD_CONTROLLER);

        // Initialize the client library by calling initialize().
        clientManager.initialize(getApplicationContext(),
                alexaSkillId,
                AlexaClientManager.SKILL_STAGE_DEVELOPMENT,
                capabilities);
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
                    Log.i("VSKFireTVApp", "ADM registration Id:" + admRegistrationId);
                    // Provide the acquired ADM registration ID.
                    AlexaClientManager.getSharedInstance().setDownChannelReady(true, admRegistrationId);
                }
            }
        } catch (Exception ex) {
            Log.e("VSKFireTVApp", "ADM initialization is failed with exception", ex);
        }
    }

    public void ShowResults(String searchTerm, JsonElement searchPayload){
        MainFragment fragmentDemo = (MainFragment) getFragmentManager().findFragmentById(R.id.main_browse_fragment);
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
        Activity currActivity = myFireTVApp.getCurrentActivity();
        if (this.equals(currActivity))
            myFireTVApp.setCurrentActivity(null);
    }
}

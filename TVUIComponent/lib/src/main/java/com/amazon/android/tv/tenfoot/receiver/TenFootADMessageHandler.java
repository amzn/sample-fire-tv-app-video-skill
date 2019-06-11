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
package com.amazon.android.tv.tenfoot.receiver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.android.contentbrowser.ContentBrowser;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.tv.tenfoot.base.TenFootApp;
import com.amazon.android.tv.tenfoot.data.Directive;
import com.amazon.android.tv.tenfoot.ui.activities.ContentSearchActivity;
import com.amazon.android.uamp.ui.PlaybackActivity;
import com.amazon.device.messaging.ADMMessageHandlerBase;
import com.amazon.device.messaging.ADMMessageReceiver;
import com.google.gson.Gson;

import java.util.List;


public class TenFootADMessageHandler extends ADMMessageHandlerBase {

    public static final String TAG = TenFootADMessageHandler.class.getSimpleName();

    public TenFootADMessageHandler() {
        super(TAG);
    }

    /**
     * The MessageAlertReceiver class listens for messages from ADM and forwards them to the
     * SampleADMMessageHandler class.
     */
    public static class MessageAlertReceiver extends ADMMessageReceiver {
        /** {@inheritDoc} */
        public MessageAlertReceiver() {
            super(TenFootADMessageHandler.class);
        }
    }

    @Override
    protected void onRegistered(final String newRegistrationId) {
        Log.d(TAG, "RegistrationId: " + newRegistrationId);
        // ADM down channel is ready. Provide the acquired ADM registration ID.
        AlexaClientManager.getSharedInstance().setDownChannelReady(true, newRegistrationId);
    }

    @Override
    protected void onUnregistered(final String registrationId)
    {
        // If your app is unregistered on this device, inform your server that
        // this app instance is no longer a valid target for messages.
    }

    @Override
    protected void onRegistrationError(final String errorId)
    {
        // You should consider a registration error fatal. In response, your app may
        // degrade gracefully, or you may wish to notify the user that this part of
        // your app's functionality is not available.
    }

    @Override
    protected void onMessage(final Intent intent) {
        // Extract the message content from the set of extras attached to
        // the com.amazon.device.messaging.intent.RECEIVE intent.

        Log.d(TAG, "Recieved a message from ADM: " + intent.toString());

        // Create strings to access the message and timeStamp fields from the JSON data.
        final String msgKey = getString(R.string.json_data_msg_key);
        final String timeKey = getString(R.string.json_data_time_key);

        // Obtain the intent action that will be triggered in onMessage() callback.
        final String intentAction = getString(R.string.intent_msg_action);

        // Obtain the extras that were included in the intent.
        final Bundle extras = intent.getExtras();

        // Extract the message and time from the extras in the intent.
        // ADM makes no guarantees about delivery or the order of messages.
        // Due to varying network conditions, messages may be delivered more than once.
        // Your app must be able to handle instances of duplicate messages.
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        Gson gson = new Gson();
        Directive directive = gson.fromJson(msg, Directive.class);

        Log.d(TAG, "onMessage: msg" + msg);
        Log.d(TAG, "onMessage: directive: " + directive.getDirective().getHeader().getName());

        TenFootApp app = ((TenFootApp) getApplication());
        Activity currentActivity = app.getCurrentActivity();
        ContentBrowser browser = null;
        if(null != currentActivity) {
            browser = ContentBrowser.getInstance(currentActivity);
        } else {
            Log.i(TAG,"Could not fetch current activity.");
            return;
        }

        if (directive != null) {
            String directiveName = directive.getDirective().getHeader().getName();
            if ("SearchAndPlay".equals(directiveName)) {

                Content playContent = null;
                String directivePayload = directive.getDirective().getPayload().getEntities().get(0).getValue().toLowerCase();

                List<ContentContainer> containers = browser.getContentLoader().getRootContentContainer().getContentContainers();
                for(ContentContainer container : containers) {
                    if(null != playContent) break;
                    for(Content c : container.getContents()) {
                        // Checking against first payload entity only
                        if(c.getTitle().toLowerCase().contains( directive.getDirective().getPayload().getEntities().get(0).getValue().toLowerCase() )) {
                            playContent = c;
                            break;
                        }
                    }
                }

                // Play video if we find some content matching directive payload
                if(null != playContent) {
                    browser.switchToRendererScreen(playContent, ContentBrowser.CONTENT_ACTION_WATCH_FROM_BEGINNING);
                } else {
                    // search for the content
                    searchForContent(app, browser, directivePayload);
                }

            } else if ("SearchAndDisplayResults".equals(directiveName)) {
                String directivePayload = directive.getDirective().getPayload().getEntities().get(0).getValue().toLowerCase();
                searchForContent(app, browser, directivePayload);
            } else if ("Pause".equals(directiveName)) {
                try {
                    PlaybackActivity playbackActivity = (PlaybackActivity) app.getCurrentActivity();
                    if(playbackActivity.isPlaying()) {
                        playbackActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playbackActivity.pause();
                            }
                        });
                    }
                } catch (Exception castE) {
                    Log.e(TAG, "Could not cast to PlayBackActivity!");
                    return;
                }

            }  else if ("Play".equals(directiveName) ) {
                try {
                    PlaybackActivity playbackActivity = (PlaybackActivity) app.getCurrentActivity();
                    playbackActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playbackActivity.play();
                        }
                    });
                } catch (Exception castE) {
                    Log.e(TAG, "Could not cast to PlayBackActivity!");
                    return;
                }

            }

        }

    }

    /**
     * @param searchQuery - Search text from the directive
     */
    private void searchForContent(TenFootApp app, ContentBrowser browser, String searchQuery) {

        browser.switchToScreen(ContentBrowser.CONTENT_SEARCH_SCREEN);
        // Wait upto 2 sec for the screen to switch
        for(int i = 0; i < 5; i++)  {
            Activity newActivity = app.getCurrentActivity();
            if(newActivity instanceof ContentSearchActivity) break;
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                return;
            }

        }

        ContentSearchActivity searchActivity = (ContentSearchActivity) app.getCurrentActivity();

        searchActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchActivity.getmFragment().setSearchQuery(searchQuery, true);
            }
        });
    }

}


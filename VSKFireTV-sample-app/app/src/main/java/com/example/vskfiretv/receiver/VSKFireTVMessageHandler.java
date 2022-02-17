/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.alexauicontroller.Element;
import com.amazon.alexauicontroller.Scene;
import com.amazon.alexauicontroller.UIAction;
import com.amazon.device.messaging.ADMMessageHandlerBase;
import com.amazon.device.messaging.ADMMessageReceiver;
import com.example.vskfiretv.DetailsActivity;
import com.example.vskfiretv.FireTVApp;
import com.example.vskfiretv.MainActivity;
import com.example.vskfiretv.Movie;
import com.example.vskfiretv.MovieList;
import com.example.vskfiretv.PlaybackActivity;
import com.example.vskfiretv.R;
import com.example.vskfiretv.data.ActionOnUIElement;
import com.example.vskfiretv.data.Directive;
import com.example.vskfiretv.data.Entity;
import com.example.vskfiretv.data.ExternalIds;
import com.example.vskfiretv.data.MediaDetailsElement;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.vskfiretv.utils.UIElementUtil;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import static com.example.vskfiretv.MainFragment.HOME_BROWSER_SCENE_IDENTIFIER;
import static com.example.vskfiretv.VideoDetailsFragment.VIDEO_DETAIL_SCENE_IDENTIFIER;
import static com.example.vskfiretv.utils.Constants.ACTION_ON_UI_ELEMENT;
import static com.example.vskfiretv.utils.Constants.ACTION_ON_MEDIA_DETAILS;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ACTION;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_TYPE;
import static com.example.vskfiretv.utils.Constants.URI_FOR_PLAY_SOMETHING;
import static com.example.vskfiretv.utils.Constants.URI_FOR_PLAY_SOMETHING_ELSE;

public class VSKFireTVMessageHandler extends ADMMessageHandlerBase {

    public static final String TAG = VSKFireTVMessageHandler.class.getSimpleName();

    public VSKFireTVMessageHandler() {
        super(TAG);
    }

    public static class MessageAlertReceiver extends ADMMessageReceiver {
        public MessageAlertReceiver() {
            super(VSKFireTVMessageHandler.class);
        }

        // Nothing else is required here; your broadcast receiver automatically
        // forwards intents to your service for processing.
    }

    @Override
    protected void onRegistered(final String newRegistrationId) {
        // You start the registration process by calling startRegister() in your Main
        // Activity. When the registration ID is ready, ADM calls onRegistered() on
        // your app. Transmit the passed-in registration ID to your server, so your
        // server can send messages to this app instance. onRegistered() is also
        // called if your registration ID is rotated or changed for any reason; your
        // app should pass the new registration ID to your server if this occurs.
        // Your server needs to be able to handle a registration ID up to 1536 characters
        // in length.

        // You have to provide the retrieved ADM registration Id to the Alexa Client library.
        Log.d(TAG, MessageFormat.format("ADM RegistrationId: {0}", newRegistrationId));
        // Provide the acquired ADM registration ID.
        AlexaClientManager.getSharedInstance().setDownChannelReady(true, newRegistrationId);
    }

    @Override
    protected void onUnregistered(final String registrationId) {
        // If your app is unregistered on this device, inform your server that
        // this app instance is no longer a valid target for messages.
    }

    @Override
    protected void onRegistrationError(final String errorId) {
        // You should consider a registration error fatal. In response, your app may
        // degrade gracefully, or you may wish to notify the user that this part of
        // your app's functionality is not available.
    }

    @Override
    protected void onMessage(final Intent intent) {
        // Extract the message content from the set of extras attached to
        // the com.amazon.device.messaging.intent.RECEIVE intent.
        Log.d(TAG, MessageFormat.format("Recieved a message from ADM: {0}", intent.toString()));

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

        final Gson gson = UIElementUtil.getGson();
        final Directive directive = gson.fromJson(msg, Directive.class);

        final JsonParser jsonParser = new JsonParser();
        final JsonElement jsonTree = jsonParser.parse(msg);

        Log.d(TAG, MessageFormat.format("onMessage: msg: {0}", msg));
        Log.d(TAG, MessageFormat.format("onMessage: directive: {0}", directive.getDirective().getHeader().getName()));

        // Most messages have a payload/entities structure, but some don't!

        if (directive != null) {
            final String directiveName = directive.getDirective().getHeader().getName();

            if ("SearchAndPlay".equals(directiveName)) {

                // For demonstration purposes, grabbing the first item in the movie list. Doesn't correspond to movie ID
                final Movie firstMovie = MovieList.getList().get(0);

                playMovie(firstMovie);

                Log.i(TAG, "Handling SearchAndPlay directive finished");

            } else if ("SearchAndDisplayResults".equals(directiveName)) {
                final String searchTerm = directive.getDirective().getPayload().getEntities().get(0).getValue().toLowerCase();
                Log.d(TAG, MessageFormat.format("Searching for: {0}", searchTerm));

                final String searchPayload = "";

                if (jsonTree.isJsonObject()) {
                    final JsonObject jsonObject = jsonTree.getAsJsonObject();

                    final JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();

                    final JsonObject jHeader = jDirective.get("header").getAsJsonObject();
                    final JsonObject jEndpoint = jDirective.get("endpoint").getAsJsonObject();
                    final JsonElement jPayload = jDirective.get("payload");

                    final String sHeaderName = jHeader.get("name").getAsString();

                    if (jPayload.isJsonObject()) {
                        final JsonObject jPayloadObject = jPayload.getAsJsonObject();
                        final JsonElement jSearchResults = jPayloadObject.get("searchResults");

                        final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                        final FireTVApp theApp = (FireTVApp) context;
                        final Activity topActivity = theApp.getCurrentActivity();

                        try {
                            final MainActivity mainActivity = (MainActivity) topActivity;
                            final String searchTermString = searchTerm;
                            final JsonElement jSearch = jSearchResults;

                            // Can only invoke this from the UI Thread
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainActivity.ShowResults(searchTermString, jSearch);
                                }
                            });

                        } catch (Exception castE) {
                            Log.e(TAG, "Could not cast to MainActivity!");
                            return;
                        }
                    }

                } else {
                    // Invalid message JSON
                }

            } else if ("Pause".equals(directiveName)) {

                final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                final FireTVApp theApp = (FireTVApp) context;
                final Activity topActivity = theApp.getCurrentActivity();
                Log.d(TAG, "Pausing MOVIE ");

                try {
                    final PlaybackActivity playbackActivity = (PlaybackActivity) topActivity;

                    // Can only invoke this from the UI Thread
                    playbackActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playbackActivity.PauseMovie();
                        }
                    });
                } catch (Exception castE) {
                    Log.e(TAG, "Could not cast to PlayBackActivity!");
                    return;
                }

            } else if ("Play".equals(directiveName)) {
                final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                final FireTVApp theApp = (FireTVApp) context;
                final Activity topActivity = theApp.getCurrentActivity();
                Log.d(TAG, "Playing MOVIE ");

                try {
                    final PlaybackActivity playbackActivity = (PlaybackActivity) topActivity;

                    // Can only invoke this from the UI Thread
                    playbackActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playbackActivity.UnpauseMovie();
                        }
                    });
                } catch (Exception castE) {
                    Log.e(TAG, "Could not cast to PlayBackActivity!");
                    return;
                }

            } else if ("Rewind".equals(directiveName)) {
                final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                final FireTVApp theApp = (FireTVApp) context;
                final Activity topActivity = theApp.getCurrentActivity();
                Log.d(TAG, "Rewinding MOVIE ");
                try {
                    final PlaybackActivity playbackActivity = (PlaybackActivity) topActivity;

                    // Can only invoke this from the UI Thread
                    playbackActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playbackActivity.RewindMovie();
                        }
                    });
                } catch (Exception castE) {
                    Log.e(TAG, "Could not cast to PlayBackActivity!");
                    return;
                }

            } else if ("AdjustSeekPosition".equals(directiveName)) {

                if (jsonTree.isJsonObject()) {
                    final JsonObject jsonObject = jsonTree.getAsJsonObject();

                    final JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();

                    final JsonObject jHeader = jDirective.get("header").getAsJsonObject();
                    final JsonObject jEndpoint = jDirective.get("endpoint").getAsJsonObject();
                    final JsonElement jPayload = jDirective.get("payload");

                    final String sHeaderName = jHeader.get("name").getAsString();

                    if (jPayload.isJsonObject()) {
                        final JsonObject jPayloadObject = jPayload.getAsJsonObject();
                        final JsonElement deltaPosElement = jPayloadObject.get("deltaPositionMilliseconds");
                        final Long deltaPosNum = deltaPosElement.getAsLong();
                        Log.d(TAG, MessageFormat.format("PAYLOAD: {0}", deltaPosNum.toString()));

                        final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                        final FireTVApp theApp = (FireTVApp) context;
                        final Activity topActivity = theApp.getCurrentActivity();
                        Log.d(TAG, "Rewinding MOVIE ");
                        try {
                            final PlaybackActivity playbackActivity = (PlaybackActivity) topActivity;
                            final long seekPos = deltaPosNum;
                            // Can only invoke this from the UI Thread
                            playbackActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    playbackActivity.SeekMovie(seekPos);
                                }
                            });
                        } catch (Exception castE) {
                            Log.e(TAG, "Could not cast to PlayBackActivity!");
                            return;
                        }
                    }

                } else {
                    // Invalid message JSON

                }

             } else if ("ActionOnUIElement".equals(directiveName)) {

                if (jsonTree.isJsonObject()) {
                    final JsonObject jsonObject = jsonTree.getAsJsonObject();

                    final JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();
                    final JsonElement jPayload = jDirective.get("payload");

                    if (jPayload != null && jPayload.isJsonObject()) {
                        final String jPayloadString = jPayload.getAsJsonObject().toString();

                        final ActionOnUIElement actionOnUIElement = gson.fromJson(jPayloadString, ActionOnUIElement.class);
                        Log.d(TAG, MessageFormat.format("The ActionOnUIElement directive is {0}", actionOnUIElement));

                        final boolean isActionOnUIElementValid = validateActionOnUIElementDirective(actionOnUIElement);
                        if (!isActionOnUIElementValid) {
                            Log.d(TAG, "The received ActionOnUIElement directive is invalid. Cannot process it.");
                            return;
                        }

                        final Intent actionOnUIElementIntent = new Intent();
                        actionOnUIElementIntent.setAction(ACTION_ON_UI_ELEMENT);
                        final String packageName = FireTVApp.getInstance().getPackageName();
                        actionOnUIElementIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        actionOnUIElementIntent.putExtra(EXTRA_UI_CONTROLLER_ELEMENT_ID, actionOnUIElement.getElement().getElementId());
                        actionOnUIElementIntent.putExtra(EXTRA_UI_CONTROLLER_ELEMENT_TYPE, actionOnUIElement.getElement().getEntity().getType());
                        actionOnUIElementIntent.putExtra(EXTRA_UI_CONTROLLER_ACTION, actionOnUIElement.getAction());

                        final Scene scene = actionOnUIElement.getScene();
                        if (scene.getSceneId().equals(HOME_BROWSER_SCENE_IDENTIFIER)) {
                            Log.d(TAG, MessageFormat.format("Setting the destination of actionOnUIElement intent to Home Screen: {0}",
                                    MainActivity.class.getName()));
                            actionOnUIElementIntent.setClassName(packageName, MainActivity.class.getName());
                        } else if (scene.getSceneId().equals(VIDEO_DETAIL_SCENE_IDENTIFIER)) {
                            Log.d(TAG, MessageFormat.format("Setting the destination of actionOnUIElement intent to Detail Screen: {0}",
                                    DetailsActivity.class.getName()));
                            actionOnUIElementIntent.setClassName(packageName, DetailsActivity.class.getName());
                        } else {
                            Log.e(TAG, MessageFormat.format("Unknown scene id {0}. Cannot process ActionOnUIElement directive", scene.getSceneId()));
                            return;
                        }
                        Log.d(TAG, MessageFormat.format("Sending the actionOnUIElement intent: {0}", actionOnUIElementIntent));
                        FireTVApp.getInstance().startActivity(actionOnUIElementIntent);
                        Log.d(TAG, "Finished processing the UIController directive");
                    }
                } else {
                    // Invalid message JSON
                    Log.e(TAG, "Invalid message JSON");
                }
            } else if ("DisplayDetails".equals(directiveName)) {

                // Checks json object
                if (!jsonTree.isJsonObject()) {
                    // Invalid message JSON
                    Log.e(TAG, "Invalid message JSON");
                    return;
                }

                // Extracts the underlying directive and payload
                final JsonObject jsonObject = jsonTree.getAsJsonObject();

                final JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();
                final JsonElement jPayload = jDirective.get("payload");

                // Checks payload
                if (jPayload == null || !jPayload.isJsonObject()) {
                    // Invalid payload
                    Log.e(TAG, "Invalid payload; payload is null or not a JsonObject");
                    return;
                }

                final String jPayloadString = jPayload.getAsJsonObject().toString();

                final MediaDetailsElement mediaDetailsElement = gson.fromJson(jPayloadString, MediaDetailsElement.class);
                Log.d(TAG, "The MediaDetailsNavigator directive is " + mediaDetailsElement);

                // Validates directive
                if (!isMediaDetailsElementValid(mediaDetailsElement)) {
                    Log.e(TAG, "The received MediaDetailsElement directive is invalid. Cannot process it.");
                    return;
                }

                final Intent mediaDetailsIntent = new Intent();
                final String currentSceneId = getCurrentSceneId();
                final String packageName = FireTVApp.getInstance().getPackageName();
                if (HOME_BROWSER_SCENE_IDENTIFIER.equals(currentSceneId)) {
                    Log.d(TAG, MessageFormat.format("Setting the destination of mediaDetailsElement intent to Home Screen: {0}",
                            MainActivity.class.getName()));
                    mediaDetailsIntent.setClassName(packageName, MainActivity.class.getName());
                } else if (VIDEO_DETAIL_SCENE_IDENTIFIER.equals(currentSceneId)) {
                    Log.d(TAG, MessageFormat.format("Setting the destination of mediaDetailsElement intent to Detail Screen: {0}",
                            DetailsActivity.class.getName()));
                    mediaDetailsIntent.setClassName(packageName, DetailsActivity.class.getName());
                } else {
                    Log.w(TAG, MessageFormat.format("Current screen does not have items to display details of. Current scene: {0}", currentSceneId));
                    return;
                }

                mediaDetailsIntent.setAction(ACTION_ON_MEDIA_DETAILS);
                mediaDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Adds entries from entity to intent
                mediaDetailsIntent.putExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE, mediaDetailsElement.getEntity().getType());
                mediaDetailsIntent.putExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE, mediaDetailsElement.getEntity().getValue());
                final ExternalIds externalIds = mediaDetailsElement.getEntity().getExternalIds();
                if (externalIds != null) {
                    Log.d(TAG, MessageFormat.format("externalIds is present: {0}", externalIds));
                    mediaDetailsIntent.putExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID, externalIds.getEntityId());
                }

                Log.d(TAG, MessageFormat.format("Sending the mediaDetailsElement intent: {0}", mediaDetailsIntent));
                FireTVApp.getInstance().startActivity(mediaDetailsIntent);
                Log.d(TAG, "Finished processing the DisplayDetails directive");
            } else if ("LaunchTarget".equals(directiveName)) {
                // Checks json object
                if (!jsonTree.isJsonObject()) {
                    // Invalid message JSON
                    Log.e(TAG, "Invalid message JSON");
                    return;
                }

                // Extracts the underlying directive and payload
                final JsonObject jsonObject = jsonTree.getAsJsonObject();

                final JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();
                final JsonElement jPayload = jDirective.get("payload");

                // Checks payload
                if (jPayload == null || !jPayload.isJsonObject()) {
                    // Invalid payload
                    Log.e(TAG, "Invalid payload; payload is null or not a JsonObject");
                    return;
                }

                final JsonObject jPayloadObject = jPayload.getAsJsonObject();
                if (!isLaunchTargetDirectivePayloadValid(jPayloadObject)) {
                    Log.e(TAG, "The received LaunchTarget directive payload is invalid. Cannot process it");
                    return;
                }

                final String name = jPayloadObject.get("name").getAsString();
                final String identifier = jPayloadObject.get("identifier").getAsString();
                final String sourceId = jPayloadObject.get("sourceId").getAsString();

                Log.d(TAG, MessageFormat.format("LaunchTarget payload name: {0}, identifier: {1}, and sourceId: {2}",
                        name, identifier, sourceId));

                if (URI_FOR_PLAY_SOMETHING.equals(identifier) || URI_FOR_PLAY_SOMETHING_ELSE.equals(identifier)) {
                    List<Movie> moviesList = MovieList.getList();
                    Random rand = new Random();
                    playMovie(moviesList.get(rand.nextInt(moviesList.size())));
                }

                Log.i(TAG, "Handling LaunchTarget directive finished");

            } else {
                Log.e(TAG, "Unknown directive received.");
            }
        }
    }

    private String getCurrentSceneId() {
        final Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
        final FireTVApp theApp = (FireTVApp) context;
        final Activity topActivity = theApp.getCurrentActivity();

        if (topActivity instanceof MainActivity) {
            return HOME_BROWSER_SCENE_IDENTIFIER;
        } else if (topActivity instanceof DetailsActivity) {
            return VIDEO_DETAIL_SCENE_IDENTIFIER;
        }
        return null;
    }

    private boolean validateActionOnUIElementDirective(final ActionOnUIElement actionOnUIElement) {
        if (actionOnUIElement == null) {
            Log.e(TAG, "ActionOnUIElement directive payload cannot be null.");
            return false;
        }

        final Scene scene = actionOnUIElement.getScene();
        final Element elementToBeActedUpOn = actionOnUIElement.getElement();
        final UIAction actionToBePerformed = actionOnUIElement.getAction();

        if (scene == null || elementToBeActedUpOn == null || actionToBePerformed == null) {
            Log.e(TAG, "Scene and elementToBeActedUpOn and actionToBePerformed cannot be null");
            return false;
        }

        if (scene.getSceneId() == null) {
            Log.e(TAG, "Scene id cannot be null");
            return false;
        }

        final String currentSceneId = getCurrentSceneId();
        Log.d(TAG, MessageFormat.format("The current scene id is {0}", currentSceneId));
        if (!scene.getSceneId().equals(currentSceneId)) {
            Log.w(TAG, MessageFormat.format("The current scene id {0} does not match with the received scene id {1}.",
                    currentSceneId, scene.getSceneId()));
            return false;
        }

        return true;
    }

    private boolean isMediaDetailsElementValid(final MediaDetailsElement mediaDetailsElement) {
        if (mediaDetailsElement == null) {
            Log.e(TAG, "MediaDetailsNavigator element cannot be null");
            return false;
        }
        final Entity entity = mediaDetailsElement.getEntity();
        if (entity == null) {
            Log.e(TAG, "Media details entity cannot be null");
            return false;
        }
        final String type = entity.getType();
        if (type == null) {
            Log.e(TAG, "Media details entity type cannot be null");
            return false;
        }
        final String value = entity.getValue();
        if (value == null) {
            Log.e(TAG, "Media details entity value cannot be null");
            return false;
        }
        return true;
    }

    private boolean isLaunchTargetDirectivePayloadValid(final JsonObject jPayloadObject) {
        final JsonElement jName = jPayloadObject.get("name");
        if (jName == null || !jName.isJsonPrimitive()) {
            // Invalid name
            Log.e(TAG, "Invalid name; name is null or not a JsonPrimitive");
            return false;
        }

        final JsonElement jIdentifier = jPayloadObject.get("identifier");
        if (jIdentifier == null || !jIdentifier.isJsonPrimitive()) {
            // Invalid identifier
            Log.e(TAG, "Invalid identifier; identifier is null or not a JsonPrimitive");
            return false;
        }

        final JsonElement jSourceId = jPayloadObject.get("sourceId");
        if (jSourceId == null || !jSourceId.isJsonPrimitive()) {
            // Invalid sourceId
            Log.e(TAG, "Invalid sourceId; sourceId is null or not a JsonPrimitive");
            return false;
        }
        return true;
    }

    private void playMovie(Movie movieToBePlayed) {
        final String movieName = movieToBePlayed.getTitle();
        Log.d(TAG, MessageFormat.format("Playing MOVIE {0}", movieName));

        final Intent playIntent = new Intent();
        final String packageName = AlexaClientManager.getSharedInstance().getApplicationContext().getPackageName();
        playIntent.setClassName(packageName, packageName + ".PlaybackActivity");
        playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // PlaybackActivity expects a movie to be currently selected, set this now in case there wasn't one
        playIntent.putExtra(DetailsActivity.MOVIE, movieToBePlayed);
        AlexaClientManager.getSharedInstance().getApplicationContext().startActivity(playIntent);
    }

}

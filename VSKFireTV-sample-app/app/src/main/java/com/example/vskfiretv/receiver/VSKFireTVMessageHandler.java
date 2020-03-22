package com.example.vskfiretv.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.device.messaging.ADMMessageHandlerBase;
import com.amazon.device.messaging.ADMMessageReceiver;
import com.example.vskfiretv.DetailsActivity;
import com.example.vskfiretv.FireTVApp;
import com.example.vskfiretv.MainActivity;
import com.example.vskfiretv.Movie;
import com.example.vskfiretv.MovieList;
import com.example.vskfiretv.PlaybackActivity;
import com.example.vskfiretv.R;
import com.example.vskfiretv.data.Directive;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        Log.d(TAG, "ADM RegistrationId: " + newRegistrationId);
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

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonTree = jsonParser.parse(msg);

        Log.d(TAG, "onMessage: msg" + msg);
        Log.d(TAG, "onMessage: directive: " + directive.getDirective().getHeader().getName());

        // Most messages have a payload/entities structure, but some don't!

        if (directive != null) {
            String directiveName = directive.getDirective().getHeader().getName();

            if ("SearchAndPlay".equals(directiveName)) {
                Movie firstMovie = MovieList.getList().get(0);
                String movieName = firstMovie.getTitle();
                Log.d(TAG, "Playing MOVIE " + movieName);

                // For demonstration purposes, grabbing the first item in the movie list. Doesn't correspond to movie ID
                Movie someMovie = MovieList.getList().get(0);

                Intent playIntent = new Intent();
                String packageName = AlexaClientManager.getSharedInstance().getApplicationContext().getPackageName();
                playIntent.setClassName(packageName, packageName + ".PlaybackActivity");
                playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // PlaybackActivity expects a movie to be currently selected, set this now in case there wasn't one
                playIntent.putExtra(DetailsActivity.MOVIE, someMovie);
                AlexaClientManager.getSharedInstance().getApplicationContext().startActivity(playIntent);

            } else if ("SearchAndDisplayResults".equals(directiveName)) {
                String searchTerm = directive.getDirective().getPayload().getEntities().get(0).getValue().toLowerCase();
                Log.d(TAG, "Searching for: " + searchTerm);

                String searchPayload = "";

                if (jsonTree.isJsonObject()) {
                    JsonObject jsonObject = jsonTree.getAsJsonObject();

                    JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();

                    JsonObject jHeader = jDirective.get("header").getAsJsonObject();
                    JsonObject jEndpoint = jDirective.get("endpoint").getAsJsonObject();
                    JsonElement jPayload = jDirective.get("payload");

                    String sHeaderName = jHeader.get("name").getAsString();

                    if (jPayload.isJsonObject()) {
                        JsonObject jPayloadObject = jPayload.getAsJsonObject();
                        JsonElement jSearchResults = jPayloadObject.get("searchResults");

                        Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                        FireTVApp theApp = (FireTVApp) context;
                        Activity topActivity = theApp.getCurrentActivity();

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

                Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                FireTVApp theApp = (FireTVApp) context;
                Activity topActivity = theApp.getCurrentActivity();
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
                Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                FireTVApp theApp = (FireTVApp) context;
                Activity topActivity = theApp.getCurrentActivity();
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
                Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                FireTVApp theApp = (FireTVApp) context;
                Activity topActivity = theApp.getCurrentActivity();
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
                    JsonObject jsonObject = jsonTree.getAsJsonObject();

                    JsonObject jDirective = jsonObject.get("directive").getAsJsonObject();

                    JsonObject jHeader = jDirective.get("header").getAsJsonObject();
                    JsonObject jEndpoint = jDirective.get("endpoint").getAsJsonObject();
                    JsonElement jPayload = jDirective.get("payload");

                    String sHeaderName = jHeader.get("name").getAsString();

                    if (jPayload.isJsonObject()) {
                        JsonObject jPayloadObject = jPayload.getAsJsonObject();
                        JsonElement deltaPosElement = jPayloadObject.get("deltaPositionMilliseconds");
                        Long deltaPosNum = deltaPosElement.getAsLong();
                        Log.d(TAG, "PAYLOAD: " + deltaPosNum.toString());

                        Context context = AlexaClientManager.getSharedInstance().getApplicationContext();
                        FireTVApp theApp = (FireTVApp) context;
                        Activity topActivity = theApp.getCurrentActivity();
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

            }

        }

    }
}

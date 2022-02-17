package com.example.vskfiretv.utils;

import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vskfiretv.utils.Constants.PLAY_SOMETHING;
import static com.example.vskfiretv.utils.Constants.PLAY_SOMETHING_ELSE;
import static com.example.vskfiretv.utils.Constants.URI_FOR_PLAY_SOMETHING;
import static com.example.vskfiretv.utils.Constants.URI_FOR_PLAY_SOMETHING_ELSE;


/**
 * Utility class that helps to build Alexa.Launcher objects for discovery and directive handling.
 */
public class AlexaLauncherUtils {
    private static final String TAG = "AlexaLauncherUtils";

    public static final String ALEXA_LAUNCHER_VERSION = "3.1";
    public static final String LAUNCH_CATALOG_ALEXA_VIDEO_APP_STORE = "ALEXA_VIDEO_APP_STORE";

    /**
     * Gets the current Alexa.Launcher version.
     * @return the current Alexa.Launcher version
     */
    public static String getAlexaLauncherVersion() {
        return ALEXA_LAUNCHER_VERSION;
    }

    /**
     * Gets the supported Alexa.Launcher catalogs.
     * @return the supported Alexa.Launcher catalogs
     */
    public static List<String> getLaunchCatalogs() {
        List<String> launchCatalogs = new ArrayList<>();
        launchCatalogs.add(LAUNCH_CATALOG_ALEXA_VIDEO_APP_STORE);
        return launchCatalogs;
    }

    /**
     * Gets the supported Alexa.Launcher targets.
     * @return the current Alexa.Launcher targets
     */
    public static Map<String, String> getLaunchTargets() {
        Map<String, String> launchTargets = new HashMap<>();
        launchTargets.put(PLAY_SOMETHING, URI_FOR_PLAY_SOMETHING);
        launchTargets.put(PLAY_SOMETHING_ELSE, URI_FOR_PLAY_SOMETHING_ELSE);
        Log.d(TAG, MessageFormat.format("Launch targets: {0}", launchTargets));
        return launchTargets;
    }
}
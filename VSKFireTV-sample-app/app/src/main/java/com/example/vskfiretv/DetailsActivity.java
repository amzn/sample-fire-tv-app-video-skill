/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.vskfiretv.utils.UIElementUtil;

import java.text.MessageFormat;

import static com.example.vskfiretv.utils.Constants.ACTION_ON_MEDIA_DETAILS;
import static com.example.vskfiretv.utils.Constants.ACTION_ON_UI_ELEMENT;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    protected FireTVApp myFireTVApp;
    public DetailsActivity.UIElementAction uiElementAction;
    private DetailsActivity.MediaDetailsAction mediaDetailsAction;

    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String MOVIE = "Movie";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        myFireTVApp = (FireTVApp) this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    protected void onResume() {
        super.onResume();
        myFireTVApp.setCurrentActivity(this);
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
    public void onAttachFragment(final Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "DetailsActivity fragment attached");

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
            Log.e(TAG, "Unknown Intent received");
        }
        Log.d(TAG, "Finished processing the received intent.");
    }

    public void setUiElementAction(final DetailsActivity.UIElementAction uiElementAction) {
        this.uiElementAction = uiElementAction;
    }

    public void setMediaDetailsAction(final DetailsActivity.MediaDetailsAction mediaDetailsAction) {
        this.mediaDetailsAction = mediaDetailsAction;
    }

    public interface UIElementAction {
        void onUIElementAction(final Bundle bundle);
    }

    public interface MediaDetailsAction {
        void onMediaDetailsAction(final Bundle bundle);
    }
}

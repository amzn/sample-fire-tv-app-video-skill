/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

/**
 * Loads {@link PlaybackVideoFragment}.
 */
public class PlaybackActivity extends FragmentActivity {

    protected FireTVApp myFireTVApp;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        myFireTVApp = (FireTVApp) this.getApplicationContext();
        myFireTVApp.setCurrentActivity(this);

        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new PlaybackVideoFragment(),"PlaybackVideoFragment")
                    .commit();
        }
    }

    public void PauseMovie(){
        final PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().pause();
    }

    public void UnpauseMovie(){
        final PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().play();
    }

    public void RewindMovie(){
        final PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().seekTo(0);
    }

    public void SeekMovie(final Long seekTime){
        final PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        final Long curPos = fragmentDemo.getPlayer().getCurrentPosition();
        final Long movieLength = fragmentDemo.getPlayer().getDuration();

        Long seekPos = curPos + seekTime;

        if (seekPos > movieLength){
            seekPos = movieLength - 10000;
        }

        if (seekPos < 0){
            seekPos = 0L;
        }

        fragmentDemo.getPlayer().seekTo(seekPos);
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
}
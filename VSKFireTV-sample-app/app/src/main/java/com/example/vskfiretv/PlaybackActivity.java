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

import androidx.fragment.app.FragmentActivity;

/**
 * Loads {@link PlaybackVideoFragment}.
 */
public class PlaybackActivity extends FragmentActivity {

    protected FireTVApp myFireTVApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().pause();
    }

    public void UnpauseMovie(){
        PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().play();
    }

    public void RewindMovie(){
        PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        fragmentDemo.getPlayer().seekTo(0);
    }

    public void SeekMovie(Long seekTime){
        PlaybackVideoFragment fragmentDemo = (PlaybackVideoFragment)
                getSupportFragmentManager().findFragmentByTag("PlaybackVideoFragment");
        Long curPos = fragmentDemo.getPlayer().getCurrentPosition();
        Long movieLength = fragmentDemo.getPlayer().getDuration();
        //Log.d("Playback", "SeekMovie curPos:" + curPos + "  duration:" + movieLength);

        Long seekPos = curPos + seekTime;

        if(seekPos > movieLength){
            seekPos = movieLength - 10000;
        }

        if(seekPos < 0){
            seekPos = 0L;
        }

        //Log.d("Playback", "SeekTime: " + seekPos);
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
        Activity currActivity = myFireTVApp.getCurrentActivity();
        if (this.equals(currActivity))
            myFireTVApp.setCurrentActivity(null);
    }
}
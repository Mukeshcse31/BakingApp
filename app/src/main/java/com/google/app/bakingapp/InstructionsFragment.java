/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.app.bakingapp;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.app.bakingapp.model.Steps;


// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class InstructionsFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = InstructionsFragment.class.getSimpleName();
    public static final String KEY_POSITION = "KEY_POSITION";
    public int startWindow;
    private static Uri mediaUri;
private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;
    private ImageView iv_default;
    private String thumbnail, videoURL;
    public static long startPosition;
    public static boolean getPlayerWhenReady;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    // Mandatory empty constructor
    public InstructionsFragment() {

        startPosition = 0;
        getPlayerWhenReady = true;
//        mExoPlayer = null;
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            startPosition = savedInstanceState.getLong(KEY_POSITION);
            Log.i("Video", "start position retrieved " + startPosition);

        }
        final View rootView = inflater.inflate(R.layout.fragment_instruction, container, false);

        // Get a reference to the GridView in the fragment_master_list xml layout file
        mPlayerView = rootView.findViewById(R.id.playerView);

        iv_default = rootView.findViewById(R.id.iv_default);

//setThumbnail
        if (StepsActivity.mTwoPane) {
            StepsActivity activity = (StepsActivity) getActivity();
            thumbnail = activity.getThumbnail();
        } else {
            InstructionsActivity activity = (InstructionsActivity) getActivity();
            thumbnail = activity.getThumbnail();

        }
        Bitmap bitmap1 = null;
//        thumbnail = "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"; for testing

        if (thumbnail == null || thumbnail.isEmpty())
            bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.question_mark);
        else {
            bitmap1 = BitmapFactory.decodeFile(thumbnail);
        }
        mPlayerView.setDefaultArtwork(bitmap1);

//        instruction_tv = rootView.findViewById(R.id.instruction);
        setInstruction();

        if (videoURL == null || videoURL != "") {

            iv_default.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.INVISIBLE);
        }
        //https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4
        if (videoURL != null && videoURL != "") {
            ViewGroup.LayoutParams params = iv_default.getLayoutParams();
            params.width = 0;
            iv_default.setLayoutParams(params);
            iv_default.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.VISIBLE);
            initializeMediaSession();
            mediaUri = Uri.parse(videoURL);
            initializePlayer();
        }

        // Return the root view
        return rootView;
    }

    public void setInstruction() {

        Steps steps = null;
        if (StepsActivity.mTwoPane) {
            steps = StepsActivity.mSteps;
        } else {
            InstructionsActivity instructionsActivity = (InstructionsActivity) getActivity();

            steps = instructionsActivity.getSteps();
        }
        if (steps != null) {
            videoURL = steps.getVideoURL();
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Release ExoPlayer.
     */
    public void releasePlayer() {
//        mNotificationManager.cancelAll();

        if(mExoPlayer != null) {
            if (!StepsActivity.mTwoPane)
                updateStartPosition();

            mExoPlayer.stop();

            mExoPlayer.release();
            mExoPlayer = null;

        }
    }

    public void resetVideo1(){

        startPosition = 0;
        try {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetVideo(){

//        mExoPlayer.seekTo(0);
        startPosition = 0;
        getPlayerWhenReady = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
//            if (mPlayerView != null) {
//
//                mPlayerView.
//            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mExoPlayer == null) {
            initializePlayer();
//            if (mPlayerView != null) {
//                mPlayerView.onResume();
//            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {

            releasePlayer();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }
    /**
     * Initialize ExoPlayer.
     *
     * @ The URI of the sample to play.
     */
    private void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);

            if(startPosition > 0){
                mExoPlayer.seekTo(startWindow, startPosition);

                if(getPlayerWhenReady)
                    mExoPlayer.setPlayWhenReady(getPlayerWhenReady);
                else
                    mExoPlayer.setPlayWhenReady(false);
            }
            else
            mExoPlayer.setPlayWhenReady(true);

            mExoPlayer.prepare(mediaSource);
        }
    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState!=null) {
            // Retrieve the user email value from bundle.
            startPosition = savedInstanceState.getLong(KEY_POSITION);

        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        updateStartPosition();
        super.onSaveInstanceState(outState);

//        Log.i("video", "startPosition 1 - " + startPosition);
//        Log.i("video", "getPlayerWhenReady 1 - " + getPlayerWhenReady);
        //Save the fragment's state here

    }

    private void updateStartPosition() {

        if (mExoPlayer != null) {
//            startAutoPlay = mExoPlayer.getPlayWhenReady();
            startWindow = mExoPlayer.getCurrentWindowIndex();
            startPosition = Math.max(0, mExoPlayer.getCurrentPosition());
            getPlayerWhenReady = mExoPlayer.getPlayWhenReady();
//            playerPosition = mExoPlayer.getCurrentPosition();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("CONFIG", "configuration changed");
        updateStartPosition();

        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //First Hide other objects (listview or recyclerview), better hide them using Gone.

            Log.i("CONFIG", "landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("CONFIG", "portrait");
        }
    }

}

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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import com.google.app.bakingapp.IdlingResource.EspressoIdlingResource;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.model.Steps;

// This activity will display a custom Android image composed of three body parts: head, body, and legs
public class InstructionsActivity extends AppCompatActivity implements Instructions_Step_Fragment.MoveStepListener {

    private Recipe mRecipe;
    private String desc, url;
    private static Steps mSteps;
    private String TAG;
    Instructions_Step_Fragment instruction_Step;
    InstructionsFragment instructionsFragment;
    private LinearLayout mLinearLayout;
    private int cur_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TAG = InstructionsActivity.class.getSimpleName();
        mLinearLayout = findViewById(R.id.android_me_linear_layout);

        Log.i("video","ON create called");

        Bundle bundle = getIntent().getExtras();

        if (bundle == null){}
        else {
            url = bundle.getString("VIDEO", null);
            mSteps = bundle.getParcelable(getString(R.string.STEP_E));
            mRecipe = bundle.getParcelable(getString(R.string.ALL_STEPS_E));
            cur_step = bundle.getInt(getString(R.string.CUR_STEP_E), 0);
        }

        setTitle();
        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {

            Log.i("video","saved instance IS null");

            // Create a new head BodyPartFragment
            instructionsFragment = new InstructionsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inst_with_video, instructionsFragment)
                    .commit();

            instruction_Step = new Instructions_Step_Fragment();
            instruction_Step.setMoveStepListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inst_step, instruction_Step)
                    .commit();
            EspressoIdlingResource.decrement();
        }
        else {

Log.i("video","saved instance not null");

        }

    }

    public static Steps getSteps() {
        return mSteps;
    }

    public String getThumbnail(){
        return mRecipe.getImage();
    }

    private void setTitle() {
        setTitle(mSteps.getShortDescription());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("CONFIG", "configuration changed");

        // Checking the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //First Hide other objects (listview or recyclerview), better hide them using Gone.

            getSupportFragmentManager().beginTransaction()
                    .hide(instruction_Step)
                    .commit();

            instructionsFragment = new InstructionsFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inst_with_video, instructionsFragment)
                    .commit();

            Log.i("CONFIG", "landscape");

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("CONFIG", "portrait");

            getSupportFragmentManager().beginTransaction()
                    .show(instruction_Step)
                    .commit();
        }
    }

    @Override
    public void onMoveNextStep() {

        Log.i(TAG, "NEXT is clicked");
        cur_step = cur_step + 1;
        mSteps = mRecipe.getSteps().get(cur_step);
        refreshStep();
    }

    @Override
    public void onMovePreviousStep() {

        Log.i(TAG, "PREVIOUS is clicked");

        cur_step = cur_step - 1;
        mSteps = mRecipe.getSteps().get(cur_step);
        refreshStep();

    }

    private void refreshStep() {

        instructionsFragment.resetVideo1();
        instructionsFragment = new InstructionsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.inst_with_video, instructionsFragment)
                .commit();

        instruction_Step = new Instructions_Step_Fragment();
        instruction_Step.setMoveStepListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.inst_step, instruction_Step)
                .commit();

        setTitle();
    }

    public boolean isFirstStep(){
return cur_step == 0;
    }

    public boolean isLastStep(){
        return cur_step == mRecipe.getSteps().size() - 1;
    }
}

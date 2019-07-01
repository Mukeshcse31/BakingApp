package com.google.app.bakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.app.bakingapp.IdlingResource.EspressoIdlingResource;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.model.Steps;
import com.google.app.bakingapp.utils.DBUtils;
import java.io.Serializable;

public class StepsActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

    private Recipe mRecipe;
    public static Steps mSteps;
    private SharedPreferences sharedPreferences;
    private static String EXTRA_RECIPE = "EXTRA_RECIPE";
    private String TAG;
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        TAG = StepsActivity.class.getSimpleName();
        //if  bundle is empty, open MainActivity

        Log.i("oncreate", "step Activity");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRecipe = bundle.getParcelable(MainActivity.RECIPE_SELECTED);
        } else {
            mRecipe = DBUtils.readRecipe(StepsActivity.this);
            if (mRecipe == null) {

                Intent intent = new Intent(StepsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        //initial step for Tablet
        mSteps = mRecipe.getSteps().get(0);
        IngredientService.startActionUpdateWidgets(this);
        setTitle(mRecipe.getName());
        setContentView(R.layout.activity_steps);

        if(findViewById(R.id.android_me_linear_layout) != null){
            mTwoPane = true;

            if(savedInstanceState == null){

                InstructionsFragment instructionsFragment = new InstructionsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.inst_with_video, instructionsFragment)
                        .commit();

                Instructions_Step_Fragment instruction_Step = new Instructions_Step_Fragment();
//                instruction_Step.setMoveStepListener(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.inst_step, instruction_Step)
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public Recipe getMyData() {
        return mRecipe;
    }

    public Steps getCurrentStep(){

        return mSteps;
    }


    public String getThumbnail(){
        return mRecipe.getImage();
    }

    public void onStepSelected(int position) {

        Log.i("STEP", "the step clicked is " + position);
        EspressoIdlingResource.increment();
        mSteps = mRecipe.getSteps().get(position);
//        videoURL = mSteps.getVideoURL();

        if(!mTwoPane) {
            Intent intent = new Intent(this, InstructionsActivity.class);

            intent.putExtra(getString(R.string.STEP_E), (Serializable) mRecipe.getSteps().get(position));
            intent.putExtra(getString(R.string.ALL_STEPS_E), mRecipe);
            intent.putExtra(getString(R.string.CUR_STEP_E), position);

            startActivity(intent);
        }
        else {

            //TODO change the step's background color to GREEN


            InstructionsFragment instructionsFragment = new InstructionsFragment();

//            instructionsFragment.resetVideo();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inst_with_video, instructionsFragment)
                    .commit();

            Instructions_Step_Fragment instruction_Step = new Instructions_Step_Fragment();
//                instruction_Step.setMoveStepListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.inst_step, instruction_Step)
                    .commit();
        }

//        setContentView(R.layout.activity_instructions);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(StepsActivity.EXTRA_RECIPE, mRecipe);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRecipe = savedInstanceState.getParcelable(StepsActivity.EXTRA_RECIPE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);
            gotoMainActivity();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoMainActivity();
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(StepsActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}

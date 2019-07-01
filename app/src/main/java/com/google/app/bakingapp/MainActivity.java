package com.google.app.bakingapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.app.bakingapp.Adapter.RecipeAdapter;
import com.google.app.bakingapp.IdlingResource.EspressoIdlingResource;
import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.model.Steps;
import com.google.app.bakingapp.provider.IngredientContract;
import com.google.app.bakingapp.utils.DBUtils;
import com.google.app.bakingapp.utils.JSONUtils;
import com.google.app.bakingapp.utils.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        RecipeAdapter.OnRecipeClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String APP_NAME = "RECIPE";
    public static String RECIPE_NAME_TEST;
    public static final String RECIPE_SELECTED = "RECIPE_SELECTED";
    private static int BAKING_SEARCH_LOADER = 33;
    private static final String SEARCH_QUERY_URL_EXTRA = "SEARCH_QUERY_URL_EXTRA";
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    public List<Recipe> allRecipes;
    private GridView mGridView;
    private RecipeAdapter mRecipeAdapter;

    public static List<Ingredients> widgetIngr;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME));

        setContentView(R.layout.activity_recipe_grid);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorMessage = findViewById(R.id.tv_error_message_display);
        mGridView = findViewById(R.id.gv_recipes);
        allRecipes = new ArrayList<>(); //TODO change this

        RECIPE_NAME_TEST = getString(R.string.recipeName_Test);

        // Get the desired recipe id from shared preferences to set it up
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Register the listener

        // as per the PDF
        //https://classroom.udacity.com/nanodegrees/nd801/parts/ec45ffe9-2c4e-4b8d-ad76-d80c5905d926/modules/015a9039-5994-4242-bed8-fe2610047d8f/lessons/a38bc34d-8c5f-468f-8562-0aed7fba6d5a/concepts/16d87c7d-a648-4c0c-be4e-e88fb5f1ac6b
        if(findViewById(R.id.view) != null) {
            mGridView.setNumColumns(3);
        }
        else {
            mGridView.setNumColumns(1);
        }

        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY_URL_EXTRA, getResources().getString(R.string.baking_url));

        EspressoIdlingResource.increment();
        if (!NetworkUtils.isOnline(MainActivity.this)) {

            Recipe mRecipe = DBUtils.readRecipe(MainActivity.this);//check for DB
            if (mRecipe == null)
                showErrorMessage(getString(R.string.no_internet_error));
            else {
                //allRecipes = new ArrayList<>();// to prevent duplicate recipes
                allRecipes.add(mRecipe);
                populateUI();
            }
        } else {

            getSupportLoaderManager().initLoader(BAKING_SEARCH_LOADER, bundle, this);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            String jsonRaw;

            @Override
            protected void onStartLoading() {

                if (bundle == null) return;

                if (jsonRaw != null) {
                    deliverResult(jsonRaw);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                String searchQueryUrlString = bundle.getString(SEARCH_QUERY_URL_EXTRA, "");

                /* If the user didn't enter anything, there's nothing to search for */
                if (TextUtils.isEmpty(searchQueryUrlString)) {
                    return null;
                }

                try {
                    URL bakingURL = new URL(searchQueryUrlString);
                    jsonRaw = NetworkUtils.getResponseFromHttpUrl(bakingURL);
                    return jsonRaw;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String data) {
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished(@NonNull Loader loader, String recipeData) {

        mProgressBar.setVisibility(View.INVISIBLE);

        if (allRecipes != null || (recipeData != null && !recipeData.isEmpty())) {
            allRecipes = JSONUtils.getRecipesFromJSON(MainActivity.this, recipeData);
            populateUI();
            EspressoIdlingResource.decrement();
        } else {
            showErrorMessage(getResources().getString(R.string.error_fetch));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private void populateUI() {

        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecipeAdapter = new RecipeAdapter(this, allRecipes);
        mGridView.setAdapter(mRecipeAdapter);
    }

    private void showErrorMessage(String message) {

        mErrorMessage.setText(message);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    public void onRecipeSelected(int position) {

        setupSharedPreferences(position);
        Log.i(APP_NAME, "recipe clicked is " + position);
        Intent intent = new Intent(MainActivity.this, StepsActivity.class);

        intent.putExtra(RECIPE_SELECTED, allRecipes.get(position));
        startActivity(intent);
    }

    private void setupSharedPreferences(int position) {

        Recipe mRecipe = allRecipes.get(position);

        /*
        //Check content provider TODO check if the desired recipe changes
        1. save for the 1st time
        2. replace afterwards if different
         */
        int desired_recipe = sharedPreferences.getInt(getString(R.string.desired_recipe), -1);
        sharedPreferences.edit().putInt(getString(R.string.desired_recipe), (int) mRecipe.getId()).commit();
        if (desired_recipe == -1) {
            DBUtils.insertRecipe(MainActivity.this, mRecipe);
            return;
        }
        if (desired_recipe == mRecipe.getId()) {
            Log.i("RECIPE", "there is no change in the selection");
        } else {
            DBUtils.deleteRecipe(MainActivity.this, (long) desired_recipe);
            DBUtils.insertRecipe(MainActivity.this, mRecipe);
//            notifyAppWidgetViewDataChanged();
        }
        Log.i("preference updated", "" + position);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        int selection = sharedPreferences.getInt(key, 0);

        if (selection > 0)
            Log.i(APP_NAME, "preference changed " + key + " Value : " + sharedPreferences.getInt(key, 0));

    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
//        getSupportLoaderManager().restartLoader(BAKING_SEARCH_LOADER, null, null);

    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}

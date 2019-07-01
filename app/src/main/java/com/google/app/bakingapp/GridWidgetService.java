package com.google.app.bakingapp;

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

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.utils.DBUtils;

import java.util.List;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    List<Ingredients> mIngredients;
    Recipe mRecipe = new Recipe();

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;

    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        //Get the desired recipe
        mRecipe = DBUtils.readRecipe(mContext);
        mIngredients = mRecipe.getIngredients();

    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget);

//        views.setTextViewText(R.id.widget_ingredient_name, mRecipe.getName());
        Ingredients curIngredient = mIngredients.get(position);
        String text = String.format("\n%s %s of %s\n",curIngredient.getQuantity(), curIngredient.getMeasure(), curIngredient.getIngredient());
        views.setTextViewText(R.id.widget_ingredient_name, text);

        Bundle extras = new Bundle();
        extras.putParcelable(MainActivity.RECIPE_SELECTED, mRecipe);//TODO open the step activity
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

//        intent.putExtra(MainActivity.RECIPE_SELECTED, mRecipe);

        views.setOnClickFillInIntent(R.id.widget_ingredient_name, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}


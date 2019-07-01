


/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.app.bakingapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.google.app.bakingapp.R;
import com.google.app.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {

    private List<Recipe> mRecipeList = new ArrayList<>();
    private Context mContext;
    private OnRecipeClickListener mOnRecipeClickListener;

    public interface OnRecipeClickListener {
        void onRecipeSelected(int position);
    }

    public RecipeAdapter(Context context, List<Recipe> recipeData) {
        mContext = context;
        mRecipeList = recipeData;
        mOnRecipeClickListener = (OnRecipeClickListener) context;

    }

    @Override
    public int getCount() {
        return mRecipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Recipe recipe = mRecipeList.get(position);

        if (convertView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.recipe_list, null);

            final ViewHolder holder = new ViewHolder();
            holder.recipe_bt = convertView.findViewById(R.id.bt_recipe);
            holder.recipe_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("GRID", "onClick: ");
                    mOnRecipeClickListener.onRecipeSelected(position);
                }
            });


            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        Button bt_recipe = holder.recipe_bt;
        String contentDesc = recipe.getName();
        if (contentDesc == null || contentDesc.isEmpty()) {
            bt_recipe.setContentDescription("");
            bt_recipe.setText("");
        } else {
            bt_recipe.setContentDescription(contentDesc);
            bt_recipe.setText(contentDesc);
        }
        return convertView;
    }

    public class ViewHolder {

        private Button recipe_bt;

    }

    public void setRecipeData(List<Recipe> recipeData) {
        mRecipeList = null;
        mRecipeList = recipeData;
        this.notifyDataSetChanged();
    }
}
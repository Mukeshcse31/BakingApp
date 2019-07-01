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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.app.bakingapp.Adapter.StepListAdapter;
import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Steps;

import java.util.List;


// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class StepListFragment extends Fragment implements StepListAdapter.OnStepClickListener{

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    OnStepClickListener mCallback;
//    GridView mRecyclerView;
    RecyclerView mRecyclerView;
    TextView mIngredients_tv, steps_lbl, ingredients_lbl;
    List<Steps> mStepsList;
    StepListAdapter mAdapter;

    @Override
    public void onclickStep(int position) {

//        resetVideoPlayer();
        StepsActivity stepsActivity = (StepsActivity) getActivity();
        changeColor(position);
        stepsActivity.onStepSelected(position);


    }

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }


    // Mandatory empty constructor
    public StepListFragment() {
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_step_list, container, false);

        // Get a reference to the GridView in the fragment_master_list xml layout file
//        mRecyclerView = rootView.findViewById(R.id.steps_grid_view);
//        mRecyclerView.setNumColumns(1);
//

        //Steps
        steps_lbl = rootView.findViewById(R.id.steps_tv);
        mRecyclerView = rootView.findViewById(R.id.steps_grid_view1);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        StepsActivity stepsActivity = (StepsActivity) getActivity();
        mStepsList = stepsActivity.getMyData().getSteps();
        mAdapter = new StepListAdapter(this, mStepsList);

        // Set the adapter on the GridView
        mRecyclerView.setAdapter(mAdapter);

        //Ingredients
        ingredients_lbl = rootView.findViewById(R.id.ingredients_tv);
        mIngredients_tv = rootView.findViewById(R.id.widget_ingredient_name);
        List<Ingredients> ingredientsList = stepsActivity.getMyData().getIngredients();
        if(ingredientsList == null || ingredientsList.size() == 0){
            mIngredients_tv.setText(getString(R.string.no_ingredients));
        }
        else {
            String ingredientText ="";
            for(int i = 0; i < ingredientsList.size(); i++){
                ingredientText += String.format("%s %s of %s, ", ingredientsList.get(i).getQuantity(),
                        ingredientsList.get(i).getMeasure(), ingredientsList.get(i).getIngredient());
            }
            ingredientText = TextUtils.substring(ingredientText, 0, ingredientText.length()-2);
            mIngredients_tv.setText(ingredientText);
        }

        // Return the root view
        return rootView;
    }

public void changeColor(int pos){

        if(StepsActivity.mTwoPane)
    for(int i = 0; i < mStepsList.size(); i ++) {
        Log.i("STEP", "clicked step is " + pos);
        View view = mRecyclerView.getChildAt(i);
        if(i == pos)
        view.setBackgroundResource(R.color.selected_step);
        else
            view.setBackgroundColor(getResources().getColor(R.color.unselected_step));
    }

}

private void resetVideoPlayer(){// todo remove
        InstructionsFragment.startPosition = 0;
        InstructionsFragment.getPlayerWhenReady= true;
}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

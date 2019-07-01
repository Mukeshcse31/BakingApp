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
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.app.bakingapp.model.Steps;


// This fragment displays all of the AndroidMe images in one large list
// The list appears as a grid of images
public class Instructions_Step_Fragment extends Fragment {

    private static final String TAG = Instructions_Step_Fragment.class.getSimpleName();
    private TextView instruction_tv;
    private Button previous_bt, next_bt;
    private MoveStepListener mMoveStepListener;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface MoveStepListener {
        void onMoveNextStep();

        void onMovePreviousStep();
    }


    public Instructions_Step_Fragment() {
    }

    // Mandatory empty constructor
    public void setMoveStepListener(Context listener) {
        mMoveStepListener = (MoveStepListener) listener;
    }

    // Inflates the GridView of all AndroidMe images
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_instruction_step, container, false);

        instruction_tv = rootView.findViewById(R.id.instruction);
        previous_bt = rootView.findViewById(R.id.previous);
        next_bt = rootView.findViewById(R.id.next);
        setInstruction();

        if (!StepsActivity.mTwoPane) {
            previous_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoveStepListener.onMovePreviousStep();
                }
            });

            next_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoveStepListener.onMoveNextStep();
                }
            });
        } else {
            previous_bt.setVisibility(View.INVISIBLE);
            next_bt.setVisibility(View.INVISIBLE);
        }
        // Return the root view
        return rootView;
    }

    public void setInstruction() {
        InstructionsActivity instructionsActivity;
        String desc;
        Steps steps = null;
        if (StepsActivity.mTwoPane) {
            steps = StepsActivity.mSteps;
        } else {
            instructionsActivity = (InstructionsActivity) getActivity();
            steps = instructionsActivity.getSteps();

            if (instructionsActivity.isFirstStep()) {
                next_bt.setVisibility(View.VISIBLE);
                previous_bt.setVisibility(View.INVISIBLE);
            }
            if (instructionsActivity.isLastStep()) {
                next_bt.setVisibility(View.INVISIBLE);
                previous_bt.setVisibility(View.VISIBLE);
            }
        }
        //set description
        if (steps != null) {
            desc = steps.getDescription();

            if (desc != null && !desc.isEmpty())
                instruction_tv.setText(desc);
            else
                instruction_tv.setText("");

        }
    }
}

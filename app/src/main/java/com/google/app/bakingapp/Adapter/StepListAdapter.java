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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.app.bakingapp.R;
import com.google.app.bakingapp.StepListFragment;
import com.google.app.bakingapp.model.Steps;

import java.util.List;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.ReviewViewHolder> {

    private static final String TAG = StepListAdapter.class.getSimpleName();

    private static int viewHolderCount;
    private StepListFragment mContext;
    private List<Steps> mSteps;
private OnStepClickListener mStepClickListener;

    public interface OnStepClickListener{
        void onclickStep(int position);
    }

    public StepListAdapter(OnStepClickListener listener, List<Steps> mReviews) {
    mStepClickListener = listener;
        mContext = (StepListFragment) listener;
        mSteps = mReviews;
        viewHolderCount = 0;
    }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        viewHolder.tv_author.setTag("" + viewHolderCount);
        viewHolder.tv_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepClickListener.onclickStep(Integer.valueOf((String)v.getTag()));
            }
        });
        viewHolderCount = viewHolderCount + 1;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView tv_author;
        public ReviewViewHolder(View itemView) {
            super(itemView);

            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
        }

        /**
         * A method we wrote for convenience. This method will take an integer as input and
         * use that integer to display the appropriate text within a list item.
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {
            tv_author.setText(mSteps.get(listIndex).getShortDescription());

        }

    }
}

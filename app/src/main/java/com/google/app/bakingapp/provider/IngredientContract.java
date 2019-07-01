package com.google.app.bakingapp.provider;

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

import android.net.Uri;
import android.provider.BaseColumns;

public class IngredientContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.recipe";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "plants" directory
    public static final String PATH_INGREDIENTS = "ingredients";

    public static final long INVALID_INGREDIENT_ID = -1;

    public static final class IngredientEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String TABLE_NAME = "ingredients";
        public static final String NAME = "recipe";
        public static final String INGREDIENTLIST = "ingredientList";
        public static final String STEPS_LIST = "stepsList";
        public static final String SERVINGS = "servings";
        public static final String IMAGE = "image";
//        public static final String COLUMN_LAST_WATERED_TIME = "lastWateredAt";
    }
}


//            private double id;
//            private String name;
//            private List<Ingredients> ingredients;
//            private List<Steps> steps;
//            private double servings;
//            private String image;

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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.app.bakingapp.provider.IngredientContract.IngredientEntry;

public class IngredientDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "ingredient.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public IngredientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the plants data
        final String SQL_CREATE_PLANTS_TABLE = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                IngredientEntry._ID + " INTEGER PRIMARY KEY," +
                IngredientEntry.NAME + " STRING NOT NULL, " +
                IngredientEntry.INGREDIENTLIST + " BLOB NOT NULL, " +
                IngredientEntry.STEPS_LIST + " BLOB NOT NULL, " +
                IngredientEntry.SERVINGS + " INTEGER NOT NULL, " +
                IngredientEntry.IMAGE + " STRING )";

        sqLiteDatabase.execSQL(SQL_CREATE_PLANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


//            private double id;
//            private String name;
//            private List<Ingredients> ingredients;
//            private List<Steps> steps;
//            private double servings;
//            private String image;

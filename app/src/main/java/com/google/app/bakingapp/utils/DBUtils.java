package com.google.app.bakingapp.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.model.Steps;
import com.google.app.bakingapp.provider.IngredientContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

public static void deleteRecipe(Context context, long mRecipeID ){
    //Delete
    Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
            IngredientContract.BASE_CONTENT_URI.buildUpon().appendPath(IngredientContract.PATH_INGREDIENTS).build(), mRecipeID);
    context.getContentResolver().delete(SINGLE_PLANT_URI, null, null);

}

public static void insertRecipe(Context context, Recipe mRecipe){

    if(mRecipe == null) return;

    // Insert the new Recipe into DB
    String recipeType = mRecipe.getName();
    List<Ingredients> ingredientsList = mRecipe.getIngredients();
    List<Steps> stepsList = mRecipe.getSteps();
    double serving = mRecipe.getServings();
    String image = mRecipe.getImage();

    ContentValues contentValues = new ContentValues();
    contentValues.put(IngredientContract.IngredientEntry._ID, (long) mRecipe.getId());
    contentValues.put(IngredientContract.IngredientEntry.NAME, recipeType);
    contentValues.put(IngredientContract.IngredientEntry.SERVINGS, serving);
    contentValues.put(IngredientContract.IngredientEntry.IMAGE, image);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try {
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(ingredientsList);
        byte[] bytes = bos.toByteArray();
        contentValues.put(IngredientContract.IngredientEntry.INGREDIENTLIST, bytes);
    } catch (Exception e) {
    }

    try {
        ByteArrayOutputStream bosSteps = new ByteArrayOutputStream();
        ObjectOutputStream oosSteps = new ObjectOutputStream(bosSteps);
        oosSteps.writeObject(stepsList);
        byte[] bytesSteps = bosSteps.toByteArray();
        contentValues.put(IngredientContract.IngredientEntry.STEPS_LIST, bytesSteps);

    } catch (Exception e) {
        e.printStackTrace();
    }
    context.getContentResolver().insert(IngredientContract.IngredientEntry.CONTENT_URI, contentValues);

}

public static Recipe readRecipe(Context mContext){

    Cursor mCursor;
    Recipe mRecipe;
    Uri _URI = IngredientContract.BASE_CONTENT_URI.buildUpon().appendPath(IngredientContract.PATH_INGREDIENTS).build();

    mCursor = mContext.getContentResolver().query(_URI,null,null,null,null);

    if (mCursor == null || mCursor.getCount() == 0) return null;
    mCursor.moveToFirst();
    mRecipe = new Recipe();
    int idIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry._ID);
    int nameIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry.NAME);
    int listIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry.INGREDIENTLIST);
    int stepIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry.STEPS_LIST);
    int servingIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry.SERVINGS);
    int imageIndex = mCursor.getColumnIndex(IngredientContract.IngredientEntry.IMAGE);

    mRecipe.setId(mCursor.getInt(idIndex));
    mRecipe.setName(mCursor.getString(nameIndex));
    mRecipe.setServings(mCursor.getInt(servingIndex));
    mRecipe.setImage(mCursor.getString(imageIndex));

    byte[] li = mCursor.getBlob(listIndex);
    byte[] stepByte = mCursor.getBlob(stepIndex);

//        List<String> curOp = new ArrayList<>();
    ByteArrayInputStream bais = new ByteArrayInputStream(li);
    ObjectInputStream ois = null;

    ByteArrayInputStream baisStep = new ByteArrayInputStream(stepByte);
    ObjectInputStream oisStep = null;
    List<Ingredients> mIngredients = null;
    try {

        ois = new ObjectInputStream(bais);
        mIngredients = ( ArrayList<Ingredients>) ois.readObject();
        mRecipe.setIngredients(mIngredients);
        ois.close();

        oisStep = new ObjectInputStream(baisStep);
        mRecipe.setSteps (( ArrayList<Steps>) oisStep.readObject());
        ois.close();

    } catch (Exception ex) {
        ex.printStackTrace();
    } finally {
        //Close the ObjectInputStream
        try {
            if (mIngredients!= null) ois.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    mCursor.close();
    return mRecipe;
}
}

package com.google.app.bakingapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.utils.DBUtils;

import java.util.List;

public class IngredientWidgetProvider extends AppWidgetProvider {

    Recipe mRecipe = new Recipe();
    List<Ingredients> mIngredients;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//         There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, IngredientWidgetProvider.class);
            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
        }
        super.onReceive(context, intent);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static RemoteViews getSingleRemoteView(Context context) {

        Log.i("Widget", "invoked");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        Recipe mRecipe = DBUtils.readRecipe(context);
        String recipeName = "RECIPE";
        if (mRecipe != null) {
//            recipeName = mRecipe.getName();

            List<Ingredients> ingredientsList = mRecipe.getIngredients();
            String ingredientText = "";
            for (int i = 0; i < ingredientsList.size(); i++) {
                ingredientText += String.format("%s %s of %s, \n", ingredientsList.get(i).getQuantity(),
                        ingredientsList.get(i).getMeasure(), ingredientsList.get(i).getIngredient());
            }
            ingredientText = TextUtils.substring(ingredientText, 0, ingredientText.length() - 2);
            views.setTextViewText(R.id.widget_ingredient_name, ingredientText);
        }

//        views.setImageViewResource(R.id.widget_ingredient_name, R.drawable.launcher_icon);
        // Construct an Intent object includes web adresss.
        Intent intent = new Intent(context, StepsActivity.class);//TODO show the correct recipe's step activity

        intent.putExtra(MainActivity.RECIPE_SELECTED, mRecipe);

        // In widget we are not allowing to use intents as usually. We have to use PendingIntent instead of 'startActivity'
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Here the basic operations the remote view can do.
        views.setOnClickPendingIntent(R.id.widget_ingredient_name, pendingIntent);
        return views;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Get current width to decide on single plant vs garden grid view

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews rv;
        if (width < 100) {
            rv = getSingleRemoteView(context);
        } else {
            rv = getIngredientGridRemoteView(context);

        }
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the GridView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the GridView mode widget
     */
    private static RemoteViews getIngredientGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);


        Recipe mRecipe = DBUtils.readRecipe(context);
        String recipeName = "RECIPE";
        if (mRecipe != null) {
            recipeName = mRecipe.getName();
        }
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridWidgetService.class);
//        intent.putExtra(MainActivity.RECIPE_SELECTED, mRecipe);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, StepsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
//        PlantWateringService.startActionUpdateWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
        IngredientService.startActionUpdateWidgets(context);
        Log.i("ONEnabled", "called");
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }
}
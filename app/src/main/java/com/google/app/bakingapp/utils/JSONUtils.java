package com.google.app.bakingapp.utils;

import android.content.Context;
import com.google.app.bakingapp.R;
import com.google.app.bakingapp.model.Ingredients;
import com.google.app.bakingapp.model.Recipe;
import com.google.app.bakingapp.model.Steps;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    public static List<Recipe> getRecipesFromJSON(Context context, String json) {

        List<Recipe> recipes = new ArrayList<>();
        if (json == null || json.isEmpty())
            return null;
        else {

            try {
                JSONArray results = new JSONArray(json);

                if (results == null || results.length() == 0) {//when the result is not available, return null
                    return null;
                } else {


                    for (int i = 0; i < results.length(); i++) {
                        Recipe recipe = new Recipe();

                        if (results.get(i) == null) continue; //if the individual recipe is empty

                        try {
                            String id = context.getString(R.string.id);
                            ;
                            String name = context.getString(R.string.name);
                            String ingredients = context.getString(R.string.ingredients);
                            String steps = context.getString(R.string.steps);
                            String servings = context.getString(R.string.servings);
                            String image = context.getString(R.string.image);

                            JSONObject result = (JSONObject) results.get(i);

                            //id
                            double idValue = result.getDouble(id);
                            recipe.setId(idValue);

                            //name
                            String nameValue = result.getString(name);
                            if (nameValue == null || nameValue.isEmpty())
                                recipe.setName("");
                            else
                                recipe.setName(nameValue);

                            //ingredients
                            recipe.setIngredients(null);
                            List<Ingredients> ingredientsList = new ArrayList<>();
                            try {
                                JSONArray ingredientsArray = result.getJSONArray(ingredients);
                                if (!(ingredientsArray == null && ingredientsArray.length() == 0))
                                    for (int j = 0; j < ingredientsArray.length(); j++) {

                                        Ingredients ingredient1 = new Ingredients();
                                        JSONObject curIngredient = (JSONObject) ingredientsArray.get(j);

                                        try {
                                            String quantity = context.getResources().getString(R.string.quantity);
                                            String measure = context.getResources().getString(R.string.measure);
                                            String ingredient = context.getResources().getString(R.string.ingredient);

                                            double quantityValue = curIngredient.getDouble(quantity);
                                            ingredient1.setQuantity(quantityValue);

                                            String measureValue = curIngredient.getString(measure);
                                            if (!(measureValue == null || measureValue.isEmpty()))
                                                ingredient1.setMeasure(measureValue);

                                            String ingredientValue = curIngredient.getString(ingredient);
                                            if (!(ingredientValue == null || ingredientValue.isEmpty()))
                                                ingredient1.setIngredient(ingredientValue);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            ingredientsList.add(ingredient1);
                                        }
                                    }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            finally {
                                recipe.setIngredients(ingredientsList);
                            }


                            // steps
                            recipe.setSteps(null);
                            List<Steps> stepsList = new ArrayList<>();
                            try {
                                JSONArray stepsArray = result.getJSONArray(steps);
                                if (!(stepsArray == null && stepsArray.length() == 0))
                                    for (int j = 0; j < stepsArray.length(); j++) {

                                        Steps step1 = new Steps();
                                        JSONObject curStep = (JSONObject) stepsArray.get(j);

                                        try {
                                            String stepid = context.getResources().getString(R.string.stepid);
                                            String shortDescription = context.getResources().getString(R.string.shortDescription);
                                            String description = context.getResources().getString(R.string.description);
                                            String videoURL = context.getResources().getString(R.string.videoURL);
                                            String thumbnailURL = context.getResources().getString(R.string.thumbnailURL);

                                            double stepidValue = curStep.getDouble(stepid);
                                            step1.setId(stepidValue);

                                            String shortDescriptionValue = curStep.getString(shortDescription);
                                            if (!(shortDescriptionValue == null || shortDescriptionValue.isEmpty()))
                                                step1.setShortDescription(shortDescriptionValue);

                                            String descriptionValue = curStep.getString(description);
                                            if (!(descriptionValue == null || descriptionValue.isEmpty()))
                                                step1.setDescription(descriptionValue);

                                            String videoURL_val = curStep.getString(videoURL);
                                            if (!(videoURL_val == null || videoURL_val.isEmpty()))
                                                step1.setVideoURL(videoURL_val);

                                            String thumbnailURL_val = curStep.getString(thumbnailURL);
                                            if (!(thumbnailURL_val == null || thumbnailURL_val.isEmpty()))
                                                step1.setThumbnailURL(thumbnailURL_val);


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            stepsList.add(step1);
                                        }
                                    }
                            }
                            catch (Exception e){
                                e.printStackTrace();}
                            finally {
                                recipe.setSteps(stepsList);
                            }

                            // servings
                            double servingsValue = result.getDouble(servings);
                            recipe.setServings(servingsValue);

                            // image
                            String imageValue = result.getString(image);
                            if (!(imageValue == null || imageValue.isEmpty()))
                                recipe.setImage(imageValue);

//                            Log.i(MainActivity.APP_NAME, String.format(" id of %d recipe is %s ", i, idValue));
//                            Log.i(MainActivity.APP_NAME, String.format(" nameValue of %d recipe is %s ", i, nameValue));
//                            Log.i(MainActivity.APP_NAME, String.format(" ingredientsList of %d recipe is %s ", i, ingredientsList.toString()));
//                            Log.i(MainActivity.APP_NAME, String.format(" stepsList of %d recipe is %f ", i, stepsList.toString()));
//                            Log.i(MainActivity.APP_NAME, String.format(" servingsValue of %d recipe is %s ", i, servingsValue));
//                            Log.i(MainActivity.APP_NAME, String.format(" imageValue of %d recipe is %s ", i, imageValue));

                            recipes.add(i, recipe);
                        } catch (Exception e) { // TODO goto the next iteration
                            e.printStackTrace();
                            //continue;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return recipes;
    }

    /*public static List<Review> getReviewsFromJSON(Context context, String json) {

        List<Review> reviews = new ArrayList<>();
        if (json == null || json.isEmpty())
            return null;
        else {

            try {
                JSONObject jsonObject = new JSONObject(json);

                // Log.i(MainActivity.APP_NAME, String.format(" JSON util %s",json));

                String resultsAttr = context.getString(R.string.results);
                JSONArray results = jsonObject.getJSONArray(resultsAttr);

                if(results == null || results.length() == 0){//when the result is not available, return null
                    return null;
                }
                else{
                    Review review = null;

                    for( int i = 0; i < results.length(); i++){

                        review = new Review();
                        if(results.get(i) == null) continue;

                        try {
                            String author = context.getString(R.string.author);
                            String content = context.getString(R.string.content);

                            JSONObject result = (JSONObject) results.get(i);

                            //title
                            String authorValue = result.getString(author);
                            if (authorValue == null || authorValue.isEmpty())
                                review.setAuthor("");
                            else
                                review.setAuthor(authorValue);

                            //title
                            String contentValue = result.getString(content);
                            if (contentValue == null || contentValue.isEmpty())
                                review.setContent("");
                            else
                                review.setContent(contentValue);
                            Log.i(MainActivity.APP_NAME, String.format(" title of %d movie is %s ", i, authorValue));
                            Log.i(MainActivity.APP_NAME, String.format(" release date of %d movie is %s ", i, contentValue));

                            reviews.add(i, review);
                        }
                        catch (Exception e){ // TODO goto the next iteration
                            e.printStackTrace();
                            //continue;
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


        return reviews;
    }

    public static List<Video> getVideosFromJSON(Context context, String json) {

        List<Video> videos = new ArrayList<>();
        if (json == null || json.isEmpty())
            return null;
        else {

            try {
                JSONObject jsonObject = new JSONObject(json);

                String resultsAttr = context.getString(R.string.results);
                JSONArray results = jsonObject.getJSONArray(resultsAttr);

                if(results == null || results.length() == 0){//when the result is not available, return null
                    return null;
                }
                else{
                    Video video = null;

                    for( int i = 0; i < results.length(); i++){

                        video = new Video();
                        if(results.get(i) == null) continue;

                        try {

                            String id = context.getString(R.string.id);
                            String iso_639_1 = context.getString(R.string.iso_639_1);
                            String iso_3166_1 = context.getString(R.string.iso_3166_1);
                            String key = context.getString(R.string.key);
                            String name = context.getString(R.string.name);
                            String site = context.getString(R.string.site);
                            String size = context.getString(R.string.size);
                            String type = context.getString(R.string.type);

                            JSONObject result = (JSONObject) results.get(i);

                            //id
                            String idValue = result.getString(id);
                            if (idValue == null || idValue.isEmpty())
                                video.setId("");
                            else
                                video.setId(idValue);

                            //iso_639_1
                            String iso_639_1Value = result.getString(iso_639_1);
                            if (iso_639_1Value == null || iso_639_1Value.isEmpty())
                                video.setIso_639_1("");
                            else
                                video.setIso_639_1(iso_639_1Value);

//                            //iso_3166_1
//                            String iso_3166_1Value = result.getString(iso_3166_1);
//                            if (iso_3166_1Value == null || iso_3166_1Value.isEmpty())
//                                video.setIso_3166_1("");
//                            else
//                                video.setIso_3166_1(iso_3166_1Value);

                            //key
                            String keyValue = result.getString(key);
                            if (keyValue == null || keyValue.isEmpty())
                                video.setKey("");
                            else
                                video.setKey(keyValue);

                            //name
                            String nameValue = result.getString(name);
                            if (nameValue == null || nameValue.isEmpty())
                                video.setName("");
                            else
                                video.setName(nameValue);

                            //size
                            Double sizeValue = result.getDouble(size);
                            if (sizeValue == null)
                                video.setSize(0);
                            else
                                video.setSize(sizeValue);

                            //site
                            String siteValue = result.getString(site);
                            if (siteValue == null || siteValue.isEmpty())
                                video.setSite("");
                            else
                                video.setSite(siteValue);

                            //type
                            String typeValue = result.getString(type);
                            if (typeValue == null || typeValue.isEmpty())
                                video.setType("");
                            else
                                video.setType(typeValue);


                            Log.i(MainActivity.APP_NAME, String.format(" title of %d movie is %s ", i, sizeValue));
                            Log.i(MainActivity.APP_NAME, String.format(" release date of %d movie is %s ", i, typeValue));

                            videos.add(video);
                        }
                        catch (Exception e){ // TODO goto the next iteration
                            e.printStackTrace();
                            //continue;
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


        return videos;
    }


    public static List<Movie> getMovieFromEntry(List<MovieEntry> movieEntries) {

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieEntries.size(); i++) {
            Movie movie = new Movie();
            movie.setId(movieEntries.get(i).getId());
            movie.setPoster_path(movieEntries.get(i).getPoster_path());
            movie.setTitle(movieEntries.get(i).getTitle());
            movie.setImage(movieEntries.get(i).getPoster());
            movie.setOverview(movieEntries.get(i).getOverview());
            movie.setVote_average(movieEntries.get(i).getVote_average());
            movie.setOriginalTitle(movieEntries.get(i).getOriginal_title());
            movie.setRelease_date(movieEntries.get(i).getRelease_date());

            movies.add(movie);

        }
        return movies;
    }
    */
}

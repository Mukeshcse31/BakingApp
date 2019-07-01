package com.google.app.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {

    private double id;
    private String name;
    private List<Ingredients> ingredients;
    private List<Steps> steps;
    private double servings;
    private String image;

    public Recipe(){}

    public Recipe(double id, String name, List<Ingredients> ingredients, List<Steps> steps, double servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public double getServings() {
        return servings;
    }

    public void setServings(double servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }


    protected Recipe(Parcel in) {
        id = in.readDouble();
        name = in.readString();
        ingredients = new ArrayList<Ingredients>();
        in.readList(ingredients, Ingredients.class.getClassLoader());
        steps = new ArrayList<Steps>();
        in.readList(steps, Steps.class.getClassLoader());
        servings = in.readDouble();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(id);
        dest.writeString(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeDouble(servings);
        dest.writeString(image);


    }
}

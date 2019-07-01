package com.google.app.bakingapp;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.app.bakingapp.IdlingResource.EspressoIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StepActivity_ActionBar_Title_Test {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest_2() {

        String recipeName = MainActivity.RECIPE_NAME_TEST;

        ViewInteraction button = onView(
                allOf(withId(R.id.bt_recipe), withContentDescription(recipeName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.gv_recipes),
                                        0),
                                0),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.bt_recipe), withText(recipeName),
                        childAtPosition(
                                withParent(withId(R.id.gv_recipes)),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        //check the action bar title
        ViewInteraction textView = onView(
                allOf(withText(recipeName),
                        childAtPosition(
//                                allOf(withId(R.id.action_bar),childAtPosition(withId(R.id.action_bar_container),0)),1),
                                withId(R.id.action_bar),0),
                        isDisplayed()));
        textView.check(matches(withText(recipeName)));

//check the steps textview
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.steps_tv), withText("Steps"),
                        childAtPosition(
                                allOf(withId(R.id.master_list_fragment),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        //check the ingredients textview
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.ingredients_tv), withText("Ingredients"),
                        childAtPosition(
                                allOf(withId(R.id.master_list_fragment),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    // Register your Idling Resource before any tests regarding this component
    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    // Unregister your Idling Resource so it can be garbage collected and does not leak any memory
    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }
}

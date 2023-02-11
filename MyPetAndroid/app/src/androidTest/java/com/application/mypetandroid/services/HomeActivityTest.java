package com.application.mypetandroid.services;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.application.mypetandroid.R;
import com.application.mypetandroid.utils.singleton_examples.UserSingletonClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class HomeActivityTest {

    private UserSingletonClass userTest;

    @Before
    public void setUp() {
        userTest = UserSingletonClass.getSingletonInstance();
    }

    @Test
    public void mapScreenIsDisplayedAfterClickOnMapIconWhenLoggedUserIsPetSitterTest() {
        userTest.setRole(2);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.map_screen)).perform(click());
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void mapScreenIsDisplayedAfterClickOnMapIconWhenLoggedUserIsNormalUserTest() {
        userTest.setRole(1);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.map_screen)).perform(click());
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void appInfoScreenIsDisplayedAfterClickOnAppInfoIconWhenLoggedUserIsPetSitterTest() {
        userTest.setRole(2);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.app_info_screen)).perform(click());
        onView(withId(R.id.appInfoFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void appInfoScreenIsDisplayedAfterClickOnAppInfoIconWhenLoggedUserIsNormalUserTest() {
        userTest.setRole(1);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.app_info_screen)).perform(click());
        onView(withId(R.id.appInfoFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void searchScreenIsDisplayedAfterClickOnSearchIconWhenLoggedUserIsPetSitterTest() {
        userTest.setRole(2);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.search_screen)).perform(click());
        onView(withId(R.id.searchFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void searchScreenIsDisplayedAfterClickOnSearchIconWhenLoggedUserIsNormalUserTest() {
        userTest.setRole(1);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.search_screen)).perform(click());
        onView(withId(R.id.searchFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void petSitterProfileScreenIsDisplayedAfterClickOnProfileIconWhenLoggedUserIsPetSitterTest() {
        userTest.setRole(2);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.profile_screen)).perform(click());
        onView(withId(R.id.petSitProfileFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void normalUserProfileScreenIsDisplayedAfterClickOnProfileIconWhenLoggedUserIsNormalUserTest() {
        userTest.setRole(1);
        ActivityScenario.launch(HomeActivity.class);

        onView(withId(R.id.profile_screen)).perform(click());
        onView(withId(R.id.normUserProfileFragment)).check(matches(isDisplayed()));
    }

}

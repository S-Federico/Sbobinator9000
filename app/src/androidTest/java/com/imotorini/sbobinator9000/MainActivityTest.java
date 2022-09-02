package com.imotorini.sbobinator9000;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testOnCreate() {
        onView(withId(R.id.is_recording_tv)).check(matches(withText("Not recording")));
    }

    @Test
    public void testIsRecording() {
        onView(withId(R.id.recorder_button)).perform(click());
        onView(withId(R.id.is_recording_tv)).check(matches(withText("Recording")));
    }
}
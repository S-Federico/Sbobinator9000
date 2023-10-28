package com.imotorini.sbobinator9000;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        activityRule.getScenario().moveToState(Lifecycle.State.RESUMED);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testOnCreate() {
        String expected = context.getString(R.string.not_recording);
        onView(withId(R.id.timer)).check(matches(withText(expected)));
    }

    @Test
    public void testIsRecording() {
        // Expect to find only "Recording", not "Recording: %1$d:%2$02d"
        // Do not want to declare a new string because it would be for testing only and it would
        // be a smell.
        String expected = context.getString(R.string.recording_time).split(":")[0];
        onView(withId(R.id.recbutt)).perform(click());
        onView(withId(R.id.timer)).check(matches(withSubstring(expected)));
    }

    @Test
    public void testStartStopRecording() {
        String expected = context.getString(R.string.recording_time).split(":")[0];
        onView(withId(R.id.recbutt)).perform(click());
        onView(withId(R.id.timer)).check(matches(withSubstring(expected)));
        onView(withId(R.id.recbutt)).perform(click());
        expected = context.getString(R.string.not_recording);
        onView(withId(R.id.timer)).check(matches(withText(expected)));
    }
}
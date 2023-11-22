package com.imotorini.sbobinator9000;


import androidx.test.espresso.intent.Intents;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.espresso.intent.matcher.IntentMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import android.content.Intent;
import android.net.Uri;


@RunWith(AndroidJUnit4.class)
public class MainActivityFilePickerTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before

    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }



    @Test
    public void testFilePickerActivityResultHandling() {
        // Rendi il pulsante visibile e abilitato
        activityRule.getScenario().onActivity(activity -> {
            activity.enableTranscribeButton();
        });

        // Simula la scelta di un file
        Intent resultData = new Intent();
        Uri fakeFileUri = Uri.parse("content://mock/audio");
        resultData.setData(fakeFileUri);

        activityRule.getScenario().onActivity(activity -> {
            activity.onActivityResult(MainActivity.RESULT_CODE_FILEPICKER, MainActivity.RESULT_OK, resultData);
        });

        }

}
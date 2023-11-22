package com.imotorini.sbobinator9000;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;

import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MainActivityFilePickerTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }


    @Test
    public void testFilePickerActivityResultHandling() {
        Intents.init();
        // Rendi il pulsante visibile e abilitato
        activityRule.getScenario().onActivity(activity -> {
            activity.enableTranscribeButton();
        });
        onView(ViewMatchers.withId(R.id.transcribe)).perform(click());

        // Configura un intent fittizio per la tua attività di scelta file
        intending(hasAction(Intent.ACTION_GET_CONTENT))
                .respondWith(new Instrumentation.ActivityResult(MainActivity.RESULT_OK, null));


        // Simula la scelta di un file
        // Verifica che sia stato creato e avviato un intent con le corrette azioni e categorie

        Intents.intended(IntentMatchers.anyIntent());
        // Termina la registrazione degli intenti
        Intents.release();

    }

}
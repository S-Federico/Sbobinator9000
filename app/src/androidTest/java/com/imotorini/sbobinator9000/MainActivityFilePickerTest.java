package com.imotorini.sbobinator9000;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.imotorini.sbobinator9000.MainActivity.RESULT_CODE_FILEPICKER;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
public class MainActivityFilePickerTest {

    public ActivityScenario<MainActivity> activityScenario;
    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.RECORD_AUDIO",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    //"android.permission.WRITE_EXTERNAL_STORAGE", --> This permission crashes the test
                    "android.permission.CHANGE_WIFI_MULTICAST_STATE",
                    "android.permission.ACCESS_WIFI_STATE");

    @Before
    public void setUp() {
        Intents.init();
        activityScenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        activityScenario.close();
        Intents.release();
    }


    @Test
    public void testFilePickerActivityResultHandling() {
        activityScenario.onActivity(activity -> activity.enableTranscribeButton());
        onView(withId(R.id.transcribe)).perform(click());

        Intent resultIntent = new Intent();
        Uri fileUri = Uri.parse("content://Music/recordings"); // Sostituisci con il percorso effettivo del tuo file
        resultIntent.setData(fileUri);

        Intents.intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(new Instrumentation.ActivityResult(MainActivity.RESULT_OK, resultIntent));
        // Verifica che sia stata creata un'intenzione per scegliere il file
        //intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(new Instrumentation.ActivityResult(MainActivity.RESULT_OK, null));
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER));
        // Verifica il testo del chooser
        // Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER));


    }

    @Test
    public void testOnActivityResult() throws IOException {
        Bundle b = new Bundle();
        b.putString("recordings", "AAA");
        InstrumentationRegistry.getInstrumentation().addResults(b);

        Intent result = new Intent();
        result.setData(Uri.parse("content://Music/recordings"));

        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose the file to transcribe");

        intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, result));

        Intent finalChooseFileIntent = chooseFileIntent;
        activityScenario.onActivity(a -> a.startActivityForResult(finalChooseFileIntent, RESULT_CODE_FILEPICKER));

    }

}
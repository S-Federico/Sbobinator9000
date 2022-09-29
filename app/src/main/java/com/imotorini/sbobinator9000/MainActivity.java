package com.imotorini.sbobinator9000;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton recorderButton;
    private TextView isRecordingTextView;
    private boolean isRecording = false;

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCrate called");
        setContentView(R.layout.activity_main);

        initializeWidgets();
        updateUI();
    }

    // This method should be called when UI updates
    private void updateUI() {
        isRecordingTextView.setText(isRecording ? "Recording" : "Not recording");
    }

    // In this method there should be set ui elements configurations
    private void initializeWidgets() {

        // Get UI elements references
        recorderButton = findViewById(R.id.recorder_button);
        isRecordingTextView = findViewById(R.id.is_recording_tv);

        // Setup recorder button to start/stop recording when pressed
        recorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Recording button pressed");
                // Star recorder service
                isRecording = !isRecording;
                updateUI();
            }
        });
    }
}
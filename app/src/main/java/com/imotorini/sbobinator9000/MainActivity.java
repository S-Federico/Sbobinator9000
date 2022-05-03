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
        setContentView(R.layout.activity_main);

        recorderButton = findViewById(R.id.recorder_button);
        isRecordingTextView = findViewById(R.id.is_recording_tv);

        initializeWidgets();
        updateUI();

    }

    // This method should be called when UI updates
    private void updateUI() {
        isRecordingTextView.setText(isRecording ? "Recording" : "Not recording");
    }

    private void initializeWidgets() {
        recorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Star recorder service
                isRecording = !isRecording;
                updateUI();
            }
        });
    }
}
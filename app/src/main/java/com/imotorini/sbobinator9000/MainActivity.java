package com.imotorini.sbobinator9000;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.imotorini.sbobinator9000.services.AudioRecordingService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageButton recorderButton;
    private Button transcribeButton;
    private TextView isRecordingTextView;
    private boolean isRecording = false;

    private AudioRecordingService audioRecordingService;

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recorderButton = findViewById(R.id.recorder_button);
        isRecordingTextView = findViewById(R.id.is_recording_tv);
        transcribeButton= findViewById(R.id.btn_transcribe);

        audioRecordingService = new AudioRecordingService(
                getContentResolver(),
                getApplicationContext(),
                this
        );

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
                if (isRecording) {
                    try {
                        audioRecordingService.startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        audioRecordingService.stopRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updateUI();
            }
        });
        transcribeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });
    }

}
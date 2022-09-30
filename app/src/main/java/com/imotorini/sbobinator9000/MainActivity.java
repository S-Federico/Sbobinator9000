package com.imotorini.sbobinator9000;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.imotorini.sbobinator9000.services.AudioRecordingService;
import com.imotorini.sbobinator9000.services.TranscriptionService;

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
        Log.d(TAG, "onCrate called");
        setContentView(R.layout.activity_main);

        recorderButton = findViewById(R.id.recorder_button);
        isRecordingTextView = findViewById(R.id.is_recording_tv);
        transcribeButton= findViewById(R.id.btn_transcribe);

        TranscriptionService transcriptionService = new TranscriptionService();

        audioRecordingService = new AudioRecordingService(
                getContentResolver(),
                getApplicationContext(),
                this,
                transcriptionService
        );

        initializeWidgets();
        updateUI();
    }

    // This method should be called when UI updates
    private void updateUI() {
        isRecordingTextView.setText(isRecording ? getResources().getString(R.string.recording) : getResources().getString(R.string.not_recording));
    }

    private void initializeWidgets() {
        recorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Recording button pressed");
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
                    } catch (Exception e) {
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
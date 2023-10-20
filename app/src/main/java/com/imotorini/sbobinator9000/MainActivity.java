package com.imotorini.sbobinator9000;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.imotorini.sbobinator9000.services.AudioRecordingService;
import com.imotorini.sbobinator9000.services.TranscriptionService;
import com.imotorini.sbobinator9000.utils.CustomAndroidUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_CODE = 100;

    private long startTime;
    private long elapsedTimeBeforePause = 0;
    private long recordingTime = 0;
    private long pauseTime = 0;
    private boolean isRecordingPaused = false;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    private long pauseStartTime = 0;

    private FloatingActionButton recorderButton;
    private ImageButton transcribeButton;
    private ImageButton playPauseButton;
    private TextView isRecordingTextView;
    private boolean isRecording = false;
    private boolean isTimerRunning = false; // Variabile per tenere traccia dello stato del timer
    private final int RESULT_CODE_FILEPICKER = 200;

    private AudioRecordingService audioRecordingService;
    private TranscriptionService transcriptionService;

    private static final String TAG = MainActivity.class.getName();
    private WaveformView waveform;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCrate called");
        setContentView(R.layout.activity_main);
        checkPermissions();

        recorderButton = findViewById(R.id.recbutt);
        isRecordingTextView = findViewById(R.id.timer);
        playPauseButton = findViewById(R.id.playpause);
        transcribeButton = findViewById(R.id.transcribe);
        waveform = findViewById(R.id.waveformView);

        transcriptionService = new TranscriptionService(BuildConfig.STT_BASE_URL);

        audioRecordingService = new AudioRecordingService(
                getContentResolver(),
                getApplicationContext(),
                this,
                transcriptionService
        );

        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {

                if (isRecording) {
                    updateUI();
                    handler.postDelayed(this, 100); // Ripeti il Runnable ogni 0.1 secondi
                }
            }
        };

        initializeWidgets();
        updateUI();
    }

    // This method should be called when UI updates
    private void updateUI() {
        // isRecordingTextView.setText(isRecording ? getResources().getString(R.string.recording) : getResources().getString(R.string.not_recording));
        if (isRecording) {
            long currentTime = System.currentTimeMillis();
            recordingTime = (currentTime - startTime) - pauseTime;

            // Calcola il tempo trascorso in secondi e visualizzalo
            int seconds = (int) (recordingTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            isRecordingTextView.setText(getString(R.string.recording_time, minutes, seconds));

            waveform.addAmplitude(audioRecordingService.getAmplitude());
        } else {
            isRecordingTextView.setText(getResources().getString(R.string.not_recording));
            waveform.clearWaveform();
        }
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
                        startTime = System.currentTimeMillis(); // Memorizza il tempo di inizio registrazione
                        if (!isTimerRunning) {
                            handler.post(updateTimeRunnable); // Avvia il timer solo se non è già in esecuzione
                            isTimerRunning = true;
                        }
                        playPauseButton.setVisibility(View.VISIBLE); // Rendi visibile il pulsante play/pausa
                        recorderButton.setImageResource(R.drawable.stop);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        audioRecordingService.stopRecording();
                        startTime = 0; // Resetta il tempo di inizio quando la registrazione si interrompe
                        pauseStartTime = 0; // Resetta il tempo di inizio della pausa
                        elapsedTimeBeforePause = 0; // Resetta il tempo trascorso prima della pausa
                        pauseTime = 0; // Resetta il tempo totale di pausa
                        if (isTimerRunning) {
                            handler.removeCallbacks(updateTimeRunnable); // Rimuovi il callback del timer se è in esecuzione
                            isTimerRunning = false;
                        }
                        playPauseButton.setVisibility(View.GONE); // Nascondi il pulsante play/pausa
                        recorderButton.setImageResource(R.drawable.microphone_24);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateUI();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    if (isRecordingPaused) {
                        // Riprendi la registrazione
                        audioRecordingService.resumeRecording();
                        playPauseButton.setImageResource(R.drawable.pause); // Cambia l'icona a pausa


                        // Calcola il tempo trascorso prima della pausa e aggiungilo al tempo totale
                        if (pauseStartTime > 0) {
                            // Calcola il tempo di pausa fino ad ora e sottrai dal tempo totale di pausa
                            long currentTime = System.currentTimeMillis();
                            pauseTime += (currentTime - pauseStartTime);
                            pauseStartTime = 0;
                        }
                        handler.post(updateTimeRunnable); // Riprendi il timer
                        isRecordingPaused = false;
                    } else {
                        // Metti in pausa la registrazione
                        audioRecordingService.pauseRecording();
                        playPauseButton.setImageResource(R.drawable.resume); // Cambia l'icona a riprendi
                        handler.removeCallbacks(updateTimeRunnable); // Rimuovi il callback del timer quando metti in pausa

                        // Memorizza il tempo di inizio quando metti in pausa
                        pauseStartTime  = System.currentTimeMillis();

                        isRecordingPaused = true;
                    }
                }
            }
        });

        transcribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityFilePicker();
            }
        });
    }

    private void startActivityFilePicker() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");

        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose the file to transcribe");
        startActivityForResult(chooseFileIntent, RESULT_CODE_FILEPICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CODE_FILEPICKER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri fileUri = data.getData();
                        String transcribedfilename=fileUri.getLastPathSegment();
                        try {
                            //byte[] fileData = Utils.fileToBytes(file);
                            byte[] fileData = CustomAndroidUtils.fileToBytes(fileUri, getApplicationContext());

                            transcriptionService.transcribeAsync(fileData, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "FAIL");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Error occurred in transcription", Toast.LENGTH_SHORT).show();
                                        }
                                    });                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.d(TAG, "SUCCESS");

                                    if (!response.isSuccessful() || response.body() == null) {
                                        Log.e(TAG, "Failed to get the transcription");
                                        return;
                                    }

                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        String transcription = jsonObject.getString("data");
                                        runOnUiThread(() -> Toast.makeText(MainActivity.this, transcription, Toast.LENGTH_SHORT).show());
                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, transcribedfilename+".txt");
                                        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                                        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                                        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

                                        if (uri != null) {
                                            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                                                if (os != null) {
                                                    os.write(transcription.getBytes());
                                                    Log.d(TAG, "Transcription saved successfully");
                                                }
                                            } catch (IOException e) {
                                                Log.e(TAG, "Error writing to file", e);
                                            }
                                        } else {
                                            Log.e(TAG, "Failed to create new MediaStore record");
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "JSON Parsing Error", e);
                                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error occurred while parsing data", Toast.LENGTH_SHORT).show());
                                    }


                                }


                            });
                        } catch (RuntimeException | IOException e) {
                            Log.e(TAG, "Error while creating file from picker uri. Details: " + e.getMessage());
                        }
                    }
                }
            default:
                Log.i(TAG, "Received " + requestCode + " as ActivityResult.");
        }
    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), ALL_PERMISSIONS_CODE);
        }
    }

}

package com.imotorini.sbobinator9000;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.imotorini.sbobinator9000.services.AudioRecordingService;
import com.imotorini.sbobinator9000.services.TranscriptionService;
import com.imotorini.sbobinator9000.utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCrate called");
        setContentView(R.layout.activity_main);

        recorderButton = findViewById(R.id.recbutt);
        isRecordingTextView = findViewById(R.id.timer);
        playPauseButton = findViewById(R.id.playpause);
        transcribeButton = findViewById(R.id.transcribe);

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
                    handler.postDelayed(this, 1000); // Ripeti il Runnable ogni secondo
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
        } else {
            isRecordingTextView.setText(getResources().getString(R.string.not_recording));
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
                        try {
                            //byte[] fileData = Utils.fileToBytes(file);
                            byte[] fileData = Utils.fileToBytes(fileUri, getApplicationContext());

                            transcriptionService.transcribeAsync(fileData, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e(TAG, "FAIL");
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.d(TAG, "SUCCESS");

                                }
                            });
                        } catch (RuntimeException | IOException e) {
                            Log.e(TAG, "Error while creating file from picker uri. Details: " + e.getMessage());
                        }
                    }
                }
            default:
                // Nothing
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Audio.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

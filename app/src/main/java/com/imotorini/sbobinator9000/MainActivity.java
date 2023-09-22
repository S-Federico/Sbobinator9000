package com.imotorini.sbobinator9000;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

    private FloatingActionButton recorderButton;
    private ImageButton transcribeButton;
    private TextView isRecordingTextView;
    private boolean isRecording = false;
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
        transcribeButton = findViewById(R.id.transcribe);

        transcriptionService = new TranscriptionService(BuildConfig.STT_BASE_URL);

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
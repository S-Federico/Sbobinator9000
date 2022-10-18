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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.imotorini.sbobinator9000.services.AudioRecordingService;
import com.imotorini.sbobinator9000.services.TranscriptionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

public class MainActivity extends AppCompatActivity {

    private ImageButton recorderButton;
    private Button transcribeButton;
    private TextView isRecordingTextView;
    private boolean isRecording = false;
    private final int RESULT_CODE_FILEPICKER=200;

    private AudioRecordingService audioRecordingService;
    private TranscriptionService transcriptionService;

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
                startActivityFilePicker();
            }
        });
    }

    private void startActivityFilePicker(){
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");

        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent= Intent.createChooser(chooseFileIntent,"Choose the file to transcribe");
        startActivityForResult(chooseFileIntent,RESULT_CODE_FILEPICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri fileUri=null;
        File file = null;
        switch(requestCode){
            case RESULT_CODE_FILEPICKER:
                if(resultCode== Activity.RESULT_OK){
                    if(data!= null){
                        fileUri=data.getData();
                        try {
                            InputStream is = getContentResolver().openInputStream(fileUri);
                            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
                            Response response=
                                    transcriptionService.transcribe(bytes);
                            Toast.makeText(this.getApplicationContext(), String.valueOf(response.isSuccessful()), Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
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
package com.imotorini.sbobinator9000.services;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.imotorini.sbobinator9000.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AudioRecordingService {

    private static final int MICROPHONE_PERMISSION_CODE = 12;
    private static final int R_PERMISSION_CODE = 13;
    private static final int W_PERMISSION_CODE = 14;

    private final ContentResolver contentResolver;
    private final Context context;
    private final Activity activity;
    private MediaRecorder audioRecorder;
    private Uri audiouri;
    private File file;
    private final TranscriptionService transcriptionService;

    private static final String TAG = "AudioRecordingService";

    public AudioRecordingService(ContentResolver contentResolver, Context context, Activity activity, TranscriptionService transcriptionService) {
        this.contentResolver = contentResolver;
        this.context = context;
        this.activity = activity;
        this.transcriptionService = transcriptionService;
    }

    public void startRecording() throws IOException {

        getMicrophonePermission();
        getStoragePermission();

        String fileName = "Recording " + LocalDateTime.now().format(Constants.defaultDateTimeFormatter) + ".mp3";
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        Uri audioUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        ParcelFileDescriptor file = contentResolver.openFileDescriptor(audioUri, "w");
        /*ContextWrapper contextWrapper = new ContextWrapper(activity.getApplicationContext());
        File musicDirectory= contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(musicDirectory,fileName);*/
        if (file != null) {
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioRecorder.setOutputFile(file.getFileDescriptor());
            audioRecorder.setAudioChannels(1);
            audioRecorder.prepare();
            audioRecorder.start();
            this.audiouri = audioUri;
            System.out.println(audioUri);
        }

    }

    public void stopRecording() throws Exception {
        audioRecorder.stop();
        audioRecorder.release();
        audioRecorder = null;
        System.out.println(audiouri.getPath());
        file = new File(audiouri.getPath());

        Toast.makeText(context, "new file audio recorded in " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        Callback onResponseCallback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "Response: " + (response.body() != null ? response.body().string() : null));
            }
        };

        //transcribe(onResponseCallback);
    }

    public void transcribe(byte[] fileData, Callback onResponseCallback) throws Exception {
        transcriptionService.transcribe(fileData, onResponseCallback);
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}
                    , MICROPHONE_PERMISSION_CODE);
        }

    }

    private void getStoragePermission() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, R_PERMISSION_CODE);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, W_PERMISSION_CODE);
        }
    }
}

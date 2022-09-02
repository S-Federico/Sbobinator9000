package com.imotorini.sbobinator9000.services;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioRecordingService {

    private static final int MICROPHONE_PERMISSION_CODE = 12;
    private static final int R_PERMISSION_CODE = 13;
    private static final int W_PERMISSION_CODE = 14;
    private final ContentResolver contentResolver;
    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private final Activity activity;
    private MediaRecorder audioRecorder;
    Uri audiouri;
    private File file;

    public AudioRecordingService(ContentResolver contentResolver, Context context, Activity activity) {
        this.contentResolver = contentResolver;
        this.context = context;
        this.activity = activity;
    }

    public void startRecording() throws IOException {

        getMicrophonePermission();
        getStoragePermission();

        String fileName = "caca " + /*System.currentTimeMillis() +*/ ".mp3";
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        Uri audiouri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        ParcelFileDescriptor file = contentResolver.openFileDescriptor(audiouri, "w");
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
            this.audiouri=audiouri;
        }

    }

    public void stopRecording() throws Exception {
        audioRecorder.stop();
        file= new File(audiouri.getPath());

        Toast.makeText(context, "new file audio recorded in "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        transcribe();
    }

    public void transcribe() throws Exception {
        testPostFile(file);
    }
    private static final MediaType MEDIA_TYPE_PLAINTEXT = MediaType
            .parse("audio/mpeg3");
    private final OkHttpClient client = new OkHttpClient();


    public void testPostFile(File file) throws Exception {
        Request request = new Request.Builder()
                .url("https://cfa37ad5-35b7-4abd-8256-fa50e2422d20.mock.pstmn.io/posttest")
                .post(RequestBody.create(MEDIA_TYPE_PLAINTEXT, file))
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        //assertTrue(response.isSuccessful());

    }
    private void getMicrophonePermission(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO}
                    ,MICROPHONE_PERMISSION_CODE);
        }

    }

    private void getStoragePermission() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},R_PERMISSION_CODE);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},W_PERMISSION_CODE);
        }
    }
}

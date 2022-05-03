package com.imotorini.sbobinator9000.services;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class AudioRecordingService {

    private static final int MICROPHONE_PERMISSION_CODE = 12;
    private static final int R_PERMISSION_CODE = 13;
    private static final int W_PERMISSION_CODE = 14;
    private final ContentResolver contentResolver;
    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private final Activity activity;

    public AudioRecordingService(ContentResolver contentResolver, Context context, Activity activity) {
        this.contentResolver = contentResolver;
        this.context = context;
        this.activity = activity;
    }

    public void startRecording() throws IOException {

        getMicrophonePermission();
        getStoragePermission();

        String fileName = "caca " + System.currentTimeMillis() + ".mp3";
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        Uri audiouri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        ParcelFileDescriptor file = contentResolver.openFileDescriptor(audiouri, "w");

        if (file != null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            Intent speechRecognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
            speechRecognizerIntent.putExtra("android.speech.extra.GET_AUDIO", true);
            speechRecognizer.setRecognitionListener(
                    new RecognitionListener() {
                        @Override
                        public void onReadyForSpeech(Bundle bundle) {

                        }

                        @Override
                        public void onBeginningOfSpeech() {

                        }

                        @Override
                        public void onRmsChanged(float v) {

                        }

                        @Override
                        public void onBufferReceived(byte[] bytes) {

                        }

                        @Override
                        public void onEndOfSpeech() {

                        }

                        @Override
                        public void onError(int i) {

                        }

                        @Override
                        public void onResults(Bundle results) {
                            System.out.println(results.toString());
                        }

                        @Override
                        public void onPartialResults(Bundle bundle) {

                        }

                        @Override
                        public void onEvent(int i, Bundle bundle) {

                        }
                    }
            );
            speechRecognizer.startListening(speechRecognizerIntent);



            /*
            MediaRecorder audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioRecorder.setOutputFile(file.getFileDescriptor());
            audioRecorder.setAudioChannels(1);
            audioRecorder.prepare();
            audioRecorder.start();

             */
        }
    }

    public void stopRecording() throws IOException {
        speechRecognizer.stopListening();
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

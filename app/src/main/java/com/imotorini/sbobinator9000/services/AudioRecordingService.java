package com.imotorini.sbobinator9000.services;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.imotorini.sbobinator9000.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.Callback;

public class AudioRecordingService {


    private boolean paused = false;
    private boolean recording = false;


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
        recording=true;

        String fileName = "Recording " + LocalDateTime.now().format(Constants.defaultDateTimeFormatter) + ".aac";
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        Uri audioUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        ParcelFileDescriptor file = contentResolver.openFileDescriptor(audioUri, "w");

        if (file != null) {
            audioRecorder = new MediaRecorder();
            audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioRecorder.setOutputFile(file.getFileDescriptor());
            audioRecorder.setAudioEncodingBitRate(16 * 44100);
            audioRecorder.setAudioSamplingRate(44100);
            audioRecorder.setAudioChannels(1);
            audioRecorder.prepare();
            audioRecorder.start();
            this.audiouri = audioUri;
        }

    }

    public void pauseRecording() {
        if (!paused) {
            if (audioRecorder != null) {
                audioRecorder.pause();
                paused = true;
            }
        }
    }

    public void resumeRecording() {
        if (paused) {
            if (audioRecorder != null) {
                audioRecorder.resume();
                paused = false;
            }
        }
    }

    public void stopRecording() throws Exception {
        recording = false;
        audioRecorder.stop();
        audioRecorder.release();
        audioRecorder = null;
        paused=false;
        String realPath = getRealPathFromURI(context, audiouri);
        Log.d("AudioRecording", "Real Path: " + realPath);

        file = new File(realPath);

    }

    public void showToast() {
        Toast.makeText(context, "New file audio recorded in " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public float getAmplitude() {
        return (float) audioRecorder.getMaxAmplitude();
    }

    public void transcribe(byte[] fileData, Callback onResponseCallback) throws Exception {
        transcriptionService.transcribeAsync(fileData, onResponseCallback);
    }

    public boolean isRecording() {
        return recording;
    }

    public boolean isPaused(){
    return paused;
    }

    public MediaRecorder getMediaRecorder() {
        return audioRecorder;
    }

}

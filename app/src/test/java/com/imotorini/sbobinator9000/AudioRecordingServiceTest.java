package com.imotorini.sbobinator9000;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.Toast;

import com.imotorini.sbobinator9000.services.AudioRecordingService;
import com.imotorini.sbobinator9000.services.TranscriptionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Callback;

public class AudioRecordingServiceTest {

    @Mock
    ContentResolver mockContentResolver;
    @Mock
    Context mockContext;
    @Mock
    Activity mockActivity;
    @Mock
    TranscriptionService mockTranscriptionService;
    private ParcelFileDescriptor parcelFileDescriptor;
    @Mock
    private Uri mockAudioUri;
    @Mock
    private MediaRecorder mediaRecorder;
    @Mock
    private Cursor cursor;
    AudioRecordingService audioRecordingService;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        audioRecordingService = new AudioRecordingService(mockContentResolver, mockContext, mockActivity, mockTranscriptionService);

        // Stubbing behavior for mock objects
        mockAudioUri = Uri.parse("content://mock/audio");
        ParcelFileDescriptor mockParcelFileDescriptor = Mockito.mock(ParcelFileDescriptor.class);

        Mockito.when(mockContentResolver.insert(eq(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI), Mockito.any(ContentValues.class)))
                .thenReturn(mockAudioUri);

        Mockito.when(mockContentResolver.openFileDescriptor(eq(mockAudioUri), eq("w")))
                .thenReturn(mockParcelFileDescriptor);

        // Call the method you want to test
        audioRecordingService.startRecording();
    }

    @Test
    public void startRecording_Success() throws IOException {

        assertTrue(audioRecordingService.isRecording()); // Check that recording is set to true
       // assertEquals(mockAudioUri, audioRecordingService.getAudioUri()); // Assuming you have a getAudioUri method to get the URI
        assertNotNull(audioRecordingService.getMediaRecorder()); // Ensure that the MediaRecorder is not null

    }


    @Test
    public void pauseRecording_WhenNotPaused() throws IOException {

        audioRecordingService.pauseRecording();

        assertTrue(audioRecordingService.isPaused());
    }

    @Test
    public void resumeRecording_WhenPaused() throws IOException {

        audioRecordingService.pauseRecording();
        audioRecordingService.resumeRecording();

        assertFalse(audioRecordingService.isPaused());
    }

    @Test
    public void stopRecording_Success() throws Exception {
        // Stubbing the behavior of your mock objects
        // Simulazione della chiamata a getContentResolver() su context
        Mockito.when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        // Stubbing the behavior of your mock objects
        Mockito.when(mockContentResolver.openFileDescriptor(mockAudioUri, "w")).thenReturn(parcelFileDescriptor);
        // Simulazione di una query sul contentResolver
        Mockito.when(mockContentResolver.query(null, new String[]{MediaStore.Audio.Media.DATA}, null, null, null)).thenReturn(cursor);
        //Toast toast = mock(Toast.class);

        // Simula il comportamento di getColumnIndexOrThrow
        Mockito.when(cursor.getColumnIndexOrThrow(Mockito.any())).thenReturn(0);

        // Simula che ci sia almeno una riga nel cursore
        Mockito.when(cursor.moveToFirst()).thenReturn(true);

        // Simula il valore che verrà restituito quando verrà chiamato getString
        Mockito.when(cursor.getString(0)).thenReturn("Music/Recordings/");

       // Mockito.when(audioRecordingService.getRealPathFromURI(mockContext,Mockito.any(Uri.class))).thenReturn("Music/Recordings/rec");
        audioRecordingService.stopRecording();

       assertFalse(audioRecordingService.isRecording());

    }


}

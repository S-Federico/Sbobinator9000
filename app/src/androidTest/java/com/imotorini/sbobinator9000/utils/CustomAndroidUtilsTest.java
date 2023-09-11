//package com.imotorini.sbobinator9000.utils;
//
//import android.net.Uri;
//
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class CustomAndroidUtilsTest {
//
//    public void testNullParams() throws IOException {
//        byte[] res = CustomAndroidUtils.fileToBytes(null, null);
//        Assert.assertEquals("Null result", null, res);
//    }
//
//    public void testFileToBytesNullUri() throws IOException {
//        byte[] res = CustomAndroidUtils.fileToBytes(null, InstrumentationRegistry.getInstrumentation().getContext());
//        Assert.assertEquals(null, res);
//    }
//
//    @Test(expected = IOException.class)
//    public void testFileToBytesFileNotExists() throws IOException {
//        InputStream is = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open("AAA.mp3");
//        Uri u = Uri.fromFile(new File(is.toString()));
//        byte[] res = CustomAndroidUtils.fileToBytes(u, InstrumentationRegistry.getInstrumentation().getContext());
//    }
//
//    @Test
//    public void testFileToBytesFileEmpty() throws IOException {
//        //InputStream is = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open("assets/emptyAudio.mp3");
//        Uri u = Uri.fromFile(new File("src/androidTest/assets/emptyAudio.mp3"));
//        byte[] res = CustomAndroidUtils.fileToBytes(u, InstrumentationRegistry.getInstrumentation().getContext());
//        Assert.assertNotNull(res);
//    }
//
//
//}
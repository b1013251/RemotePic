package com.koulowenergy.remotepicandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends Activity implements Delegate {
    // Thread
    private TCPServer thread;
    // camera
    private boolean taken = false;
    private Camera camera;
    private CameraPreview preview;
    // sound
    private SoundPool soundPool;
    private int soundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.camera = getCameraInstance();
        this.preview = new CameraPreview(this, camera);

        thread = new TCPServer();
        thread.delegate = this;
        thread.start();

        FrameLayout frame = findViewById(R.id.cameraFrame);
        frame.addView(this.preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        this.soundId = soundPool.load(getApplicationContext(), R.raw.se, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.soundPool.release();
        this.camera.release();
    }


    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Log.d("TCP", "picture has taken.");
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1);
            File pictureFile = getOutputMediaFile();
            if(pictureFile == null) {
                Log.d("FILE_OUTPUT", "error creating media file, check storage permissions: ");
                return;
            }

            try {
                // save
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes);
                fos.close();
                addImageToDB(pictureFile.getPath());

                // resume
                camera.startPreview();
                taken = false;

            } catch (FileNotFoundException e) {
                Log.d("FILE_OUTPUT", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("FILE_OUTPUT", "Error accessing file: " + e.getMessage());
            }
        }
    };

    private Camera.AutoFocusCallback autofocus = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.autoFocus(null);
            camera.takePicture(null,null,picture);
            taken = true;
        }
    };

    @Override
    public void callbackString(String str) {
        Log.d("TCP", "callback:" + str);
        if (!this.taken) {
            camera.autoFocus(this.autofocus);
        }
    }

    //private
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "camera is not available");
        }
        return c;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        ), "RemotePic");

        // create directory
        if (!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                Log.d("FILE_OUTPUT", "failed to create directory");
                return null;
            }
        }

        // make name from timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");

        return mediaFile;
    }

    private void addImageToDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}

package com.koulowenergy.remotepicandroid;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;
/**
 * Created by bdm on 17/07/29.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;

    public CameraPreview(Context context,Camera camera) {
        super(context);

        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            //解像度変更
            Camera.Parameters params = this.camera.getParameters();
            List<Camera.Size> sizeList = params.getSupportedPictureSizes();
            int maxSize=0;
            int maxSizeIndex=0;
            for(int i=0; i<sizeList.size(); i++) {
                if(maxSize < sizeList.get(i).width + sizeList.get(i).height) {
                    maxSize =  sizeList.get(i).width + sizeList.get(i).height;
                    maxSizeIndex = i;
                }
            }
            params.setPictureSize(sizeList.get(maxSizeIndex).width, sizeList.get(maxSizeIndex).height);
            camera.setParameters(params);

            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.holder.getSurface() == null) {
            return ;
        }

        try {
            this.camera.stopPreview();
        } catch (Exception e) {
            // ignore.
        }

        try {
            this.camera.setPreviewDisplay(holder);
            this.camera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview" + e.getMessage());
        }
    }
}

package com.maheshtiria.easypass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.annotation.CameraExecutor;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTextActivity extends AppCompatActivity {
    private ImageCapture imageCapture;
    private VideoCapture recorderVideoCapture;
    private Recording recording;
    private ExecutorService cameraExecutor;
    private PreviewView cameraDisplay;
    private Button button;
    static final int CAMERA_REQUEST = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_text);
        cameraDisplay = findViewById(R.id.viewFinder);
        button = findViewById(R.id.goBack);
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            String requiredPermissions[] = new String[1];
            requiredPermissions[0] = Manifest.permission.CAMERA;
            ActivityCompat.requestPermissions(
                    this,requiredPermissions , CameraTextActivity.CAMERA_REQUEST);

        }

        button.setOnClickListener((view)->{
            Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
            result.putExtra("surprise","go back clicked!");
            setResult(Activity.RESULT_OK, result);

            finish();
        });
        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void startCamera() {
        //camera event
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        cameraProviderFuture.addListener(()->{
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider processCameraProvider;
            try {
                processCameraProvider =   (ProcessCameraProvider) cameraProviderFuture.get();

                //display screen initialization
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraDisplay.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                try{
                    processCameraProvider.unbindAll();
                    // Bind use cases to camera
                    processCameraProvider.bindToLifecycle(
                            this, cameraSelector, preview);
                }catch (Exception e){
                    Log.d("OKAY",e.getMessage());
                    Toast.makeText(this,"Unable to use camera",Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (Exception e) {
                Log.d("OKAY",e.getMessage());
                Toast.makeText(this,"Unable to use camera",Toast.LENGTH_LONG).show();
                finish();
            }
        },ContextCompat.getMainExecutor(this));

    }

    private boolean allPermissionsGranted() {
        Boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CameraTextActivity.CAMERA_REQUEST:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startCamera();
                }
                else{

                    Toast.makeText(this,"Permissions not granted !",Toast.LENGTH_SHORT).show();
                    Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
                    result.putExtra("surprise","permissions maybe");
                    setResult(Activity.RESULT_OK, result);

                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

}
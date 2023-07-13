package com.maheshtiria.easypass;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.maheshtiria.easypass.recognizer.TextDetection;
import com.maheshtiria.easypass.viewmodel.CameraViewModel;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTextActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private PreviewView cameraDisplay;
    private View surfaceView;
    private TextView tv;
    private Button button;
    private Button click;
    private CameraViewModel viewModel;
    private int cropWidth=200;
    private int cropHeight = 100;
    static final int CAMERA_REQUEST = 0;
    private double RATIO_4_3_VALUE = (4.0/3.0);
    private double RATIO_16_9_VALUE = (16.0/9.0);
    private DisplayMetrics displayMetrics;
    private int crop_height_percent = 40;
    private StringBuilder msgb = new StringBuilder();

    private TextRecognizer detector = TextDetection.detector;
    private int width=0;
    private int height=60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view init
        setContentView(R.layout.activity_camera_text);
        cameraDisplay = findViewById(R.id.viewFinder);
        button = findViewById(R.id.goBack);
        surfaceView = findViewById(R.id.scanner_view);
        tv = findViewById(R.id.current_text);
        click = findViewById(R.id.click);
        //view init
        msgb.append(" ");
        Log.d("OKAY","onCreated");

        viewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        viewModel.init();
        viewModel.scan.observe(this,it->{
            tv.setText(it);
        });

        //screen height width
        initDisplayMetrics();
        Rect rect = getWindowManager().getCurrentWindowMetrics().getBounds();
        width = displayMetrics.widthPixels;

        height= (int)((float)height*displayMetrics.density);
        Log.d("VALUES","width : "+width+" window width : "+rect.width());
        Log.d("VALUES","height : "+height+" window height : "+rect.height());
        cameraDisplay.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        getSupportActionBar().hide();

        // Request camera permissions
        if (!allPermissionsGranted()) {
            String requiredPermissions[] = new String[2];
            requiredPermissions[0] = Manifest.permission.CAMERA;
            requiredPermissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            ActivityCompat.requestPermissions(
                    this,requiredPermissions , CameraTextActivity.CAMERA_REQUEST);

        }

        button.setOnClickListener((view)->{
            Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
            result.putExtra("surprise",msgb.toString());
            setResult(Activity.RESULT_OK, result);

            finish();
        });


        cameraExecutor = Executors.newSingleThreadExecutor();


    }

    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
    protected void  onStart() {
        super.onStart();
        startCamera();
    }

    @SuppressLint("RestrictedApi")
    @ExperimentalGetImage
    private void startCamera() {
        //camera event
        Log.d("OKAY","start camera ");
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        

        cameraProviderFuture.addListener(()->{
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider processCameraProvider;
            try {
                processCameraProvider =   (ProcessCameraProvider) cameraProviderFuture.get();



                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                //display screen initialization if you want to use the whole screen
                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(cameraDisplay.getSurfaceProvider());

                //viewport image of concern
                ViewPort viewPort =  new ViewPort.Builder(new Rational(width, height),preview.getTargetRotation()).build();
                ImageCapture imageCapture =
                        new ImageCapture.Builder()
                                .setTargetRotation(preview.getTargetRotation())
                                .build();

                click.setOnClickListener((view)->{

                    Log.d("VALUES","height : "+surfaceView.getWidth()+"   "+surfaceView.getHeight());

                    imageCapture.takePicture(cameraExecutor,
                            new ImageCapture.OnImageCapturedCallback(){
                                @Override
                                public void onCaptureSuccess(@NonNull ImageProxy image) {
                                    Log.d("VALUES","height : "+image.getWidth()+"   "+image.getHeight()+"   "+image.getCropRect().left+"   "+image.getCropRect().right);

                                    InputImage img = InputImage.fromMediaImage(image.getImage(),image.getImageInfo().getRotationDegrees());

                                    Task<Text> result = detector.process(img)
                                            .addOnSuccessListener(
                                            new OnSuccessListener<Text>() {
                                                @Override
                                                public void onSuccess(Text text) {
                                                    String value = text.getText();
                                                    Log.d("VALUES","SCAN : "+value);
                                                    msgb.delete(0,msgb.length());
                                                    msgb.append(value);

                                                    tv.setText(value);
                                                }
                                            }
                                            ).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    }
                                            );
                                }
                            }
                    );

                });

                UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                        .addUseCase(preview)
                        .addUseCase(imageCapture)
                        .setViewPort(viewPort)
                        .build();

                try{
                    processCameraProvider.unbindAll();
                    // Bind use cases to camera
                    processCameraProvider.bindToLifecycle(this,cameraSelector,useCaseGroup);

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
        granted = granted && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
        return granted;
    }

    private void initDisplayMetrics() {
        displayMetrics = this.getResources().getDisplayMetrics();
    }

    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
package com.maheshtiria.easypass;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognizer;
import com.maheshtiria.easypass.imageutils.ImageUtil;
import com.maheshtiria.easypass.recognizer.TextDetection;
import com.maheshtiria.easypass.viewmodel.CameraViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTextActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private PreviewView cameraDisplay;
    private TextView tv;
  private Button click;
    static final int CAMERA_REQUEST = 0;
    private DisplayMetrics displayMetrics;
    private final StringBuilder msgb = new StringBuilder();

    private final TextRecognizer detector = TextDetection.detector;
    private int width=0;
    private int height=60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view init
        setContentView(R.layout.activity_camera_text);
        cameraDisplay = findViewById(R.id.viewFinder);
      Button button = findViewById(R.id.goBack);
      tv = findViewById(R.id.current_text);
        click = findViewById(R.id.click);
        //view init
        msgb.append(" ");

      CameraViewModel viewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        viewModel.init();
        viewModel.scan.observe(this, it-> tv.setText(it));

        //screen height width
        initDisplayMetrics();
        width = displayMetrics.widthPixels;

        height= (int)((float)height*displayMetrics.density);
        cameraDisplay.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        ActionBar topbar = getSupportActionBar();
        if(topbar!=null)
          topbar.hide();

        // Request camera permissions
        if (!allPermissionsGranted()) {
            String[] requiredPermissions = new String[2];
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
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        

        cameraProviderFuture.addListener(()->{
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider processCameraProvider;
            try {
                processCameraProvider = cameraProviderFuture.get();



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

                click.setOnClickListener((view)-> imageCapture.takePicture(cameraExecutor,
                    new ImageCapture.OnImageCapturedCallback(){
                        @Override
                        public void onCaptureSuccess(@NonNull ImageProxy image) {

                            InputImage img = ImageUtil.crop(image);

                            detector.process(img)
                                .addOnSuccessListener(
                                  text -> {
                                      String value = text.getText();
                                      msgb.delete(0,msgb.length());
                                      msgb.append(value);

                                      tv.setText(value);
                                  }
                                ).addOnFailureListener(
                                e -> Toast.makeText(getApplicationContext(),"Error occured while scanning",Toast.LENGTH_LONG).show()
                              );
                        }
                    }
                ));

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
                    Toast.makeText(this,"Unable to use camera",Toast.LENGTH_LONG).show();
                    finish();
                }



            } catch (Exception e) {
                Toast.makeText(this,"Unable to use camera",Toast.LENGTH_LONG).show();
                finish();
            }
        },ContextCompat.getMainExecutor(this));

    }

    private boolean allPermissionsGranted() {
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == CameraTextActivity.CAMERA_REQUEST) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startCamera();
        } else {

          Toast.makeText(this, "Permissions not granted !", Toast.LENGTH_SHORT).show();
          Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
          result.putExtra("surprise", "permissions maybe");
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
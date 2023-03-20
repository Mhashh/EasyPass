package com.maheshtiria.easypass;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recording;
import androidx.camera.view.PreviewView;
import androidx.camera.view.transform.OutputTransform;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraTextActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private PreviewView cameraDisplay;
    private SurfaceView surfaceView;
    private TextView tv;
    private Button button;
    private int cropWidth=200;
    private int cropHeight = 100;
    static final int CAMERA_REQUEST = 0;

    private final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private final double RATIO_16_9_VALUE = 16.0 / 9.0;


    private StringBuilder msgb = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_text);
        cameraDisplay = findViewById(R.id.viewFinder);
        button = findViewById(R.id.goBack);
        surfaceView = findViewById(R.id.scanner_view);
        tv = findViewById(R.id.current_text);
        msgb.append(" ");

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        cropWidth = (int)((displayMetrics.widthPixels/displayMetrics.xdpi)*displayMetrics.densityDpi);
        cropWidth = (cropWidth*80)/100;

        cropHeight = (int)((displayMetrics.heightPixels/displayMetrics.ydpi)*displayMetrics.densityDpi);
        cropHeight = (cropHeight*8)/100;
        surfaceView.getLayoutParams().height=cropHeight;
        surfaceView.getLayoutParams().width=cropWidth;
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
            result.putExtra("surprise",msgb.toString());
            setResult(Activity.RESULT_OK, result);

            finish();
        });
        cameraExecutor = Executors.newSingleThreadExecutor();


    }

    private void startCamera() {
        //camera event
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        Log.d("OKAY","W : "+surfaceView.getWidth()+" H:"+surfaceView.getHeight());

        cameraProviderFuture.addListener(()->{
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider processCameraProvider;
            try {
                processCameraProvider =   (ProcessCameraProvider) cameraProviderFuture.get();


                //display screen initialization if you want to use the whole screen
                Preview preview = new Preview.Builder()
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();
                preview.setSurfaceProvider(cameraDisplay.getSurfaceProvider());


                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                Log.d("OKAY","w:"+cropWidth+" h:"+cropHeight);
                //Building image analyzer
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                imageAnalysis.setAnalyzer(cameraExecutor,new ImageFromText());



                drawOverlay(surfaceView.getHolder(),80,8);
                try{
                    processCameraProvider.unbindAll();
                    // Bind use cases to camera
                    processCameraProvider.bindToLifecycle(
                            this, cameraSelector,preview,imageAnalysis);
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
    private int aspectRatio(int width,int height){
        double previewRatio = Math.log((double) Math.max(width, height) / (double)Math.min(width, height));
        if (Math.abs(previewRatio - Math.log(RATIO_4_3_VALUE))
                <= Math.abs(previewRatio - Math.log(RATIO_16_9_VALUE))
        ) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;

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

    private void drawOverlay(
            SurfaceHolder holder,
            int heightCropPercent,
            int widthCropPercent
    ) {
        Canvas canvas = holder.lockCanvas();
        Paint bgPaint = new Paint();
        bgPaint.setAlpha(140);

        canvas.drawPaint(bgPaint);
        Paint rectPaint = new Paint();
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.WHITE);

        Paint outlinePaint = new Paint();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setColor(Color.WHITE);
        outlinePaint.setStrokeWidth(4f);

        int surfaceWidth = holder.getSurfaceFrame().width();
        int surfaceHeight = holder.getSurfaceFrame().height();

        float cornerRadius = 25f;
        // Set rect centered in frame
        float rectTop = surfaceHeight * heightCropPercent / 2 / 100f;
        float rectLeft = surfaceWidth * widthCropPercent / 2 / 100f;
        float rectRight = surfaceWidth * (1 - widthCropPercent / 2 / 100f);
        float rectBottom = surfaceHeight * (1 - heightCropPercent / 2 / 100f);
        RectF rect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
        canvas.drawRoundRect(
                rect, cornerRadius, cornerRadius, rectPaint
        );
        canvas.drawRoundRect(
                rect, cornerRadius, cornerRadius, outlinePaint
        );

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50F);

        String overlayText = getString(R.string.overlay_help);
        Rect textBounds = new Rect();
        textPaint.getTextBounds(overlayText, 0, overlayText.length(), textBounds);
        float textX = (surfaceWidth - textBounds.width()) / 2f;
        float textY = rectBottom + textBounds.height() + 15f; // put text below rect and 15f padding
        canvas.drawText(getString(R.string.overlay_help), textX, textY, textPaint);
        holder.unlockCanvasAndPost(canvas);
    }

    public class ImageFromText implements ImageAnalysis.Analyzer {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        @Override
        public void  analyze(@NonNull ImageProxy image) {

            @OptIn(markerClass = ExperimentalGetImage.class)
            Image curImage = image.getImage();
            Rect rect = image.getCropRect();
            Log.d("OKAY", String.valueOf(image.getPlanes()[0].getBuffer().limit()));
            Log.d("OKAY"," ("+ curImage.getWidth()+","+curImage.getHeight()+")");
            Log.d("OKAY"," ("+rect.left+","+rect.top+")"+" ("+rect.right+","+rect.bottom+")");


            if(curImage!=null){
                InputImage inpimage = InputImage.fromMediaImage(curImage,image.getImageInfo().getRotationDegrees());
                recognizer.process(inpimage).addOnSuccessListener(
                        (Text output)->{
                            msgb.delete(0, msgb.length());
                            msgb.append(output.getText());
                            tv.setText(msgb.toString());
                        }
                ).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        image.close();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Text>() {
                    @Override
                    public void onComplete(@NonNull Task<Text> task) {
                        image.close();
                    }
                });
            }

        }
    }

}
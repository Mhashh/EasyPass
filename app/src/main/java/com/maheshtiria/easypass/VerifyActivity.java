package com.maheshtiria.easypass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.maheshtiria.easypass.encryption.PassEncrypt;

import javax.crypto.spec.IvParameterSpec;

public class VerifyActivity extends AppCompatActivity {

  EditText inp;
  Button button;
  ImageButton imgcam;
  ActivityResultLauncher<Intent> passTextForResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify);
    inp = findViewById(R.id.masterkey);
    button = findViewById(R.id.verify);

    imgcam = findViewById(R.id.acc_cam);

    //register for result from camera activity callback
    passTextForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent intent = result.getData();
          // Handle the Intent
          String msg = intent != null ? intent.getStringExtra("surprise") : null;
          inp.setText(msg);
          Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
        }
      });

    //sends to camera activity for scanning
    imgcam.setOnClickListener(
      v-> passTextForResult.launch(
        new Intent(this, CameraTextActivity.class)
      )
    );


    button.setOnClickListener((view)->{
      String auth = this.getIntent().getStringExtra("auth");
      String encrypt = this.getIntent().getStringExtra("encrypt");
      String salt = this.getIntent().getStringExtra("salt");
      byte[] ivBytes = this.getIntent().getByteArrayExtra("iv");
      Log.d("VALUES",new String(ivBytes));
      IvParameterSpec iv = new IvParameterSpec(ivBytes);
      String decrypt = PassEncrypt.decryptAuth(encrypt,inp.getText().toString(),salt,iv);
      Log.d("VALUES",decrypt);
      Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
      result.putExtra("Authorized", decrypt.equals(auth));
      setResult(Activity.RESULT_OK, result);
      finish();
    });
  }

  @Override
  public void onBackPressed() {
    //super.onBackPressed();
  }
}
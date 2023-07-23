package com.maheshtiria.easypass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class EncryptActivity extends AppCompatActivity {

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
    SharedPreferences sharedPreferences = this.getSharedPreferences(Globals.USERAUTH, Context.MODE_PRIVATE);

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

      //userauth password
      String authencrypt = sharedPreferences.getString("encryt","");
      String auth = sharedPreferences.getString("auth","");
      String authsalt = sharedPreferences.getString("salt","");
      String authiv = sharedPreferences.getString("iv","");
      Log.d("VALUES","encryptAct : "+authencrypt+"  "+auth+"  "+authsalt+"  "+authiv);
      String authdecrypt = PassEncrypt.decryptAuth(authencrypt,inp.getText().toString(),authsalt,new IvParameterSpec(authiv.getBytes()));
      if(authdecrypt.equals(auth)){

        String pass = this.getIntent().getStringExtra("pass");

        //salt for encryption
        String salt = String.valueOf(new Date().getTime());
        //initialization vector for crypto purposes
        IvParameterSpec iv = PassEncrypt.generateIv();

        String encrypt = PassEncrypt.storeEncryptAuth(pass,inp.getText().toString(),salt,iv);


        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        result.putExtra("Authorized",true);
        result.putExtra("encrypt",encrypt);
        result.putExtra("salt",salt);
        result.putExtra("sugar",new String(iv.getIV()));
        setResult(Activity.RESULT_OK, result);
      }
      else{
        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        result.putExtra("Authorized",false);
        setResult(Activity.RESULT_OK, result);
      }
      finish();
    });
  }
}
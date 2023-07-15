package com.maheshtiria.easypass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.maheshtiria.easypass.encryption.PassEncrypt;

import javax.crypto.spec.IvParameterSpec;

public class DecryptActivity extends AppCompatActivity {

  EditText inp;
  Button button;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify);
    inp = findViewById(R.id.masterkey);
    button = findViewById(R.id.verify);



    button.setOnClickListener((view)->{
      String auth = this.getIntent().getStringExtra("auth");
      String encrypt = this.getIntent().getStringExtra("encrypt");
      String salt = this.getIntent().getStringExtra("salt");
      byte[] ivBytes = this.getIntent().getByteArrayExtra("iv");
      Log.d("VALUES",new String(ivBytes));
      IvParameterSpec iv = new IvParameterSpec(ivBytes);
      String decrypt = PassEncrypt.decryptAuth(encrypt,inp.getText().toString(),salt,iv);
      Log.d("VALUES",decrypt);
      if(decrypt.equals(auth)){
        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        result.putExtra("Authorized",true);
        result.putExtra("decrypt",decrypt);
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
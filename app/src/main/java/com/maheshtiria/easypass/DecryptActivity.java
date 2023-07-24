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

import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;
import com.maheshtiria.easypass.encryption.PassEncrypt;

import javax.crypto.spec.IvParameterSpec;

public class DecryptActivity extends AppCompatActivity {

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
      String auth = sharedPreferences.getString("auth","");

      boolean authorized = PassEncrypt.verifyMainPassword(inp.getText().toString(),auth);

      if(authorized){
        String encrypt = this.getIntent().getStringExtra("encrypt");
        String name = this.getIntent().getStringExtra("name");
        int index = this.getIntent().getIntExtra("index",-1);

        //getting local db interface
        Pdb dbInstance = Pdb.getDb(this);
        PassDao pd = dbInstance.passDao();

        //db thread to get salt and iv vector
        dbInstance.getQueryExecutor().execute(
          ()->{
            Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
            try{

              String ivString = pd.findSugarByAppName(name);
              String salt = pd.findSaltByAppName(name);
              String decrypt;
              IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());

              decrypt = PassEncrypt.decryptAuth(encrypt,inp.getText().toString(),salt,iv);
              Log.d("VALUES","decrypt Pass  : "+encrypt+"  "+name+"  "+index+" "+salt+" "+decrypt);
              if(decrypt.equals("")){

                result.putExtra("Authorized",false);
                result.putExtra("index",-1);
                setResult(Activity.RESULT_OK, result);
              }
              else {

                result.putExtra("Authorized", true);
                result.putExtra("index",index);
                result.putExtra("decrypt", decrypt);
                setResult(Activity.RESULT_OK, result);
              }


            }catch(Exception e){
              result.putExtra("Authorized",false);
              result.putExtra("index",-1);
              setResult(Activity.RESULT_OK, result);
            }
            finish();
          }
        );
      }
      else{
        Intent result = new Intent("com.example.RESULT_ACTION", Uri.parse("content://result_uri"));
        result.putExtra("Authorized",false);
        result.putExtra("index",-1);
        setResult(Activity.RESULT_OK, result);
        finish();
      }

    });
  }
}
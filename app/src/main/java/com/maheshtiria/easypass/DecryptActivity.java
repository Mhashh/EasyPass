package com.maheshtiria.easypass;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            // Handle the Intent
            String msg = intent.getStringExtra("surprise");
            inp.setText(msg);
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
          }
        }
      });

    //sends to camera activity for scanning
    imgcam.setOnClickListener(
      v->{
        passTextForResult.launch(
          new Intent(this, CameraTextActivity.class)
        );
      }
    );


    button.setOnClickListener((view)->{

      //userauth password
      String authencrypt = sharedPreferences.getString("encryt","");
      String auth = sharedPreferences.getString("auth","");
      String authsalt = sharedPreferences.getString("salt","");
      String authiv = sharedPreferences.getString("iv","");

      String authdecrypt = PassEncrypt.decryptAuth(authencrypt,inp.getText().toString(),authsalt,new IvParameterSpec(authiv.getBytes()));

      if(authdecrypt.equals(auth)){
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
              String decrypt="";
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
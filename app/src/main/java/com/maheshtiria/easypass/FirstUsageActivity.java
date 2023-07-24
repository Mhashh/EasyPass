package com.maheshtiria.easypass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maheshtiria.easypass.encryption.PassEncrypt;

import java.util.Objects;

public class FirstUsageActivity extends AppCompatActivity {
  EditText pass;
  EditText confirm;
  Button save;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_first_usage);
    SharedPreferences sharedPreferences = this.getSharedPreferences("USERAUTH",Context.MODE_PRIVATE);
    if(sharedPreferences.contains("auth")){
      finish();
    }

    pass = findViewById(R.id.inputpassword);
    confirm = findViewById(R.id.confirmpassword);
    save = findViewById(R.id.save);

    save.setOnClickListener(v -> {
      String password = pass.getText().toString();
      String reenter = confirm.getText().toString();

      if(password.equals(reenter)){
        String auth = PassEncrypt.hashDigest(password);
        if(!Objects.equals(auth, "")){
          if(sharedPreferences.edit()
            .putString("auth",auth)
            .commit()){
            finish();
          }
          else{
            Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_LONG).show();
          }

        }
        else{
          Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_LONG).show();
        }
      }
      else if(password.equals("")){
        Toast.makeText(getApplicationContext(), "Empty password", Toast.LENGTH_LONG).show();
      }
      else{
        Toast.makeText(getApplicationContext(), "Input mismatch.", Toast.LENGTH_LONG).show();
      }
    });
  }


}
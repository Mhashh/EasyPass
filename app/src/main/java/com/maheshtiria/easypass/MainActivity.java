package com.maheshtiria.easypass;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
  String current="Add New";

  ActivityResultLauncher<Intent> verifyForResult;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SharedPreferences sharedPreferences = this.getSharedPreferences(Globals.USERAUTH,Context.MODE_PRIVATE);
    Log.d("VALUES","okay place");

    verifyForResult = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent intent = result.getData();
          // Handle the Intent
          boolean msg = intent.getBooleanExtra("Authorized",false);
          if(msg)
            Toast.makeText(getApplicationContext(),"Authorized",Toast.LENGTH_LONG).show();
          else {
            Toast.makeText(getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
            finish();
          }
        }
      });

    if(!sharedPreferences.contains("auth")){
      Log.d("VALUES","not here");
      String auth = new Date().toString();
      Intent intent = new Intent(this, FirstUsageActivity.class);
      intent.putExtra("auth",auth);
      startActivity(intent);
    }
    else{
      Log.d("VALUES","Correct place");
      String ivString = sharedPreferences.getString("iv","");
      Log.d("VALUES",ivString);
      byte[] iv = ivString.getBytes();
      verifyForResult.launch(
        new Intent(getApplicationContext(), VerifyActivity.class)
          .putExtra("auth",sharedPreferences.getString("auth",""))
          .putExtra("salt",sharedPreferences.getString("salt",""))
          .putExtra("encrypt",sharedPreferences.getString("encryt",""))
          .putExtra("iv",iv)
      );
    }


    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    NavController navController = navHostFragment.getNavController();
    BottomNavigationView bv = (BottomNavigationView)findViewById(R.id.nav_bar);
    NavigationUI.setupWithNavController(bv,navController);

  }
}
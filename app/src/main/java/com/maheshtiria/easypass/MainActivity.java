package com.maheshtiria.easypass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
          if(intent!=null) {
            boolean msg = intent.getBooleanExtra("Authorized", false);
            if (msg)
              Toast.makeText(getApplicationContext(), "Authorized", Toast.LENGTH_LONG).show();
            else {
              Toast.makeText(getApplicationContext(), "Not Authorized", Toast.LENGTH_LONG).show();
              finish();
            }
          }
        }
      });

    if(!sharedPreferences.contains("auth")){
      Intent intent = new Intent(this, FirstUsageActivity.class);
      startActivity(intent);
    }
    else{
      verifyForResult.launch(
        new Intent(getApplicationContext(), VerifyActivity.class)
          .putExtra("auth",sharedPreferences.getString("auth",""))
      );
    }


    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    NavController navController;
    if (navHostFragment != null) {
      navController = navHostFragment.getNavController();
      BottomNavigationView bv = findViewById(R.id.nav_bar);
      NavigationUI.setupWithNavController(bv,navController);

    }


  }
}
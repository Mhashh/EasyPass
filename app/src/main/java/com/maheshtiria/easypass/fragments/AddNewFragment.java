package com.maheshtiria.easypass.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.maheshtiria.easypass.CameraTextActivity;
import com.maheshtiria.easypass.EncryptActivity;
import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;

public class AddNewFragment extends Fragment {
  //input fields
  EditText inp1;
  EditText inp2;
  EditText inp3;
  //buttons
  ImageButton accCam;
  ImageButton appCam;
  ImageButton passCam;

  //db related
  private Pdb dbInstance;
  private PassDao pd;

  ActivityResultLauncher<Intent> appTextForResult;

  ActivityResultLauncher<Intent> accTextForResult;

  ActivityResultLauncher<Intent> passTextForResult;

  ActivityResultLauncher<Intent> encryptForResult;

  public AddNewFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dbInstance = Pdb.getDb(getContext());
    pd = dbInstance.passDao();

    appTextForResult = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent intent = result.getData();
          // Handle the Intent
          String msg = intent != null ? intent.getStringExtra("surprise") : null;
          inp1.setText(msg);
        }
      });

    accTextForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent intent = result.getData();
          // Handle the Intent
          String msg = intent != null ? intent.getStringExtra("surprise") : null;
          inp2.setText(msg);
        }
      });

    passTextForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
          Intent intent = result.getData();
          // Handle the Intent
          String msg = intent != null ? intent.getStringExtra("surprise") : null;
          inp3.setText(msg);
        }
      });

    encryptForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
      result -> {
        if(result.getResultCode() == Activity.RESULT_OK){
          Intent intent = result.getData();
          // Handle the Intent
          boolean authorized = intent.getBooleanExtra("Authorized",false);
          if(authorized){
            String encrypt = intent.getStringExtra("encrypt");
            String salt = intent.getStringExtra("salt");
            String sugar = intent.getStringExtra("sugar");
            addNewRecord(encrypt,salt,sugar);
          }
          else{
            Toast.makeText(getContext(),"Not Authorized !",Toast.LENGTH_LONG).show();
          }

        }
      });
  }

  private void addNewRecord(String encrypt,String salt,String sugar){

    String acc = inp1.getText().toString();
    String apw = inp2.getText().toString();
    String pass = inp3.getText().toString();
    acc = acc.trim();
    apw = apw.trim();
    pass = pass.trim();
    if(acc.equals("") || apw.equals("") || pass.equals("")){
      Toast.makeText(getContext(),"Fields are Empty !",Toast.LENGTH_LONG).show();
      return;
    }

    Pass newPass = new Pass(acc,apw,encrypt,salt,sugar);

    dbInstance.getQueryExecutor().execute(
      () -> {
        try {
          pd.insert(newPass);
          Looper.prepare();
          Toast.makeText(getContext(), "Successfully added the account !", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
          String[] msgs = e.getMessage().split(" ");
          String msg = "Unknown Error during storage!";
          if(msgs[0].equals("UNIQUE")){
            msg = "Account already exists !";
          }else if(msgs[1].equals("NULL")){
            msg = "Fields are Empty !";
          }
          Looper.prepare();
          Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

        }
      }
    );

    inp1.setText("");
    inp2.setText("");
    inp3.setText("");

  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    View rootview = inflater.inflate(R.layout.fragment_add_new, container, false);
    inp1 = rootview.findViewById(R.id.edit_text_app);
    inp2 = rootview.findViewById(R.id.edit_text_acc);
    inp3 = rootview.findViewById(R.id.edit_text_pass);

    appCam=rootview.findViewById(R.id.app_cam);
    accCam=rootview.findViewById(R.id.acc_cam);
    passCam=rootview.findViewById(R.id.pass_cam);
    Button submit = rootview.findViewById(R.id.submit);

    appCam.setOnClickListener((view)-> appTextForResult.launch(
      new Intent(getContext(), CameraTextActivity.class)
    ));
    accCam.setOnClickListener((view)-> accTextForResult.launch(
      new Intent(getContext(), CameraTextActivity.class)
    ));
    passCam.setOnClickListener((view)-> passTextForResult.launch(
      new Intent(getContext(), CameraTextActivity.class)
    ));

    submit.setOnClickListener(view -> {
      //string values for input fields
      String acc = inp1.getText().toString();
      String apw = inp2.getText().toString();

      //check fields are not empty
      if(acc.equals("") || apw.equals("") || inp3.getText().toString().equals("")){
        Toast.makeText(getContext(),"Fields are Empty !",Toast.LENGTH_LONG).show();
      }
      else {
        //going to encrypt activity for encrypted password
        encryptForResult.launch(
          new Intent(getContext(), EncryptActivity.class)
            .putExtra("pass", inp3.getText().toString())
        );
      }
    });

    return rootview;
  }
}
package com.maheshtiria.easypass.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.maheshtiria.easypass.CameraTextActivity;
import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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


    public AddNewFragment() {
        // Required empty public constructor
    }

    public static AddNewFragment newInstance(String param1, String param2) {
        AddNewFragment fragment = new AddNewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbInstance = Pdb.getDb(getContext());
        pd = dbInstance.passDao();

        appTextForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        String msg = intent.getStringExtra("surprise");
                        inp1.setText(msg);
                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                    }
                }
            });

        accTextForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        String msg = intent.getStringExtra("surprise");
                        inp2.setText(msg);
                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                    }
                }
            });

        passTextForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        String msg = intent.getStringExtra("surprise");
                        inp3.setText(msg);
                        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void addNewRecord(){

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
        Pass newPass = new Pass(acc,apw,pass);

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

        appCam.setOnClickListener((view)->{
            appTextForResult.launch(
                    new Intent(getContext(), CameraTextActivity.class)
            );
        });
        accCam.setOnClickListener((view)->{
            accTextForResult.launch(
                    new Intent(getContext(), CameraTextActivity.class)
            );
        });
        passCam.setOnClickListener((view)->{
            passTextForResult.launch(
                    new Intent(getContext(), CameraTextActivity.class)
            );
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewRecord();
                Log.d("OKAY","submit clicked!");
            }
        });

        return rootview;
    }
}
package com.maheshtiria.easypass.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maheshtiria.easypass.DecryptActivity;
import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;
import com.maheshtiria.easypass.recyclelist.PassListAdapter;

import java.util.List;

public class ListFragment extends Fragment {

    private PassDao pd;
    private LiveData<List<Pass>> list;
    PassListAdapter pl;
    ActivityResultLauncher<Intent> decryptForResult;
    View focus;

    OnItemClickListener onRecyclerViewItemClicked = new OnItemClickListener() {
        @Override
        public void onItemClickListener(View v, int index) {
            decryptForResult.launch(
              new Intent(getContext(), DecryptActivity.class)
                .putExtra("index",index)
                .putExtra("name",((TextView)v.findViewById(R.id.brand)).getText().toString())
                .putExtra("encrypt",((TextView)v.findViewById(R.id.password)).getText().toString())
            );
            focus = v;

        }
    };



    public ListFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Pdb dbInstance = Pdb.getDb(getContext());
        pd = dbInstance.passDao();
        dbInstance.getQueryExecutor().execute(
            ()->{
                try{
                    list = pd.getAll();
                }catch(Exception e){
                    Looper.prepare();
                    Toast.makeText(getContext(),"Error in loading data!",Toast.LENGTH_LONG).show();
                }
            }
        );

        decryptForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
          result -> {
              Log.d("VALUES","callback");
              if(result.getResultCode() == Activity.RESULT_OK){
                  Intent intent = result.getData();
                  // Handle the Intent
                  if(intent!=null) {

                      boolean authorized = intent.getBooleanExtra("Authorized", false);
                      Log.d("VALUES", "callback2");
                      if (authorized) {
                          String decrypt = intent.getStringExtra("decrypt");
                          int index = intent.getIntExtra("index", -1);

                          pl.showTruePass(index, decrypt);
                          ((TextView) focus.findViewById(R.id.password)).setVisibility(View.VISIBLE);
                      } else {
                          Toast.makeText(getContext(), "Not Authorized !", Toast.LENGTH_LONG).show();
                      }
                  }

              }
          });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_list, container, false);
        Log.d("OKAY","onCreate start");


        pl = new PassListAdapter(onRecyclerViewItemClicked);

        LinearLayoutManager llm = new LinearLayoutManager( getActivity());
        RecyclerView rv = rootview.findViewById(R.id.rcview);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(pl);



        list.observe(getViewLifecycleOwner(), passes -> pl.submitList(passes));
        Log.d("OKAY","listss");

        return rootview;
    }
}


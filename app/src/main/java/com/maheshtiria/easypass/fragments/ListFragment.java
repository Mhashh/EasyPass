package com.maheshtiria.easypass.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;
import com.maheshtiria.easypass.recyclelist.PassListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private Pdb dbInstance;
    private PassDao pd;
    private LiveData<List<Pass>> list;

    public ListFragment() {

    }

    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbInstance = Pdb.getDb(getContext());
        pd = dbInstance.passDao();
        dbInstance.getQueryExecutor().execute(
                ()->{
                    try{
                        list = pd.getAll();
                    }catch(Exception e){
                        Looper.prepare();
                        Toast.makeText(getContext(),"Error in loading data!",Toast.LENGTH_LONG);
                    }
                }
        );



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_list, container, false);
        Log.d("OKAY","onCreate start");


        PassListAdapter pl = new PassListAdapter();

        LinearLayoutManager llm = new LinearLayoutManager( getActivity());
        RecyclerView rv = rootview.findViewById(R.id.rcview);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(pl);

        list.observe(getViewLifecycleOwner(), new Observer<List<Pass>>() {
            @Override
            public void onChanged(List<Pass> passes) {
                pl.submitList(passes);
            }
        });
        Log.d("OKAY","listss");

        return rootview;
    }
}
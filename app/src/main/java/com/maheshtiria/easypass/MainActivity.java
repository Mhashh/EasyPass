package com.maheshtiria.easypass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.recyclelist.PassListAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pass[] list = new Pass[6];
        list[0] = new Pass("a","b","c");
        list[1] = new Pass("r","b","c");
        list[2] = new Pass("y","b","c");
        list[3] = new Pass("w","b","c");
        list[4] = new Pass("x","b","c");
        list[5] = new Pass("x","b","c");
        PassListAdapter pl = new PassListAdapter(list);

        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        RecyclerView rv = findViewById(R.id.rcview);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(pl);
        Log.d("OKAY","listss");

    }
}
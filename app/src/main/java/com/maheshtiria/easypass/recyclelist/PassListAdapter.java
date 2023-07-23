package com.maheshtiria.easypass.recyclelist;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.fragments.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PassListAdapter extends RecyclerView.Adapter<PassListAdapter.ViewHolder> {

    private final ArrayList<Pass> passes=new ArrayList<>();
    OnItemClickListener itemClickListener;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView comp;
        private final TextView acc;
        private final TextView pswd;

        ActivityResultLauncher<Intent> decryptForResult;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            comp = (TextView) view.findViewById(R.id.brand);
            acc = (TextView) view.findViewById(R.id.account);
            pswd = (TextView) view.findViewById(R.id.password);



        }

        public void setValues(Pass current) {
            comp.setText(current.what);
            acc.setText(current.accname);
            pswd.setText(current.pswd);
        }

    }


    public PassListAdapter(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.display_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("OKAY","onBindViewHolder "+viewHolder.toString()+" "+position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.setValues(passes.get(position));

        //setting onLongClickListener to decrypt password
        viewHolder.itemView.setOnLongClickListener(
          v->{
              itemClickListener.onItemClickListener(v,position);
              return true;
          }
        );
    }
    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<Pass> newData){

        passes.clear();
        passes.addAll(newData);
        notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public  void showTruePass(int index, String value){
        if(index>=0) {
            Pass cur = passes.get(index);
            cur.pswd = value;
            notifyDataSetChanged();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return passes.size();
    }
}


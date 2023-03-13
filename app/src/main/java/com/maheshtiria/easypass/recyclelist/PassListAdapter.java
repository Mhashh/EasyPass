package com.maheshtiria.easypass.recyclelist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;

public class PassListAdapter extends RecyclerView.Adapter<PassListAdapter.ViewHolder> {

    private Pass[] passes;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView comp;
        private final TextView acc;
        private final TextView pswd;
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

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public PassListAdapter(Pass[] dataSet) {
        passes = dataSet;
    }

    // Create new views (invoked by the layout manager)
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
        viewHolder.setValues(passes[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return passes.length;
    }
}


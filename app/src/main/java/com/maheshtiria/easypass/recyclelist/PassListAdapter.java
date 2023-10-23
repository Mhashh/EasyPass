package com.maheshtiria.easypass.recyclelist;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maheshtiria.easypass.R;
import com.maheshtiria.easypass.database.Pass;
import com.maheshtiria.easypass.database.PassDao;
import com.maheshtiria.easypass.database.Pdb;
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
      comp = view.findViewById(R.id.brand);
      acc = view.findViewById(R.id.account);
      pswd = view.findViewById(R.id.password);

      comp.setOnLongClickListener(v -> {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.actions, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {
          if (item.getItemId() == R.id.delete_this) {//Alert box before delete
            AlertDialog.Builder sureDelete = new AlertDialog.Builder(pswd.getContext());

            sureDelete.setCancelable(true);
            sureDelete.setMessage("Do you want to delete this password?");
            sureDelete.setTitle("Delete");

            sureDelete.setPositiveButton("Yes", (dialog, which) -> {
              //Database variables
              Pdb dbInstance = Pdb.getDb(pswd.getContext());
              PassDao pd = dbInstance.passDao();
              //deleting from database
              dbInstance.getQueryExecutor().execute(
                () -> {
                  try {
                    pd.delete(comp.getText().toString());
                    Looper.prepare();
                    Toast.makeText(pswd.getContext(), "Successfully deleted the account !", Toast.LENGTH_LONG).show();

                  } catch (Exception e) {
                    Looper.prepare();
                    Toast.makeText(pswd.getContext(), "Error in deleting data!", Toast.LENGTH_LONG).show();

                  }
                }
              );
            });

            sureDelete.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            sureDelete.show();
            return true;
          }
          return false;
        });
        return true;
      });



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


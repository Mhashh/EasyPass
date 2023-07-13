package com.maheshtiria.easypass.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maheshtiria.easypass.database.Pass;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class CameraViewModel extends ViewModel {

   public MutableLiveData<String> scan = new MutableLiveData<>();
   public MutableLiveData<Pair<Integer,Integer>> crop = new MutableLiveData<>();

   public void updateScan(String newscan){
      scan.setValue(newscan);
   }

   public void init(){
      crop.setValue(new Pair<Integer,Integer>(80,8));
   }

}

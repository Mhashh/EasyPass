package com.maheshtiria.easypass.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.maheshtiria.easypass.database.Pass;

import java.util.List;

public class CameraViewModel extends ViewModel {
   LiveData<List<Pass>> data;
}

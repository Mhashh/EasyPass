package com.maheshtiria.easypass.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.maheshtiria.easypass.database.Pass;

import java.util.List;

public class PassViewModel extends ViewModel {
   LiveData<List<Pass>> data;
}

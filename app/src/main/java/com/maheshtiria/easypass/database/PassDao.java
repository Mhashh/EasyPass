package com.maheshtiria.easypass.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PassDao {
    @Query("SELECT * FROM pass")
    LiveData<List<Pass>> getAll() throws Exception;


    @Query("SELECT salt FROM pass WHERE appname = :name")
    String findSaltByAppName(String name) throws Exception;

    @Query("SELECT sugar FROM pass WHERE appname = :name")
    String findSugarByAppName(String name) throws Exception;

    @Insert
    void insert(Pass onepass) throws Exception;

    @Query("UPDATE pass SET pass = :newpass WHERE accname = :name")
    void update(String name,String newpass) throws Exception;

}

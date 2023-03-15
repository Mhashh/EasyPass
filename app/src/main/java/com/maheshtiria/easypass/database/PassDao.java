package com.maheshtiria.easypass.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PassDao {
    @Query("SELECT * FROM pass")
    LiveData<List<Pass>> getAll() throws Exception;

    @Query("SELECT * FROM pass WHERE accname IN (:accNames)")
    List<Pass> loadAllByIds(String[] accNames) throws Exception;

    @Query("SELECT * FROM pass WHERE accname = :name")
    Pass findByAccName(String name) throws Exception;

    @Insert
    void insert(Pass onepass) throws Exception;

    @Query("UPDATE pass SET pass = :newpass WHERE accname = :name")
    void update(String name,String newpass) throws Exception;

    @Delete
    void delete(Pass passes) throws Exception;

}

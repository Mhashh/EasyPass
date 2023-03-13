package com.maheshtiria.easypass.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface PassDao {
    @Query("SELECT * FROM pass")
    List<Pass> getAll();

    @Query("SELECT * FROM pass WHERE accname IN (:accNames)")
    List<Pass> loadAllByIds(String[] accNames);

    @Query("SELECT * FROM pass WHERE accname = :name")
    Pass findByAccName(String name);

    @Insert
    void insert(Pass onepass);

    @Query("UPDATE pass SET pass = :newpass WHERE accname = :name")
    void update(String name,String newpass);

    @Delete
    void delete(Pass passes);

}

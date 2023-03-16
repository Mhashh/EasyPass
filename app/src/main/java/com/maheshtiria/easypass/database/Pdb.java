package com.maheshtiria.easypass.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Pass.class},version=1,exportSchema = false)
public abstract class Pdb extends RoomDatabase {
    public abstract PassDao passDao();

    public static Pdb db;
    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(6);

    public static Pdb getDb(final Context context){
        if(db==null){
            db = Room.databaseBuilder(context.getApplicationContext(),Pdb.class,"pass_db").setQueryExecutor(databaseExecutor).build();

        }

        return db;
    }
}

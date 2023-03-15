package com.maheshtiria.easypass.database;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pass {
    @PrimaryKey
    @NonNull
    public String accname;

    @ColumnInfo(name = "appname")
    public String what;

    @ColumnInfo(name = "pass")
    public String pswd;

    public Pass(String accname, String what, String pswd) {
        this.accname = accname;
        this.what = what;
        this.pswd = pswd;
    }
}

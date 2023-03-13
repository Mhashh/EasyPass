package com.maheshtiria.easypass.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pass {
    @PrimaryKey
    public String accname;

    @ColumnInfo(name = "appname")
    public String what;

    @ColumnInfo(name = "pass")
    public String pswd;

    public Pass(String a, String b, String c) {
        accname = a;
        what = b;
        pswd = c;
    }
}

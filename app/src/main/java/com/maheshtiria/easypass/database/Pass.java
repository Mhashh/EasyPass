package com.maheshtiria.easypass.database;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pass {
    @ColumnInfo(name = "accname")
    public String accname;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "appname")
    public String what;

    @NonNull
    @ColumnInfo(name = "pass")
    public String pswd;

    @NonNull
    @ColumnInfo(name = "salt")
    public String salt;

    @NonNull
    @ColumnInfo(name = "sugar")
    public String sugar;

    public Pass(String accname, String what, String pswd,String salt,String sugar) {
        this.accname = accname;
        this.what = what;
        this.pswd = pswd;
        this.salt = salt;
        this.sugar = sugar;
    }
}

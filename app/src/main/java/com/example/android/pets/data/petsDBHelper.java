package com.example.android.pets.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.pets.data.petsContract.petsEntry;

import static com.example.android.pets.data.petsContract.petsEntry.COLUMN_BREED;
import static com.example.android.pets.data.petsContract.petsEntry.COLUMN_GENDER;
import static com.example.android.pets.data.petsContract.petsEntry.COLUMN_WEIGHT;

public class petsDBHelper  extends SQLiteOpenHelper{
    private final static int version =1;
    private final static String name = "shell.db";

    public petsDBHelper(Context context){

        super(context,name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.v("xxxxxx2", "db = " + db.toString());
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + petsEntry.TABLE_NAME + " ("
                + petsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + petsEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + petsEntry.COLUMN_BREED + " TEXT, "
                + petsEntry.COLUMN_GENDER + " INTEGER NOT NULL DEFAULT 0, "
                + petsEntry.COLUMN_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + petsEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

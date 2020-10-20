package com.example.contactslist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

// contain all crud methods
public class MyDbHelper extends SQLiteOpenHelper {
    public MyDbHelper(@Nullable Context context) {
        super(context,ContactsDB.DB_NAME, null , ContactsDB.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table on this database
        sqLiteDatabase.execSQL(ContactsDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // update database if there's any changes on the db versions
        //drop older table if exist
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContactsDB.TABLE_NAME );
        //create table again
        onCreate(sqLiteDatabase);
    }

     // insert/ add a contact into db
    public long insertRecord(String name, String image , String phone ,String email){ // whatsApp,caregiver/child
        // to write into the database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // insert values
        values.put(ContactsDB.C_NAME,name);
        values.put(ContactsDB.C_IMAGE,image);
        values.put(ContactsDB.C_PHONE,phone);
        values.put(ContactsDB.C_EMAIL,email);
        // whatsApp,caregiver/child

        // insert row with all values +id which is autoIncrement
        long id = db.insert(ContactsDB.TABLE_NAME , null , values);
        // close db
        db.close();
        return id;
    }
}

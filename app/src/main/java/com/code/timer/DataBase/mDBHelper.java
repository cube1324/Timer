package com.code.timer.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;

import com.code.timer.R;

import java.io.Serializable;


public class mDBHelper extends SQLiteOpenHelper implements Serializable {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data.db";

    public mDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NavDrawerItem.SQL_DELETE_NAVTABLE);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NavDrawerItem.SQL_CREATE_NAVTABLE);
    }

    public void populateMenu(Menu menu){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(NavDrawerItem.NavDrawerEntry.TABLE_NAME, null, null, null, null, null, null);

        //If Elements are in the Database
        if (cursor.moveToFirst()){
            do {
                menu.add(R.id.timer_group, cursor.getInt(0),Menu.NONE, cursor.getString(1)).setIcon(cursor.getInt(2)).setCheckable(true);
            } while (cursor.moveToNext());
        }else{
            //Add First Item and repeat
            //addMenuItem("New Timer", R.drawable.ic_timer_black_24dp, menu);
            //populateMenu(menu);
        }

        cursor.close();
        db.close();
    }

   public int addMenuItem(String name, int icon_id, Menu menu){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NavDrawerItem.NavDrawerEntry.COLUMN_NAME_TITLE, name);
        values.put(NavDrawerItem.NavDrawerEntry.COLUMN_NAME_ICON, icon_id);

        int id = (int)db.insert(NavDrawerItem.NavDrawerEntry.TABLE_NAME, null, values);
        db.close();

        menu.add(R.id.timer_group, id, Menu.NONE, name).setIcon(icon_id).setCheckable(true);
        return id;
   }

   public void editMenuItem(int id, String name, Menu menu){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(NavDrawerItem.NavDrawerEntry.COLUMN_NAME_TITLE, name);
        String where = NavDrawerItem.NavDrawerEntry._ID + "=" + id;
        db.update(NavDrawerItem.NavDrawerEntry.TABLE_NAME, args, where, null);

        for (int i = 0; i < menu.size(); i++){
            if (menu.getItem(i).getItemId() == id){
                menu.getItem(i).setTitle(name);
                break;
            }
        }
   }

   public void delMenuItem(int id, Menu menu){
        String[] statements = { Integer.toString(id) };
        SQLiteDatabase db = getWritableDatabase();
        menu.removeItem(id);

        db.delete(NavDrawerItem.NavDrawerEntry.TABLE_NAME, NavDrawerItem.NavDrawerEntry._ID + " = ?", statements);
   }
}

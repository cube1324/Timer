package com.example.testdrawer.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.example.testdrawer.R;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


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


        if (cursor.moveToFirst()){
            do {
                menu.add(R.id.timer_group, cursor.getInt(0),Menu.NONE, cursor.getString(1)).setIcon(cursor.getInt(2)).setCheckable(true);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

   public void addMenuItem(String name, int icon_id, Menu menu){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NavDrawerItem.NavDrawerEntry.COLUMN_NAME_TITLE, name);
        values.put(NavDrawerItem.NavDrawerEntry.COLUMN_NAME_ICON, icon_id);

        int id = (int)db.insert(NavDrawerItem.NavDrawerEntry.TABLE_NAME, null, values);
        db.close();

        menu.add(R.id.timer_group, id, Menu.NONE, name).setIcon(icon_id).setCheckable(true);
   }

   public void editMenuItem(int id, String name, Menu menu){
        SQLiteDatabase db = getWritableDatabase();
        Log.v("ADWAWDAWD", Integer.toString(id));

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

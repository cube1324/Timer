package com.code.timer.DataBase;

import android.provider.BaseColumns;

public final class NavDrawerItem {

    private NavDrawerItem(){}

    public static class NavDrawerEntry implements BaseColumns{
        public static final String TABLE_NAME = "navitems";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_DATA = "data";
    }

    public static final String SQL_CREATE_NAVTABLE =
            "CREATE TABLE " + NavDrawerEntry.TABLE_NAME + " (" +
                    NavDrawerEntry._ID + " INTEGER PRIMARY KEY," +
                    NavDrawerEntry.COLUMN_NAME_TITLE + " TEXT," +
                    NavDrawerEntry.COLUMN_NAME_ICON + " INTEGER," +
                    NavDrawerEntry.COLUMN_NAME_DATA + " TEXT)";

    public static final String SQL_DELETE_NAVTABLE =
            "DROP TABLE IF EXISTS " + NavDrawerEntry.TABLE_NAME;
}

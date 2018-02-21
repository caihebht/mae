package com.example.nadine.datenbank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nadine on 18.12.17.
 */

public class DatenbankMemoHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatenbankMemoHelper.class.getSimpleName();


    public static final String DB_NAME = "shopping_list.db";
    public static final int DB_VERSION = 1;


    // Einkaufslist übersicht
    public static final  String TABLE_SHOPPINGLIST = "shoppinglist";
    public static final  String COLUMN_SHOPPINGLIST_ID = "_id";
    public static final  String COLUMN_SHOPPLIST_NAME = "name";
    public static final  String COLUMN_IMAGE_PATH = "imagepath";

    // Einkaufslist Artikel
    public static final String TABLE_SHOPPING_LISTITEM = "shoppinglistitems";
    public static final String COLUMN_SHOPPINGLISTITEM_ID = "shoppinglistitem_id";
    public static final String COLUMN_PRODUCT = "product";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_SHOPPINGLISTITEM_SHOPPINGLIST_ID = "shppinglist_id";


    // SQL statment zum Erzeugen von Einfkaufslist

    public static final  String SQL_CREATE_SHOPPLINGLIST =
            "CREATE TABLE" + TABLE_SHOPPINGLIST +
                    "(" + COLUMN_SHOPPINGLIST_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SHOPPLIST_NAME + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_PATH + " TEXT NOT NULL);";

    // SQL statment zum Erzeugen von Einkaufslist Artikel

    public static final String SQL_CREATE = "CREATE TABLE " + TABLE_SHOPPING_LISTITEM + "("
            + COLUMN_SHOPPINGLISTITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PRODUCT + " TEXT NOT NULL, "
            + COLUMN_QUANTITY + " INTEGER NOT NULL"
            + COLUMN_SHOPPINGLISTITEM_SHOPPINGLIST_ID + "INTERGER NOT NULL"
            + "FOREIGN KEY(" + COLUMN_SHOPPINGLISTITEM_SHOPPINGLIST_ID +") REFRENCES "
            + TABLE_SHOPPINGLIST +"(_id)"
            + ");";

    public DatenbankMemoHelper(Context context) {
        //super(context, "PLATZHALTER_DATENBANKNAME", null, 1);
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE_SHOPPLINGLIST);
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE_SHOPPLINGLIST + " angelegt.");

        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

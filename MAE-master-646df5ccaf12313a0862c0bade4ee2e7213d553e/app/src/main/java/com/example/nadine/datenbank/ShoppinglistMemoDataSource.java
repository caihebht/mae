package com.example.nadine.datenbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cai on 01.02.18.
 */

public class ShoppinglistMemoDataSource {
    private static final String LOG_TAG = ShoppinglistMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DatenbankMemoHelper dbHelper;
    private Context mcontext;
    private String [] columnslist ={
            DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID,
            DatenbankMemoHelper.COLUMN_SHOPPLIST_NAME,
            DatenbankMemoHelper.COLUMN_IMAGE_PATH
    };

    // VERBINDUNG ZUR DATENBANK
    public ShoppinglistMemoDataSource(Context context) {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new DatenbankMemoHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    // SPEICHERN DER DATEN IN DER DATENBANK
    public ShoppinglistMemo createShoppingListmemo(String name, String imagepath) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_SHOPPLIST_NAME, name);
        values.put(DatenbankMemoHelper.COLUMN_IMAGE_PATH, imagepath);

        long insertId = database.insert(DatenbankMemoHelper.TABLE_SHOPPINGLIST, null, values);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPINGLIST,
                columnslist, DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        ShoppinglistMemo shoppinglistMemo = cursorToDatenbankMemolist(cursor);
        cursor.close();

        return shoppinglistMemo;
    }
    // Ein Einkaufslist und mit ihm alle Artikel werden gelöscht

    public void deleteShoppingListMemo(ShoppinglistMemo shoppinglistMemo) {
        long id = shoppinglistMemo.getId();

        DatenbankMemoDataSource datenbankMemodatasource = new DatenbankMemoDataSource(mcontext);
        List<DatenbankMemo> artikellist = datenbankMemodatasource.getAllShoppingMemos();
        if(artikellist != null && !artikellist.isEmpty()){
            for (DatenbankMemo d : artikellist){
                datenbankMemodatasource.deleteShoppingMemo(d);
            }
        }

        database.delete(DatenbankMemoHelper.TABLE_SHOPPINGLIST,
                DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + shoppinglistMemo.toString());
    }
    // UPDATE DER LISTE
    public ShoppinglistMemo updateShoppingMemo(long id, String name, String imagepath) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_SHOPPLIST_NAME, name);
        values.put(DatenbankMemoHelper.COLUMN_IMAGE_PATH, imagepath);

        database.update(DatenbankMemoHelper.TABLE_SHOPPINGLIST,
                values,
                DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID + "=" + id,
                null);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPINGLIST,
                columnslist, DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        ShoppinglistMemo shoppinglistMemo = cursorToDatenbankMemolist(cursor);
        cursor.close();

        return shoppinglistMemo;
    }

    private ShoppinglistMemo cursorToDatenbankMemolist(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_SHOPPINGLIST_ID);
        int idName = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_SHOPPLIST_NAME);
        int idImagepath = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_IMAGE_PATH);

        long id = cursor.getLong(idIndex);
        String name = cursor.getString(idName);
        String imagepath = cursor.getString(idImagepath);

        ShoppinglistMemo shoppinglistMemo = new ShoppinglistMemo(id, name, imagepath);

        return shoppinglistMemo;
    }

    // UNSERE LISTE WIRD ERSTELLT - INHALT WIRD ANGEZEIGT

    public List<ShoppinglistMemo> getAllShoppinglistMemos() {



        List<ShoppinglistMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPINGLIST,
                columnslist, null, null, null, null, null);

        cursor.moveToFirst();
        ShoppinglistMemo shoppingMemo;

        /* während der cursor nicht bis letzte Zeile läuft, wird pro Rheihe eine DatenbankMemo (bzw ein Datensatz)
            durch den Methode cursoToDatenbanMemo generierte, und dann zu shoppingMenoList hingefügt
         */

        while (!cursor.isAfterLast()) {
            shoppingMemo = cursorToDatenbankMemolist(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(LOG_TAG, "ID: " + shoppingMemo.getId() + ", Einkaufszettel: " +shoppingMemo.getName() + " Bildpfade: "+ shoppingMemo.getImagePath());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }


}

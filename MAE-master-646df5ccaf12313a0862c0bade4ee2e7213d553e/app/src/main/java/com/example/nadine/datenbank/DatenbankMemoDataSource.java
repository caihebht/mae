package com.example.nadine.datenbank;

/**
 * Created by nadine on 18.12.17.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatenbankMemoDataSource {

    private static final String LOG_TAG = DatenbankMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private DatenbankMemoHelper dbHelper;
    private Context mContext;

    private String[] columnsitem = {
            DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID,
            DatenbankMemoHelper.COLUMN_PRODUCT,
            DatenbankMemoHelper.COLUMN_QUANTITY
    };




    // VERBINDUNG ZUR DATENBANK
    public DatenbankMemoDataSource(Context context){
        dbHelper = new DatenbankMemoHelper(context);
        this.mContext = context;
        // Datenbanköffnen
        try{
            open();
        }catch (SQLException e) {
            Log.e(LOG_TAG, "Fehler bei den Öffnen der Datenbank" + e.getMessage());
            e.printStackTrace();
        }
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
    public DatenbankMemo createDatenbankMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_PRODUCT, product);
        values.put(DatenbankMemoHelper.COLUMN_QUANTITY, quantity);

        long insertId = database.insert(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM, null, values);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM,
                columnsitem, DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo shoppingMemo = cursorToDatenbankMemoitem(cursor);
        cursor.close();

        return shoppingMemo;
    }
    // EINTEAG LÖSCHEN AUS DER LISTE

    public void deleteShoppingMemo(DatenbankMemo datenbankMemo) {
        long id = datenbankMemo.getId();

        database.delete(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM,
                DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + datenbankMemo.toString());
    }
    // UPDATE DER LISTE
    public DatenbankMemo updateShoppingMemo(long id, String newProduct, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_PRODUCT, newProduct);
        values.put(DatenbankMemoHelper.COLUMN_QUANTITY, newQuantity);

        database.update(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM,
                values,
                DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID + "=" + id,
                null);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM,
                columnsitem, DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo datenbankMemo = cursorToDatenbankMemoitem(cursor);
        cursor.close();

        return datenbankMemo;
    }

    private DatenbankMemo cursorToDatenbankMemoitem(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_SHOPPINGLISTITEM_ID);
        int idProduct = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_QUANTITY);
        int idImagepath = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_IMAGE_PATH);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        String imagepath = cursor.getString(idImagepath);

        DatenbankMemo shoppingMemo = new DatenbankMemo(product, quantity, id, imagepath);

        return shoppingMemo;
    }

    // UNSERE LISTE WIRD ERSTELLT - INHALT WIRD ANGEZEIGT

    public List<DatenbankMemo> getAllShoppingMemos() {
        List<DatenbankMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LISTITEM,
                columnsitem, null, null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo datenbankmemo;

        /* während der cursor nicht bis letzte Zeile läuft, wird pro Rheihe eine DatenbankMemo (bzw ein Datensatz)
            durch den Methode cursoToDatenbanMemo generierte, und dann zu shoppingMenoList hingefügt
         */

        while (!cursor.isAfterLast()) {
            datenbankmemo = cursorToDatenbankMemoitem(cursor);
            shoppingMemoList.add(datenbankmemo);
            Log.d(LOG_TAG, "ID: " + datenbankmemo.getId() + ", Inhalt: " + datenbankmemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }

    public List<DatenbankMemo> getItemofShoppinglist ( long shoppinglistId){
        List<DatenbankMemo> artikellist = new ArrayList<DatenbankMemo>();

        Cursor cursor = database.query(dbHelper.TABLE_SHOPPING_LISTITEM, columnsitem,
                                        dbHelper.COLUMN_SHOPPINGLIST_ID+ " = ?",
                new String [] {String.valueOf(shoppinglistId)},null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            DatenbankMemo artikel = cursorToDatenbankMemoitem(cursor);
            artikellist.add(artikel);
            cursor.moveToNext();

        }
        cursor.close();
        return artikellist;
    }

}

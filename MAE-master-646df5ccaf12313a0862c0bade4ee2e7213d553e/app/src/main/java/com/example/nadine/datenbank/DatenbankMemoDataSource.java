package com.example.nadine.datenbank;

/**
 * Created by nadine on 18.12.17.
 */

import android.content.Context;
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

    private String[] columns = {
            DatenbankMemoHelper.COLUMN_ID,
            DatenbankMemoHelper.COLUMN_PRODUCT,
            DatenbankMemoHelper.COLUMN_QUANTITY
    };

    // VERBINDUNG ZUR DATENBANK
    public DatenbankMemoDataSource(Context context) {
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

    // ERSTELLEN DER DATENBANK
    public DatenbankMemo createDatenbankMemo(String product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_PRODUCT, product);
        values.put(DatenbankMemoHelper.COLUMN_QUANTITY, quantity);

        long insertId = database.insert(DatenbankMemoHelper.TABLE_SHOPPING_LIST, null, values);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LIST,
                columns, DatenbankMemoHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo shoppingMemo = cursorToDatenbankMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }
    // EINTEAG LÖSCHEN AUS DER LISTE

    public void deleteShoppingMemo(DatenbankMemo datenbankMemo) {
        long id = datenbankMemo.getId();

        database.delete(DatenbankMemoHelper.TABLE_SHOPPING_LIST,
                DatenbankMemoHelper.COLUMN_ID + "=" + id,
                null);

        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + datenbankMemo.toString());
    }
    // UPDATE DER LISTE
    public DatenbankMemo updateShoppingMemo(long id, String newProduct, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put(DatenbankMemoHelper.COLUMN_PRODUCT, newProduct);
        values.put(DatenbankMemoHelper.COLUMN_QUANTITY, newQuantity);

        database.update(DatenbankMemoHelper.TABLE_SHOPPING_LIST,
                values,
                DatenbankMemoHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LIST,
                columns, DatenbankMemoHelper.COLUMN_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo datenbankMemo = cursorToDatenbankMemo(cursor);
        cursor.close();

        return datenbankMemo;
    }

    private DatenbankMemo cursorToDatenbankMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(DatenbankMemoHelper.COLUMN_QUANTITY);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);

        DatenbankMemo shoppingMemo = new DatenbankMemo(product, quantity, id);

        return shoppingMemo;
    }

    // UNSERE LISTE WIRD ERSTELLT - INHALT WIRD ANGEZEIGT

    public List<DatenbankMemo> getAllShoppingMemos() {
        List<DatenbankMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(DatenbankMemoHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        DatenbankMemo shoppingMemo;

        while (!cursor.isAfterLast()) {
            shoppingMemo = cursorToDatenbankMemo(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(LOG_TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }
}

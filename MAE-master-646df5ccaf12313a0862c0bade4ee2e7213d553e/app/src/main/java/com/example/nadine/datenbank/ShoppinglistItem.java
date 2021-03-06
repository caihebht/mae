package com.example.nadine.datenbank;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;


public class ShoppinglistItem extends Activity {


    public static final String LOG_TAG = ShoppinglistItem.class.getSimpleName();
    private DatenbankMemoDataSource dataSource;

    ImageView shoppingimage;
    Intent intent1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    final int requcode = 3;
    Uri bilduri;
    Bitmap bitmap;
    InputStream minputStream;
    private boolean isSelectimage = false;
    public static final int KITKAT_VALUE = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Titel verstecken
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shopping_list_item);


        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");
        dataSource = new DatenbankMemoDataSource(this);
        activateAddButton();
        initializeContextualActionBar();

        final Button pickImage= (Button)findViewById(R.id.pick_image);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {
                    intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent1.addCategory(Intent.CATEGORY_OPENABLE);
                    intent1.setType("*/*");
                    startActivityForResult(intent1, KITKAT_VALUE);
                } else {
                    intent1 = new Intent();
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, KITKAT_VALUE);
                }

            }
        });
    }

    // ANZEIGEN ALLER EINTRÄGE
    private void showAllListEntries() {
        List<DatenbankMemo> shoppingMemoList = dataSource.getAllShoppingMemos();
        ArrayAdapter<DatenbankMemo> shoppingMemoArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                shoppingMemoList);
        ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);

    /*Überprüfen ob die List nicht leer ist, dann wird das erste Element genommen werden
    Das Bild davon wird gezeigt, wenn den App startet
    */
        if(!shoppingMemoList.isEmpty()) {
            try {
                DatenbankMemo firstitem = shoppingMemoList.get(0);
                Uri showimageuri = Uri.parse(firstitem.getImagepath());
                minputStream = getContentResolver().openInputStream(showimageuri);
                bitmap = BitmapFactory.decodeStream(minputStream);
                shoppingimage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

        // ÖFFNEN UND SCHLIEßEN DER DATENBANK - LOG STATEMENTS IN DER CONSOLE
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();

        Log.d(LOG_TAG, "Folgende Einträge sind in der Datenbank vorhanden:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == KITKAT_VALUE){
                bilduri = data.getData();
                Log.d("datenbank","pfad:  " +bilduri.toString());
                try {
                    minputStream = getContentResolver().openInputStream(bilduri);
                    bitmap = BitmapFactory.decodeStream(minputStream);
                    shoppingimage.setImageBitmap(bitmap);
                    isSelectimage = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isSelectimage = false;
                }
            }

        }
    }

    /// EINTRÄGE HINZUFÜGEN DURCH DEN "ADD-BUTTON"
    private void activateAddButton() {
        Button buttonAddProduct = (Button) findViewById(R.id.button_add_product);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextProduct = (EditText) findViewById(R.id.editText_product);
        shoppingimage = (ImageView)findViewById(R.id.shoppingimage);


        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProduct.getText().toString();

                if (TextUtils.isEmpty(quantityString)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if (TextUtils.isEmpty(product)) {
                    editTextProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                int quantity = Integer.parseInt(quantityString);
                editTextQuantity.setText("");
                editTextProduct.setText("");

               /* kein Bild ausgesucht -->imagepath = leere String:
                    imagepath  = bilduri.toString(); ---> Uri --> in String umwandeln


                    Richitige Pfad von URI herausfinden
                    Bildpfad  zu file://**** Konvetieren!!!
                     bsp file:///storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20180127-WA0000.jpg
                */
               String imagepath="";
               if (isSelectimage == false){
                   imagepath = "";
               }else {
                   String wholeID = DocumentsContract.getDocumentId(bilduri);
                   String id = wholeID.split(":")[1];
                   String[] column = { MediaStore.Images.Media.DATA };
                   String sel = MediaStore.Images.Media._ID + "=?";

                   Cursor cursor = getContentResolver().
                           query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                   column, sel, new String[]{ id }, null);

                   int columnIndex = cursor.getColumnIndex(column[0]);

                   if (cursor.moveToFirst()) {
                       imagepath = "file://"+ cursor.getString(columnIndex);
                   }

                   cursor.close();
               }

                dataSource.createDatenbankMemo(product, quantity, imagepath);
                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });

    }





    private void initializeContextualActionBar() {

        final ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        shoppingMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            int selCount = 0;


            // In dieser Callback-Methode zählen wir die ausgewählen Listeneinträge mit
            // und fordern ein Aktualisieren der Contextual Action Bar mit invalidate() an
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            // In dieser Callback-Methode legen wir die CAB-Menüeinträge an
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            // In dieser Callback-Methode reagieren wir auf den invalidate() Aufruf
            // Wir lassen das Edit-Symbol verschwinden, wenn mehr als 1 Eintrag ausgewählt ist
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_change);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                return true;
            }


            // In dieser Callback-Methode reagieren wir auf Action Item-Klicks
            // Je nachdem ob das Löschen- oder Ändern-Symbol angeklickt wurde
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedShoppingMemosPositions = shoppingMemosListView.getCheckedItemPositions();

                boolean checked = touchedShoppingMemosPositions.valueAt(0);
                if (checked) {
                    int postitionInListViewx = touchedShoppingMemosPositions.keyAt(0);
                    DatenbankMemo shoppingMemo = (DatenbankMemo) shoppingMemosListView.getItemAtPosition(postitionInListViewx);
                    Log.d(LOG_TAG, "Position im ListView: " + postitionInListViewx + " Inhalt: " + shoppingMemo.toString());

                    Uri showimageuri = Uri.parse(shoppingMemo.getImagepath());
                    try {
                        minputStream = getContentResolver().openInputStream(showimageuri);
                        bitmap = BitmapFactory.decodeStream(minputStream);
                        shoppingimage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d ("exception","Fehler!!!!");
                    }
                }



                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                DatenbankMemo shoppingMemo = (DatenbankMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteShoppingMemo(shoppingMemo);
                                shoppingimage.setImageResource(0);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        break;

                    case R.id.cab_change:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if (isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                DatenbankMemo shoppingMemo = (DatenbankMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());

                                AlertDialog editShoppingMemoDialog = createEditShoppingMemoDialog(shoppingMemo);
                                editShoppingMemoDialog.show();
                            }
                        }

                        mode.finish();
                        break;




                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            // In dieser Callback-Methode reagieren wir auf das Schließen der CAB
            // Wir setzen den Zähler auf 0 zurück
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;
            }
        });
    }

    private AlertDialog createEditShoppingMemoDialog(final DatenbankMemo datenbankMemo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_memo, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(datenbankMemo.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(datenbankMemo.getProduct());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String product = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(product))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        DatenbankMemo updatedShoppingMemo = dataSource.updateShoppingMemo(datenbankMemo.getId(), product, quantity);

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + datenbankMemo.getId() + " Inhalt: " + datenbankMemo.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedShoppingMemo.getId() + " Inhalt: " + updatedShoppingMemo.toString());

                        showAllListEntries();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
/*
    @Override
   public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it minputStream present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

}

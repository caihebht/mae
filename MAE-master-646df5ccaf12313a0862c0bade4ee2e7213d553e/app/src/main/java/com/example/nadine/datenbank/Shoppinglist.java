package com.example.nadine.datenbank;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Shoppinglist extends AppCompatActivity {
    Button baddlist;
    ListView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);


        simpleList= (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(Shoppinglist.this, android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.listinhalt));
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent= new Intent(Shoppinglist.this,ShoppinglistItem.class);
                intent.putExtra("ListNamen",simpleList.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
        simpleList.setAdapter(adapter);

    }
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
        if (id == R.id.action_addshoppinglist) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogsView = inflater.inflate(R.layout.dialog_add_shoppinglist, null);

            final EditText addshopplistname = (EditText) dialogsView.findViewById(R.id.shoppinglistname);
            final Button addbutton = (Button) dialogsView.findViewById(R.id.button_add);
            final Button cancelbutton = (Button) dialogsView.findViewById(R.id.button_cancel);

            builder.setView(dialogsView)
                    .setTitle("neue Einkaufslist Einfügen")
                    .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {


                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }


        return super.onOptionsItemSelected(item);
    }

}

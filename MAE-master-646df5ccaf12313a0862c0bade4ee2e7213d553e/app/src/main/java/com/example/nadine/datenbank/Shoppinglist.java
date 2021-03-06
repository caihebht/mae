package com.example.nadine.datenbank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Shoppinglist extends AppCompatActivity {
    Button button;
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

}

package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ItemListAdapter adapter;
    private RecyclerView rcListWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();

        Database database = new Database(this);
        ArrayList<ItemData> list = database.getAll();

        adapter = new ItemListAdapter(list);
        rcListWord.setAdapter(adapter);
        rcListWord.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setView() {
        rcListWord = findViewById(R.id.rc_list_item);
    }
}
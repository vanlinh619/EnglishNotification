package com.example.englishnotification;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {

    private ItemListAdapter adapter;
    private RecyclerView rcListWord;
    private ImageView imAdd;
    public Database database;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();

        database = new Database(this);
        ArrayList<ItemData> list = database.getAll();

        adapter = new ItemListAdapter(list, MainActivity.this);
        rcListWord.setAdapter(adapter);
        rcListWord.setLayoutManager(new LinearLayoutManager(this));

        imAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord.newInstance(MainActivity.this);
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reloadList(Database database){
        ArrayList<ItemData> list = database.getAll();
        adapter = new ItemListAdapter(list, MainActivity.this);
        rcListWord.setAdapter(adapter);
    }

    private void setView() {
        rcListWord = findViewById(R.id.rc_list_item);
        imAdd = findViewById(R.id.im_add_new_word);
    }
}
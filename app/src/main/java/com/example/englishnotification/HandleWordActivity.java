package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.englishnotification.handle.CustomList.MeanAdapter;
import com.example.englishnotification.handle.CustomList.TypeAdapter;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Type;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class HandleWordActivity extends AppCompatActivity {

    private RecyclerView rcMean;
    private ArrayList<Type> choseTypes;
    private ChipGroup cgType;
    private MeanAdapter meanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_word);

        setView();

        Intent intent = getIntent();
        ArrayList<Type> types = (ArrayList<Type>) intent.getSerializableExtra("types");

        choseTypes = new ArrayList<>();

        meanAdapter = new MeanAdapter(choseTypes, this);
        rcMean.setAdapter(meanAdapter);
        rcMean.setLayoutManager(new LinearLayoutManager(this));

        for (Type type: types){
            Chip chip = new Chip(this);
            chip.setText(type.name);
            chip.setCheckable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chip.isChecked()){
                        choseTypes.add(type);
                        meanAdapter.notifyItemInserted(choseTypes.indexOf(type));
                    } else {
                        int index = choseTypes.indexOf(type);
                        choseTypes.remove(type);
                        meanAdapter.notifyItemRemoved(index);
                    }
                }
            });
            cgType.addView(chip);
        }
    }

    private void setView() {
        rcMean = findViewById(R.id.rc_mean);
        cgType = findViewById(R.id.cg_type);
    }
}
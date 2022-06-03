package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishnotification.handle.CustomList.MeanAdapter;
import com.example.englishnotification.handle.CustomList.TypeAdapter;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.Word;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HandleWordActivity extends AppCompatActivity {

    public static final int ADD = 0;

    private RecyclerView rcMean;
    private ArrayList<Type> choseTypes;
    private ChipGroup cgType;
    private MeanAdapter meanAdapter;
    private ImageView imHandle;
    private EditText edEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_word);

        setView();

        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag", -1);

        choseTypes = new ArrayList<>();

        meanAdapter = new MeanAdapter(choseTypes, this);
        rcMean.setAdapter(meanAdapter);
        rcMean.setLayoutManager(new LinearLayoutManager(this));

        for (Type type: MainActivity.types){
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

        switch (flag){
            case ADD:
                imHandle.setOnClickListener(addWord());
                break;
        }
    }

    public View.OnClickListener addWord(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                ArrayList<Mean> means = new ArrayList<>();
                for (Type type: choseTypes){
                    int index = choseTypes.indexOf(type);
                    MeanAdapter.ViewHolder holder = (MeanAdapter.ViewHolder) rcMean.getChildViewHolder(rcMean.getChildAt(index));
                    Mean mean = new Mean(0, type, holder.edMean.getText().toString().trim());
                    means.add(mean);
                }
                if (!english.equals("") && !MainActivity.wordExists(english, -1) && notNullOrEmpty(means)) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    String date = format.format(new Date());
                    Word word = new Word(0, date, english, 0, 1, 1, 0);
                    MainActivity.database.addNewWord(word);
                    if(means.size() > 0){
                        MainActivity.database.addMeans(means);
                    }

                    Word item = MainActivity.database.getNewWord();
                    MainActivity.listWord.add(0, item);
                    MainActivity.notifyItemInserted(0);
                    finish();
                } else {
                    if (english.equals("")) {
                        Toast.makeText(HandleWordActivity.this, "Please fill english text!", Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.showAlertDialog(HandleWordActivity.this, "This word already exists!", english);
                    }
                }
            }
        };
    }

    private boolean notNullOrEmpty(ArrayList<Mean> means){
        for (Mean mean: means){
            if(mean.meanWord == null || mean.meanWord.equals("")){
                return false;
            }
        }
        return true;
    }

    private void setView() {
        rcMean = findViewById(R.id.rc_mean);
        cgType = findViewById(R.id.cg_type);
        imHandle = findViewById(R.id.im_handle);
        edEnglish = findViewById(R.id.ed_english);
    }
}
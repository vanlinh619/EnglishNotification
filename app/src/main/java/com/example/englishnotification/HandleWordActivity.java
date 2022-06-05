package com.example.englishnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishnotification.handle.CustomList.MeanAdapter;
import com.example.englishnotification.handle.CustomList.TypeAdapter;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.UtilContent;
import com.example.englishnotification.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HandleWordActivity extends AppCompatActivity implements MeanAdapter.RecyclerViewReady {

    public static final int ADD = 0;
    public static final int UPDATE = 1;
    public static final int TRANSLATE = 2;

    private RecyclerView rcMean;
    private ArrayList<Type> choseTypes;
    private ChipGroup cgType;
    private MeanAdapter meanAdapter;
    public ImageView imHandle;
    public EditText edEnglish;
    private TextView txTitle;
    public int flag;
    private Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_word);

        setView();

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", UtilContent.NON);
        word = (Word) intent.getSerializableExtra("word");
        if(word != null){
            for (Word w: MainActivity.listWord){
                if (w.id == word.id){
                    word = w;
                }
            }
        }

        switch (flag) {
            case ADD:
                txTitle.setText("Add New Word");
                createEmptyListMean(this);
                imHandle.setOnClickListener(addWord());
                createListChip(null);
                break;
            case UPDATE:
                txTitle.setText("Update Word");
                edEnglish.setText(word.english);
                imHandle.setImageResource(R.drawable.edit);

                ArrayList<Integer> positionChips = new ArrayList<>();
                if (word.means == null) {
                    createEmptyListMean(this);
                } else {
                    createListMean(word.means, positionChips);
                }
                createListChip(positionChips);

                imHandle.setOnClickListener(updateWord(word));
                break;
            case TRANSLATE:
                txTitle.setText("Translate Word");
                imHandle.setImageResource(R.drawable.translate);
                createEmptyListMean(this);
                createListChip(null);
                edEnglish.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        MeanAdapter.ViewHolder holder = (MeanAdapter.ViewHolder) rcMean.getChildViewHolder(rcMean.getChildAt(0));
                        if (!s.toString().equals("") && !holder.edMean.getText().toString().trim().equals("")) {
                            imHandle.setImageResource(R.drawable.add);
                            imHandle.setOnClickListener(addWord());
                        } else {
                            imHandle.setImageResource(R.drawable.translate);
                            imHandle.setOnClickListener(translateWord(edEnglish, holder.edMean));
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void ready(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("") && !edEnglish.getText().toString().trim().equals("")) {
                    imHandle.setImageResource(R.drawable.add);
                    imHandle.setOnClickListener(addWord());
                } else {
                    imHandle.setImageResource(R.drawable.translate);
                    imHandle.setOnClickListener(translateWord(edEnglish, editText));
                }
            }
        });
    }

    private View.OnClickListener translateWord(EditText edEnglish, EditText edMean) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                String vietnamese = edMean.getText().toString().trim();
                if (!english.equals("") && vietnamese.equals("")) {
                    MainActivity.translatorEnglish.translate(english)
                            .addOnSuccessListener(
                                    new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            edMean.setText(o.toString());
                                            imHandle.setImageResource(R.drawable.add);
                                            imHandle.setOnClickListener(addWord());
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error.
                                            Toast.makeText(HandleWordActivity.this, "Please wait while app download file language!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                } else if (!vietnamese.equals("") && english.equals("")) {
                    MainActivity.translatorVietnamese.translate(vietnamese)
                            .addOnSuccessListener(
                                    new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            edEnglish.setText(o.toString());
                                            imHandle.setImageResource(R.drawable.add);
                                            imHandle.setOnClickListener(addWord());
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error.
                                            Toast.makeText(HandleWordActivity.this, "Please wait while app download file language!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                } else if (english.equals("") && vietnamese.equals("")) {
                    Toast.makeText(HandleWordActivity.this, "Please fill text!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void createListChip(ArrayList<Integer> positionChips) {
        for (Type type : MainActivity.types) {
            Chip chip = new Chip(this);
            chip.setText(type.name);
            chip.setCheckable(true);
            if (positionChips != null) {
                for (Integer position : positionChips) {
                    if (position == MainActivity.types.indexOf(type)) {
                        chip.setChecked(true);
                    }
                }
            }
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chip.isChecked()) {
                        addType(type);
                    } else {
                        removeType(type);
                    }
                }
            });
            cgType.addView(chip);
        }
    }

    private void createEmptyListMean(Context context) {
        Type nonType = new Type(UtilContent.NON, null);
        choseTypes = new ArrayList<>();
        choseTypes.add(nonType);

        meanAdapter = new MeanAdapter(flag, choseTypes, context);
        rcMean.setAdapter(meanAdapter);
        rcMean.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createListMean(ArrayList<Mean> means, ArrayList<Integer> positionChips) {
        choseTypes = new ArrayList<>();
        if (means.size() == 1 && means.get(0).type.id == UtilContent.NON) {
            Type nonType = new Type(UtilContent.NON, null);
            choseTypes.add(nonType);
        } else {
            for (Mean mean : means) {
                for (Type type : MainActivity.types) {
                    if (mean.type.id == type.id) {
                        choseTypes.add(type);
                        positionChips.add(MainActivity.types.indexOf(type));
                    }
                }
            }
        }

        meanAdapter = new MeanAdapter(flag, means, choseTypes);
        rcMean.setAdapter(meanAdapter);
        rcMean.setLayoutManager(new LinearLayoutManager(this));
    }

    private void removeType(Type type) {
        Type nonType = new Type(UtilContent.NON, null);
        if (choseTypes.size() == 1 && choseTypes.get(0).id != UtilContent.NON) {
            choseTypes.remove(0);
            choseTypes.add(nonType);
            meanAdapter.notifyItemChanged(0);
        } else {
            meanAdapter.notifyItemRemoved(choseTypes.indexOf(type));
            choseTypes.remove(type);
        }
    }

    private void addType(Type type) {
        if (choseTypes.size() == 1 && choseTypes.get(0).id == UtilContent.NON) {
            choseTypes.remove(0);
            choseTypes.add(type);
            meanAdapter.notifyItemChanged(0);
        } else {
            choseTypes.add(type);
            meanAdapter.notifyItemInserted(choseTypes.size() - 1);
        }
    }

    private View.OnClickListener updateWord(Word word) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                ArrayList<Mean> means = new ArrayList<>();
                boolean notNullOrEmpty = true;
                for (Type type : choseTypes) {
                    int index = choseTypes.indexOf(type);
                    MeanAdapter.ViewHolder holder = (MeanAdapter.ViewHolder) rcMean.getChildViewHolder(rcMean.getChildAt(index));
                    Mean mean = new Mean(0, type, holder.edMean.getText().toString().trim(), 0);
                    if (!notNullOrEmpty(mean) && !isTypeNon(choseTypes)) {
                        notNullOrEmpty = false;
                    }
                    means.add(mean);
                }
                if (!english.equals("") && !MainActivity.wordExists(english, word.id) && notNullOrEmpty) {
                    int indexWord = MainActivity.listWord.indexOf(word);
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    String date = format.format(new Date());
                    word.date = date;
                    word.english = english;
                    MainActivity.database.updateWord(word);
                    for (Mean mean : means) {
                        mean.wordId = word.id;
                    }
                    if(word.means != null){
                        MainActivity.database.deleteMeans(word.id);
                    }
                    MainActivity.database.addMeans(means);
                    word.means = means;
                    MainActivity.notifyItemChanged(indexWord);
                    finish();
                } else {
                    if (english.equals("") || !notNullOrEmpty) {
                        Toast.makeText(HandleWordActivity.this, "Please fill text!", Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.showAlertDialog(HandleWordActivity.this, "This word already exists!", english);
                    }
                }
            }
        };
    }

    public View.OnClickListener addWord() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                ArrayList<Mean> means = new ArrayList<>();
                boolean notNullOrEmpty = true;
                for (Type type : choseTypes) {
                    int index = choseTypes.indexOf(type);
                    MeanAdapter.ViewHolder holder = (MeanAdapter.ViewHolder) rcMean.getChildViewHolder(rcMean.getChildAt(index));
                    Mean mean = new Mean(0, type, holder.edMean.getText().toString().trim(), 0);
                    if (!notNullOrEmpty(mean) && !isTypeNon(choseTypes)) {
                        notNullOrEmpty = false;
                    }
                    means.add(mean);
                }
                if (!english.equals("") && !MainActivity.wordExists(english, -1) && notNullOrEmpty) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    String date = format.format(new Date());
                    Word word = new Word(0, date, english, 0, 1, 1, 0);
                    MainActivity.database.addNewWord(word);
                    Word newWord = MainActivity.database.getNewWord();
                    if (means.size() > 0) {
                        for (Mean mean : means) {
                            mean.wordId = newWord.id;
                        }
                        MainActivity.database.addMeans(means);
                        newWord.means = means;
                    }
                    MainActivity.listWord.add(0, newWord);
                    MainActivity.notifyItemInserted(0);
                    finish();
                } else {
                    if (english.equals("") || !notNullOrEmpty) {
                        Toast.makeText(HandleWordActivity.this, "Please fill text!", Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.showAlertDialog(HandleWordActivity.this, "This word already exists!", english);
                    }
                }
            }
        };
    }

    private boolean isTypeNon(ArrayList<Type> choseTypes){
        return (choseTypes.size() == 1 && choseTypes.get(0).id == UtilContent.NON);
    }

    private boolean notNullOrEmpty(ArrayList<Mean> means) {
        for (Mean mean : means) {
            if (mean.meanWord == null || mean.meanWord.equals("")) {
                return false;
            }
        }
        return true;
    }

    private boolean notNullOrEmpty(Mean mean) {
        if (mean.meanWord == null || mean.meanWord.equals("")) {
            return false;
        }
        return true;
    }

    private void setView() {
        rcMean = findViewById(R.id.rc_mean);
        cgType = findViewById(R.id.cg_type);
        imHandle = findViewById(R.id.im_handle);
        edEnglish = findViewById(R.id.ed_english);
        txTitle = findViewById(R.id.tx_title);
    }
}
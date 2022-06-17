package com.example.englishnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishnotification.handle.CustomList.MeanAdapter;
import com.example.englishnotification.handle.CustomList.SearchAdapter;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.RelationWord;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.UtilContent;
import com.example.englishnotification.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HandleWordActivity extends AppCompatActivity implements MeanAdapter.RecyclerViewReady, Serializable {

    public static final int ADD = 0;
    public static final int UPDATE = 1;
    public static final int TRANSLATE = 2;

    private RecyclerView rcMean;
    private ArrayList<Type> choseTypes;
    private ArrayList<Tag> choseTags;
    private ChipGroup cgType, cgTag, cgRelated, cgSynonym, cgAntonym;
    private MeanAdapter meanAdapter;
    public ImageView imHandle, imRelated, imSynonym, imAntonym, imDRelated, imDSynonym, imDAntonym, imBack;
    public EditText edEnglish, edRelated, edSynonym, edAntonym;
    public RecyclerView rcRelated, rcSynonym, rcAntonym;
    private TextView txTitle, txRelated, txSynonym, txAntonym;
    private ConstraintLayout ctRelated, ctSynonym, ctAntonym;
    public ArrayList<Word> choseWordRelated, choseWordSynonym, choseWordAntonym;
    public int flag;
    public Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_word);

        setView();

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", UtilContent.NON);
        int idWord = intent.getIntExtra("wordId", UtilContent.NON);
        choseTags = new ArrayList<>();
        choseWordRelated = new ArrayList<>();
        choseWordSynonym = new ArrayList<>();
        choseWordAntonym = new ArrayList<>();

        switch (flag) {
            case ADD:
                txTitle.setText("Add New Word");
                createEmptyListMean(this);
                imHandle.setOnClickListener(addWord());
                createListTypeChip(null);
                createListTagChip(null);
                break;
            case UPDATE:
                word = null;
                for (Word w : MainActivity.listWord) {
                    if (w.id == idWord) {
                        word = w;
                    }
                }
                choseWordRelated.addAll(MainActivity.getWordsByRelations(word, RelationWord.RELATED));
                choseWordSynonym.addAll(MainActivity.getWordsByRelations(word, RelationWord.SYNONYM));
                choseWordAntonym.addAll(MainActivity.getWordsByRelations(word, RelationWord.ANTONYM));
                createListRelationWord(choseWordRelated, imRelated, cgRelated);
                createListRelationWord(choseWordSynonym, imSynonym, cgSynonym);
                createListRelationWord(choseWordAntonym, imAntonym, cgAntonym);

                txTitle.setText("Update Word");
                edEnglish.setText(word.english);
                imHandle.setImageResource(R.drawable.edit);

                ArrayList<Integer> choseTypeChips = new ArrayList<>();
                if (word.means == null) {
                    createEmptyListMean(this);
                } else {
                    createListMean(word.means, choseTypeChips);
                }
                createListTypeChip(choseTypeChips);
                ArrayList<Integer> choseTagChips = new ArrayList<>();
                for (Tag tag : word.tags) {
                    choseTagChips.add(MainActivity.tags.indexOf(tag));
                    choseTags.add(tag);
                }
                createListTagChip(choseTagChips);

                imHandle.setOnClickListener(updateWord(word));
                break;
            case TRANSLATE:
                txTitle.setText("Translate Word");
                imHandle.setImageResource(R.drawable.translate);
                createEmptyListMean(this);
                createListTypeChip(null);
                createListTagChip(null);
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

        ctRelated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edRelated.getVisibility() == View.GONE) {
                    edRelated.setVisibility(View.VISIBLE);
                    rcRelated.setVisibility(View.VISIBLE);
                    cgRelated.setVisibility(View.VISIBLE);
                    txRelated.setVisibility(View.GONE);
                    imDRelated.setVisibility(View.GONE);
                } else {
                    edRelated.setVisibility(View.GONE);
                    rcRelated.setVisibility(View.GONE);
                    imRelated.setVisibility(View.GONE);
                    cgRelated.setVisibility(View.GONE);
                    txRelated.setVisibility(View.VISIBLE);
                    imDRelated.setVisibility(View.VISIBLE);
                    edRelated.setText("");
                    unCheckChip(cgRelated);
                }
            }
        });

        ctSynonym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edSynonym.getVisibility() == View.GONE) {
                    edSynonym.setVisibility(View.VISIBLE);
                    rcSynonym.setVisibility(View.VISIBLE);
                    cgSynonym.setVisibility(View.VISIBLE);
                    txSynonym.setVisibility(View.GONE);
                    imDSynonym.setVisibility(View.GONE);
                } else {
                    edSynonym.setVisibility(View.GONE);
                    rcSynonym.setVisibility(View.GONE);
                    imSynonym.setVisibility(View.GONE);
                    cgSynonym.setVisibility(View.GONE);
                    txSynonym.setVisibility(View.VISIBLE);
                    imDSynonym.setVisibility(View.VISIBLE);
                    edSynonym.setText("");
                    unCheckChip(cgSynonym);
                }
            }
        });

        ctAntonym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edAntonym.getVisibility() == View.GONE) {
                    edAntonym.setVisibility(View.VISIBLE);
                    rcAntonym.setVisibility(View.VISIBLE);
                    cgAntonym.setVisibility(View.VISIBLE);
                    txAntonym.setVisibility(View.GONE);
                    imDAntonym.setVisibility(View.GONE);
                } else {
                    edAntonym.setVisibility(View.GONE);
                    rcAntonym.setVisibility(View.GONE);
                    imAntonym.setVisibility(View.GONE);
                    cgAntonym.setVisibility(View.GONE);
                    txAntonym.setVisibility(View.VISIBLE);
                    imDAntonym.setVisibility(View.VISIBLE);
                    edAntonym.setText("");
                    unCheckChip(cgSynonym);
                }
            }
        });

        edRelated.addTextChangedListener(textWatcher(rcRelated, imRelated, cgRelated, choseWordRelated));
        edSynonym.addTextChangedListener(textWatcher(rcSynonym, imSynonym, cgSynonym, choseWordSynonym));
        edAntonym.addTextChangedListener(textWatcher(rcAntonym, imAntonym, cgAntonym, choseWordAntonym));

        imRelated.setOnClickListener(removeChip(cgRelated, choseWordRelated, edRelated));
        imSynonym.setOnClickListener(removeChip(cgSynonym, choseWordSynonym, edSynonym));
        imAntonym.setOnClickListener(removeChip(cgAntonym, choseWordAntonym, edAntonym));

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void unCheckChip(ChipGroup chipGroup) {
        for (int i = chipGroup.getChildCount() - 1; i >= 0; i--) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(false);
        }
    }

    public View.OnClickListener removeChip(ChipGroup chipGroup, ArrayList<Word> choseWords, EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = chipGroup.getChildCount() - 1; i >= 0; i--) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.isChecked()) {
                        for (Word word : choseWords) {
                            if (word.english.equals(chip.getText().toString())) {
                                choseWords.remove(word);
                                break;
                            }
                        }
                        chipGroup.removeViewAt(i);
                    }
                }
                v.setVisibility(View.GONE);
                editText.setText("");
            }
        };
    }

    public TextWatcher textWatcher(RecyclerView recyclerView, ImageView imageView, ChipGroup chipGroup, ArrayList<Word> choseWords) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<Word> ls = new ArrayList<>();
                for (int i = 0; i < MainActivity.listWord.size() && ls.size() < 10; i++) {
                    Word word = MainActivity.listWord.get(i);
                    if (word.english.toLowerCase().indexOf(s.toString().toLowerCase()) != -1 && s.toString().length() != 0 &&
                            (HandleWordActivity.this.word == null || (HandleWordActivity.this.word != null &&
                                    HandleWordActivity.this.word.id != word.id))) {
                        ls.add(word);
                    }
                }
                SearchAdapter adapter = new SearchAdapter(ls, HandleWordActivity.this, chipGroup, choseWords, imageView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(HandleWordActivity.this));
            }
        };
    }

    public void createListRelationWord(ArrayList<Word> wordRelations, ImageView imDelete, ChipGroup cgRelation) {
        for (Word word : wordRelations) {
            Chip chip = new Chip(this);
            chip.setText(word.english);
            chip.setCheckable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chip.isChecked()) {
                        imDelete.setVisibility(View.VISIBLE);
                    } else {
                        boolean haveCheckChip = false;
                        for (int i = 0; i < cgRelation.getChildCount(); i++) {
                            Chip chip = (Chip) cgRelation.getChildAt(i);
                            Chip c = (Chip) v;
                            if (!chip.getText().toString().equals(c.getText().toString()) && chip.isChecked()) {
                                haveCheckChip = true;
                                break;
                            }
                        }
                        if (!haveCheckChip) {
                            imDelete.setVisibility(View.GONE);
                        }
                    }
                }
            });
            cgRelation.addView(chip);
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

    private void createListTypeChip(ArrayList<Integer> choseChips) {
        for (Type type : MainActivity.types) {
            Chip chip = new Chip(this);
            chip.setText(type.name);
            chip.setCheckable(true);
            if (choseChips != null) {
                for (Integer position : choseChips) {
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

    private void createListTagChip(ArrayList<Integer> choseChips) {
        for (Tag tag : MainActivity.tags) {
            Chip chip = new Chip(this);
            chip.setText(tag.name);
            chip.setCheckable(true);
            if (choseChips != null) {
                for (Integer position : choseChips) {
                    if (position == MainActivity.tags.indexOf(tag)) {
                        chip.setChecked(true);
                    }
                }
            }
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chip.isChecked()) {
                        choseTags.add(tag);
                    } else {
                        choseTags.remove(tag);
                    }
                }
            });
            cgTag.addView(chip);
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
                    Mean mean = new Mean(0, type, holder.edMean.getText().toString().trim(), word.id);
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
                    boolean updateMean = checkNotEqual(means, word.means);
                    if (updateMean) {
                        for (Mean mean : means) {
                            mean.wordId = word.id;
                        }
                        if (word.means != null) {
                            MainActivity.database.deleteMeans(word.id);
                        }
                        MainActivity.database.addMeans(means);
                        word.means = means;
                    }
                    boolean updateTag = checkNotEqual(choseTags, word.tags);
                    if (updateTag) {
                        MainActivity.database.deleteTagWordByWordId(word.id);
                        MainActivity.database.addTagWords(word.id, choseTags);
                        word.tags = choseTags;
                    }
                    ArrayList<Word> relation = new ArrayList<>();
                    relation.addAll(choseWordRelated);
                    relation.addAll(choseWordSynonym);
                    relation.addAll(choseWordAntonym);
                    ArrayList<RelationWord> relationWords = createRelations(word, choseWordRelated, choseWordSynonym, choseWordAntonym);
                    boolean updateRelation = checkNotEqual(word.relationWords, relationWords);
                    if (updateRelation) {
                        if(word.relationWords != null){
                            for (RelationWord relationWord : word.relationWords) {
                                MainActivity.database.deleteRelation(relationWord.id);
                                Word w = null;
                                if(relationWord.wordId == word.id){
                                    w = MainActivity.getWordById(relationWord.relationWordId, MainActivity.listWord);

                                } else {
                                    w = MainActivity.getWordById(relationWord.wordId, MainActivity.listWord);
                                }
                                int indexRel = contain(w.relationWords, relationWord);
                                w.relationWords.remove(indexRel);
                                MainActivity.notifyItemChanged(MainActivity.listWord.indexOf(w));
                            }
                            word.relationWords.clear();
                        }
                        addRelationWord(word, choseWordRelated, RelationWord.RELATED);
                        addRelationWord(word, choseWordSynonym, RelationWord.SYNONYM);
                        addRelationWord(word, choseWordAntonym, RelationWord.ANTONYM);
                    }
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

    public ArrayList<RelationWord> createRelations(Word word, ArrayList<Word> related, ArrayList<Word> synonym, ArrayList<Word> antonym) {
        ArrayList<RelationWord> relationWords = new ArrayList<>();
        for (Word w : related) {
            relationWords.add(new RelationWord(0, word.id, w.id, RelationWord.RELATED));
        }
        for (Word w : synonym) {
            relationWords.add(new RelationWord(0, word.id, w.id, RelationWord.SYNONYM));
        }
        for (Word w : antonym) {
            relationWords.add(new RelationWord(0, word.id, w.id, RelationWord.ANTONYM));
        }
        return relationWords;
    }

    private boolean checkNotEqual(Object o1, Object o2) {
        ArrayList<Object> objects1 = (ArrayList<Object>) o1;
        ArrayList<Object> objects2 = (ArrayList<Object>) o2;
        if(objects1 == null || objects2 == null){
            return true;
        }
        boolean isUpdate = false;
        if (objects1.size() != objects2.size()) {
            isUpdate = true;
        } else {
            for (Object o : objects1) {
                if (!contain(objects2, o)) {
                    isUpdate = true;
                }
            }
        }
        return isUpdate;
    }

    private boolean contain(ArrayList<Object> objects, Object object) {
        for (Object o : objects) {
            if (o instanceof Mean) {
                Mean mean1 = (Mean) o;
                Mean mean2 = (Mean) object;
                if (mean1.type.equals(mean2.type) && mean1.meanWord.equals(mean2.meanWord) && mean1.wordId == mean2.wordId) {
                    return true;
                }
            }
            if (o instanceof RelationWord) {
                RelationWord r1 = (RelationWord) o;
                RelationWord r2 = (RelationWord) object;
                if (((r1.wordId == r2.wordId && r1.relationWordId == r2.relationWordId) ||
                        (r1.wordId == r2.relationWordId && r1.relationWordId == r2.wordId)) &&
                        r1.relationType == r2.relationType) {
                    return true;
                }
            }
            if (o.equals(object)) {
                return true;
            }
        }
        return false;
    }

    private int contain(ArrayList<RelationWord> relationWords, RelationWord relationWord) {
        for (RelationWord r : relationWords) {
            if (((r.wordId == relationWord.wordId && r.relationWordId == relationWord.relationWordId) ||
                    (r.wordId == relationWord.relationWordId && r.relationWordId == relationWord.wordId)) &&
                    r.relationType == relationWord.relationType) {
                return relationWords.indexOf(r);
            }
        }
        return -1;
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
                    MainActivity.database.addTagWords(newWord.id, choseTags);
                    newWord.tags = choseTags;
                    addRelationWord(newWord, choseWordRelated, RelationWord.RELATED);
                    addRelationWord(newWord, choseWordSynonym, RelationWord.SYNONYM);
                    addRelationWord(newWord, choseWordAntonym, RelationWord.ANTONYM);
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

    public void addRelationWord(Word word, ArrayList<Word> words, int type) {
        for (Word w : words) {
            MainActivity.database.addNewRelationWord(word, w, type);
            RelationWord relationWord = MainActivity.database.getNewRelationWord();
            MainActivity.addRelationWord(word, relationWord);
            MainActivity.addRelationWord(w, relationWord);
            MainActivity.notifyItemChanged(MainActivity.listWord.indexOf(w));
        }
    }

    private boolean isTypeNon(ArrayList<Type> choseTypes) {
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
        cgTag = findViewById(R.id.cg_tag);
        imHandle = findViewById(R.id.im_handle);
        edEnglish = findViewById(R.id.ed_english);
        txTitle = findViewById(R.id.tx_title);
        ctRelated = findViewById(R.id.ct_item_related);
        ctAntonym = findViewById(R.id.ct_item_antonym);
        ctSynonym = findViewById(R.id.ct_item_synonym);
        edRelated = findViewById(R.id.ed_related);
        edSynonym = findViewById(R.id.ed_synonym);
        edAntonym = findViewById(R.id.ed_antonym);
        imRelated = findViewById(R.id.im_related_add);
        imSynonym = findViewById(R.id.im_synonym_add);
        imAntonym = findViewById(R.id.im_antonym_add);
        rcRelated = findViewById(R.id.rc_related);
        rcSynonym = findViewById(R.id.rc_synonym);
        rcAntonym = findViewById(R.id.rc_antonym);
        cgRelated = findViewById(R.id.cg_related);
        cgSynonym = findViewById(R.id.cg_synonym);
        cgAntonym = findViewById(R.id.cg_antonym);
        txRelated = findViewById(R.id.tx_related);
        txSynonym = findViewById(R.id.tx_synonym);
        txAntonym = findViewById(R.id.tx_antonym);
        imDRelated = findViewById(R.id.im_related_directional);
        imDSynonym = findViewById(R.id.im_synonym_directional);
        imDAntonym = findViewById(R.id.im_antonym_directional);
        imBack = findViewById(R.id.im_back);
    }
}
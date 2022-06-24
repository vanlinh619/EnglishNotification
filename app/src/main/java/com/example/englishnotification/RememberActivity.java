package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.englishnotification.handle.CustomList.ItemListAdapter;
import com.example.englishnotification.model.RelationWord;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Word;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class RememberActivity extends AppCompatActivity implements ListChipFragment.ClickChipListener {

    private TabLayout tlTag;
    private ImageView imBack;
    private TextView txIdWord, txForget, txDate, txEnglish, txMean;
    private ConstraintLayout ctInformationChip, ctAntonymExpand, ctSynonymExpand, ctRelatedExpand;
    private RecyclerView rcRelated, rcSynonym, rcAntonym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember);

        setView();

        for (Tag tag: MainActivity.tags){
            if(MainActivity.tags.indexOf(tag) == 0){
                createPageChip(tag.name);
            }
            TabLayout.Tab tab = tlTag.newTab();
            tab.setText(tag.name);
            tlTag.addTab(tab);
        }

        tlTag.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                createPageChip(tab.getText().toString().trim());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createPageChip(String tagName){
        ArrayList<Word> words = new ArrayList<>();
        for (Word word: MainActivity.listWord){
            for (Tag tag: word.tags){
                if(tag.name.equals(tagName)){
                    words.add(word);
                    break;
                }
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        ListChipFragment listChipFragment = ListChipFragment.newInstance(words);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_page, listChipFragment);
        transaction.commit();
    }

    private void setView() {
        tlTag = findViewById(R.id.tl_tag);
        imBack = findViewById(R.id.im_back);
        txDate = findViewById(R.id.tx_date_expand);
        txEnglish = findViewById(R.id.tx_english_expand);
        txForget = findViewById(R.id.tx_forget_expand);
        txIdWord = findViewById(R.id.tx_id_expand);
        txMean = findViewById(R.id.tx_vietnamese_expand);
        rcRelated = findViewById(R.id.rc_item_related_expand);
        rcSynonym = findViewById(R.id.rc_item_synonym_expand);
        rcAntonym = findViewById(R.id.rc_item_antonym_expand);
        ctInformationChip = findViewById(R.id.ct_infor_chip);
        ctRelatedExpand = findViewById(R.id.ct_body_item_expand_related);
        ctSynonymExpand = findViewById(R.id.ct_body_item_expand_synonym);
        ctAntonymExpand = findViewById(R.id.ct_body_item_expand_antonym);
    }

    @Override
    public void action(Word word, Chip chip) {

        new AlertDialog.Builder(this)
                .setTitle("Show information")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chip.setChecked(false);
                    }
                })
                .setPositiveButton("Show", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showInforWord(word);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        chip.setChecked(false);
                    }
                })
                .show();

    }

    public void showInforWord(Word word){
        ctInformationChip.setVisibility(View.VISIBLE);
        txDate.setText(word.date);
        txMean.setText(MainActivity.meansToString(word));
        txIdWord.setText(word.id + "");
        txForget.setText((word.forget + 1) + "");
        txEnglish.setText(word.english);

        ArrayList<Word> wordRelated = MainActivity.getWordsByRelations(word, RelationWord.RELATED);
        ArrayList<Word> wordSynonym = MainActivity.getWordsByRelations(word, RelationWord.SYNONYM);
        ArrayList<Word> wordAntonym = MainActivity.getWordsByRelations(word, RelationWord.ANTONYM);

        if(wordRelated.size() > 0){
            ctRelatedExpand.setVisibility(View.VISIBLE);
            ItemListAdapter.addRecycleView(wordRelated, rcRelated, this);
        } else {
            ctRelatedExpand.setVisibility(View.GONE);
        }
        if(wordSynonym.size() > 0){
            ctSynonymExpand.setVisibility(View.VISIBLE);
            ItemListAdapter.addRecycleView(wordSynonym, rcSynonym, this);
        } else {
            ctSynonymExpand.setVisibility(View.GONE);
        }
        if(wordAntonym.size() > 0){
            ctAntonymExpand.setVisibility(View.VISIBLE);
            ItemListAdapter.addRecycleView(wordAntonym, rcAntonym, this);
        } else {
            ctAntonymExpand.setVisibility(View.GONE);
        }
        MainActivity.database.incrementOnceForget(word);
    }

    @Override
    public void hide() {
        ctInformationChip.setVisibility(View.GONE);
    }
}
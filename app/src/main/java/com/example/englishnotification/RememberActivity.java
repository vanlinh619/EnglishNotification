package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Word;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class RememberActivity extends AppCompatActivity {

    private TabLayout tlTag;
    private FrameLayout flPage;
    private ImageView imBack;

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
        flPage = findViewById(R.id.fl_page);
        imBack = findViewById(R.id.im_back);
    }
}
package com.ale.englishnotification;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ale.englishnotification.model.Word;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class ListChipFragment extends Fragment {

    private ArrayList<Word> words;
    private ChipGroup cgWord;
    private CompoundButton buttonViewCache;

    public static ListChipFragment newInstance(ArrayList<Word> words) {
        ListChipFragment fragment = new ListChipFragment();
        Bundle args = new Bundle();
        args.putSerializable("words", words);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            words = (ArrayList<Word>) getArguments().getSerializable("words");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_chip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cgWord = view.findViewById(R.id.cg_word);

        cgWord.removeAllViews();

        for (Word word: words){
            Chip chip = new Chip(getContext());
            chip.setText(word.english);
            chip.setCheckable(true);
            chip.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MainActivity.showAlertDialog(getContext(), "Forget!", word.english);
                    return true;
                }
            });
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Activity activity = getActivity();
                        if(activity instanceof RememberActivity){
                            RememberActivity rememberActivity = (RememberActivity) activity;
                            rememberActivity.action(word, chip);
                            buttonViewCache = buttonView;
                        }
                    } else {
                        Activity activity = getActivity();
                        if(activity instanceof RememberActivity && buttonViewCache.equals(buttonView)){
                            RememberActivity rememberActivity = (RememberActivity) activity;
                            rememberActivity.hide();
                        }
                    }
                }
            });
            cgWord.addView(chip);
        }
    }

    public interface ClickChipListener{
        public void action(Word word, Chip chip);
        public void hide();
    }
}
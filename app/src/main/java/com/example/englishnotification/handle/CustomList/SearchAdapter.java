package com.example.englishnotification.handle.CustomList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.HandleWordActivity;
import com.example.englishnotification.R;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.Word;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Word> words;
    private Context context;
    private ChipGroup chipGroup;
    private ArrayList<Word> choseWords;
    private ImageView imAdd;
    private HandleLastListener handleLastListener;

    public SearchAdapter(ArrayList<Word> words, Context context, ChipGroup chipGroup, ArrayList<Word> choseWords, ImageView imAdd) {
        this.words = words;
        this.context = context;
        this.chipGroup = chipGroup;
        this.choseWords = choseWords;
        this.imAdd = imAdd;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Word word = words.get(position);

        holder.txSearch.setText(word.english);
        if(choseWords.indexOf(word) != -1){
            //holder.ctSearch.setBackgroundResource(R.color.gray);
            holder.imCheck.setVisibility(View.VISIBLE);
        }

        holder.ctSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choseWords.indexOf(word) == -1){
                    Chip chip = new Chip(context);
                    chip.setText(word.english);
                    chip.setCheckable(true);
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(chip.isChecked()){
                                imAdd.setVisibility(View.VISIBLE);
                            } else {
                                boolean haveCheckChip = false;
                                for (int i = 0; i < chipGroup.getChildCount(); i++){
                                    Chip chip = (Chip) chipGroup.getChildAt(i);
                                    Chip c = (Chip) v;
                                    if(!chip.getText().toString().equals(c.getText().toString()) && chip.isChecked()){
                                        haveCheckChip = true;
                                        break;
                                    }
                                }
                                if (!haveCheckChip){
                                    imAdd.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                    chipGroup.addView(chip);
                    choseWords.add(word);
                    if(handleLastListener != null){
                        handleLastListener.addIgnore(word);
                    }
                    //holder.ctSearch.setBackgroundResource(R.color.gray);
                    holder.imCheck.setVisibility(View.VISIBLE);
                } else {
                    //holder.ctSearch.setBackgroundResource(R.color.white);
                    holder.imCheck.setVisibility(View.GONE);
                    removeChip(word);
                    if(handleLastListener != null){
                        handleLastListener.removeIgnore(word);
                    }
                }
            }
        });
    }

    private void removeChip(Word word){
        for (int i = 0; i < chipGroup.getChildCount(); i++){
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if(chip.getText().toString().equals(word.english)){
                chipGroup.removeViewAt(i);
                break;
            }
        }
        choseWords.remove(word);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txSearch;
        public ConstraintLayout ctSearch;
        public ImageView imCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txSearch = itemView.findViewById(R.id.tx_search);
            ctSearch = itemView.findViewById(R.id.ct_item_search);
            imCheck = itemView.findViewById(R.id.im_check);
        }
    }

    public void setHandleLastListener(HandleLastListener handleLastListener){
        this.handleLastListener = handleLastListener;
    }

    public interface HandleLastListener{
        public void removeIgnore(Word word);
        public void addIgnore(Word word);
    }
}

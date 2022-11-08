package com.example.englishnotification.handle.CustomList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.R;
import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Word;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StringHorizontalAdapter extends RecyclerView.Adapter<StringHorizontalAdapter.ViewHolder> {

    ArrayList<Word> words;
    List<Tag> tags;

    public StringHorizontalAdapter(ArrayList<Word> words) {
        this.words = words;
    }

    public StringHorizontalAdapter(List<Tag> tags){
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, parent, false);
        return new StringHorizontalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = "";
        if(words != null) {
            Word word = words.get(position);
            data = word.english;
        } else {
            Tag tag = tags.get(position);
            data = tag.name;
        }

        holder.txString.setText(data);
    }

    @Override
    public int getItemCount() {
        if(words != null){
            return words.size();
        } else {
            return tags.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txString = itemView.findViewById(R.id.tx_string);
        }
    }
}

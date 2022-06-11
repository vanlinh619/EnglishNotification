package com.example.englishnotification.handle.CustomList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.R;
import com.example.englishnotification.model.Word;

import java.util.ArrayList;

public class StringHorizontalAdapter extends RecyclerView.Adapter<StringHorizontalAdapter.ViewHolder> {

    ArrayList<Word> words;

    public StringHorizontalAdapter(ArrayList<Word> words) {
        this.words = words;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string, parent, false);
        return new StringHorizontalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = words.get(position);

        holder.txString.setText(word.english);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txString = itemView.findViewById(R.id.tx_string);
        }
    }
}

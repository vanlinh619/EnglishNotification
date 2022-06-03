package com.example.englishnotification.handle.CustomList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.R;
import com.example.englishnotification.model.Mean;
import com.example.englishnotification.model.Type;

import java.util.ArrayList;

public class MeanAdapter extends RecyclerView.Adapter<MeanAdapter.ViewHolder> {

    private ArrayList<Type> choTypes;
    private Context context;

    public MeanAdapter(ArrayList<Type> choTypes, Context context) {
        this.choTypes = choTypes;
        this.context = context;
    }

    @NonNull
    @Override
    public MeanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mean, parent, false);
        return new MeanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeanAdapter.ViewHolder holder, int position) {
        Type type = choTypes.get(position);

        holder.edMean.setHint(type.name);
    }

    @Override
    public int getItemCount() {
        return choTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText edMean;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            edMean = itemView.findViewById(R.id.ed_mean);
        }
    }
}

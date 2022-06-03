package com.example.englishnotification.handle.CustomList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.HandleWordActivity;
import com.example.englishnotification.R;
import com.example.englishnotification.model.Type;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private ArrayList<Type> types;
    private ArrayList<Type> choseTypes;
    private Context context;

    public TypeAdapter(ArrayList<Type> types, ArrayList<Type> choseTypes, Context context) {
        this.types = types;
        this.choseTypes = choseTypes;
        this.context = context;
    }

    @NonNull
    @Override
    public TypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        return new TypeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeAdapter.ViewHolder holder, int position) {
        Type type = types.get(position);
        holder.chType.setText(type.name);
        holder.chType.setCheckable(true);

        holder.chType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.chType.isChecked()){
                    holder.chType.setChecked(false);
                } else {
                    holder.chType.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Chip chType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chType = itemView.findViewById(R.id.ch_type);
        }
    }
}

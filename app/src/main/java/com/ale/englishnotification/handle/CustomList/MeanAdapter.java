package com.ale.englishnotification.handle.CustomList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ale.englishnotification.HandleWordActivity;
import com.ale.englishnotification.MainActivity;
import com.ale.englishnotification.model.Mean;
import com.ale.englishnotification.model.Type;
import com.ale.englishnotification.R;

import java.util.ArrayList;

public class MeanAdapter extends RecyclerView.Adapter<MeanAdapter.ViewHolder> {

    private ArrayList<Type> choTypes;
    private ArrayList<Mean> means;
    private int flag;
    private Context context;

    public MeanAdapter(int flag, ArrayList<Mean> means, ArrayList<Type> choTypes) {
        this.means = means;
        this.flag = flag;
        this.choTypes = choTypes;
    }

    public MeanAdapter(int flag, ArrayList<Type> choTypes, Context context) {
        this.choTypes = choTypes;
        this.flag = flag;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mean, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Type type = choTypes.get(position);
        holder.edMean.setHint(type.name);
        holder.edMean.setText("");
        holder.edMean.setOnTouchListener(MainActivity.clearEdittext());
        if (flag == HandleWordActivity.UPDATE) {
            for (Mean mean: means){
                if (mean.type.id == type.id){
                    holder.edMean.setText(mean.meanWord);
                }
            }
        } else if (flag == HandleWordActivity.TRANSLATE && position == 0 && context instanceof HandleWordActivity){
            HandleWordActivity handleWordActivity = (HandleWordActivity) context;
            handleWordActivity.ready(holder.edMean);
        }
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

    public interface RecyclerViewReady{
        public void ready(EditText editText);
    }
}

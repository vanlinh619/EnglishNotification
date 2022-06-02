package com.example.englishnotification.handle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.R;
import com.example.englishnotification.model.ItemDataExample;

import java.util.ArrayList;

public class ItemListExampleAdapter extends RecyclerView.Adapter<ItemListExampleAdapter.ViewHolder> {

    ArrayList<ItemDataExample> listData;
    MainActivity mainActivity;

    public ItemListExampleAdapter(ArrayList<ItemDataExample> listData, MainActivity mainActivity) {
        this.listData = listData;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ItemListExampleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_example, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListExampleAdapter.ViewHolder holder, int position) {
        ItemDataExample itemDataExample = listData.get(position);
        holder.txId.setText(position + "");
        holder.txVietnamese.setText(itemDataExample.vietnamese);
        holder.txEnglish.setText(itemDataExample.english);
        holder.imSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imSpeak.setImageResource(R.drawable.volume_blue);
                holder.imSpeak.setEnabled(false);
                mainActivity.speak(itemDataExample.english, itemDataExample.vietnamese);
                CountDownTimer countDownTimer = new CountDownTimer(2000, 2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        holder.imSpeak.setImageResource(R.drawable.volume);
                        holder.imSpeak.setEnabled(true);
                    }
                }.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txId;
        public TextView txEnglish;
        public TextView txVietnamese;
        public ImageView imSpeak;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txId = itemView.findViewById(R.id.tx_example_id);
            txEnglish = itemView.findViewById(R.id.tx_example_english);
            txVietnamese = itemView.findViewById(R.id.tx_example_vietnamese);
            imSpeak = itemView.findViewById(R.id.im_example_speak);
        }
    }
}

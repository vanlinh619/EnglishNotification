package com.example.englishnotification;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private ArrayList<ItemData> listData;
    private ItemListAdapter.ViewHolder viewHolder;

    public ItemListAdapter(ArrayList<ItemData> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        String id = "";
        if(viewHolder != null){
           id  = viewHolder.txId.getText().toString();
        }

        String ii = listData.get(position).id + "";
        if(viewHolder != null && !viewHolder.txId.getText().toString().equals(listData.get(position).id + "")){
            shrinkView(holder);
        }

        holder.txEnglish.setText(listData.get(position).english);
        holder.txVietnamese.setText(listData.get(position).vietnamese);
        holder.txDate.setText(listData.get(position).date);
        holder.txId.setText(listData.get(position).id + "");

        holder.txEnglishExpand.setText(listData.get(position).english);
        holder.txVietnameseExpand.setText(listData.get(position).vietnamese);
        holder.txDateExpand.setText(listData.get(position).date);
        holder.txIdExpand.setText(listData.get(position).id + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder != null && !viewHolder.equals(holder) ){
                    shrinkView(viewHolder);
                }
                if(holder.ctItemShrink.getVisibility() == View.VISIBLE){
                    expandView(holder);
                }else {
                    shrinkView(holder);
                }
                viewHolder = holder;
            }
        });
    }

    private void expandView(ItemListAdapter.ViewHolder holder){
        holder.ctItemShrink.setVisibility(View.GONE);
        holder.ctItemExpand.setVisibility(View.VISIBLE);
    }

    public void shrinkView(ItemListAdapter.ViewHolder holder){
        holder.ctItemExpand.setVisibility(View.GONE);
        holder.ctItemShrink.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout ctItemShrink;
        public ConstraintLayout ctItemExpand;

        public TextView txEnglish;
        public TextView txVietnamese;
        public TextView txDate;
        public TextView txId;

        public TextView txEnglishExpand;
        public TextView txVietnameseExpand;
        public TextView txDateExpand;
        public TextView txIdExpand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ctItemShrink = itemView.findViewById(R.id.ct_item_shrink);
            ctItemExpand = itemView.findViewById(R.id.ct_item_expand);

            txEnglish = itemView.findViewById(R.id.tx_english);
            txVietnamese = itemView.findViewById(R.id.tx_vietnamese);
            txDate = itemView.findViewById(R.id.tx_date);
            txId = itemView.findViewById(R.id.tx_id);

            txEnglishExpand = itemView.findViewById(R.id.tx_english_expand);
            txVietnameseExpand = itemView.findViewById(R.id.tx_vietnamese_expand);
            txDateExpand = itemView.findViewById(R.id.tx_date_expand);
            txIdExpand = itemView.findViewById(R.id.tx_id_expand);
        }
    }
}

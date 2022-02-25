package com.example.englishnotification;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements Serializable {

    private ArrayList<ItemData> listData;
    private ItemListAdapter.ViewHolder viewHolder;
    private MainActivity mainActivity;
    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public ItemListAdapter(ArrayList<ItemData> listData, MainActivity mainActivity) {
        this.listData = listData;
        this.mainActivity = mainActivity;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(viewHolder != null && !viewHolder.txId.getText().toString().equals(listData.get(position).id + "")){
            shrinkView(holder);
        }

        ItemData itemData = listData.get(position);

        holder.txEnglish.setText(itemData.english);
        holder.txVietnamese.setText(itemData.vietnamese);
        holder.txDate.setText(itemData.date);
        holder.txId.setText(itemData.id + "");

        holder.txEnglishExpand.setText(itemData.english);
        holder.txVietnameseExpand.setText(itemData.vietnamese);
        holder.txDateExpand.setText(itemData.date);
        holder.txIdExpand.setText(itemData.id + "");

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

        holder.imUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = mainActivity.getSupportFragmentManager();
                DialogAddEditWord dialogAddEditWord = (DialogAddEditWord) DialogAddEditWord
                        .newInstance(mainActivity, listData.get(position));
                dialogAddEditWord.show(fm, "fragment_edit_name");
            }
        });

        holder.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Delete!")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mainActivity.database.deleteData(listData.get(position).id);
                                mainActivity.listData.remove(position);
                                mainActivity.reloadList();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        holder.imSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.textToSpeechEnglish = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            mainActivity.textToSpeechEnglish.setLanguage(Locale.ENGLISH);
                            mainActivity.textToSpeechEnglish.speak(itemData.english, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });

                mainActivity.textToSpeechVietnamese = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            mainActivity.textToSpeechVietnamese.speak(itemData.vietnamese, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });
            }
        });

        holder.imTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.translator.translate(itemData.english)
                        .addOnSuccessListener(
                                new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        new AlertDialog.Builder(mainActivity)
                                                .setTitle(itemData.english)
                                                .setMessage(o.toString())
                                                .setNegativeButton("Cancel", null)
                                                .show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error.
                                        Toast.makeText(mainActivity, "Please wait while app download file language!", Toast.LENGTH_LONG).show();
                                    }
                                });
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

        public ImageView imUpdate;
        public ImageView imDelete;

        public ImageView imTranslate;
        public ImageView imSpeak;

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

            imDelete = itemView.findViewById(R.id.im_delete);
            imUpdate = itemView.findViewById(R.id.im_update);

            imTranslate = itemView.findViewById(R.id.im_translate);
            imSpeak = itemView.findViewById(R.id.im_speak);
        }
    }
}

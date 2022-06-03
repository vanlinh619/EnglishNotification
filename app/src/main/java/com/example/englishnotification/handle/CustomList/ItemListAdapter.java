package com.example.englishnotification.handle.CustomList;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
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

import com.example.englishnotification.DialogAddEditWord;
import com.example.englishnotification.DialogExample;
import com.example.englishnotification.MainActivity;
import com.example.englishnotification.R;
import com.example.englishnotification.handle.Example;
import com.example.englishnotification.model.Word;
import com.example.englishnotification.model.ItemDataExample;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements Serializable {

    private ArrayList<Word> listWord;
    private ItemListAdapter.ViewHolder viewHolder;
    private MainActivity mainActivity;
    private ArrayList<ItemDataExample> listExample;

    public ItemListAdapter(ArrayList<Word> listWord, MainActivity mainActivity) {
        this.listWord = listWord;
        this.mainActivity = mainActivity;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (viewHolder != null && !viewHolder.txId.getText().toString().equals(listWord.get(position).id + "")) {
            shrinkView(holder);
        }

        Word word = listWord.get(position);

        holder.txEnglish.setText(word.english);
        //holder.txVietnamese.setText(word.vietnamese);
        holder.txDate.setText(word.date);
        holder.txId.setText((listWord.size() - listWord.indexOf(word)) + "");

        holder.txEnglishExpand.setText(word.english);
        //holder.txVietnameseExpand.setText(word.vietnamese);
        holder.txDateExpand.setText(word.date);
        holder.txIdExpand.setText((listWord.size() - listWord.indexOf(word)) + "");

        if (word.notification == 0) {
            holder.imNotification.setImageResource(R.drawable.notification);
        } else {
            holder.imNotification.setImageResource(R.drawable.notification_blue);
        }

        if (mainActivity.config.autoNotify == 0) {
            holder.imAutoNotification.setVisibility(View.GONE);
        } else {
            holder.imAutoNotification.setVisibility(View.VISIBLE);
            if (word.auto == 0) {
                holder.imAutoNotification.setImageResource(R.drawable.bot);
            } else {
                holder.imAutoNotification.setImageResource(R.drawable.bot_blue);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder != null && !viewHolder.equals(holder)) {
                    shrinkView(viewHolder);
                }
                if (holder.ctItemShrink.getVisibility() == View.VISIBLE) {
                    expandView(holder);
                } else {
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
                        .newInstance(mainActivity, listWord.get(position), DialogAddEditWord.UPDATE);
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
                                mainActivity.database.deleteData(listWord.get(position).id);
                                mainActivity.listWord.remove(position);
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
                holder.imSpeak.setImageResource(R.drawable.volume_blue);
                holder.imSpeak.setEnabled(false);
                //mainActivity.speak(word.english, word.vietnamese);
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

        holder.imTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.translatorEnglish.translate(word.english)
                        .addOnSuccessListener(
                                new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
//                                        if (o.toString().toLowerCase().equals(word.vietnamese.toLowerCase())) {
//                                            new AlertDialog.Builder(mainActivity)
//                                                    .setTitle(word.english)
//                                                    .setMessage(o.toString())
//                                                    .setNegativeButton("Close", null)
//                                                    .show();
//                                        } else {
//                                            new AlertDialog.Builder(mainActivity)
//                                                    .setTitle(word.english)
//                                                    .setMessage(o.toString())
//                                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                                                        @RequiresApi(api = Build.VERSION_CODES.N)
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//                                                            String date = format.format(new Date());
//                                                            word.date = date;
//                                                            word.vietnamese = o.toString();
//                                                            mainActivity.database.updateWord(word);
//                                                            for (Word item : mainActivity.listWord) {
//                                                                if (item.id == word.id) {
//                                                                    item.english = word.english;
//                                                                    item.vietnamese = word.vietnamese;
//                                                                    item.date = word.date;
//                                                                    break;
//                                                                }
//                                                            }
//                                                            mainActivity.reloadList();
//                                                            dialog.dismiss();
//                                                        }
//                                                    })
//                                                    .setNegativeButton("Close", null)
//                                                    .show();
//                                        }
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

        holder.imNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (word.notification == 0) {
                    holder.imNotification.setImageResource(R.drawable.notification_blue);
                    word.notification = 1;
                    mainActivity.setRepeatAlarm(word);
                } else {
                    holder.imNotification.setImageResource(R.drawable.notification);
                    word.notification = 0;
                    mainActivity.destroyRepeatAlarm(word);
                }
                mainActivity.database.updateWord(word);
            }
        });

        holder.imAutoNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (word.auto == 0) {
                    holder.imAutoNotification.setImageResource(R.drawable.bot_blue);
                    word.auto = 1;
                } else {
                    holder.imAutoNotification.setImageResource(R.drawable.bot);
                    word.auto = 0;
                }
                mainActivity.database.updateWord(word);
            }
        });

        holder.imExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Example example = new Example(new Example.ExampleListener() {
                    @Override
                    public void translate(Elements elements) {
                        listExample = new ArrayList<>();
                        DataLoop dataLoop = new DataLoop();
                        translateElement(dataLoop, elements, listExample, word.english);
                    }
                }, mainActivity);
                example.execute(word.english);
            }
        });

        holder.imCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EnglishNotification", word.english);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mainActivity, "Copied: " + word.english, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void translateElement(DataLoop dataLoop, Elements elements, ArrayList<ItemDataExample> listExample, String english) {
        if (dataLoop.i < 10 && dataLoop.i < elements.size()) {
            String text = elements.get(dataLoop.i).text();
            ItemDataExample itemDataExample = new ItemDataExample(text);
            mainActivity.translatorEnglish.translate(text)
                    .addOnSuccessListener(
                            new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    itemDataExample.vietnamese = o.toString();
                                    listExample.add(itemDataExample);
                                    dataLoop.i++;
                                    translateElement(dataLoop, elements, listExample, english);
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
        } else {
            FragmentManager fm = mainActivity.getSupportFragmentManager();
            DialogExample dialogExample = (DialogExample) DialogExample.newInstance(mainActivity, listExample, english);
            dialogExample.show(fm, "fragment_edit_name");
        }
    }

    private void expandView(ItemListAdapter.ViewHolder holder) {
        holder.ctItemShrink.setVisibility(View.GONE);
        holder.ctItemExpand.setVisibility(View.VISIBLE);
    }

    public void shrinkView(ItemListAdapter.ViewHolder holder) {
        holder.ctItemExpand.setVisibility(View.GONE);
        holder.ctItemShrink.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listWord.size();
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

        public ImageView imNotification;
        public ImageView imAutoNotification;

        public ImageView imExample;
        public ImageView imCopy;

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

            imNotification = itemView.findViewById(R.id.im_notification);
            imAutoNotification = itemView.findViewById(R.id.im_notification_auto_random);

            imExample = itemView.findViewById(R.id.im_example);
            imCopy = itemView.findViewById(R.id.im_copy);
        }
    }

    public class DataLoop {
        public int i;

        public DataLoop() {
            this.i = 0;
        }
    }
}
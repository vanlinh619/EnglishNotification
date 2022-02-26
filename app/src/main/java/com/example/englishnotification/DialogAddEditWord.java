package com.example.englishnotification;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DialogAddEditWord extends DialogFragment {

    private EditText edEnglish;
    private EditText edVietnamese;
    private Button btAdd;
    private Button btCancel;
    private TextView txTitle;
    private MainActivity mainActivity;

    public static DialogAddEditWord newInstance(MainActivity mainActivity) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        frag.setArguments(arg);
        return frag;
    }

    public static DialogAddEditWord newInstance(MainActivity mainActivity, ItemData itemData) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        arg.putSerializable("itemData", itemData);
        frag.setArguments(arg);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_word, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setView(view);

        Bundle bundle = getArguments();
        mainActivity = (MainActivity) bundle.getSerializable("mainActivity");
        ItemData itemData = (ItemData) bundle.getSerializable("itemData");

        if(itemData != null){
            btAdd.setText("Update");
            txTitle.setText("Edit Word");
            edEnglish.setText(itemData.english);
            edVietnamese.setText(itemData.vietnamese);
            btAdd.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    String english = edEnglish.getText().toString().trim();
                    String vietnamese = edVietnamese.getText().toString().trim();
                    if(!english.equals("") && !wordExists(english)){
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String date = format.format(new Date());
                        itemData.date = date;
                        itemData.english = english;
                        itemData.vietnamese = vietnamese;
                        mainActivity.database.updateData(itemData);
                        for (ItemData item: mainActivity.listData){
                            if(item.id == itemData.id){
                                item.english = itemData.english;
                                item.vietnamese = itemData.vietnamese;
                                item.date = itemData.date;
                                break;
                            }
                        }
                        mainActivity.reloadList();
                        dismiss();
                    } else {
                        if(english.equals("")){
                            Toast.makeText(mainActivity, "Please fill english text!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mainActivity, "This word already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            btAdd.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    String english = edEnglish.getText().toString().trim();
                    String vietnamese = edVietnamese.getText().toString().trim();
                    if(!english.equals("") && !wordExists(english)){
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String date = format.format(new Date());
                        ItemData itemData = new ItemData(0, date, english, vietnamese, 0, 1);
                        mainActivity.database.addData(itemData);
                        ItemData item = mainActivity.database.getNewItem();
                        mainActivity.listData.add(0, item);
                        mainActivity.reloadList();
                        mainActivity.startAlarm(mainActivity.database.getDataForNotification());
                        dismiss();
                    } else {
                        if(english.equals("")){
                            Toast.makeText(mainActivity, "Please fill english text!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mainActivity, "This word already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean wordExists(String english){
        for (ItemData itemData: mainActivity.listData){
            if(itemData.english.toLowerCase().equals(english.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private void setView(View view) {
        edEnglish = view.findViewById(R.id.ed_english);
        edVietnamese = view.findViewById(R.id.ed_vietnamese);
        btAdd = view.findViewById(R.id.bt_add);
        btCancel = view.findViewById(R.id.bt_cancel);
        txTitle = view.findViewById(R.id.tx_add_new_word);
    }


}

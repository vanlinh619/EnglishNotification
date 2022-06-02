package com.example.englishnotification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.englishnotification.model.ItemData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogAddEditWord extends DialogFragment {

    private EditText edEnglish;
    private EditText edVietnamese;
    private Button btAdd;
    private Button btCancel;
    private TextView txTitle;
    private MainActivity mainActivity;

    public static DialogAddEditWord newInstance(MainActivity mainActivity, ItemData itemData, int flags) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        arg.putSerializable("itemData", itemData);
        arg.putSerializable("flags", flags);
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
        int flags = (int) bundle.getSerializable("flags");

        if (flags == MainActivity.UPDATE) {
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
                    if (!english.equals("") && !wordExists(english, itemData.id)) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String date = format.format(new Date());
                        itemData.date = date;
                        itemData.english = english;
                        itemData.vietnamese = vietnamese;
                        mainActivity.database.updateData(itemData);
                        for (ItemData item : mainActivity.listData) {
                            if (item.id == itemData.id) {
                                item.english = itemData.english;
                                item.vietnamese = itemData.vietnamese;
                                item.date = itemData.date;
                                break;
                            }
                        }
                        mainActivity.reloadList();
                        dismiss();
                    } else {
                        if (english.equals("")) {
                            Toast.makeText(mainActivity, "Please fill english text!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mainActivity, "This word already exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else if (flags == MainActivity.ADD_WORD) {
            btAdd.setOnClickListener(addWord());
        } else if (flags == MainActivity.TRANSLATE){
            txTitle.setText("Translate Word");
            btAdd.setText("Translate");
            edEnglish.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("") && !edVietnamese.getText().toString().trim().equals("")) {
                        btAdd.setText("Add");
                        btAdd.setOnClickListener(addWord());
                    } else {
                        btAdd.setText("Translate");
                        btAdd.setOnClickListener(translateWord());
                    }
                }
            });
            edVietnamese.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals("") && !edEnglish.getText().toString().trim().equals("")) {
                        btAdd.setText("Add");
                        btAdd.setOnClickListener(addWord());
                    } else {
                        btAdd.setText("Translate");
                        btAdd.setOnClickListener(translateWord());
                    }
                }
            });
            btAdd.setOnClickListener(translateWord());
        } else if (flags == MainActivity.GAME){
            txTitle.setText("Check Old Word");
            btAdd.setText("Check");
            edEnglish.setText(itemData.english);
            btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    DialogInterface.OnClickListener listenerClose = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainActivity.shrinkButtonSearch();
                        }
                    };
                    DialogInterface.OnClickListener listenerContinue = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mainActivity.showDialogCheckWord();
                        }
                    };
                    mainActivity.setTextForSearch(edEnglish.getText().toString().trim());
                    String title = "";
                    if(edVietnamese.getText().toString().trim().toLowerCase().equals(itemData.vietnamese.toLowerCase())){
                        title = "Exact!";
                    } else {
                        title = "Incorrect!";
                    }
                    new AlertDialog.Builder(mainActivity)
                            .setTitle(title)
                            .setMessage(itemData.english + " : " + itemData.vietnamese)
                            .setNegativeButton("Close", listenerClose)
                            .setPositiveButton("Continue", listenerContinue)
                            .show();
                }
            });
        }

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        edEnglish.setOnTouchListener(delete());
        edVietnamese.setOnTouchListener(delete());
    }

    private View.OnTouchListener delete(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                EditText editText = (EditText) v;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        editText.setText("");
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private View.OnClickListener translateWord() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                String vietnamese = edVietnamese.getText().toString().trim();
                if (!english.equals("") && vietnamese.equals("")) {
                    mainActivity.translatorEnglish.translate(english)
                            .addOnSuccessListener(
                                    new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            edVietnamese.setText(o.toString());
                                            btAdd.setText("Add");
                                            btAdd.setOnClickListener(addWord());
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
                } else if (!vietnamese.equals("") && english.equals("")) {
                    mainActivity.translatorVietnamese.translate(vietnamese)
                            .addOnSuccessListener(
                                    new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            edEnglish.setText(o.toString());
                                            btAdd.setText("Add");
                                            btAdd.setOnClickListener(addWord());
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
                } else if (english.equals("") && vietnamese.equals("")) {
                    Toast.makeText(mainActivity, "Please fill text!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private View.OnClickListener addWord() {
        return new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String english = edEnglish.getText().toString().trim();
                String vietnamese = edVietnamese.getText().toString().trim();
                if (!english.equals("") && !wordExists(english, -1)) {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    String date = format.format(new Date());
                    ItemData itemData = new ItemData(0, date, english, vietnamese, 0, 1, 1, "",
                            "", "", "", "", 0);
                    mainActivity.database.addData(itemData);
                    ItemData item = mainActivity.database.getNewItem();
                    mainActivity.listData.add(0, item);
                    mainActivity.reloadList();
                    dismiss();
                } else {
                    if (english.equals("")) {
                        Toast.makeText(mainActivity, "Please fill english text!", Toast.LENGTH_SHORT).show();
                    } else {
                        dismiss();
                        mainActivity.setTextForSearch(edEnglish.getText().toString().trim());
                        mainActivity.showAlertDialog(mainActivity, "This word already exists!");
                        mainActivity.expandButtonSearch();
                    }
                }
            }
        };
    }

    private boolean wordExists(String english, int id) {
        for (ItemData itemData : mainActivity.listData) {
            if (itemData.english.toLowerCase().equals(english.toLowerCase()) && id != itemData.id) {
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

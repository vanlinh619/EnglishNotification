package com.example.englishnotification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.englishnotification.model.Tag;
import com.example.englishnotification.model.Type;
import com.example.englishnotification.model.UtilContent;
import com.example.englishnotification.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogAddEditWord extends DialogFragment {

    public static final int ADD_WORD = 6;
    public static final int TRANSLATE = 7;
    public static final int GAME = 8;
    public static final int UPDATE = 9;
    public static final int ADD_TYPE = 10;
    public static final int ADD_TAG = 11;

    private EditText edEnglish;
    private EditText edVietnamese;
    private Button btAdd;
    private Button btCancel;
    private TextView txTitle;
    private MainActivity mainActivity;

    public static DialogAddEditWord newInstance(MainActivity mainActivity, Word word, int flags) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        arg.putSerializable("itemData", word);
        arg.putSerializable("flags", flags);
        frag.setArguments(arg);
        return frag;
    }

    public static DialogAddEditWord newInstance(MainActivity mainActivity, Type type, int flags) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
        arg.putSerializable("mainActivity", mainActivity);
        arg.putSerializable("type", type);
        arg.putSerializable("flags", flags);
        frag.setArguments(arg);
        return frag;
    }

    public static DialogAddEditWord newInstance(int flags) {
        DialogAddEditWord frag = new DialogAddEditWord();
        Bundle arg = new Bundle();
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
        Word word = (Word) bundle.getSerializable("itemData");
        int flags = (int) bundle.getSerializable("flags");

        if (flags == UPDATE) {
            btAdd.setText("Update");
            txTitle.setText("Edit Word");
            edEnglish.setText(word.english);
//            edVietnamese.setText(word.vietnamese);
            btAdd.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    String english = edEnglish.getText().toString().trim();
                    String vietnamese = edVietnamese.getText().toString().trim();
                    if (!english.equals("") && !wordExists(english, word.id)) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String date = format.format(new Date());
                        word.date = date;
                        word.english = english;
//                        word.vietnamese = vietnamese;
                        mainActivity.database.updateWord(word);
                        for (Word item : mainActivity.listWord) {
                            if (item.id == word.id) {
                                item.english = word.english;
//                                item.vietnamese = word.vietnamese;
                                item.date = word.date;
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
        } else if (flags == ADD_WORD) {
            btAdd.setOnClickListener(addWord());
        } else if (flags == TRANSLATE){
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
        } else if (flags == GAME){
            txTitle.setText("Check Old Word");
            btAdd.setText("Check");
            edEnglish.setText(word.english);
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
//                    if(edVietnamese.getText().toString().trim().toLowerCase().equals(word.vietnamese.toLowerCase())){
//                        title = "Exact!";
//                    } else {
//                        title = "Incorrect!";
//                    }
                    new AlertDialog.Builder(mainActivity)
                            .setTitle(title)
//                            .setMessage(word.english + " : " + word.vietnamese)
                            .setNegativeButton("Close", listenerClose)
                            .setPositiveButton("Continue", listenerContinue)
                            .show();
                }
            });
        } else if (flags == ADD_TYPE){
            txTitle.setText("Add Type");
            edVietnamese.setVisibility(View.GONE);
            edEnglish.setHint("Type");
            edEnglish.setCompoundDrawables(null, null, null, null);
            btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = edEnglish.getText().toString().trim();
                    if (!type.equals("") && !MainActivity.typeExists(type, UtilContent.NON)) {
                        MainActivity.database.addNewType(new Type(0, type));
                        Type t = MainActivity.database.getNewType();
                        MainActivity.types.add(t);
                        Activity activity = getActivity();
                        if (activity instanceof OptionActivity){
                            OptionActivity optionActivity = (OptionActivity) activity;
                            optionActivity.createNewTypeChip();
                        }
                        Toast.makeText(getContext(), type + " Added!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        if (type.equals("")){
                            Toast.makeText(getContext(), "Please fill text!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Type already exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else if (flags == ADD_TAG){
            txTitle.setText("Add Tag");
            edVietnamese.setVisibility(View.GONE);
            edEnglish.setHint("Tag");
            edEnglish.setCompoundDrawables(null, null, null, null);
            btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = edEnglish.getText().toString().trim();
                    if (!tag.equals("") && !MainActivity.tagExists(tag, UtilContent.NON)) {
                        MainActivity.database.addNewTag(new Tag(0, tag));
                        Tag t = MainActivity.database.getNewTag();
                        MainActivity.tags.add(t);
                        Activity activity = getActivity();
                        if (activity instanceof OptionActivity){
                            OptionActivity optionActivity = (OptionActivity) activity;
                            optionActivity.createNewTagChip();
                        }
                        Toast.makeText(getContext(), tag + " Added!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        if (tag.equals("")){
                            Toast.makeText(getContext(), "Please fill text!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Tag already exist!", Toast.LENGTH_SHORT).show();
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

//        edEnglish.setOnTouchListener(delete());
//        edVietnamese.setOnTouchListener(delete());
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
                    Word word = new Word(0, date, english, 0, 1, 1, 0);
                    mainActivity.database.addNewWord(word);
                    Word item = mainActivity.database.getNewWord();
                    mainActivity.listWord.add(0, item);
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
        for (Word word : mainActivity.listWord) {
            if (word.english.toLowerCase().equals(english.toLowerCase()) && id != word.id) {
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

package com.example.englishnotification.handle;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translator;

public class Translate {
//    public static void englishToVietnamese(Translator translator, String english, EditText editText){
//        translator.translate(english)
//                .addOnSuccessListener(
//                        new OnSuccessListener() {
//                            @Override
//                            public void onSuccess(Object o) {
//                                editText.setText(o.toString());
//                                btAdd.setText("Add");
//                                btAdd.setOnClickListener(addWord());
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Error.
//                                Toast.makeText(mainActivity, "Please wait while app download file language!", Toast.LENGTH_LONG).show();
//                            }
//                        });
//    }
}

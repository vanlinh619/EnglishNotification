package com.example.englishnotification.handle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.englishnotification.MainActivity;
import com.example.englishnotification.handle.CustomList.ItemListAdapter;
import com.example.englishnotification.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Translate extends AsyncTask<String, Void, String> {

    private String url = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=%s&op=translate";
    private Activity activity;
    private Word word;
    private AlertDialog alertDialog;
    private boolean isTranslate;
    private int position;

    public Translate(Word word, Activity activity, int position){
        this.activity = activity;
        this.word = word;
        isTranslate = false;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        alertDialog = new AlertDialog.Builder(activity)
                .setTitle("Translating...")
                .setNegativeButton("Hide", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... strings) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Document document = Jsoup.connect(String.format(url, word.english)).get();
            Elements elements = document.getElementsByClass("ryNqvb");
            Log.d("AAA", elements.size() + "");
            elements.forEach(element -> {
                Log.d("AAA1", elements.toString());
                stringBuilder.append(element.text());
                stringBuilder.append("\n");
            });
//            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        alertDialog.cancel();
        if (!isTranslate) {
            new AlertDialog.Builder(activity)
                    .setTitle("Couldn't translate!")
                    .setNegativeButton("Close", null)
                    .show();
        } else {
            ItemListAdapter.updateWordTranslated(this.word, s, activity, position);
        }
    }
}

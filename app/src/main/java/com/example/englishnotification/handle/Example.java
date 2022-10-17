package com.example.englishnotification.handle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import java.io.IOException;

public class Example extends AsyncTask<String, Void, String> {

    private String url = "https://sentence.yourdictionary.com/";
    private ExampleListener exampleListener;
    private Context context;
    private AlertDialog alertDialog;
    private boolean example;

    public Example(ExampleListener exampleListener, Context context) {
        this.exampleListener = exampleListener;
        this.context = context;
        example = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        alertDialog = new AlertDialog.Builder(context)
                .setTitle("Looking for examples...")
                .setNegativeButton("Hide", null)
                .show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String word = strings[0];
        try {
            Document document = Jsoup.connect(url + word).get();
            Elements elements = document.getElementsByClass("sentence-item__text");
            exampleListener.translate(elements);
        } catch (IOException e) {
            e.printStackTrace();
            example = false;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        alertDialog.cancel();
        if (example == false) {
            new AlertDialog.Builder(context)
                    .setTitle("Couldn't find an example!")
                    .setNegativeButton("Close", null)
                    .show();
        }
    }

    public interface ExampleListener {
        public void translate(Elements elements);
    }
}

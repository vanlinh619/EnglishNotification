package com.example.englishnotification;

import android.os.AsyncTask;

public class Example extends AsyncTask<String, String, String> {

    private String url = "https://dictionary.cambridge.org/dictionary/english/";

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}

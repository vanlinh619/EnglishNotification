package com.example.englishnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishnotification.handle.CustomList.ItemListAdapter;

public class NetworkActivity extends AppCompatActivity {

    private ImageView imBack;
    private WebView wvNetWork;
    private String url = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=%s&op=translate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        MainActivity.hideSystemBar(this);

        Intent intent = getIntent();
        String english = intent.getStringExtra(ItemListAdapter.ENGLISH);

        setView();

        imBack.setOnClickListener(view -> {
            finish();
        });

        WebSettings webSettings = wvNetWork.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wvNetWork.addJavascriptInterface(new WebAppInterface(this), "Android");
        wvNetWork.loadUrl(String.format(url, english != null ? english : ""));
//        wvNetWork.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//
//                String script = "(function() { document.body.innerHTML = '<button>aaaaaaaa</button>'; return 'JavaScript executed successfully......'; })();";
//                view.evaluateJavascript(script, new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        Log.d("output", value);
//                        //prints:"JavaScript executed successfully."
//                    }
//                });
//            }
//        });
    }

    private void setView() {
        imBack = findViewById(R.id.im_back);
        wvNetWork = findViewById(R.id.wv_Network);
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}
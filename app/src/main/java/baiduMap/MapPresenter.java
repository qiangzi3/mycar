package baiduMap;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapPresenter {

    WebView webView;
    @SuppressLint("JavascriptInterface")
    public  void  initWeb(WebView webView, WebViewClient webClient){
        this.webView = webView;
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(webClient);
    }
}
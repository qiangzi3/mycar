package baiduMap;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  void  AndroidToJs(double lat,double lon){
        int version = Build.VERSION.SDK_INT;
        if (version<18){
            webView.loadUrl("javascript:call()");
        }else {
            webView.evaluateJavascript("javascript:call()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //js的返回结果
                }
            });
        }
    }
    @JavascriptInterface
    public  void  JsToAndroid(){

    }
}
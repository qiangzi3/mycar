package baiduMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yw.sdk.entity.CarStatusInfo;

import org.json.JSONObject;

public class MapPresenter extends AppCompatActivity {

    WebView webView;
    @SuppressLint("JavascriptInterface")
    public  void  initWeb(WebView webView, WebViewClient webClient) {
        this.webView = webView;
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(webClient);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }
        });
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
    public  void showToast(String name) {
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public String JsToAndroid() {
        //CarStatusInfo carStatus = new CarStatusInfo();
        JSONObject obj = new JSONObject();
        try {
            /*obj.put("lat", carStatus.getLatitude());
            obj.put("lon", carStatus.getLatitude());
            obj.put("speed", carStatus.getSpeed() + "KM/H");*/
            obj.put("lat", 106.81175449);
            obj.put("lon", 26.54419939);
            obj.put("speed", "100 KM/S");
            return obj.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return obj.toString();
        }
    }
}
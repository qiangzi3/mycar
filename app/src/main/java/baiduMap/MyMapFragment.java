package baiduMap;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gyjc.carmonitor.CarApp;
import com.gyjc.carmonitor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMapFragment extends Fragment {


    public MyMapFragment() {
        // Required empty public constructor
    }

    WebView webView;
    String stateUrl = "file:///android_asset/satellitemap/demo.html";//卫星地图
    String electronUrl="file:///android_asset/electronicmap/demo.html";//电子地图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MapPresenter presenter = new MapPresenter();
        presenter.initWeb(webView,webViewClient);
        webView.loadUrl(electronUrl);
        webView.setVisibility(View.VISIBLE);
    }

    WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

}

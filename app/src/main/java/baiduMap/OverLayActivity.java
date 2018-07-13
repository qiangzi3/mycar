package baiduMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.FileTileProvider;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Tile;
import com.baidu.mapapi.map.TileOverlay;
import com.baidu.mapapi.map.TileOverlayOptions;
import com.baidu.mapapi.map.TileProvider;
import com.baidu.mapapi.map.UrlTileProvider;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapsdkplatform.comapi.map.MapRenderer;
import com.gyjc.carmonitor.R;

import java.io.InputStream;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OverLayActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.online_tv)
    TextView online_tv;
    @BindView(R.id.offline_tv)
    TextView offline_tv;
    @BindView(R.id.mapview)
    MapView mapView;
    @BindView(R.id.web)
    WebView webView;
    BaiduMap mBaiduMap;
    MapStatusUpdate mMapStatusUpdate;

    String url="http://47.94.165.141/map1/demo.html";
    // 设置瓦片图的在线缓存大小，默认为20 M
    private static final int TILE_TMP = 20 * 1024 * 1024;
    private static final int MAX_LEVEL = 21;
    private static final int MIN_LEVEL = 3;

    TileProvider tileProvider;
    TileOverlay tileOverlay;
    Tile offlineTile;
    private boolean mapLoaded = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay_layout);
        ButterKnife.bind(this);
        initdata();
    }
    private void initdata(){
        MapPresenter presenter = new MapPresenter();
        mBaiduMap = mapView.getMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16.0f);
        builder.target(new LatLng(39.914935D, 116.403119D));
        mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        online_tv.setOnClickListener(this);
        offline_tv.setOnClickListener(this);
        presenter.initWeb(webView,webViewClient);
        webView.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.offline_tv:
//                offlineTile();
                if (webView.getVisibility()==View.VISIBLE){
                        webView.setVisibility(View.GONE);
                }else {
                    if (Utils.isNetworkAvailable(this)){
                        webView.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.online_tv:
//                onlineTile();
                break;
        }

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

    /**
     * 使用瓦片图的在线方式
     */
    private void onlineTile(){

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        if (tileOverlay != null && mBaiduMap != null) {
            tileOverlay.removeTileOverlay();
        }
        /**
         * 定义瓦片图的在线Provider，并实现相关接口
         * MAX_LEVEL、MIN_LEVEL 表示地图显示瓦片图的最大、最小级别
         * urlString 表示在线瓦片图的URL地址
         */
        TileProvider tileProvider = new UrlTileProvider() {
            @Override
            public int getMaxDisLevel() {
                return MAX_LEVEL;
            }

            @Override
            public int getMinDisLevel() {
                return MIN_LEVEL;
            }

            @Override
            public String getTileUrl() {
                return "";
            }

        };
        TileOverlayOptions options = new TileOverlayOptions();
        // 构造显示瓦片图范围，当前为世界范围
        LatLng northeast = new LatLng(80, 180);
        LatLng southwest = new LatLng(-80, -180);
        // 通过option指定相关属性，向地图添加在线瓦片图对象
        tileOverlay = mBaiduMap.addTileLayer(options.tileProvider(tileProvider).setMaxTileTmp(TILE_TMP)
                .setPositionFromBounds(new LatLngBounds.Builder().include(northeast).include(southwest).build()));
        if (mapLoaded) {
            /* 设置底图类型为NONE，即空白地图，
             * 以避免瓦片图标注与底图标注存在叠加显示，影响暂时效果
             * 同时提高加载速度
             */
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
            mBaiduMap.setMaxAndMinZoomLevel(21.0f, 3.0f);
            mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }
    }

    /**
     * 瓦片图的离线添加
     */
    private void offlineTile() {
        if (tileOverlay != null && mBaiduMap != null) {
            tileOverlay.removeTileOverlay();
        }

        /**
         * 定义瓦片图的离线Provider，并实现相关接口
         * MAX_LEVEL、MIN_LEVEL 表示地图显示瓦片图的最大、最小级别
         * Tile 对象表示地图每个x、y、z状态下的瓦片对象
         */
        tileProvider = new FileTileProvider() {
            @Override
            public Tile getTile(int x, int y, int z) {
                // 根据地图某一状态下x、y、z加载指定的瓦片图
                String filedir = "LocalTileImage/" + z + "/" + z + "_" + x + "_" + y + ".jpg";
                Bitmap bm = getFromAssets(filedir);
                if (bm == null) {
                    return null;
                }
                // 瓦片图尺寸必须满足256 * 256
                offlineTile = new Tile(bm.getWidth(), bm.getHeight(), toRawData(bm));
                bm.recycle();
                return offlineTile;
            }

            @Override
            public int getMaxDisLevel() {
                return MAX_LEVEL;
            }

            @Override
            public int getMinDisLevel() {
                return MIN_LEVEL;
            }

        };
        TileOverlayOptions options = new TileOverlayOptions();
        // 构造显示瓦片图范围，当前为世界范围
        LatLng northeast = new LatLng(80, 180);
        LatLng southwest = new LatLng(-80, -180);
        // 设置离线瓦片图属性option
        options.tileProvider(tileProvider)
                .setPositionFromBounds(new LatLngBounds.Builder().include(northeast).include(southwest).build());
        // 通过option指定相关属性，向地图添加离线瓦片图对象
        tileOverlay = mBaiduMap.addTileLayer(options);
        if (mapLoaded) {
            setMapStatusLimits();
        }

    }
    private void setMapStatusLimits() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(39.94001804746338, 116.41224644234747))
                .include(new LatLng(39.90299859954822, 116.38359947963427));
        mBaiduMap.setMaxAndMinZoomLevel(17.0f, 16.0f);
        mBaiduMap.setMapStatusLimits(builder.build());
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
    }

    /**
     * 瓦片文件解析为Bitmap
     * @param fileName
     * @return 瓦片文件的Bitmap
     */
    public Bitmap getFromAssets(String fileName) {
        AssetManager am = this.getAssets();
        InputStream is = null;
        Bitmap bm;

        try {
            is = am.open(fileName);
            bm = BitmapFactory.decodeStream(is);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析Bitmap
     * @param bitmap
     * @return
     */
    byte[] toRawData(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth()
                * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] data = buffer.array();
        buffer.clear();
        return data;
    }
}
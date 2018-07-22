package baiduMap;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.gyjc.carmonitor.CarApp;

public class LocationService {

    public LocationClient client;
    private Object objLock = new Object();
    private LocationClientOption mOption, DIYoption;
    private boolean isLocation = false;//标记是否定位成功
    public  double lat;
    public  double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    BaiduMap baiduMap;
    BDLocationListener locationListener;

    /***
     *
     * @param locationContext
     *
     */
    public LocationService(Context locationContext) {
        synchronized (objLock) {
            if (client == null) {
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
                locationListener();
                Log.i("qqq","LocationService");
            }
        }

    }

    /**
     * 注册定位监听器
     *
     * @return
     */
    public boolean registerListener() {
        boolean isSuccess = false;
        if (locationListener != null) {
            Log.i("qqq","registerListener---");
            client.registerLocationListener(locationListener);
//            client.registerLocationListener(bdAbstractLocationListener);
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 取消定位监听器
     *
     * @param
     */
    public void unregisterListener() {
        if (locationListener != null) {
            client.unRegisterLocationListener(locationListener);
        }
    }

    /**
     * 开始定位
     */
    public void startlocation() {
        synchronized (objLock) {
            if (client != null && !client.isStarted()) {
                client.start();
                Log.i("qqq","startlocation");

            }
        }
    }

    /**
     * 停止定位
     */
    public void stoplocation() {
        synchronized (objLock) {
            if (client != null && client.isStarted()) {
                client.stop();
            }
        }
    }

    /**
     * 设置定位时的默认值
     *
     * @return
     */
    public LocationClientOption getDefaultLocationClientOption() {
        isOPen(CarApp.getInstance());
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setOpenGps(true);
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        }
        return mOption;
    }

    LocationSuccessListener locationSuccessListener;
    public  void  setlocationSucListenrer(LocationSuccessListener locationSuccessListener){
            this.locationSuccessListener = locationSuccessListener;
    }
    public void locationListener() {
        if (locationListener == null) {
            Log.i("qqq","locationListener");
            locationListener = new BDLocationListener() { //定位监听
                @Override
                public void onReceiveLocation(BDLocation location) {
                    //此处获取定位信息,此处是在子线程中
                    Log.i("qqq","location--"+location.getLongitude());
                    if (locationSuccessListener !=null){
                        locationSuccessListener.location(location.getLatitude(),location.getLongitude());
                    }
                }
            };
        }
    }



    /**
     *  
     *      * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的 
     *      * @param context 
     *      * @return true 表示开启 
     *      
     */
    public  boolean  isOPen(Context context) {
//        LocationManager locationManager = (LocationManager)
//                context.getSystemService(Context.LOCATION_SERVICE);
        LocationManager locManager =(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//判断用户是否打开gps
// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        boolean network = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isnet =   Utils.isNetworkConnected(context);
        if (!gps){
            openGPS(context);
        }
        if (gps||network){
            return true;
        }else{
            return false;
        }

    }
    /**
     * 打开gps
     * @param context
     */
    public  final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
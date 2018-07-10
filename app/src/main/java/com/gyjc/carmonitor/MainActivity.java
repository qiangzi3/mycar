package com.gyjc.carmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.gyjc.carmonitor.Utils.NetWorkUtil;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        boolean networkConnected = NetWorkUtil.isNetworkConnected(this);
//        Toast.makeText(this,"联网："+networkConnected,Toast.LENGTH_SHORT).show();
//        Beta.getUpgradeInfo();
//    }
//}
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.yw.sdk.YwSdkManager;
import com.yw.sdk.common.callback.ReceiveCarStatusListener;
import com.yw.sdk.entity.CarStatusInfo;
import com.yw.sdk.entity.MediaInfo;
import com.yw.sdk.video.callback.PlayHistoryControlListener;
import com.yw.sdk.video.callback.PlayHistoryListener;
import com.yw.sdk.video.callback.QueryHistoryListener;

public class MainActivity extends Activity implements Callback,QueryHistoryListener
        ,ReceiveCarStatusListener,PlayHistoryListener,PlayHistoryControlListener{
    private String TAG = "MainActivity";
    private YwSdkManager mYwSdkManager;
    private SurfaceView mSurfaceView;
    private Camera myCamera;//相机声明
    private SurfaceHolder mHolder;//surfaceHolder声明
    private String startTime = "2018-05-01 13:00";//历史视频查询开始时间
    private String endTime = "2018-12-31 13:00";//历史视频查询结束时间
    private List<MediaInfo> mHistoryVideoList;//历史视频列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        Beta.getUpgradeInfo();
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mYwSdkManager = YwSdkManager.getInstants(this);
        mYwSdkManager.setmQueryHistoryCallBack(this);
        mYwSdkManager.setmOnReceiveCarStatusCallBack(this);
        mYwSdkManager.setmPlayHistoryListener(this);
        mYwSdkManager.setmPlayHistoryControlListener(this);

        findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.showAllChannelVideo();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.circlePlayRealVideo();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.stopCirclePlay();

            }
        });

        findViewById(R.id.btn4).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.playRealTimeVideo((short)1);
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.queryHistoryVideo(startTime, endTime, "全部");
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mHistoryVideoList == null || mHistoryVideoList.size() == 0){
                    Toast.makeText(MainActivity.this,"请先查询历史视频",Toast.LENGTH_LONG).show();
                    return;
                }
                MediaInfo mediaInfo = mHistoryVideoList.get(0);
                mYwSdkManager.playHistoryHistoryVideo(mediaInfo);
            }
        });

        findViewById(R.id.btn7).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mYwSdkManager.playHistoryControl((byte) 2);
            }
        });
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        //设置参数并开始预览
        Camera.Parameters params = myCamera.getParameters();
        params.setPictureFormat(PixelFormat.JPEG);

        myCamera.setParameters(params);
        myCamera.startPreview();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(myCamera == null)
        {
            initCamera();
            try {
                myCamera.setPreviewDisplay(holder);
            } catch (IOException e) {

            }
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onQueryHistoryEnd(List<MediaInfo> medialist) {
        mHistoryVideoList = medialist;
        if(mHistoryVideoList != null)
            Log.i(TAG, "query history video list size is :::::" + mHistoryVideoList.size());
    }



    @Override
    public void onReceiveCarStatus(CarStatusInfo carStatus) {
		Log.i(TAG, "car Lat:::::::::" + carStatus.getLatitude());
		Log.i(TAG, "car Log:::::::::" + carStatus.getLongitude());
		Log.i(TAG, "car speed::::::::::::" + carStatus.getSpeed() + "KM/H");
    }


    @Override
    public void onPlayHistoryVideoCallBack(byte result) {
//		Toast.makeText(MainActivity.this,"播放历史视频:" + result,Toast.LENGTH_LONG).show();
        Log.i(TAG, "播放历史视频结果：：：：：：" + result);
    }


    @Override
    public void onControlVideoPlayCallBack(byte ctrlType, byte result) {
        Log.i(TAG, "控制历史视频的结果");
    }
    /**
     * 初始化摄像头
     */
    private void initCamera(){
        int CammeraIndex=FindBackCamera();
        if(CammeraIndex==-1){
            CammeraIndex=FindFrontCamera();
        }
        myCamera = Camera.open(CammeraIndex);
    }

    /**前置摄像头
     * @return
     */
    private int FindFrontCamera(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    /**后置摄像头
     * @return
     */
    private int FindBackCamera(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }
}
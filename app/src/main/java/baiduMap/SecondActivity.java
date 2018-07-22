package baiduMap;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.gyjc.carmonitor.CarApp;
import com.gyjc.carmonitor.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecondActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    TextView navigation_tv;
    @BindView(R.id.video)
    TextView video_tv;
    @BindView(R.id.workorder)
    TextView workorder_tv;
    @BindView(R.id.msg)
    TextView msg_tv;
    @BindView(R.id.viewpager)
    CustomViewpager vp;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        ButterKnife.bind(this);
        initView();
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                Log.i("qqq","---"+permission[0]);
                Log.i("qqq","--11111-"+permission[1]);
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                Log.i("qqq","---"+permission[0]);
                Log.i("qqq","--11111-"+permission[1]);
            }
        },permissions);
    }


    @Override
    protected void onStart() {
        super.onStart();
        startLocation();
    }

    private  void  startLocation(){
        CarApp.locationService.registerListener();//注册监听
        CarApp.locationService.startlocation();//定位开始

    }


    private List<Fragment> mList;
    MyPagerAdapter adapter;
    private void  initView(){
        mList = new ArrayList<>();
        mList.add(new MyMapFragment());
        mList.add(new VideoFragment());
        mList.add(new WorkFragment());
        mList.add(new MessageFragment());
        adapter =new MyPagerAdapter(getSupportFragmentManager(),mList);
        vp.setScanScroll(false);
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);


    }
    @OnClick({R.id.navigation,R.id.video})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.navigation:
                vp.setCurrentItem(0);
                break;
            case R.id.video:
                vp.setCurrentItem(1);
                break;
        }
    }
}
package com.archer.truesure.treasure.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.archer.truesure.R;
import com.archer.truesure.common.ActivityUtils;
import com.archer.truesure.components.TreasureView;
import com.archer.truesure.treasure.Treasure;
import com.archer.truesure.treasure.map.Map1Fragment;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends MvpActivity<DetailView, DetailPresenter> implements DetailView {

    private static final String KEY_TREASURE = "key_treasure";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.frameLayout)
    FrameLayout frameLayout;
    @Bind(R.id.treasureView)
    TreasureView treasureView;
    @Bind(R.id.tv_detail_description)
    TextView tvDetailDescription;

    private Treasure treasure;

    private BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.treasure_expanded);

    public static void open(@NonNull Context context, @NonNull Treasure treasure) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }

    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_treasure_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        treasure = (Treasure) intent.getSerializableExtra(KEY_TREASURE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(treasure.getTitle());
            treasureView.bindTreasure(treasure);
        }

        initMap();

        getPresenter().detail(new Detail(treasure.getId()));

    }

    @NonNull
    @Override
    public DetailPresenter createPresenter() {
        return new DetailPresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nevigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private LatLng latLng;

    private void initMap() {

        BaiduMapOptions options = new BaiduMapOptions();
        options.compassEnabled(false);//
        options.overlookingGesturesEnabled(false);//
        options.rotateGesturesEnabled(false);//
        options.scaleControlEnabled(false);//
        options.zoomGesturesEnabled(false);//
        options.zoomControlsEnabled(false);//

        //目的地
        latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());

        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(18)
                .target(latLng)
                .overlook(-20)
                .build();

        options.mapStatus(mapStatus);

        MapView mapView = new MapView(this, options);

        frameLayout.addView(mapView);

        BaiduMap baiduMap = mapView.getMap();

        MarkerOptions options1 = new MarkerOptions();
        options1.icon(icon);
        options1.position(latLng);

        baiduMap.addOverlay(options1);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String street = Map1Fragment.getStreet();
        String location = treasure.getLocation();

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.nevigation_walk:
                startWalkingNavi(Map1Fragment.getMyLocation(), street, latLng, location);
                break;

            case R.id.nevigation_bike:
                startBikingNavi(Map1Fragment.getMyLocation(), street, latLng, location);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<DetailResult> data) {

        if (data.size() >= 1) {
            DetailResult detailResult = data.get(0);
            tvDetailDescription.setText(detailResult.description);
            return;
        }

//        activityUtils.showToast("没有记录");

    }

    // -----------------------------

    /**
     * 启动百度地图步行导航(Native)
     */
    public void startWalkingNavi(LatLng startPt, String startAdr, LatLng endPt, String endAdr) {
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startPt).endPoint(endPt)
                .startName(startAdr).endName(endAdr);

        if (!BaiduMapNavigation.openBaiduMapWalkNavi(para, this)) {
            showDialog();
        }

    }

    /**
     * 启动百度地图骑行导航(Native)
     */
    public void startBikingNavi(LatLng startPt, String startAdr, LatLng endPt, String endAdr) {
        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startPt).endPoint(endPt)
                .startName(startAdr).endName(endAdr);

        if (!BaiduMapNavigation.openBaiduMapBikeNavi(para, this)) {
            showDialog();
        }
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(DetailActivity.this);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
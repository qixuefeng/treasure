package com.archer.truesure.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.archer.truesure.R;
import com.archer.truesure.common.ActivityUtils;
import com.archer.truesure.treasure.TreasureRepo;
import com.archer.truesure.user.UserPres;
import com.baidu.mapapi.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HideActivity extends MvpActivity<HideView,HidePresenter> implements HideView {


    private static final String EXTRA_KEY_TITLE = "key_title";
    private static final String EXTRA_KEY_LOCATION = "key_location";
    private static final String EXTRA_KEY_LAT_LNG = "key_latlng";
    private static final String EXTRA_KEY_ALTITUDE = "key_altitude";

    /**
     * 进入当前Activity
     */
    public static void open(Context context, String title, String location, LatLng latLng, double altitude) {
        Intent intent = new Intent(context, HideActivity.class);
        intent.putExtra(EXTRA_KEY_TITLE, title);
        intent.putExtra(EXTRA_KEY_LOCATION, location);
        intent.putExtra(EXTRA_KEY_LAT_LNG, latLng);
        intent.putExtra(EXTRA_KEY_ALTITUDE, altitude);
        context.startActivity(intent);
    }

    private ActivityUtils activityUtils;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_description)
    EditText etDdscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_hide_treasure);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_KEY_TITLE));
        }

    }

    @NonNull
    @Override
    public HidePresenter createPresenter() {
        return new HidePresenter();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hide_treasure, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            // 确定上传
            case R.id.action_send:
                Intent preIntent = getIntent();
                LatLng latLng = preIntent.getParcelableExtra(EXTRA_KEY_LAT_LNG);
                double altitude = preIntent.getDoubleExtra(EXTRA_KEY_ALTITUDE, 0);
                String location = preIntent.getStringExtra(EXTRA_KEY_LOCATION);
                String title = preIntent.getStringExtra(EXTRA_KEY_TITLE);
                int tokenId = UserPres.getInt(UserPres.TOKEN_ID);
                String descroption = etDdscription.getText().toString();
                // 执行业务
                HideTreasure hideTreasure = new HideTreasure();
                hideTreasure.setLatitude(latLng.latitude);
                hideTreasure.setLongitude(latLng.longitude);
                hideTreasure.setAltitude(altitude);
                hideTreasure.setLocation(location);
                hideTreasure.setTitle(title);
                hideTreasure.setTokenId(tokenId);
                hideTreasure.setDescription(descroption);
                getPresenter().hideTreasure(hideTreasure);
                break;
        }
        return true;
    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "", "上传中，请稍等...");
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        finish();
        // 清理宝藏仓库
        TreasureRepo.getInstance().clear();
    }
}

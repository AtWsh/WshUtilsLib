package it.wsh.cn.wshutilslib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import it.wsh.cn.wshutilslib.islandwifi.OnekeyInternetVillageActivity;

public class MainActivityForHDTest extends AppCompatActivity {

    private TextView mIslandWifiTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_hd);

        initView();
        initAction();
    }

    private void initView() {
        mIslandWifiTv = findViewById(R.id.tv_island_wifi_test);
    }

    private void initAction() {
        mIslandWifiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnekeyInternetVillageActivity.startSelf(MainActivityForHDTest.this);
            }
        });
    }
}

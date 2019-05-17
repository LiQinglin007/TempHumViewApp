package com.xiaomi.temphum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TempHumView mDeviceTempHum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mDeviceTempHum = findViewById(R.id.device_temp_hum);
        mDeviceTempHum.setHum(20);
        mDeviceTempHum.setTemp(30);
    }
}

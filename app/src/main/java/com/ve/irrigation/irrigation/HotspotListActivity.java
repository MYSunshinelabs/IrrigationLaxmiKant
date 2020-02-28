package com.ve.irrigation.irrigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ve.irrigation.DataValues.AppDataBase;
import com.ve.irrigation.DataValues.ConnectionSourceDAO;
import com.ve.irrigation.DataValues.ConnectionSourceData;
import com.ve.irrigation.DataValues.WifiHotspot;
import com.ve.irrigation.hotspot.ClientScanResult;
import com.ve.irrigation.hotspot.WifiApManager;

import java.util.ArrayList;
import java.util.List;

public class HotspotListActivity extends AppCompatActivity implements HotSpotAdapter.HotspotListener {


    RecyclerView mRecyclerView_HotspotList;
    HotSpotAdapter hotSpotAdapter;
    private GestureDetectorCompat mDetector;
    WifiApManager wifiApManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_list);
        wifiApManager = new WifiApManager(this);
        mDetector = new GestureDetectorCompat(this, new HotspotListActivity.MyGestureListner());
        initViews();
        settData();

    }

    private void initViews() {
        mRecyclerView_HotspotList = (RecyclerView) findViewById(R.id.recyclerview_hotspotlist);
    }


    private void settData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ConnectionSourceData>connectionSourceDatas= AppDataBase.getAppDataBase(HotspotListActivity.this).connectionSourceDAO().getAllConnectionSource();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        hotSpotAdapter = new HotSpotAdapter(HotspotListActivity.this,connectionSourceDatas);
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(HotspotListActivity.this);
                        mRecyclerView_HotspotList.setHasFixedSize(true);

                        mRecyclerView_HotspotList.setLayoutManager(mLinearLayoutManager);
                        mRecyclerView_HotspotList.setAdapter(hotSpotAdapter);
                    }
                });
            }
        }).start();

    }

    @Override
    public void setUpHotSpot(WifiHotspot wifiHotspot) {

        sendResultFromSelectedHotspot(wifiHotspot);
        finish();
    }


    public class MyGestureListner extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case 1:
                    finish();
                    HotspotListActivity.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    return true;
                case 2:

                    return true;
                case 3:

                    return true;
                case 4:

                    return true;
            }
            return false;
        }

        private int getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135)
                // top
                return 1;
            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                // left
                return 2;
            if (angle < -45 && angle >= -135)
                // down
                return 3;
            if (angle > -45 && angle <= 45)
                // right
                return 4;
            return 0;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        HotspotListActivity.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void sendResultFromSelectedHotspot(WifiHotspot wifiHotspot) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", wifiHotspot);
        setResult(Activity.RESULT_OK, returnIntent);
    }


}



package com.ve.irrigation.irrigation;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.ve.irrigation.DataValues.AppDataBase;
import com.ve.irrigation.DataValues.ConnectionSourceData;
import com.ve.irrigation.DataValues.FirstHeartBeat;
import com.ve.irrigation.DataValues.HeartBeat;
import com.ve.irrigation.DataValues.MySharedPreferences;
import com.ve.irrigation.DataValues.WifiHotspot;
import com.ve.irrigation.customview.CustomTextView;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.hotspot.ClientScanResult;
import com.ve.irrigation.hotspot.FinishScanListener;
import com.ve.irrigation.hotspot.WifiApManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    WifiApManager wifiApManager;
    private GestureDetectorCompat mDetector;
    String LOGTAG = "MainActivity";

    CustomTextView mTextViewProgress;
    CustomTextView mTextViewTimer;
    RecyclerView mRecyclerView_Status;
    LinearLayout mLinearLayoutMainView;
    CustomTextView mTextView_Pressure, mTextView_Flow, mTextView_RealVol, mTextView_TotalVol, mTextView_Pump, mTextView_Valve;
    CustomTextViewLight mTextView_ConnectedHotspotName, mTextView_ConnectedTime, mTextView_HeartBeat;
    TextView mTextView_NotificationArea;
    AllConnectedDevice allConnectedDevice;

    boolean shouldSocketConnect;
    Toast mToast;
    Handler handler;
    Runnable runnable;

    LinearLayout mBlue, mGreen, mRed;
    ProgressBar progressBar;
    LinearLayout mLayoutMain;
    boolean mboolBlue, mboolGreen, mboolRed;
    int mSwitchModeVal = 0;

    ImageView mImageViewBlue, mImageViewGreen, mImageViewRed, mImageViewLed, mImageViewRemoteMode;
    DetailedModeFragment detailedModeFragment;
    private int[] imageResource = {R.mipmap.led_off, R.mipmap.led_red, R.mipmap.led_green, R.mipmap.led_blue, R.mipmap.led_white, R.mipmap.led_yellow, R.mipmap.led_cyan, R.mipmap.led_purple};
    private ImageView imgExit;
    int count;
    int REQUEST_CODE_HOTSPOT = 2018;
    WifiHotspot wifiHotspot;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();


        mDetector = new GestureDetectorCompat(this, new MyGestureListner());


        wifiApManager = new WifiApManager(this);

        wifiApManager.showWritePermissionSettings(true);

        progressBar.setVisibility(View.VISIBLE);
        mTextViewProgress.setVisibility(View.VISIBLE);
        mTextViewTimer.setVisibility(View.VISIBLE);
        mLinearLayoutMainView.setVisibility(View.INVISIBLE);
        mLayoutMain.setVisibility(View.INVISIBLE);

        addFirstTimeNetwork();

        try {
            makeRSWA(mSwitchModeVal, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void initViews() {
        mRecyclerView_Status = (RecyclerView) findViewById(R.id.recyclerview_status);
        mTextView_Pressure = (CustomTextView) findViewById(R.id.textview_realactivity_pressure);
        mTextView_Flow = (CustomTextView) findViewById(R.id.textview_realactivity_flow);
        mTextView_RealVol = (CustomTextView) findViewById(R.id.textview_realactivity_realvol);
        mTextView_TotalVol = (CustomTextView) findViewById(R.id.textview_realactivity_totalvol);
        mTextView_Pump = (CustomTextView) findViewById(R.id.textview_realactivity_pump);
        mTextView_Valve = (CustomTextView) findViewById(R.id.textview_realactivity_valve);


        mTextView_NotificationArea = (TextView) findViewById(R.id.textview_notificationactivity);


        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mBlue = (LinearLayout) findViewById(R.id.layout_blueswitch);
        mGreen = (LinearLayout) findViewById(R.id.layout_greenwitch);
        mRed = (LinearLayout) findViewById(R.id.layout_redswitch);

        mImageViewLed = (ImageView) findViewById(R.id.img_led);
        mImageViewBlue = (ImageView) findViewById(R.id.img_blueswitch);
        mImageViewGreen = (ImageView) findViewById(R.id.img_greenwitch);
        mImageViewRed = (ImageView) findViewById(R.id.img_redswitch);
        mImageViewRemoteMode = (ImageView) findViewById(R.id.img_remote_mode);


        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mTextViewProgress = (CustomTextView) findViewById(R.id.tv_progress);
        mTextViewTimer = (CustomTextView) findViewById(R.id.tv_timer);

        mLinearLayoutMainView = (LinearLayout) findViewById(R.id.ll_mainview);


        mLayoutMain = (LinearLayout) findViewById(R.id.layout_main);
        registerSwitchEvent();

        imgExit = (ImageView) findViewById(R.id.img_exit);

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mTextView_ConnectedHotspotName = (CustomTextViewLight) findViewById(R.id.textView_connectedhotspotname);
        mTextView_ConnectedTime = (CustomTextViewLight) findViewById(R.id.textview_connectedhotspottime);
        mTextView_HeartBeat = (CustomTextViewLight) findViewById(R.id.textview_heartbeatcount);


    }

    private void addFirstTimeNetwork() {
        wifiHotspot = new WifiHotspot();
        wifiHotspot.setSsid("sun00");
        wifiHotspot.setPassword("sun01234");
        wifiHotspot.setType("hotspot");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (wifiHotspot.getType().toString().equalsIgnoreCase("hotspot") && MySharedPreferences.getMySharedPreferences().isSimAvailable(this)) {
            wifiApManager.showWritePermissionSettings(false);
            startTimeout();
            setupHotSpot(wifiHotspot);

        } else {
            if (MySharedPreferences.getMySharedPreferences().isSimAvailable(this)) {
                mToast.setText("The app should connect to URL to use app");
                showMainStatusAfterTimeoutFailure("The app should connect to URL to use app");
            } else {
                mToast.setText("We found no sim card in your mobile, Please connect with URL");
                showMainStatusAfterTimeoutFailure("Please insert a sim card to enable hotspot");
            }
            mToast.show();

        }


    }

    private void startSyncIfStartedManually() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                syncData(3001);
            }
        };
        handler.postDelayed(runnable, 2000);

    }


    private void showMainStatusAfterTimeoutFailure(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        mTextViewProgress.setVisibility(View.INVISIBLE);
        mTextViewTimer.setVisibility(View.INVISIBLE);
        mLayoutMain.setVisibility(View.VISIBLE);
        mLinearLayoutMainView.setVisibility(View.VISIBLE);
        mTextView_NotificationArea.setText(message);
    }

    private void setupHotSpot(WifiHotspot wifiHotspot) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Settings.System.canWrite(MainActivity.this) == true) {
                wifiApManager.setWifiApEnabled(null, true);
                if (wifiApManager.setWifiApConfiguration(wifiHotspot.getSsid(), wifiHotspot.getPassword()))
                    startTimer();

                else {
                    mToast.setText("The app should connect to URL to use app");
                    mToast.show();
                    showMainStatusAfterTimeoutFailure("App can not start hotspot with current Android version");
                    startSyncIfStartedManually();
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (Settings.System.canWrite(MainActivity.this) == true) {
                mTextViewProgress.setText("Starting Hotspot");
                wifiApManager.setWifiApEnabled(null, true);
                if (wifiApManager.setWifiApConfiguration(wifiHotspot.getSsid(), wifiHotspot.getPassword()))
                    startTimer();
            }
            mTextView_ConnectedHotspotName.setText("Hotspot " + wifiHotspot.getSsid());
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTextViewProgress.setText("Starting Hotspot");
            wifiApManager.setWifiApEnabled(null, true);
            if (wifiApManager.setWifiApConfiguration(wifiHotspot.getSsid(), wifiHotspot.getPassword()))
                startTimer();
            mTextView_ConnectedHotspotName.setText("Hotspot " + wifiHotspot.getSsid());
        }


    }


    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                shouldSocketConnect = false;

                for (ClientScanResult clientScanResult : clients) {


                    if (clientScanResult.isReachable())
                        shouldSocketConnect = true;


                }

                if (shouldSocketConnect) {
                    settData(clients);

                    if (shouldSocketConnect) {
                        progressBar.setVisibility(View.INVISIBLE);
                        mTextViewProgress.setVisibility(View.INVISIBLE);
                        mTextViewTimer.setVisibility(View.INVISIBLE);
                        mLinearLayoutMainView.setVisibility(View.VISIBLE);
                        mLayoutMain.setVisibility(View.VISIBLE);
                        syncData(3001);
                    }
                } else {

                    showMainStatusAfterTimeoutFailure("Module is not connected to Hotspot" + wifiHotspot.getSsid());


                }


            }
        });
    }

    private void startTimeout() {
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextViewTimer.setText("Seconds Remaining: " + millisUntilFinished / 1000);
                if (wifiApManager.isWifiApEnabled())
                    mTextViewProgress.setText("Hotspot is Enabled\n" + "Looking for Connected Module with App");
            }

            public void onFinish() {
                mTextViewTimer.setText("Timeout!");
            }
        }.start();
    }


    private void settData(ArrayList<ClientScanResult> clientScanResults) {

        allConnectedDevice = new AllConnectedDevice(clientScanResults, this);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView_Status.setHasFixedSize(true);

        mRecyclerView_Status.setLayoutManager(mLinearLayoutManager);
        mRecyclerView_Status.setAdapter(allConnectedDevice);

        detailedModeFragment = (DetailedModeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
                    Intent i = new Intent(MainActivity.this, ConfigurationScreenActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);


                    Log.d(LOGTAG, "top");
                    return true;
                case 2:
                    Intent iLeft = new Intent(MainActivity.this, SwipeLeftActivity.class);
                    startActivity(iLeft);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                    Log.d(LOGTAG, "left");


                    return true;
                case 3:
                    Log.d(LOGTAG, "down");
                    Intent iDown = new Intent(MainActivity.this, HotspotListActivity.class);
                    startActivityForResult(iDown, REQUEST_CODE_HOTSPOT);
                    overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
                    Log.d(LOGTAG, "right");
                    return true;
                case 4:
                    Intent iRight = new Intent(MainActivity.this, SwipeRightActivity.class);
                    startActivity(iRight);
                    overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                    Log.d(LOGTAG, "right");

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
    protected void onPause() {
        super.onPause();


    }


    DatagramSocket socket;

    private void syncData(int port) {


        final RXSocketReceiver newClass = new RXSocketReceiver();


        try {

            socket = new MulticastSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));

        } catch (SocketException e) {
            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();
        }


        Observable.interval(0, 3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    public void call(Long aLong) {
                        // here is the task that should repeat

                        newClass.request(socket)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String flag) {

                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(flag);

                                            if (jsonObject.has("destip")) {
                                                FirstHeartBeat heartBeat = new Gson().fromJson(flag, FirstHeartBeat.class);
                                                mTextView_NotificationArea.setText("First heartbear arrived at " + MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
                                                mTextView_ConnectedTime.setText(MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));


                                            } else {
                                                HeartBeat heartBeat = new Gson().fromJson(flag, HeartBeat.class);
                                                updateUI(heartBeat);
                                                detailedModeFragment.setValueFromHeartBeat(heartBeat);

                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();

                                        }

                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable ex) {
                                        Log.e("Heartbeat", "Unable to sync", ex);
                                    }
                                });


                    }
                });


    }

    private void updateUI(HeartBeat heartBeat) {

        count = count + 1;
        setRealData(heartBeat);
        mTextView_NotificationArea.setText("Last heartbeat arrived at " + MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
        mTextView_ConnectedTime.setText(MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
        mTextView_HeartBeat.setText("Heart Beat No:" + count);

    }


    private void setRealData(HeartBeat heartBeat) {
        String pumpStatus = (Integer.parseInt(heartBeat.getPump())) == 1 ? "On" : "Off";

        String valveStatus = (Integer.parseInt(heartBeat.getValve1())) == 1 ? "On" : "Off";


        String pressure = "<font color=#094316>Pressure: </font> <font color=#1e3224>" + heartBeat.getPress() + "</font>";
        mTextView_Pressure.setText(Html.fromHtml(pressure));

        String flow = "<font color=#094316>Flow: </font> <font color=#1e3224>" + heartBeat.getFlow() + "</font>";
        mTextView_Flow.setText(Html.fromHtml(flow));


        String realVol = "<font color=#094316>Real Vol: </font> <font color=#1e3224>" + heartBeat.getRqdvol() + "</font>";
        mTextView_RealVol.setText(Html.fromHtml(realVol));

        String totalVol = "<font color=#094316>Total Vol: </font> <font color=#1e3224>" + heartBeat.getTtvol() + "</font>";
        mTextView_TotalVol.setText(Html.fromHtml(totalVol));

        String pump = "<font color=#094316>Pump: </font> <font color=#1e3224>" + pumpStatus + "</font>";
        mTextView_Pump.setText(Html.fromHtml(pump));

        String valve = "<font color=#094316>Valve: </font> <font color=#1e3224>" + valveStatus + "</font>";
        mTextView_Valve.setText(Html.fromHtml(valve));

    }


    private void startTimer() {


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                scan();

            }
        };
        handler.postDelayed(runnable, 20000);

    }


    private void registerSwitchEvent() {

        mImageViewRemoteMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSwitchModeVal == 0) {
                    mImageViewRemoteMode.setImageResource(R.mipmap.mode_on);
                    mSwitchModeVal = 1;
                } else {
                    mImageViewRemoteMode.setImageResource(R.mipmap.mode_off);
                    mSwitchModeVal = 0;
                }


            }
        });


        mBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mboolBlue) {

                    mImageViewBlue.setImageResource(R.mipmap.blue_button_on);

                } else {
                    mImageViewBlue.setImageResource(R.mipmap.blue_button_off);
                }
                mboolBlue = !mboolBlue;
                callSwitchfunctions();
            }
        });


        mGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mboolGreen) {
                    mImageViewGreen.setImageResource(R.mipmap.green_button_on);
                } else {
                    mImageViewGreen.setImageResource(R.mipmap.green_button_off);
                }
                mboolGreen = !mboolGreen;
                callSwitchfunctions();

            }
        });

        mRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!mboolRed) {
                    mImageViewRed.setImageResource(R.mipmap.red_button_on);
                } else {
                    mImageViewRed.setImageResource(R.mipmap.red_button_off);
                }
                mboolRed = !mboolRed;
                callSwitchfunctions();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        wifiApManager.setWifiApEnabled(null, false);
    }


    private void makeRSWA(int remoteNo, int switchCode) throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://192.168.43.252:80/rswa/" + remoteNo + "/" + switchCode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("RSWA res: ", s);
                setLEDColor();

            }
        });
    }


    private void callSwitchfunctions() {

        try {

            if (mboolRed == true && mboolGreen == false && mboolBlue == false) {
                makeRSWA(mSwitchModeVal, 1);
            } else if (mboolRed == true && mboolGreen == true && mboolBlue == false) {
                makeRSWA(mSwitchModeVal, 3);
            } else if (mboolRed == false && mboolGreen == true && mboolBlue == false) {
                makeRSWA(mSwitchModeVal, 2);
            } else if (mboolRed == false && mboolGreen == true && mboolBlue == true) {
                makeRSWA(mSwitchModeVal, 6);
            } else if (mboolRed == false && mboolGreen == false && mboolBlue == true) {
                makeRSWA(mSwitchModeVal, 4);
            } else if (mboolRed == false && mboolGreen == false && mboolBlue == false) {
                makeRSWA(mSwitchModeVal, 0);
            }

        } catch (IOException e) {

        }
    }


    private void setLEDColor() {


        if (mboolRed == true && mboolGreen == false && mboolBlue == false) {
            setLed(1);
        } else if (mboolRed == true && mboolGreen == true && mboolBlue == false) {
            setLed(3);
        } else if (mboolRed == false && mboolGreen == true && mboolBlue == false) {
            setLed(2);
        } else if (mboolRed == false && mboolGreen == true && mboolBlue == true) {
            setLed(6);
        } else if (mboolRed == false && mboolGreen == false && mboolBlue == true) {
            setLed(4);
        } else if (mboolRed == false && mboolGreen == false && mboolBlue == false) {
            setLed(0);
        }


    }

    private void setLed(int index) {
        mImageViewLed.setImageResource(imageResource[index]);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2018) {
            if (resultCode == RESULT_OK) {
                WifiHotspot wifiHotspotNew = (WifiHotspot) data.getSerializableExtra("result");
                try {
                    wifiHotspot = (WifiHotspot) wifiHotspotNew.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

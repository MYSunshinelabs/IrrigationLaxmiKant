package com.ve.irrigation.irrigation;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ve.irrigation.DataValues.ConfigurationDDGET;
import com.ve.irrigation.DataValues.ConfigurationSetting;
import com.ve.irrigation.DataValues.MySharedPreferences;

import java.io.IOException;
import java.util.Calendar;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ConfigurationScreenActivity extends AppCompatActivity {

    TextView mTextViewNotBeforeTime, mTextViewNotAfterTime;
    EditText mEditTextPumpCapacity, mEditTextNumberofDevice;
    Spinner mSpinnerLanguage, mSpinnerMode;
    Button mButtonSave;
    Toast mToast;
    private GestureDetectorCompat mDetector;
    ConfigurationDDGET configurationDDGET;
    int mNotBefore, mNotAfter;
    boolean notAfter;
    ImageView mImageViewLock;
    boolean mLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_screen);
        mDetector = new GestureDetectorCompat(this, new ConfigurationScreenActivity.MyGestureListner());
        initViews();
        setSpinnerValues();
        perfomClick();
        //getSavedSettings();
        registerEditTextListener();
        registerSpinerListener();
    }

    private void initViews() {
        mTextViewNotBeforeTime = (TextView) findViewById(R.id.not_before_time);
        mTextViewNotAfterTime = (TextView) findViewById(R.id.not_after_time);

        mEditTextPumpCapacity = (EditText) findViewById(R.id.edittext_pumpcapacity);
        mEditTextNumberofDevice = (EditText) findViewById(R.id.edittext_numberofdevices);

        mSpinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        mSpinnerMode = (Spinner) findViewById(R.id.spinner_mode);

        mButtonSave = (Button) findViewById(R.id.btn_save);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mImageViewLock = (ImageView) findViewById(R.id.img_lock);

    }

    private void setSpinnerValues() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLanguage.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterMode = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMode.setAdapter(adapterMode);
    }


    private void perfomClick() {
        mTextViewNotBeforeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(mTextViewNotBeforeTime, false);
            }
        });


        mTextViewNotAfterTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(mTextViewNotAfterTime, true);
            }
        });


        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveSettings();

                try {
                    permanentChange();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mImageViewLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLock) {
                    mImageViewLock.setImageResource(R.mipmap.mode_on);
                    mLock = !mLock;
                } else {
                    mImageViewLock.setImageResource(R.mipmap.mode_off);
                    mLock = !mLock;
                }

            }
        });
    }

    private void registerEditTextListener() {
        mEditTextPumpCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                try {
                    makeRequest("pumpcap", mEditTextPumpCapacity.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        mEditTextNumberofDevice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {
                    makeRequest("cfg.nos", mEditTextNumberofDevice.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private void registerSpinerListener() {
        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    try {
                        makeRequest("cfg.lang", "English");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        makeRequest("cfg.lang", "Chinese");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mSpinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    try {
                        makeRequest("mode", "Expert");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        makeRequest("mode", "Detailed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void saveSettings() {

        ConfigurationSetting configurationSetting = new ConfigurationSetting();
        configurationSetting.setNotBeforeTime(mTextViewNotBeforeTime.getText().toString());
        configurationSetting.setNotAfterTime(mTextViewNotAfterTime.getText().toString());
        configurationSetting.setPumpCapacity(mEditTextPumpCapacity.getText().toString());
        configurationSetting.setNoofDevicesinChain(mEditTextNumberofDevice.getText().toString());
        configurationSetting.setLanguage(String.valueOf(mSpinnerLanguage.getSelectedItemPosition()));
        configurationSetting.setMode(String.valueOf(mSpinnerMode.getSelectedItemPosition()));
        MySharedPreferences.getMySharedPreferences().saveConfigurationSetting(this, configurationSetting);

        mToast.setText("Configuration setting has been updated");
        mToast.show();


    }


    private void getSavedSettings() {
        ConfigurationSetting configurationSetting = MySharedPreferences.getMySharedPreferences().getConfigurationSetting(this);
        if (configurationSetting != null) {
            mTextViewNotBeforeTime.setText(configurationSetting.getNotBeforeTime());
            mTextViewNotAfterTime.setText(configurationSetting.getNotAfterTime());
            mEditTextPumpCapacity.setText(configurationSetting.getPumpCapacity());
            mEditTextNumberofDevice.setText(configurationSetting.getNoofDevicesinChain());
            mSpinnerLanguage.setSelection(Integer.parseInt(configurationSetting.getLanguage()));
            mSpinnerMode.setSelection(Integer.parseInt(configurationSetting.getMode()));
        }
    }


    private void showTimePicker(final TextView view, final boolean notAfter) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ConfigurationScreenActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                view.setText(selectedHour + ":" + selectedMinute);

                if (!notAfter) {
                    mNotBefore = (selectedHour * 60 * 60) + (selectedMinute * 60);
                    try {
                        makeRequest("nbefore", String.valueOf(mNotBefore));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mNotAfter = (selectedHour * 60 * 60) + (selectedMinute * 60);
                    try {
                        makeRequest("nafter", String.valueOf(mNotAfter));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


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
                    if (mLock) {
                        Intent i = new Intent(ConfigurationScreenActivity.this, EngineeringActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    }

                    return true;
                case 2:

                    return true;
                case 3:
                    Log.d("Config", "down");
                    finish();
                    ConfigurationScreenActivity.this.overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
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
        ConfigurationScreenActivity.this.overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            makeDDGET();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void makeDDGET() throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://192.168.43.252:80/ddget/0").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
            @Override
            public Observable<? extends String> call(Throwable throwable) {
                return null;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("DDGET res: ", s);
                //MySharedPreferences.getMySharedPreferences().saveDDGET(ConfigurationScreenActivity.this, s);

                if (s != null && s.length() > 0) {
                    configurationDDGET = new Gson().fromJson(s, ConfigurationDDGET.class);
                    setValuesFromDDGET(configurationDDGET);
                }
            }
        });
    }


    private void permanentChange() throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://192.168.43.252:80/rcom/dsav/").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
            @Override
            public Observable<? extends String> call(Throwable throwable) {
                return null;
            }
        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("permanentChange res: ", s);

                    }
                });
    }


    private void setValuesFromDDGET(ConfigurationDDGET configurationDDGET) {
        mTextViewNotBeforeTime.setText(MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getNbefore())));
        mTextViewNotAfterTime.setText(MySharedPreferences.getMySharedPreferences().convertSecondtoTime(Integer.parseInt(configurationDDGET.getNafter())));
        mEditTextPumpCapacity.setText(configurationDDGET.getPumpcap());
        mEditTextNumberofDevice.setText(configurationDDGET.getCfgnos());

        if (configurationDDGET.getCfglang().equalsIgnoreCase("English"))
            mSpinnerLanguage.setSelection(0);
        else
            mSpinnerLanguage.setSelection(1);

        if (configurationDDGET.getMode().equalsIgnoreCase("Expert"))
            mSpinnerMode.setSelection(0);
        else
            mSpinnerMode.setSelection(1);
    }


    private void makeRequest(String property, String value) throws IOException {
        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://192.168.43.252:80/dbset/" + property + "/" + value).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("res: ", s);

            }
        });
    }


}

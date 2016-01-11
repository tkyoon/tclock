package com.example.administrator.myapplication2;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener, SensorEventListener, CompoundButton.OnCheckedChangeListener{
    TextToSpeech _tts;
    boolean _ttsActive = false;

    EditText etBText;
    EditText etAText;
    String beforeReadingText;
    String afterReadingText;
    String clock;
    String minutes;
    String second;
    String time;

    CheckBox ckbStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MotionService ms = new MotionService();
//        _tts = new TextToSpeech(this, this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
//
//            mStarted = true;
        //NotificationSomethins1();


        ckbStatus = (CheckBox)findViewById(R.id.chk_status);
        ckbStatus.setOnCheckedChangeListener(this) ;




        initNofification();



    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            ShowNotification();
        }else{
            HideNotification();
        }
    }




        NotificationManager nm;
    NotificationCompat.Builder mCompatBuilder;
    public void initNofification(){
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mCompatBuilder = new NotificationCompat.Builder(this);

        mCompatBuilder.setSmallIcon(R.drawable.tk);
        mCompatBuilder.setTicker("Talking Clock");
        mCompatBuilder.setWhen(System.currentTimeMillis());
        //mCompatBuilder.setNumber(10);
        mCompatBuilder.setContentTitle("Talking Clock");
        //mCompatBuilder.setContentText("NotificationCompat.Builder Massage");
        //mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setContentIntent(pendingIntent);
        //mCompatBuilder.setAutoCancel(false);
        mCompatBuilder.setOngoing(true);
    }



    public void ShowNotification() {
        nm.notify(222, mCompatBuilder.build());
    }

    public void HideNotification() {
        nm.cancel(222);
    }

    public void fn_read(View v) {
        sayClock();
    }

    public void sayClock(){
        etBText = (EditText)findViewById(R.id.et_btext);
        beforeReadingText = etBText.getText().toString();

        etAText = (EditText)findViewById(R.id.et_atext);
        afterReadingText = etAText.getText().toString();

        _tts.setLanguage(Locale.KOREA);
        _ttsActive = true;

        clock = new java.text.SimpleDateFormat("HH").format(new java.util.Date());
        minutes = new java.text.SimpleDateFormat("mm").format(new java.util.Date());
        second = new java.text.SimpleDateFormat("ss").format(new java.util.Date());
        time = clock + "시" + minutes + "분" + second + "초";

//        Toast.makeText(getApplicationContext(), beforeReadingText, Toast.LENGTH_LONG).show();
        _tts.speak(beforeReadingText+time+afterReadingText, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onInit(int status){

    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            if(_tts != null){
                _tts.stop();
                _tts = new TextToSpeech(getApplicationContext(), this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        _tts = new TextToSpeech(getApplicationContext(), this);
    }

//    @Override
//    public void onDestory(){
//        super.onDestroy();
//        try{
//            if(_tts != null){
//                _tts.shutdown();
//                _tts = null;
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }


    private static final String TAG = "MotionControlService";

    private float mGZ = 0;//gravity acceleration along the z axis
    private int mEventCountSinceGZChanged = 0;
    private static final int MAX_COUNT_GZ_CHANGE = 10;

    private SensorManager mSensorManager;
    boolean mStarted = false;

//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        Log.d(TAG, "onStartCommand, Started: " + mStarted);
//
//        if (!mStarted) {
//            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//            mSensorManager.registerListener(this,
//                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                    SensorManager.SENSOR_DELAY_GAME);
//
//            mStarted = true;
//        }
//        //return START_STICKY;
//        return 1;
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            float gz = event.values[2];
            if (mGZ == 0) {
                mGZ = gz;
            } else {
                if ((mGZ * gz) < 0) {
                    mEventCountSinceGZChanged++;
                    if (mEventCountSinceGZChanged == MAX_COUNT_GZ_CHANGE) {
                        mGZ = gz;
                        mEventCountSinceGZChanged = 0;
                        if (gz > 0) {
                            Log.d(TAG, "now screen is facing up.");
                            sayClock();
                        } else if (gz < 0) {
                            Log.d(TAG, "now screen is facing down.");
                            sayClock();
                        }
                    }
                } else {
                    if (mEventCountSinceGZChanged > 0) {
                        mGZ = gz;
                        mEventCountSinceGZChanged = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

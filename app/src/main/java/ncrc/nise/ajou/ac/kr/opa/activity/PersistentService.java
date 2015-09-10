package ncrc.nise.ajou.ac.kr.opa.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import ncrc.nise.ajou.ac.kr.opa.data.ManboValues;

public class PersistentService extends Service implements Runnable, SensorEventListener
{

    private static final int REBOOT_DELAY_TIMER = 10 * 1000; // 서비스 재시작 연기 시간 10초(Activity 활성화 시간을 염두)
    private static final int LOCATION_UPDATE_DELAY = 3 * 1000; // 주기 시간 3초

    private Handler mHandler; //핸들러 선언
    private boolean mIsRunning;  // 상태 체크할 불리언 변수 선언


    int count = ManboValues.Step;

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;


    // 기본 개념 : onBind()가 추상메소드 이므로 문법적으로 반드시 재정의 해주어야 하고 모든 서비스는 onCreate()에서 생성되고 onDestroy()에서 종료
	/*
	최초에 서비스를 실행하고 서비스를 죽일 경우
	1. AlarmManager 등록
	2. 정해진 시간이 흐른 뒤 Intent를 BroadCasting
	3. BroadcastReceiver는 Broadcast된 Intent를 받고 미리 정의한 행동(PersistentService 살리기)을 수행
	*/


    @Override
    public IBinder onBind(Intent intent) {  //추상메소드 재정의는 필수
        return null;
    }

    @Override
    public void onCreate()
    {  //  onCreate()에서 서비스가 생성됨
        unregisterRestartAlarm();  //이미 등록된 알람은 제거
        super.onCreate(); //서비스 생성

        Log.i("lee", "Service is created");

        mIsRunning = false; //상태 지정

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public void onDestroy()
    {
        registerRestartAlarm(); //서비스가 죽을 때 알람 등록

        super.onDestroy(); //서비스 종료

        mIsRunning = false;  //상태 지정

        if(sensorManager != null)
            sensorManager.unregisterListener((SensorEventListener) this);
    }


    @Override
    public void onStart(Intent intent, int startId) {  // 서비스 시작을 위한 메소드

        super.onStart(intent, startId); //서비스는 시작된다

        mHandler = new Handler(); //핸들러 선언
        mHandler.postDelayed(this, LOCATION_UPDATE_DELAY); // 잠시 보관 뒤 실행.(여기선 3초로 set) 일정 시간이 지난후에 메소드를 동작시킬 수 있다.
        mIsRunning = true; //상태 지정

        if(accelerometerSensor != null)
            sensorManager.registerListener((SensorEventListener) this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void run()
    { //running 중인 상태에서 지속적으로 확인(실행)하는 코드

        if(!mIsRunning)  //서비스가 실행되고 있지 않다면
        {
            return;

        } else { //서비스가 실행 중이라면
            mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);// 잠시 보관 뒤 실행.(여기선 3초로 set) 일정 시간이 지난후에 메소드를 동작시킬 수 있다.
            mIsRunning = true; //상태 지정
        }
    }

    private void registerRestartAlarm()
    { //재시작 알람을 등록하는 코드(메소드)

        Intent intent = new Intent(PersistentService.this, RestartService.class);//intent객체 생성
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);//intent 객체의 action 지정(서비스 재시작)
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);// 브로드케스트할 Intent

        long firstTime = SystemClock.elapsedRealtime(); // 현재 시간
        firstTime += REBOOT_DELAY_TIMER; // 10초 후에 알람이벤트 발생

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE); // 알람 서비스 등록
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, REBOOT_DELAY_TIMER, sender);//장치가 부팅된 이후로 지정된 길이의 시간이 지나면 intent발생 후 필요한 경우 장치를 깨움
    }

    private void unregisterRestartAlarm()
    {  //등록된 재시작 알람을 제거하는 코드(메소드)

        Intent intent = new Intent(PersistentService.this, RestartService.class); //intent객체 생성
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE); //intent 객체의 action 지정(서비스 재시작)
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);  //브로드케스트할 intent

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);  //알람 서비스 변수에 등록
        am.cancel(sender);  // 등록된 알람 제거
    }

    public void onSensorChanged(SensorEvent event) {
        //TODO Auto-generated method stub

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            if(gabOfTime > 100) {
                lastTime = currentTime;

                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x+y+z-lastX-lastY-lastZ) / gabOfTime * 10000;

                if(speed > SHAKE_THRESHOLD) {
                    Log.e("lee", "Step");

                    Intent myFilteredResponse = new Intent("ncrc.nise.ajou.ac.kr.opa.step");

                    ManboValues.Step = count++;

                    ManboValues.Step = (ManboValues.Step)/2;

                    String msg = ManboValues.Step + "";
                    Log.i("lee", msg);
                    myFilteredResponse.putExtra("serviceData", msg);

                    sendBroadcast(myFilteredResponse);
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
package com.example.clock;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.GregorianCalendar;

public class MyService extends Service {

    public static RequestQueue requestQueue;  // 요청 큐
    BackgroundThread thread;
    private static final String TAG = "MyService";
    boolean State = true;

    GregorianCalendar today;
    SharedPreferences sp;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"바인드됨");

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        if(requestQueue == null){
            //리퀘스트큐 생성 (MainActivit가 메모리에서 만들어질 때 같이 생성이 될것이다.
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        Log.d(TAG,"생성됨");
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"종료됨");
        if(thread != null){
            thread.interrupt();
            thread = null;

        }
        super.onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("startForeground".equals(intent.getAction())){
            Log.d(TAG,"foregroundservice 실행");
            startForegroundService();
        }
        else if ("stopForeground".equals(intent.getAction())){
            Log.d(TAG,"foreground 종료");
            stopForegroundService();
        }
        if (thread == null) {
            Log.d(TAG,"백그라운드 실행");
            thread = new BackgroundThread();
            thread.start();
        }
        return START_STICKY;

    }

    private void startForegroundService(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");//오레오 부터 channelId가 반드시 필요하다.
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("");
        builder.setContentText("타이머 실행중");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0 );
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//오레오 이상부터 이 코드가 동작한다.
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());//id를 0으로 하면안된다.
    }

    private void stopForegroundService(){
        stopForeground(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }



    class BackgroundThread extends Thread {

        public void run() {

            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {}


                //달력에서 시간, 분 가져오기
                today = new GregorianCalendar();
                int HOUR = today.get(today.HOUR_OF_DAY);
                int MINUTE = today.get(today.MINUTE);

                sp = getSharedPreferences("sFile", MODE_PRIVATE);
                int hour = sp.getInt("hour", -1);
                int minute = sp.getInt("minute", -1);


                if (hour == HOUR && minute == MINUTE && State) {

                    String url = "http://192.168.0.40/off"; //http://192.168.0.40/off
                    StringRequest request = new StringRequest(
                            Request.Method.GET,
                            url,
                            new Response.Listener<String>() {  //응답을 문자열로 받아서 여기다 넣어달란말임(응답을 성공적으로 받았을 떄 이메소드가 자동으로 호출됨
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, hour + "시 " + minute + "분 불끄기 명령 완료됨!!!!!! ");
                                    stopForegroundService();
                                }
                            },
                            new Response.ErrorListener(){ //에러발생시 호출될 리스너 객체
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    ){};

                    request.setShouldCache(false);
                    requestQueue.add(request);

                    State =false;
                }

                /*handler.post(new Runnable() {
                    public void run() {
                        textView.setText("value 값 : " + value);
                    }
                });*/
            }
        }
    }
}






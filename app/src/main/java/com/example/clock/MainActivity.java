package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.Volley;

import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    TimePicker timePicker;
    int getHour,getMinute;

    SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        timePicker = findViewById(R.id.timePicker);
        Button button1 = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);

        sp = getSharedPreferences("sFile", MODE_PRIVATE);
        int curhour = sp.getInt("hour", -1);
        int curminute = sp.getInt("minute", -1);

        textView.setText(curhour+ "시 "+ curminute +"분에 예약" );

        Intent intent = new Intent(getApplicationContext(), MyService.class);

        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                time_reservation();

                intent.setAction("startForeground");
                //오레오 이상부터 동작하는 코드
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }


            }
        });

        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                intent.setAction("stopForeground");
                //오레오 이상부터 동작하는 코드
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }

            }
        });



    }


    public void time_reservation(){


        //TimePicker에서 시간 가져오기
        if(Build.VERSION.SDK_INT < 23){
            getHour = timePicker.getCurrentHour();
            getMinute = timePicker.getCurrentMinute();
        } else{
            getHour = timePicker.getHour();
            getMinute = timePicker.getMinute();

        }

        //데이터 저장
        SharedPreferences sp = getSharedPreferences("sFile",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("hour",getHour);
        editor.putInt("minute",getMinute);
        editor.commit();

        //텍스트에 현재 예약 시간 표시
        textView.setText(getHour+ "시 "+ getMinute +"분에 예약" );


    }



}
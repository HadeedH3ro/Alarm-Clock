package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.alarmclock.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannel();
        binding.selecttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              timePicker = new MaterialTimePicker.Builder()
                      .setTimeFormat(TimeFormat.CLOCK_12H)
                      .setHour(12)
                      .setMinute(0)
                      .setTitleText("Select Alarm Time")
                      .build();
              timePicker.show(getSupportFragmentManager(), "Hadeed");
              timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     if(timePicker.getHour()>12){
                         binding.selecttime.setText(
                                String.format("%02d",(timePicker.getHour()-12))+":"+ String.format("%02d",timePicker.getHour())+"PM"

                         );
                     }
                     else{
                        binding.selecttime.setText(timePicker.getHour()+":"+timePicker.getMinute()+"AM");
                     }
                     calendar= Calendar.getInstance();
                     calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                     calendar.set(Calendar.MINUTE, timePicker.getMinute());
                     calendar.set(Calendar.SECOND,0);
                     calendar.set(Calendar.MILLISECOND,0);
                  }
              });

            }
        });
        binding.setalarm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedImmutableFlag")
            @Override
            public void onClick(View v) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent );
                Toast.makeText(MainActivity.this,"Alarm Set", Toast.LENGTH_SHORT).show();

            }
        });

        binding.Canclealarm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedImmutableFlag")
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
              //  pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0, intent,0);

              if(alarmManager == null){
                   alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

              }
              alarmManager.cancel(pendingIntent);
              Toast.makeText(MainActivity.this,"Alarm Canceled",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           CharSequence name = "Hadeed";
           String desc = "Alarm Management";
           int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Hadeed", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
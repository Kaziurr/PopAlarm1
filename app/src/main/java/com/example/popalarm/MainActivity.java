package com.example.popalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextClock textClock;
    private Button setAlarmButton;
    private Button stopAlarmButton;
    private Ringtone ringtone;
    
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);
        textClock = findViewById(R.id.textClock);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        stopAlarmButton = findViewById(R.id.stopAlarmButton);
        stopAlarmButton.setEnabled(false);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                playAlarm();
                textView.setText("Alarm aktywowany");
                stopAlarmButton.setEnabled(true);
            }
        }, new IntentFilter("ALARM_ACTION"));
    }
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setAlarm(selectedHour, selectedMinute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("ALARM_ACTION");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        Calendar currentTime = Calendar.getInstance();

        if (alarmTime.before(currentTime)) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long alarmTriggerTime = alarmTime.getTimeInMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTriggerTime, pendingIntent);

        Toast.makeText(this, "Alarm ustawionyy na " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        stopAlarmButton.setEnabled(false);
    }

    private boolean playAlarm() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();
        return false;
    }

    private void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            stopAlarmButton.setEnabled(false);
            textView.setText("Alarm nieaktywny");
        }
    }
}

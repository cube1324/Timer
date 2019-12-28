package com.example.testdrawer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.appwidget.AppWidgetManager;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.testdrawer.Support.ListElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.testdrawer.App.CHANNEL_ID;

public class TimerService extends Service {
    public class TimerBinder extends Binder{
        TimerService getService(){
            return TimerService.this;
        }
    }


    public static final String ELEMENTS_MESSAGE = "elements";
    public static final String UPDATE_UI = "update-ui";
    public static final String UPDATE_NAMES_BOOL = "update-names";
    public static final String UPDATE_CURRENT_NAME = "update_current_name";
    public static final String UPDATE_NEXT_NAME = "update_next_name";
    public static final String UPDATE_TIMER = "update_timer";
    public static final String UPDATE_CURRENT_LOOP = "update_loop_name";
    public static final String UPDATE_CURRENT_REPETITIONS = "update_loop_repetitions";

    private static final int CHANEL_ID = 1;



    private ArrayList<ListElement> elements;
    private RemoteViews contentView;
    private CountDownTimer mCountDownTimer;
    private TimerBinder mBinder;
    private int currentPos = 0;
    private long mTimeLeft = -1;
    private MediaPlayer high_player;
    private MediaPlayer low_player;
    PendingIntent pendingIntent;

    public boolean isPaused = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (elements == null) {
            elements = (ArrayList<ListElement>) intent.getSerializableExtra(ELEMENTS_MESSAGE);
        }

        if (mTimeLeft == -1){
            mTimeLeft = elements.get(currentPos).getNumber();
        }

        //TODO broken
        Intent timerIntent = new Intent(this, TimerActivity.class);
        timerIntent.putExtra("data", elements);
        pendingIntent = PendingIntent.getActivity(this, 0, timerIntent, 0);

        contentView = new RemoteViews(getPackageName(), R.layout.timer_notification);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(contentView)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(CHANEL_ID, notification);
        updateUI(true);
        return START_NOT_STICKY;
    }

    private void updateUI(boolean updateNames){
        //Send message to TimerActivity to update UI
        Intent message = new Intent(UPDATE_UI);
        if (updateNames){
            message.putExtra(UPDATE_NAMES_BOOL, true);
            message.putExtra(UPDATE_CURRENT_NAME, elements.get(currentPos).getName());
            message.putExtra(UPDATE_CURRENT_LOOP, elements.get(currentPos).getCurrentLoopName());
            message.putExtra(UPDATE_CURRENT_REPETITIONS, elements.get(currentPos).getCurrentLoopNum());
            message.putExtra(UPDATE_NEXT_NAME, currentPos+1 < elements.size() ? elements.get(currentPos).getName() : "Done");

            contentView.setTextViewText(R.id.name_view, elements.get(currentPos).getName());
            contentView.setTextViewText(R.id.next_name_view, currentPos+1 < elements.size() ? elements.get(currentPos).getName() : "Done");
        }else {
            message.putExtra(UPDATE_NAMES_BOOL, false);
        }
        message.putExtra(UPDATE_TIMER, formatTime(mTimeLeft));
        LocalBroadcastManager.getInstance(this).sendBroadcast(message);

        //Update notification and apply changes
        contentView.setTextViewText(R.id.timer_view, formatTime(mTimeLeft));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(contentView)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(CHANEL_ID, notification);
    }

    private String formatTime(long time){
        int min = (int) time / 1000 / 60;
        int sec = (int) time / 1000 % 60;
        return String.format("%02d : %02d", min, sec);
    }

    public void timerButton(){
        if (isPaused){
            startTimer();
        }else {
            pauseTimer();
        }
    }

    /*
    public Map<String, String> getUI(){
        Map<String, String> map = new HashMap<>();
        map.put(UPDATE_CURRENT_NAME, elements.get(currentPos).getName());
        map.put(UPDATE_CURRENT_LOOP, elements.get(currentPos).getCurrentLoopName());
        map.put(UPDATE_CURRENT_REPETITIONS, elements.get(currentPos).getCurrentLoopNum());
        map.put(UPDATE_TIMER, formatTime(mTimeLeft));
        map.put(UPDATE_NEXT_NAME, currentPos+1 < elements.size() ? elements.get(currentPos+1).getName() : "Done");

        return map;
    }
     */

    public void skipButton(){
        //TODO
    }

    public void reverseButton(){
        //TODO
    }

    private void startTimer(){
        if (isPaused){
            isPaused = false;
        }else {
            mTimeLeft = elements.get(currentPos).getNumber();
        }
        mCountDownTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                updateUI(false);
            }

            @Override
            public void onFinish() {
                currentPos++;
                if (currentPos < elements.size()){
                    mTimeLeft = elements.get(currentPos).getNumber();
                    updateUI(true);
                    startTimer();
                }else {
                    currentPos = 0;
                    isPaused = true;
                    updateUI(true);
                }
            }
        }.start();
    }

    private void pauseTimer(){
        mCountDownTimer.cancel();
        isPaused = true;
        //TODO update ui
    }

    @Override
    public void onDestroy() {
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null){
            mBinder = new TimerBinder();
        }
        return mBinder;
    }
}

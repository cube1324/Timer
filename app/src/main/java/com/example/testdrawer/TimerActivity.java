package com.example.testdrawer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.testdrawer.Dialogs.TimerEditDialog;
import com.example.testdrawer.Support.ListElement;
import com.example.testdrawer.Support.LoopStartElement;
import com.example.testdrawer.Support.TimerElement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;

import static com.example.testdrawer.TimerService.ELEMENTS_MESSAGE;
import static com.example.testdrawer.TimerService.UPDATE_BUTTON_BOOL;
import static com.example.testdrawer.TimerService.UPDATE_BUTTON_RESOURCE;
import static com.example.testdrawer.TimerService.UPDATE_CURRENT_LOOP;
import static com.example.testdrawer.TimerService.UPDATE_CURRENT_NAME;
import static com.example.testdrawer.TimerService.UPDATE_CURRENT_REPETITIONS;
import static com.example.testdrawer.TimerService.UPDATE_NAMES_BOOL;
import static com.example.testdrawer.TimerService.UPDATE_NEXT_NAME;
import static com.example.testdrawer.TimerService.UPDATE_TIMER;
import static com.example.testdrawer.TimerService.UPDATE_UI;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ListElement> elements;

    private TextView text_view_countdown;
    private TextView name_view;
    private TextView loop_name;
    private TextView loop_repetition;
    private TextView next_timer;
    private FloatingActionButton timer_button;
    private FloatingActionButton skip_button;
    private FloatingActionButton reverse_button;

    private boolean myServiceBound = false;
    private TimerService mService;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //Setup Top Bar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Create Back Button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Keep Screen on Permanently
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Initialize Elements in view
        text_view_countdown = findViewById(R.id.text_view_countdown);
        name_view = findViewById(R.id.name_view);
        loop_name = findViewById(R.id.loop_name_view);
        loop_repetition = findViewById(R.id.loop_current_view);
        next_timer = findViewById(R.id.next_up_name);
        timer_button = findViewById(R.id.timer_button);
        skip_button = findViewById(R.id.skip_button);
        reverse_button = findViewById(R.id.reverse_button);

        //Get elements from Mainactivity
        Intent intent = getIntent();
        elements = (ArrayList<ListElement>) intent.getSerializableExtra("data");

        //Register as receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(UPDATE_UI));

        //Start service for timer
        serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra(ELEMENTS_MESSAGE, elements);
        startService(serviceIntent);

        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

        timer_button.setOnClickListener(this);
        skip_button.setOnClickListener(this);
        reverse_button.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        //Register as receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(UPDATE_UI));

        //Start service for timer
        serviceIntent.putExtra(ELEMENTS_MESSAGE, elements);
        startService(serviceIntent);

        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
        super.onResume();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(UPDATE_BUTTON_BOOL, false)){
                final AnimatedVectorDrawableCompat avd = AnimatedVectorDrawableCompat.create(getApplicationContext(), intent.getIntExtra(UPDATE_BUTTON_RESOURCE, R.drawable.play_to_pause_anim));
                timer_button.setImageDrawable(avd);
                avd.start();
            }

            if (intent.getBooleanExtra(UPDATE_NAMES_BOOL, false)){
                name_view.setText(intent.getStringExtra(UPDATE_CURRENT_NAME));
                loop_name.setText(intent.getStringExtra(UPDATE_CURRENT_LOOP));
                loop_repetition.setText(intent.getStringExtra(UPDATE_CURRENT_REPETITIONS));
                next_timer.setText(intent.getStringExtra(UPDATE_NEXT_NAME));
            }
            text_view_countdown.setText(intent.getStringExtra(UPDATE_TIMER));

        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            mService = binder.getService();
            myServiceBound = true;
            timer_button.setImageResource(mService.isPaused ? R.drawable.ic_play_arrow_32px : R.drawable.ic_pause_black_40dp);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myServiceBound = false;
        }
    };

    //Handle Button Clicks
    @Override
    public void onClick(View v) {
        if (myServiceBound) {
            switch (v.getId()) {
                case R.id.timer_button:
                    final AnimatedVectorDrawableCompat avd = AnimatedVectorDrawableCompat.create(getApplicationContext(), mService.isPaused ?  R.drawable.play_to_pause_anim : R.drawable.pause_to_play_anim);
                    timer_button.setImageDrawable(avd);
                    avd.start();

                    mService.timerButton();
                    break;
                case R.id.skip_button:
                    mService.skipButton();
                    break;
                case R.id.reverse_button:
                    mService.reverseButton();
                    break;
            }
        }
    }


    public void onStop(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if(myServiceBound){
            unbindService(mServiceConnection);
            myServiceBound = false;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
    }


}

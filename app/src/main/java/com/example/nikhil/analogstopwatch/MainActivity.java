package com.example.nikhil.analogstopwatch;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    long mStartTime = 0L,mStartSubTime = 0L;
    long mTimeInMilliseconds = 0L,tTimeInMilliseconds = 0L;
    long mTimeSwapBuff = 0L,tTimeSwapBuff = 0L;
    long mUpdatedTime = 0L, tUpdatedTime = 0L;
    int resetConstant = 1;
    int secs = 0,tSecs = 0;
    int mins = 0,tMins = 0;
    int milliseconds = 0, tMilliseconds = 0;
    Handler handler = new Handler();
    Handler subHandler = new Handler();



    @BindView(R.id.start)
    FloatingActionButton butnstart;
    @BindView(R.id.reset)
    FloatingActionButton butnreset;

    @BindView(R.id.big_hand_iv)
    ImageView bigHandIv;
    @BindView(R.id.small_hand_iv)
    ImageView smallHandIv;


    @BindView(R.id.sub_times_ll)
    LinearLayout subTimeLinearLayout;


    @BindView(R.id.left_timer)
    TextView leftTimer ;
    @BindView(R.id.right_timer)
    TextView rightTimer ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.start)
    public void startPressed(){
        if (resetConstant == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                butnstart.setImageResource(android.R.drawable.ic_media_pause);
            }
            mStartTime = SystemClock.uptimeMillis();
            mStartSubTime = SystemClock.uptimeMillis();

            handler.postDelayed(updateTimer, 0);
            subHandler.postDelayed(updateSubTimer, 0);
            resetConstant = 0;
        } else {
            butnstart.setImageResource(android.R.drawable.ic_media_play);
            mTimeSwapBuff += mTimeInMilliseconds;
            tTimeSwapBuff += tTimeInMilliseconds;
            handler.removeCallbacks(updateTimer);
            subHandler.removeCallbacks(updateSubTimer);
            resetConstant = 1;
        }
    }

    @OnClick(R.id.reset)
    public void resetPressed(){
        mStartTime = 0L;
        mTimeInMilliseconds = 0L;
        mTimeSwapBuff = 0L;
        mUpdatedTime = 0L;
        resetConstant = 1;
        secs = 0;
        mins = 0;
        milliseconds = 0;
        butnstart.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(updateTimer);
        subHandler.removeCallbacks(updateSubTimer);
        rightTimer.setText("00:00:00");
        leftTimer.setText("00:00:00");
        performTick(0, 0);
        subTimeLinearLayout.removeAllViewsInLayout();
    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            mTimeInMilliseconds = SystemClock.uptimeMillis() - mStartTime;
            mUpdatedTime = mTimeSwapBuff + mTimeInMilliseconds;
            secs = (int) (mUpdatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (mUpdatedTime % 1000);
            performTick(secs, mins);
            rightTimer.setText(String.format("%d:%s:%s", mins, String.format("%02d", secs), String.format("%03d", milliseconds)));
            handler.postDelayed(this, 0);
        }
    };

    public Runnable updateSubTimer = new Runnable() {
        public void run() {
            tTimeInMilliseconds = SystemClock.uptimeMillis() - mStartSubTime;
            tUpdatedTime = tTimeSwapBuff + tTimeInMilliseconds;

            tSecs = (int) (tUpdatedTime / 1000);
            tMins = tSecs / 60;
            tSecs = tSecs % 60;
            tMilliseconds = (int) (tUpdatedTime % 1000);
            leftTimer.setText(String.format("%d:%s:%s", tMins, String.format("%02d", tSecs), String.format("%03d", tMilliseconds)));
            subHandler.postDelayed(this, 0);
        }
    };

    public void performTick(int seconds, int min) {
        rotateHand((seconds == 59) ? min + 1 : min, smallHandIv);
        rotateHand(seconds, bigHandIv);
    }

    private void rotateHand(int seconds, ImageView handView) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                (seconds) * 6, seconds * 6,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);

        handView.startAnimation(rotateAnimation);
    }

    public void working(View view){
        if(resetConstant==1){
            return ;
        }
        mStartSubTime = 0L;tTimeInMilliseconds = 0L;tTimeSwapBuff = 0L;
        tUpdatedTime = 0L;tSecs = 0;tMins = 0;tMilliseconds = 0;

        LinearLayout timerRow = getSubTimeRow();
        ((TextView) timerRow.findViewById(R.id.left_timer)).setText(leftTimer.getText().toString());
        ((TextView) timerRow.findViewById(R.id.right_timer)).setText(rightTimer.getText().toString());
        subTimeLinearLayout.addView(timerRow,0);
        subHandler.removeCallbacks(updateSubTimer);
        mStartSubTime = SystemClock.uptimeMillis();
        subHandler.postDelayed(updateSubTimer, 0);

    }

    public LinearLayout getSubTimeRow(){
        LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       return  (LinearLayout) inflater.inflate(R.layout.timer_layout, null);
    }

}


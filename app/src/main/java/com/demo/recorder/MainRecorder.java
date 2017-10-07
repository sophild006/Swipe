package com.demo.recorder;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.swipe.demo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wwq on 2017/10/7.
 */

public class MainRecorder extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    private MediaPlayer mPlayer;
    Handler mHandler=new Handler();
    private SeekBar seekBar;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_recorder);

        seekBar= (SeekBar) findViewById(R.id.play_song_seekbar);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#50339dff"), PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.parseColor("#349dff"), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(this);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
    }


    public void StartRecorder(View view){
        Log.d("wwq","Start...");
        if (AudioRecorderManger.getInstance().startRecord()) {
            Log.d("wwq","start recorder");
        }

    }
    String sessionId;
    public void StopRecorder(View view){
        Log.d("wwq","StopRecorder...");
        sessionId= AudioRecorderManger.getInstance().stopRecord();
        Log.d("wwq","seesionId: "+sessionId);
        AudioRecorderManger.getInstance().releaseSession();
        playRecord();

    }

    public void playRecorder(View view){
        Log.d("wwq","playRecorder..."+  sessionId);
        try {
            if (mPlayer.isPlaying()) {
                if (mPlayer != null) {
                    mPlayer.pause();
                }
            } else {
                if (mPlayer != null) {
                    mPlayer.start();
                    Log.d("wwq","----------------");
                    updateProgressBar();
                } else {
                    playRecord();
                }
            }
        } catch (Exception e) {
            Log.d("wwq","e: "+e.getLocalizedMessage());
            e.printStackTrace();
        }


    }
    private static final String PATH = Environment.getExternalStorageDirectory() + "/audio/";
    public void playRecord() {
        Log.d("wwq","playRecord  "+sessionId);

        try {
            mPlayer.reset();
            File filePath = new File(PATH + sessionId + ".amr");
            FileInputStream is = new FileInputStream(filePath);
            mPlayer.setDataSource(is.getFD());
            mPlayer.prepare();
            is.close();
        } catch (Exception e) {
            Log.d("wwq","e:  "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
        Log.d("wwq","updateProgressBar...");
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();
            Log.d("wwq","mUpdateTimeTask    " + totalDuration);
            Log.d("wwq","mUpdateTimeTask    " + currentDuration);
            int progress = getProgressPercentage(currentDuration, totalDuration);
            seekBar.setProgress(progress);
            mHandler.postDelayed(this, 100);
        }
    };

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("wwq","completion...");
        mHandler.removeCallbacks(mUpdateTimeTask);
        mPlayer.seekTo(0);
        seekBar.setProgress(0);
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("wwq","onPrepared");
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setVolume(1.5f, 1.5f);
        mPlayer.start();
        seekBar.setProgress(0);
        seekBar.setMax(100);
        updateProgressBar();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
        mPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }
}

package com.demo.recorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.swipe.demo.R;

import java.io.File;
import java.util.UUID;

/**
 * Created by caosc on 2016/12/14.
 */

public class AudioRecorderManger {

    private static AudioRecorderManger instance;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording;
    private String mSessionId;

    private static final String PATH = Environment.getExternalStorageDirectory() + "/audio";

    public static AudioRecorderManger getInstance() {
        if (instance == null) {
            synchronized (AudioRecorderManger.class) {
                if (instance == null) {
                    instance = new AudioRecorderManger();
                }
            }
        }
        return instance;
    }

    private AudioRecorderManger() {

    }

    public boolean startRecord() {
        Log.d("wwq","startRecord" + isRecording);
        if (isRecording) {
            Log.d("wwq","recording return");
            return false;
        }
        mSessionId = UUID.randomUUID().toString();
        String path = getFilePath(mSessionId);
        File directory = new File(path).getParentFile();

        if (!directory.exists() && !directory.mkdirs()) {
            return false;
        }


        if (TextUtils.isEmpty(path)) {
            mSessionId = "";
            return false;
        }
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(path);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            Log.d("wwq","record  exception:" + e);
            return startRecordForException(path);
        }
        isRecording = true;
        return true;
    }


    public boolean startRecordForException(String path) {
        Log.d("wwq","startRecordForException");
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(path);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            Log.d("wwq","startRecordForException" + e);
            mMediaRecorder = null;
            mSessionId = "";
            return false;
        }
        isRecording = true;
        Toast.makeText(GlobalContext.getAppContext(), "不支持", Toast.LENGTH_LONG).show();
        return true;
    }

    public String stopRecord() {
        if (!isRecording) {
            return null;
        }

        if (TextUtils.isEmpty(mSessionId)) {
            return null;
        }

        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            } catch (Exception e) {
                Log.d("wwq",e.getLocalizedMessage());
                isRecording = false;
                mMediaRecorder = null;
                return null;
            }
            isRecording = false;
            mMediaRecorder = null;
        }
        return mSessionId;
    }

    public void releaseSession() {
        mSessionId = "";
    }


    private String getFilePath(String sessionId) {
        if (!sessionId.startsWith("/")) {
            sessionId = "/" + sessionId;
        }
        if (!sessionId.contains(".")) {
            sessionId += ".amr";
        }
        return PATH + sessionId;
    }

}

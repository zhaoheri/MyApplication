package test.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zhaoheri on 3/5/15.
 */
public class AlarmReceiver extends BroadcastReceiver {


    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub


        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        // Show the toast  like in above screen shot
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        Log.d("ALARM", "receive alarm");
        mContext = context;
        AudioManager am;
        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
    }

}


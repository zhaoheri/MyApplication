package test.myapplication;

/**
 * Created by zhaoheri on 3/3/15.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;


public class MainActivity extends Activity {

    SeekBar sb;
    MediaPlayer mp;
    AudioManager am;
    TimePicker tp_start;
    TimePicker tp_end;
    MyDBHelper myDBHelper;
    Integer vol;

    // database variable
    MyDB myDB;

    // alarm variable
    PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = (SeekBar) findViewById(R.id.sbVolume);
        mp = MediaPlayer.create(this, R.raw.sample);
        mp.start();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxV = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(maxV);
        sb.setProgress(curV);
        vol = curV;
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                vol = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tp_start = (TimePicker) findViewById(R.id.timePicker_start);
        tp_end = (TimePicker) findViewById(R.id.timePicker_end);

        // database set up
        myDB = new MyDB(this);
        try {
            myDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTime(View V)
    {
        // get current system time
        // not need to use for now
//        DateFormat[] formats = new DateFormat[] {
//                DateFormat.getDateInstance(),
//                DateFormat.getDateTimeInstance(),
//                new SimpleDateFormat("HH:mm"),
//                DateFormat.getTimeInstance()
//        };
//        for (DateFormat df : formats) {
//            df.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
//            Log.d("TIME", df.format(new Date()));
//        }

        String str;
        str = "  timepicker is " + tp_start.getCurrentHour() + " : " + tp_start.getCurrentMinute();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();

        // insert into database
        String start_time;
        String end_time;
        String type;
        start_time = tp_start.getCurrentHour() + ":" + tp_start.getCurrentMinute();
        end_time = tp_end.getCurrentHour() + ":" + tp_end.getCurrentMinute();
        type = vol.toString();
        Rules rule = new Rules(start_time, end_time, type);
        myDB.insert(rule);
        Log.d("DB", "insert: " + start_time + ", " + end_time + ", " + type);
        List<Rules> rulesList = myDB.select();
        Log.d("DB", "database list: ");
        for (Rules r : rulesList)
            Log.d("DB", r.start_time + ", " + r.end_time + ", " + r.vtype);

        // register time into alarm
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.putExtra("vtype", 0);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // calculate first alarm time in millis
        long currTime = System.currentTimeMillis();
        DateFormat[] formats = new DateFormat[] {
                new SimpleDateFormat("HH"),
                new SimpleDateFormat("mm")
        };

        int interval = 8000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

    }



    @Override
    protected void onResume() {
        try {
            myDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        myDB.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action b
        // ar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package test.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TimePicker;


public class EditRuleActivity extends Activity {

    public final static int DELETE = 101;

    private SeekBar sb;
    private MediaPlayer mp;
    private AudioManager am;
    private TimePicker tp_start;
    private TimePicker tp_end;
    private Integer vol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rule); sb = (SeekBar) findViewById(R.id.sbVolume);

        Intent intent = getIntent();

        String startTime = intent.getStringExtra(MainActivity.START_TIME);
        String endTime = intent.getStringExtra(MainActivity.END_TIME);
        String volume = intent.getStringExtra(MainActivity.VOLUME);

        mp = MediaPlayer.create(this, R.raw.sample);
        mp.start();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxV = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = Integer.parseInt(volume);
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
        int startHour = Integer.parseInt(startTime.substring(0, startTime.indexOf(':')));
        int startMin = Integer.parseInt(startTime.substring(startTime.indexOf(':') + 1));
        tp_start.setCurrentHour(startHour);
        tp_start.setCurrentMinute(startMin);
        tp_end = (TimePicker) findViewById(R.id.timePicker_end);
        int endHour = Integer.parseInt(endTime.substring(0, endTime.indexOf(':')));
        int endMin = Integer.parseInt(endTime.substring(endTime.indexOf(':') + 1));
        tp_end.setCurrentHour(endHour);
        tp_end.setCurrentMinute(endMin);
    }

    public void saveChange(View V) {
        Intent intent = getIntent();
        String start_time;
        String end_time;
        String volume;
        start_time = tp_start.getCurrentHour() + ":" + tp_start.getCurrentMinute();
        end_time = tp_end.getCurrentHour() + ":" + tp_end.getCurrentMinute();
        volume = vol.toString();
        intent.removeExtra(MainActivity.START_TIME);
        intent.removeExtra(MainActivity.END_TIME);
        intent.removeExtra(MainActivity.VOLUME);
        intent.putExtra(MainActivity.START_TIME, start_time);
        intent.putExtra(MainActivity.END_TIME, end_time);
        intent.putExtra(MainActivity.VOLUME, volume);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void deleteRule(View V) {
        setResult(DELETE, getIntent());
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }
    @Override
    protected void onPause() {
        mp.stop();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_rule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

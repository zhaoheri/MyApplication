package test.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class DisplayRuleActivity extends ListActivity {

    public final static int EDIT_RULE_CODE = 1;

    // database variable
    private MyDB myDB;
    private List<Rule> rules;
    private List<Long> ruleIDs;
    private long editRuleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_rule);

        // database set up
        myDB = new MyDB(this);
        try {
            myDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String startTime = null;
        String endTime = null;
        String volume = null;

        Intent intent = getIntent();

        startTime = intent.getStringExtra(MainActivity.START_TIME);
        endTime = intent.getStringExtra(MainActivity.END_TIME);
        volume = intent.getStringExtra(MainActivity.VOLUME);

        if (startTime != null && endTime != null && volume !=null) {
            Rule rule = new Rule(startTime, endTime, volume);
            Log.d("DB", "insert: " + startTime + ", " + endTime + ", " + volume);
            myDB.insert(rule);

            // register time into alarm
            Intent intentAlarm = new Intent(this, AlarmReceiver.class);
            intentAlarm.putExtra("volume", 0);
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // calculate first alarm time in millis
            long currTime = System.currentTimeMillis();
            DateFormat[] formats = new DateFormat[] {
                    new SimpleDateFormat("HH"),
                    new SimpleDateFormat("mm")
            };
            int interval = 24 * 60 * 60 * 1000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currTime + 10 * 1000, interval, pendingIntent);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
        Log.d("DB", "database list: ");
        rules = myDB.select();

        ruleIDs = new ArrayList<Long>();
        ArrayList<String> ruleStrings = new ArrayList<String>();
        for (Rule r : rules) {
            String tmp = convertTime(r.getStart_time()) + " - " + convertTime(r.getEnd_time()) + " Volume: " + r.getVolume();
            ruleStrings.add(tmp);
            ruleIDs.add(r.getId());
            Log.d("DB", r.getStart_time() + ", " + r.getEnd_time() + ", " + r.getVolume());
        }
        editRuleId = -1;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ruleStrings);
        setListAdapter(adapter);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        editRuleId = ruleIDs.get(position).longValue();

        Intent intent = new Intent(this, EditRuleActivity.class);

        intent.putExtra(MainActivity.START_TIME, rules.get(position).getStart_time());
        intent.putExtra(MainActivity.END_TIME, rules.get(position).getEnd_time());
        intent.putExtra(MainActivity.VOLUME, rules.get(position).getVolume());
        startActivityForResult(intent, EDIT_RULE_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_RULE_CODE) {

            if (resultCode == RESULT_OK) {
                String startTime = data.getStringExtra(MainActivity.START_TIME);
                String endTime = data.getStringExtra(MainActivity.END_TIME);
                String volume = data.getStringExtra(MainActivity.VOLUME);

                myDB.updateById(editRuleId, startTime, endTime, volume);

            } else if (resultCode == EditRuleActivity.DELETE) {
                myDB.deleteById(editRuleId);
            }
            rules = myDB.select();

            ruleIDs = new ArrayList<Long>();
            ArrayList<String> ruleStrings = new ArrayList<String>();
            for (Rule r : rules) {
                String tmp = convertTime(r.getStart_time()) + " - " + convertTime(r.getEnd_time()) + " Volume: " + r.getVolume();
                ruleStrings.add(tmp);
                ruleIDs.add(r.getId());
                Log.d("DB", r.getStart_time() + ", " + r.getEnd_time() + ", " + r.getVolume());
            }
            editRuleId = -1;

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, ruleStrings);
            setListAdapter(adapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_rule, menu);
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
        } else if (id == R.id.action_add_rule) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String convertTime(String s) {
        String res = null;
       int hour = Integer.parseInt(s.substring(0, s.indexOf(':')));
        if (hour > 11) {
            if (hour != 12) {
                hour -= 12;
            }
            res = hour + s.substring(s.indexOf(':')) + " PM";
        } else {
            if (hour == 0) {
                hour += 12;
            }
            res = hour + s.substring(s.indexOf(':')) + " AM";
        }
        return res;
    }
}

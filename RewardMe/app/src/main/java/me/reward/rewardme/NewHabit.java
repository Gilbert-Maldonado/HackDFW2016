package me.reward.rewardme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import me.reward.rewardme.DataObjects.Habit;
import me.reward.rewardme.DataObjects.UserInfo;

public class NewHabit extends AppCompatActivity implements ApiCallLibrary.Callback {

    private EditText habitName;
    private TextView deadline;
    private TextView expiration_date;
    private String repeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_habit);
        habitName = (EditText) findViewById(R.id.habit_activity);
        deadline = (TextView) findViewById(R.id.deadline);
        expiration_date = (TextView) findViewById(R.id.expiration_date);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_habit, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_none:
                if (checked)
                    repeats = "none";
                    break;
            case R.id.radio_daily:
                if (checked)
                    repeats = "daily";
                    break;
            case R.id.radio_weekly:
                if (checked)
                    repeats = "weekly";
                    break;
            case R.id.radio_monthly:
                if (checked)
                    repeats = "monthly";
                    break;
        }
    }

    public void createTask(View view) {
        String title = habitName.getText().toString();
        String current_date  = "4172016";
        String deadline_date = deadline.getText().toString();
//        String expiration = expiration_date.getText().toString();
        String expiration = "7295084";
        Habit newHabit = new Habit(title, repeats,
                current_date, deadline_date,
                expiration);
        UserInfo.addNewHabit(newHabit);

        String url = "http://www.utexas.io/user/"
                + UserInfo.getCurrentUserId() + "/tasks/add";
        try{
            JSONObject newUser = new JSONObject().put("title", title).put("frequency", repeats)
                    .put("currentDate", current_date).put("deadlineDate", deadline_date)
                    .put("endDate", expiration);
            Log.d("Loggin!!!", newUser.toString());
            Log.d("Loggin!!!", url + " URL");
            ApiCallLibrary add_task = new ApiCallLibrary(url, newUser.toString());
            add_task.post(this);
        } catch (JSONException|IOException e) {
            e.printStackTrace();
        }




    }

    public void onResponse(String s) {
        Toast.makeText(this, "TASK ADDED", Toast.LENGTH_SHORT);
    }
}


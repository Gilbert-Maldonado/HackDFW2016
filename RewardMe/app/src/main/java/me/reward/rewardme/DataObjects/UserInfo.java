package me.reward.rewardme.DataObjects;

import com.getpebble.android.kit.PebbleKit;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.reward.rewardme.R;

/**
 * Created by Cindy on 4/16/2016.
 */
public class UserInfo {

    public static final int PEBBLE_HABIT_MAX = 5;
    private String name;
    private String email;
    private String id;
    private List<String> habitsString;
    private List<Habit> habits;
    private double balance;
    public static UserInfo currentUser;


    public UserInfo() {
        habitsString = new ArrayList<String>();
        habitsString.add("HELLO");
        habitsString.add("THIS SUCKS");
        habitsString.add("SO SO LONG");
        habits = new ArrayList<Habit>();
    }

    public UserInfo(JSONObject json) {
        this();
        try {
            if (json.has("name"))
                name = json.getString("name");
            if (json.has("email"))
                email = json.getString("email");
            if (json.has("id"))
                id = json.getString("id");

            // do something about the habits here
            // i think it is a json array of objects
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserInfo(String name, String email) {
        this(email);
        this.name = name;
    }

    public UserInfo(String email) {
        this();
        this.email = email;
    }

    public static synchronized void setCurrentUser(UserInfo user) {
        currentUser = user;
    }

    public static synchronized UserInfo getCurrentUser() {
        return currentUser;
    }

    public static synchronized UserInfo setCurrentId(String id) {
        assert(currentUser != null);
        currentUser.id = id;
        return currentUser;
    }

    public static synchronized List<String> getCurrentHabitsString() {
        return currentUser.habitsString;
    }

    public static synchronized List<Habit> getCurrentHabits() {
        return currentUser.habits;
    }

    public static synchronized void removeHabit(int pos){
        // API CALL to remove stuff, make callback null.
        currentUser.habitsString.remove(pos);
        currentUser.habits.remove(pos);
    }

    public static synchronized void addHabitsJson(JSONArray jsonarray){
        try {
            for (int i = 0; i < jsonarray.length(); i++) {
                Habit habit = new Habit(jsonarray.getJSONObject(i));
                currentUser.habits.add(habit);
                currentUser.habitsString.add(habit.getTitle());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addNewHabit(Habit habit) {
        currentUser.habits.add(habit);
        currentUser.habitsString.add(habit.getTitle());
    }

    private class UserInfoSerializer implements JsonSerializer<UserInfo> {
        public JsonElement serialize(UserInfo src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static synchronized String getCurrentUserId() {
        return currentUser.id;
    }
}


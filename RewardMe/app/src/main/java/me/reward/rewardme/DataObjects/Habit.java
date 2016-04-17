package me.reward.rewardme.DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cindy on 4/16/2016.
 */
public class Habit {
    private int id;
    private String title;
    private String frequency;
    private String creationDate;
    private String deadlineDate;
    private String endDate;
    private int[] counter;

    public Habit(JSONObject json) {
        try{
            if (json.has("id")){
                // make sure this is an ID
                this.id = json.getInt("id");
            }
            if (json.has("title")) {
                this.title = json.getString("title");
            }
            if (json.has("frequency")) {
                this.frequency = json.getString("frequency");
            }
            if (json.has("creationDate")){
                this.creationDate = json.getString("creationDate");
            }
            if (json.has("deadlineDate")){
                this.deadlineDate = json.getString("deadlineDate");
            }
            if (json.has("endDate")) {
                this.endDate = json.getString("endDate");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Habit(String title, String frequency, String creationDate, String deadlineDate, String endDate) {
        this.title = title;
        this.frequency = frequency;
        this.creationDate = creationDate;
        this.deadlineDate = deadlineDate;
        this.endDate = endDate;

    }

    public String getTitle() {
        return this.title;
    }
}

package me.reward.rewardme;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Cindy on 4/16/2016.
 */


public class ApiCallLibrary {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    private Request.Builder mRequest;
    private RequestBody mRequestBody;
    private Headers.Builder mHeaders;

    public ApiCallLibrary(String url){
        mRequest = new Request.Builder().url(url);
    }

    public ApiCallLibrary(String url, String json){
        mRequest = new Request.Builder().url(url);
        mRequestBody = RequestBody.create(JSON, json);
    }


    public void addBody(String json){
        mRequestBody = RequestBody.create(JSON, json);
    }

    public void addHeader(String name, String value) {
        mRequest.addHeader(name, value);
    }

    public void post(Callback callback) throws IOException {
        new PostApi().execute(callback);
    }

    public void get(Callback callback) throws IOException {
        new GetApi().execute(callback);
    }

    private class PostApi extends AsyncTask<Callback, Integer, String> {
        Callback callback;

        protected String doInBackground(Callback ... callback) {
            this.callback = callback[0];
            Request request = mRequest.post(mRequestBody).build();
            String json_result = null;
            try{
                Response response = client.newCall(request).execute();
                json_result = response.body().string();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return json_result;
        }

        protected void onProgressUpdate(Integer... progress) {
//
        }

        protected void onPostExecute(String result) {
            callback.onResponse(result);
        }
    }

    private class GetApi extends AsyncTask<Callback , Integer, String> {
        Callback callback;

        protected String doInBackground(Callback ... callback) {
            this.callback = callback[0];
            Request request = mRequest.build();
            String json_result = null;
            try{
                Response response  = client.newCall(request).execute();
                json_result = response.body().string();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return json_result;
        }

        protected void onProgressUpdate(Integer... progress) {
//
        }

        protected void onPostExecute(String result) {
            callback.onResponse(result);
        }
    }

    public interface Callback {
        void onResponse(String json);
    }
}

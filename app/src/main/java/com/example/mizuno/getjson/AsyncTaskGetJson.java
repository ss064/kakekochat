package com.example.mizuno.getjson;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mizuno on 2016/01/14.
 */
public class AsyncTaskGetJson extends AsyncTask<Void, Void, String> {

    private final static String API_URL = "http://kakeko01.sist.ac.jp/posts/api";
    private JsonActivity activity;


    public AsyncTaskGetJson(JsonActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String result = new String();
        ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String data = EntityUtils.toString(responseEntity);

                JSONObject rootObject = new JSONObject(data);

                JSONArray userArray = rootObject.getJSONArray("Post");
                Log.d("json1_data", userArray.toString());

                for (int n = 0; n < userArray.length(); n++) {
                    // User data
                    JSONObject userObject = userArray.getJSONObject(n);
                    String userId = userObject.getString("id");
                    String userName = userObject.getString("name");
                    String userComment = userObject.getString("comment");
                    result +=userId+":"+ userName+":" +userComment +"\r\n";//末尾に改行コードを付加する。
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        // sの中に,doInBackgroundのResultが格納されている。
        //sを改行コードで分割する。Stringの配列に格納。
        String[] comments =s.split("\r\n");
        for(int i=0;i<comments.length;i++) {
            activity.arrayAdapter.add(comments[i]);
        }
    }
}

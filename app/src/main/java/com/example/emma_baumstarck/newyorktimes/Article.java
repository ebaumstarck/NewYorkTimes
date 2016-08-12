package com.example.emma_baumstarck.newyorktimes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by emma_baumstarck on 8/9/16.
 */
@Parcel
public class Article {

    public String webUrl;
    public String headline;
    public String thumbNail;

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public void setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
    }

    public Article() {}

    public Article(JSONObject jsonObject){
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array ){
        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0 ; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch(JSONException e) {
                e.printStackTrace();
            }

        }

        return results;
    }
}

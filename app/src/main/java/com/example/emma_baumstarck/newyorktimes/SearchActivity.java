package com.example.emma_baumstarck.newyorktimes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    ArrayList<Article> articles;
    ArticleArrayAdapter adpater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
        String query = etQuery.getText().toString();
        onArticleSearch("celebrities");
        gvResults.setAdapter(adpater);

        btnSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

//                set query to the new york search api to get the data

//                Intent activityChangeIntent = new Intent(PresentActivity.this, NextActivity.class);
                // currentContext.startActivity(activityChangeIntent);
//                PresentActivity.this.startActivity(activityChangeIntent);
            }
        });

    }

    public void setupViews(){
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adpater = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adpater);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.more){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String query) {

        params.put("api-key", "76713f4d373043ce8347453635788cf8");
        params.put("page", 0);
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                Log.d("Debug", response.toString());

                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adpater.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        Toast.makeText(this, "Search for" +query, Toast.LENGTH_LONG).show();
    }
}

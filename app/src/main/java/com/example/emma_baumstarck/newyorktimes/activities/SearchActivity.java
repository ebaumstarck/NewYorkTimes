package com.example.emma_baumstarck.newyorktimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.emma_baumstarck.newyorktimes.Article;
import com.example.emma_baumstarck.newyorktimes.ArticleArrayAdapter;
import com.example.emma_baumstarck.newyorktimes.R;
import com.example.emma_baumstarck.newyorktimes.SearchOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.toolbar) Toolbar toolbar;

    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    ArrayList<Article> articles;
    ArticleArrayAdapter adpater;

    SearchOptionsFragment searchOptionsFragment;
    SearchOptions searchOptions;

    String lastQuery;
    int lastQueryPageLoaded;
    boolean searchIsFinished;

    public static final String SEARCH_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static final String API_KEY = "76713f4d373043ce8347453635788cf8";
    private final int ARTICLES_PER_PAGE = 10;
    private final int MINIMUM_ARTICLES_TO_LOAD = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        lastQuery = null;
        lastQueryPageLoaded = -1;
        searchIsFinished = false;
        searchOptions = new SearchOptions();
        searchOptions.year = -1;
        searchOptions.dayOfMonth = -1;
        searchOptions.monthOfYear = -1;
        searchOptions.oldest = false;
        searchOptions.searchArts = false;
        searchOptions.searchFashionStyle = false;
        searchOptions.searchSports = false;

        articles = new ArrayList<>();
        adpater = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adpater);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
            Article article = articles.get(position);
            intent.putExtra("article", Parcels.wrap(article));
            startActivity(intent);
            }
        });

        gvResults.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int numItems = firstVisibleItem + visibleItemCount;
                if (numItems > 0 && numItems >= totalItemCount) {
                    Log.d("SCROLLING", firstVisibleItem + "/" + visibleItemCount + "/" + totalItemCount);
                    loadNextQueryPage();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("QUERY", "Query submitted: " + query);
                adpater.clear();
                onArticleSearch(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("QUERY", "Query changed: " + newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showSearchOptionsFragment() {
        FragmentManager fragment = getSupportFragmentManager();
        searchOptionsFragment = SearchOptionsFragment.newInstance(searchOptions);
        searchOptionsFragment.show(fragment, "search_options_fragment");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            showSearchOptionsFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String query) {
        lastQuery = query;
        lastQueryPageLoaded = -1;
        searchIsFinished = false;
        loadNextQueryPage();
    }

    private int numArticlesLoaded() {
        return Math.max(0, lastQueryPageLoaded) * ARTICLES_PER_PAGE;
    }

    private void loadNextQueryPage() {
        final int nextPage = lastQueryPageLoaded + 1;
        params.put("api-key", API_KEY);
        params.put("page", nextPage);
        params.put("q", lastQuery);

        if (searchOptions.year > 0) {
            params.put(
                    "begin_date",
                    String.format("%d%02d%02d", searchOptions.year, searchOptions.monthOfYear + 1, searchOptions.dayOfMonth));
        }

        params.put("sort", searchOptions.oldest ? "oldest" : "newest");

        if (searchOptions.searchArts || searchOptions.searchFashionStyle || searchOptions.searchSports) {
            List<String> filters = new ArrayList<>();
            if (searchOptions.searchArts) {
                filters.add("Arts");
            }
            if (searchOptions.searchFashionStyle) {
                filters.add("Fashion & Style");
            }
            if (searchOptions.searchSports) {
                filters.add("Sports");
            }
            params.put("fq", "news_desk:(\"" + TextUtils.join("\" \"", filters) + "\")");
        }

        Log.d("SEARCH2", params.toString());
        client.get(SEARCH_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("RESULTS", response.toString());
                lastQueryPageLoaded = nextPage;
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adpater.notifyDataSetChanged();
                    searchIsFinished = articleJsonResults.length() < ARTICLES_PER_PAGE;
                    if (!searchIsFinished && numArticlesLoaded() < MINIMUM_ARTICLES_TO_LOAD) {
                        Log.d("PAGING", numArticlesLoaded() + "/" + MINIMUM_ARTICLES_TO_LOAD);
                        loadNextQueryPage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

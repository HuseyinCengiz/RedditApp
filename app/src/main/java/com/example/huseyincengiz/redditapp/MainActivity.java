package com.example.huseyincengiz.redditapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huseyincengiz.redditapp.Account.LoginActivity;
import com.example.huseyincengiz.redditapp.Comments.CommentsActivity;
import com.example.huseyincengiz.redditapp.model.Feed;
import com.example.huseyincengiz.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private EditText edtfeedName;
    private Button refreshFeed;
    private String CurrentFeedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtfeedName = (EditText) findViewById(R.id.edtFeedName);
        refreshFeed = (Button) findViewById(R.id.btnRefreshFeed);
        setupToolbar();
        Init();
        refreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedName = edtfeedName.getText().toString();
                if (!feedName.equals("")) {
                    CurrentFeedName = feedName;
                    Init();
                } else {
                    Init();
                }
            }
        });
    }

    private void Init() {
        FeedApi feed = FeedClient.getFeedApiImplements(FeedClient.BASE_URL, SimpleXmlConverterFactory.create());
        Call<Feed> mycall = feed.getFeed(CurrentFeedName);
        mycall.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "Onresponse:Server Response " + response.toString());
                List<Entry> entries = response.body().getEntrys();

                final ArrayList<Post> posts = new ArrayList<Post>();
                for (int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML = new ExtractXML(entries.get(i).getContent(), "<a href=");
                    ExtractXML extractXML2 = new ExtractXML(entries.get(i).getContent(), "<img src=");
                    List<String> postContent = extractXML.getResult();
                    try {
                        postContent.add(extractXML2.getResult().get(0));
                    } catch (NullPointerException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse:NullPointerException(Thumbnail):" + e.getMessage());
                    } catch (IndexOutOfBoundsException e) {
                        postContent.add(null);
                        Log.e(TAG, "onResponse:IndexOutOfBoundsException(Thumbnail):" + e.getMessage());
                    }
                    int lastPostPosition = postContent.size() - 1;
                    posts.add(new Post(
                            entries.get(i).getTitle(),
                            entries.get(i).getAuthor().getName(),
                            entries.get(i).getUpdated(),
                            postContent.get(0),
                            postContent.get(lastPostPosition),
                            entries.get(i).getId()
                    ));
                }
                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, posts);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Post post = posts.get(position);
                        Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("@string/post_url", post.getPostUrl());
                        commentsIntent.putExtra("@string/post_thumbnail", post.getThumbnailUrl());
                        commentsIntent.putExtra("@string/post_title", post.getTitle());
                        commentsIntent.putExtra("@string/post_author", post.getAuthor());
                        commentsIntent.putExtra("@string/post_updated", post.getData_updated());
                        commentsIntent.putExtra("@string/post_id", post.getId());
                        startActivity(commentsIntent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.d(TAG, "Onfailure:Unable to retrieve Rss " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked item.");
                switch (item.getItemId()) {
                    case R.id.navLogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
}

package com.example.huseyincengiz.redditapp.Comments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huseyincengiz.redditapp.Account.LoginActivity;
import com.example.huseyincengiz.redditapp.ExtractXML;
import com.example.huseyincengiz.redditapp.FeedApi;
import com.example.huseyincengiz.redditapp.FeedClient;
import com.example.huseyincengiz.redditapp.MainActivity;
import com.example.huseyincengiz.redditapp.R;
import com.example.huseyincengiz.redditapp.WebViewActivity;
import com.example.huseyincengiz.redditapp.model.Feed;
import com.example.huseyincengiz.redditapp.model.entry.Entry;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.CollationKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.HeaderMap;

/**
 * Created by HuseyinCengiz on 31.08.2017.
 */

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    private static String postURL;
    private static String postThumbnail;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;
    private static String postId;

    private String Username;
    private String Modhash;
    private String Cookie;

    private int defaultImage;
    private String CurrentFeed;
    private ProgressBar mProgressBar;
    private TextView progressText;
    ArrayList<Comment> mComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mProgressBar = (ProgressBar) findViewById(R.id.commentsLoadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        progressText = (TextView) findViewById(R.id.progressText);
        setupToolbar();
        getSessionParams();
        setupImageLoader();
        InitPost();
        InitComments();
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
                        Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void InitComments() {
        FeedApi feedApi = FeedClient.getFeedApiImplements(FeedClient.BASE_URL, SimpleXmlConverterFactory.create());

        Call<Feed> call = feedApi.getFeed(CurrentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "CommentsAct->Onresponse:Server Response " + response.toString());

                List<Entry> entries = response.body().getEntrys();
                mComments = new ArrayList<Comment>();
                for (int i = 0; i < entries.size(); i++) {
                    ExtractXML extractXML = new ExtractXML(entries.get(i).getContent(), "<div class=\"md\"><p>", "</p>");
                    List<String> commentDetails = extractXML.getResult();

                    try {
                        mComments.add(new Comment(commentDetails.get(0),
                                entries.get(i).getAuthor().getName(),
                                entries.get(i).getUpdated(),
                                entries.get(i).getId()));
                    } catch (IndexOutOfBoundsException e) {
                        mComments.add(new Comment("Error reading comment",
                                "None",
                                "None",
                                "None"));
                        Log.e(TAG, "CommentsActivity->onResponse:IndexOutOfBoundsException:" + e.getMessage());
                    } catch (NullPointerException e) {
                        mComments.add(new Comment(commentDetails.get(0),
                                "None",
                                entries.get(i).getUpdated(),
                                entries.get(i).getId()));
                        Log.e(TAG, "CommentsActivity->onResponse:NullPointerException:" + e.getMessage());
                    }
                }
                ListView listView = (ListView) findViewById(R.id.commentsListView);
                CommentsListAdapter adapter = new CommentsListAdapter(CommentsActivity.this, mComments);
                listView.setAdapter(adapter);
                mProgressBar.setVisibility(View.GONE);
                progressText.setText("");
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "onItemClick: reply");
                        String commentID = mComments.get(position).getId();
                        getUserComment(commentID);
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.d(TAG, "CommentsAct->Onfailure:Unable to retrieve Rss " + t.getMessage());
                Toast.makeText(CommentsActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitPost() {
        Intent myIntent = getIntent();

        postURL = myIntent.getStringExtra("@string/post_url");
        postThumbnail = myIntent.getStringExtra("@string/post_thumbnail");
        postTitle = myIntent.getStringExtra("@string/post_title");
        postAuthor = myIntent.getStringExtra("@string/post_author");
        postUpdated = myIntent.getStringExtra("@string/post_updated");
        postId = myIntent.getStringExtra("@string/post_id");

        TextView txtTitle = (TextView) findViewById(R.id.postTitle);
        TextView txtAuthor = (TextView) findViewById(R.id.postAuthor);
        TextView txtUpdated = (TextView) findViewById(R.id.postUpdated);
        ImageView thumbnail = (ImageView) findViewById(R.id.postThumbnail);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.postLoadingProgressBar);
        Button btnReply = (Button) findViewById(R.id.btnCommentReply);

        txtTitle.setText(postTitle);
        txtAuthor.setText(postAuthor);
        txtUpdated.setText(postUpdated);
        DisplayImage(thumbnail, postThumbnail, progressBar);

        try {
            String[] splitUrl = postURL.split(FeedClient.BASE_URL);
            CurrentFeed = splitUrl[1];
            Log.d(TAG, "InitPost->CurrentFeed:" + CurrentFeed);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.d(TAG, "InitPost->ArrayIndexOutOfBoundsException:" + e.getMessage());
        }

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: reply");
                getUserComment(postId);
            }
        });
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Opening Url in Webview :" + postURL);
                Intent myIntent = new Intent(CommentsActivity.this, WebViewActivity.class);
                myIntent.putExtra("url", postURL);
                startActivity(myIntent);
            }
        });
    }

    private void getUserComment(final String post_id) {
        final Dialog dialog = new Dialog(CommentsActivity.this);
        dialog.setTitle("Comment");
        dialog.setContentView(R.layout.comment_input_dialog);
        //Varsayılan yükseklik ve genişliği alıyoruz sonra bunların yüzde 95'ni alıyoruz
        //ve yeni değerleri dialoga basıyoruz.
        int widht = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);

        dialog.getWindow().setLayout(widht, height);
        dialog.show();

        final EditText edtComment = (EditText) dialog.findViewById(R.id.edtDialogComment);
        Button btnpostcomment = (Button) dialog.findViewById(R.id.btnPostComment);


        btnpostcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textcomment = edtComment.getText().toString();
                if (!textcomment.equals("")) {
                    Log.d(TAG, "onClick: Attempting to post comment");
                    FeedApi feedApi = FeedClient.getFeedApiImplements(FeedClient.COMMENT_URL, GsonConverterFactory.create());
                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("User-Agent", Username);
                    headerMap.put("X-Modhash", Modhash);
                    headerMap.put("cookie", "reddit_session=" + Cookie);

                    Log.d(TAG, "onClick:" + "\n" +
                            "Username:" + Username + "\n" +
                            "Modhash:" + Modhash + "\n" +
                            "Cookie:" + Cookie + "\n"
                    );


                    Call<CheckComment> call = feedApi.submitComment(headerMap, "comment", post_id, textcomment);

                    call.enqueue(new Callback<CheckComment>() {
                        @Override
                        public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {
                            try {
                                Log.d(TAG, "Onresponse:Server Response " + response.toString());
                                String postSuccess = response.body().getSuccess();
                                if (postSuccess.equals("true")) {
                                    dialog.dismiss();
                                    Toast.makeText(CommentsActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CommentsActivity.this, "An Error Occured.Did you sign in?", Toast.LENGTH_SHORT).show();
                                }

                            } catch (NullPointerException e) {
                                Log.e(TAG, "onResponse:NullPointerException:" + e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckComment> call, Throwable t) {
                            Log.d(TAG, "Onfailure:Unable to retrieve Json " + t.getMessage());
                            Toast.makeText(CommentsActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CommentsActivity.this, "Please do not blank message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getSessionParams() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);
        Username = sharedPreferences.getString("@string/SessionUsername", "");
        Modhash = sharedPreferences.getString("@string/SessionModhash", "");
        Cookie = sharedPreferences.getString("@string/SessionCookie", "");

        Log.d(TAG, "getSessionParams: Get the session params stored in memory from login activity" + "\n" +
                "Username:" + Username + "\n" +
                "Modhash:" + Modhash + "\n" +
                "Cookie:" + Cookie + "\n"
        );
    }

    private void DisplayImage(ImageView ImageThum, String imgUrl, final ProgressBar myprogress) {

        ImageLoader imgLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage)
                .build();

        imgLoader.displayImage(imgUrl, ImageThum, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                myprogress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                myprogress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                myprogress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                myprogress.setVisibility(View.GONE);
            }
        });
    }

    private void setupImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);

        defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/reddit_alien", null, CommentsActivity.this.getPackageName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSessionParams();
    }
}

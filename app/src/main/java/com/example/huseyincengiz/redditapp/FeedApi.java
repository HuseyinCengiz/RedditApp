package com.example.huseyincengiz.redditapp;

import com.example.huseyincengiz.redditapp.Account.CheckLogin;
import com.example.huseyincengiz.redditapp.Comments.CheckComment;
import com.example.huseyincengiz.redditapp.model.Feed;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by HuseyinCengiz on 29.08.2017.
 */

public interface FeedApi {

    //Non-Static Dynamic
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);


    //Static
    // @GET("earthporn/.rss")
    // Call<Feed> getFeed();

    @POST("{username}")
    Call<CheckLogin> signIn(@HeaderMap Map<String, String> headers,
                            @Path("username") String username,
                            @Query("user") String user,
                            @Query("passwd") String passwd,
                            @Query("api_type") String type);

    @POST("{comment}")
    Call<CheckComment> submitComment(@HeaderMap Map<String, String> headers,
                                     @Path("comment") String comment,
                                     @Query("parent") String parent,
                                     @Query("amp;text") String text);


}

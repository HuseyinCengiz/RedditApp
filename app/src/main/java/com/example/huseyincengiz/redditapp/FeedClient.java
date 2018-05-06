package com.example.huseyincengiz.redditapp;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by HuseyinCengiz on 30.08.2017.
 */

public class FeedClient {

    public final static String BASE_URL = "https://www.reddit.com/r/";
    public final static String LOGIN_URL = "https://www.reddit.com/api/login/";
    public final static String COMMENT_URL = "https://www.reddit.com/api/";

    private static Retrofit getRetrofitInstance(String url, Converter.Factory factory) {
        Retrofit retro = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(factory)
                .build();
        return retro;
    }


    public static FeedApi getFeedApiImplements(String URL, Converter.Factory factory) {
        FeedApi feed = getRetrofitInstance(URL, factory).create(FeedApi.class);
        return feed;
    }
}

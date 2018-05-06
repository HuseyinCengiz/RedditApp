package com.example.huseyincengiz.redditapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuseyinCengiz on 30.08.2017.
 */

public class ExtractXML {
    private static final String TAG = "ExtractXML";

    private String content;
    private String tag;
    private String endtag;

    public ExtractXML(String content, String tag) {
        this.content = content;
        this.tag = tag;
        this.endtag = "NONE";
    }

    public ExtractXML(String content, String tag, String endtag) {
        this.content = content;
        this.tag = tag;
        this.endtag = endtag;
    }

    public List<String> getResult() {
        List<String> Result = new ArrayList<String>();
        String[] splitXml = null;
        String marker = null;
        if (endtag.equals("NONE")) {
            marker = "\"";
            splitXml = content.split(tag + marker);
        } else {
            marker = endtag;
            splitXml = content.split(tag);
        }
        int count = splitXml.length;
        for (int i = 1; i < count; i++) {
            String temp = splitXml[i];
            int index = temp.indexOf(marker);
            temp = temp.substring(0, index);
            Log.d(TAG, "Snipped:" + temp);
            Result.add(temp);
        }
        return Result;
    }
}

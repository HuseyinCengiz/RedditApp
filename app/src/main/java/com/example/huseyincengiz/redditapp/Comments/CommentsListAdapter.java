package com.example.huseyincengiz.redditapp.Comments;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.huseyincengiz.redditapp.Post;
import com.example.huseyincengiz.redditapp.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by HuseyinCengiz on 30.08.2017.
 */

public class CommentsListAdapter extends BaseAdapter {
    private static final String TAG = "CommentsListAdapter";
    private List<Comment> mDataList;
    private Context mContext;

    public CommentsListAdapter(Context context, List<Comment> DataList) {
        mContext = context;
        mDataList = DataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        Comment comment = (Comment) getItem(i);
        final ViewHolder holder;
        View result = convertView;
        try {
            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.comments_layout, parent, false);
                holder = new ViewHolder(result);
                result.setTag(holder);
            } else {
                holder = (ViewHolder) result.getTag();
                holder.progress.setVisibility(View.VISIBLE);
            }

            holder.comment.setText(comment.getComment());
            holder.author.setText(comment.getAuthor());
            holder.updated.setText(comment.getUpdated());
            holder.progress.setVisibility(View.GONE);
            return result;

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "getView:IllegalArgumentException:" + e.getMessage());
            return result;
        }
    }

    private class ViewHolder {

        public TextView comment;
        public TextView author;
        public TextView updated;
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            comment = itemView.findViewById(R.id.comment);
            author = itemView.findViewById(R.id.commentAuthor);
            updated = itemView.findViewById(R.id.commentUpdated);
            progress = itemView.findViewById(R.id.commentProgressBar);
        }
    }
}

package com.example.huseyincengiz.redditapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class CustomListAdapter extends BaseAdapter {
    private static final String TAG = "CustomListAdapter";

    private List<Post> mDataList;
    private Context mContext;

    public CustomListAdapter(Context context, List<Post> DataList) {
        mContext = context;
        mDataList = DataList;
        setupImageLoader();
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

        Post post = (Post) getItem(i);
        final ViewHolder holder;
        View result = convertView;
        try {
            if (result == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.card_layout_main, parent, false);
                holder = new ViewHolder(result);
                result.setTag(holder);
            } else {
                holder = (ViewHolder) result.getTag();
            }

            holder.title.setText(post.getTitle());
            holder.author.setText(post.getAuthor());
            holder.updated.setText(post.getData_updated());

            ImageLoader imgLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/reddit_alien", null, mContext.getPackageName());

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage)
                    .build();
            imgLoader.displayImage(post.getThumbnailUrl(), holder.thumbnail, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.progress.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.progress.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    holder.progress.setVisibility(View.GONE);
                }
            });


        } catch (IllegalArgumentException e) {


        }
        return result;
    }

    private void setupImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
    }

    private class ViewHolder {

        public TextView title;
        public TextView author;
        public TextView updated;
        public ImageView thumbnail;
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            title = itemView.findViewById(R.id.cardTitle);
            author = itemView.findViewById(R.id.cardAuthor);
            updated = itemView.findViewById(R.id.cardUpdated);
            thumbnail = itemView.findViewById(R.id.cardImage);
            progress = itemView.findViewById(R.id.cardProgressBar);
        }
    }
}

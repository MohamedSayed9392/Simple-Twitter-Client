package com.memoseed.simpletwitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.activities.FollowerInformation_;
import com.memoseed.simpletwitterclient.generalUtils.TWUtils;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TweetsRVAdapter extends RecyclerView.Adapter<TweetsRVAdapter.ViewHolderItem> {

    public List<Tweet> listTweets = new ArrayList<>();
    Context context;

    SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa, dd MMM yy");

    String TAG = getClass().getSimpleName();

    public static class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txtDate, txtTweet;
        ImageView imLine;


        public ViewHolderItem(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTweet = itemView.findViewById(R.id.txtTweet);
            imLine = itemView.findViewById(R.id.imLine);
        }
    }


    public TweetsRVAdapter(List<Tweet> listTweets, Context context) {
        this.listTweets = listTweets;
        this.context = context;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet, parent, false);
        return new ViewHolderItem(view);
    }

    public void addTweet(Tweet tweetFollower) {
        listTweets.add(tweetFollower);
        notifyDataSetChanged();
    }


    public void addTweets(List<Tweet> tweetFollowers) {
        listTweets.addAll(tweetFollowers);
        notifyDataSetChanged();
    }

    public void removeTweets() {
        listTweets.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        Tweet tweet = listTweets.get(position);
        if (position == listTweets.size()-1) {
            holder.imLine.setVisibility(View.GONE);
        } else {
            holder.imLine.setVisibility(View.VISIBLE);
        }

        try {
            holder.txtDate.setText(format.format(TWUtils.parseTwitterUTC(tweet.createdAt)));
        } catch (ParseException e) {
            holder.txtDate.setText(tweet.createdAt);
            e.printStackTrace();
        }

        holder.txtTweet.setText(tweet.text);

    }

    @Override
    public int getItemCount() {
        return listTweets.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

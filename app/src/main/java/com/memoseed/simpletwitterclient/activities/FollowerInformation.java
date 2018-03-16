package com.memoseed.simpletwitterclient.activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.adapters.TweetsRVAdapter;
import com.memoseed.simpletwitterclient.generalUtils.UTils;
import com.memoseed.simpletwitterclient.twitterApi.MyTwitterApiClient;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_follower_information)
public class FollowerInformation extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    private TwitterSession twitterSession;

    String name, description, profileImageUrl, profileBannerUrl;
    long id;

    List<Tweet> listTweets = new ArrayList<>();
    TweetsRVAdapter tweetsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        try {
            id = getIntent().getExtras().getLong("id");
            name = getIntent().getExtras().getString("name");
            description = getIntent().getExtras().getString("description");
            profileImageUrl = getIntent().getExtras().getString("profileImageUrl");
            profileBannerUrl = getIntent().getExtras().getString("profileBannerUrl");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click
    void imBack(){
        FollowerInformation.super.onBackPressed();
    }

    @ViewById
    Toolbar toolbar;

    @ViewById
    ImageView imBackground;
    @Click
    void imBackground(){
        if(profileBannerUrl!=null && !profileBannerUrl.isEmpty()){
            new ImageViewer.Builder(FollowerInformation.this, new String[]{profileBannerUrl})
                    .setStartPosition(0)
                    .show();
        }else{
            new ImageViewer.Builder(FollowerInformation.this, new String[]{Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.twitter_cover).toString()})
                    .setStartPosition(0)
                    .show();
        }
    }

    @ViewById
    TextView txtName;
    @ViewById
    TextView txtBio;
    @ViewById
    RoundedImageView imPic;
    @Click
    void imPic(){
        new ImageViewer.Builder(FollowerInformation.this, new String[]{profileImageUrl})
                .setStartPosition(0)
                .show();
    }

    @ViewById
    RecyclerView rView;
    @ViewById
    RelativeLayout rlProgress;


    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);

        if(profileBannerUrl!=null && !profileBannerUrl.isEmpty()){
            Glide.with(this).load(profileBannerUrl).into(imBackground);
        }else{
            Glide.with(this).load(R.drawable.twitter_cover).into(imBackground);
        }

        if (description != null && !description.isEmpty()) txtBio.setText(description);
        else txtBio.setVisibility(View.GONE);

        Glide.with(this).load(profileImageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Glide.with(FollowerInformation.this).load(R.drawable.avatar).into(imPic);
                e.printStackTrace();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imPic);
        txtName.setText(name);

        tweetsRVAdapter = new TweetsRVAdapter(listTweets,this);
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(tweetsRVAdapter);

        getTweetsList();
    }

    private void getTweetsList() {
        rlProgress.setVisibility(View.VISIBLE);
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomService().user_timeline(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String result = "";
                try {
                    result = response.body().string();
                    Log.d(TAG, "result : " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                    getTweetsTryAgain();
                }

                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject userJSON = jsonArray.getJSONObject(i);
                                Tweet tweet = new GsonBuilder().create().fromJson(userJSON.toString(), Tweet.class);
                                listTweets.add(tweet);
                            }

                            Log.d(TAG,"listTweets size : "+listTweets.size());
                            tweetsRVAdapter.notifyDataSetChanged();
                            rlProgress.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            getTweetsTryAgain();
                        }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                getTweetsTryAgain();
            }
        });
    }

    private void getTweetsTryAgain() {
        rlProgress.setVisibility(View.GONE);
        UTils.show2OptionsDialoge(FollowerInformation.this, getString(R.string.error_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getTweetsList();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }, getString(R.string.try_again), getString(R.string.cancel));
    }

}

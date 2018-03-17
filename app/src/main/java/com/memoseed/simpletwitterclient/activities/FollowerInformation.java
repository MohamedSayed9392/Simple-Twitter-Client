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
import com.memoseed.simpletwitterclient.TWParameters;
import com.memoseed.simpletwitterclient.adapters.TweetsRVAdapter;
import com.memoseed.simpletwitterclient.generalUtils.UTils;
import com.memoseed.simpletwitterclient.twitterApi.MyTwitterApiClient;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

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

    TWParameters p;

    private TwitterSession twitterSession;

    User user;

    List<Tweet> listTweets = new ArrayList<>();
    TweetsRVAdapter tweetsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = new TWParameters(this);
        UTils.changeLocale(this, getResources().getStringArray(R.array.languages_tag)[p.getInt("language", 0)]);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        try {
            user = (User) getIntent().getExtras().getSerializable("user");
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
        if(user.profileBannerUrl!=null && !user.profileBannerUrl.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(user.profileBannerUrl);
            stringBuilder.append("/1500x500");
            new ImageViewer.Builder(FollowerInformation.this, new String[]{stringBuilder.toString()})
                    .setStartPosition(0)
                    .show();
        }else{
            new ImageViewer.Builder(FollowerInformation.this, new String[]{"https://rlhb.lexblogplatformthree.com/wp-content/uploads/sites/111/2016/10/twitter-company-statistics.jpg"})
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
        new ImageViewer.Builder(FollowerInformation.this, new String[]{user.profileImageUrl.replace("_normal","")})
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

        if(user.profileBannerUrl!=null && !user.profileBannerUrl.isEmpty()){
            Glide.with(this).load(user.profileBannerUrl).into(imBackground);
        }else{
            Glide.with(this).load(R.drawable.twitter_cover).into(imBackground);
        }

        if (user.description != null && !user.description.isEmpty()) txtBio.setText(user.description);
        else txtBio.setVisibility(View.GONE);

        Glide.with(this).load(user.profileImageUrl.replace("_normal","")).listener(new RequestListener<Drawable>() {
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
        txtName.setText(user.name);

        tweetsRVAdapter = new TweetsRVAdapter(listTweets,this);
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(tweetsRVAdapter);

        getTweetsList();
    }

    private void getTweetsList() {
        rlProgress.setVisibility(View.VISIBLE);
        if(UTils.isOnline(this)) {
            MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
            myTwitterApiClient.getCustomService().user_timeline(user.id).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String result = "";
                    try {
                        result = response.body().string();
                        Log.d(TAG, "result : " + result);
                    } catch (IOException e) {
                        e.printStackTrace();
                        getTweetsTryAgain(getString(R.string.error_try_again));
                    }

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject userJSON = jsonArray.getJSONObject(i);
                            Tweet tweet = new GsonBuilder().create().fromJson(userJSON.toString(), Tweet.class);
                            listTweets.add(tweet);
                        }

                        Log.d(TAG, "listTweets size : " + listTweets.size());
                        tweetsRVAdapter.notifyDataSetChanged();
                        rlProgress.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getTweetsTryAgain(getString(R.string.error_try_again));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    getTweetsTryAgain(getString(R.string.error_try_again));
                }
            });
        }else{
            getTweetsTryAgain(getString(R.string.no_internet));
        }
    }

    private void getTweetsTryAgain(String message) {
        rlProgress.setVisibility(View.GONE);
        UTils.show2OptionsDialoge(FollowerInformation.this, message, new DialogInterface.OnClickListener() {
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

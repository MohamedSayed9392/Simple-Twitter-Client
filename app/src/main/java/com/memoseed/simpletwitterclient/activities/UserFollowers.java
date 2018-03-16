package com.memoseed.simpletwitterclient.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.GsonBuilder;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.adapters.UserFollowersRVAdapter;
import com.memoseed.simpletwitterclient.generalUtils.CacheHelper;
import com.memoseed.simpletwitterclient.generalUtils.UTils;
import com.memoseed.simpletwitterclient.twitterApi.MyTwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.androidannotations.annotations.AfterViews;
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

@EActivity(R.layout.activity_user_followers)
public class UserFollowers extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    private TwitterSession twitterSession;

    UserFollowersRVAdapter userFollowersRVAdapter;
    List<User> listUserFollowers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    @ViewById
    RecyclerView rView;
    @ViewById
    RelativeLayout rlProgress;

    @AfterViews
    void afterViews(){
        userFollowersRVAdapter = new UserFollowersRVAdapter(listUserFollowers,this);
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(userFollowersRVAdapter);

        getFollowerList();
    }

    private void getFollowerList()
    {
        rlProgress.setVisibility(View.VISIBLE);
        if(UTils.isOnline(this)) {
            Call<User> userCall = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, false, false);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
                    myTwitterApiClient.getCustomService().list(response.body().id).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String result = "";
                            try {
                                result = response.body().string();
                                Log.d(TAG, "result : " + result);
                            } catch (IOException e) {
                                e.printStackTrace();
                                getFollowersTryAgain();
                            }

                            try {
                                listUserFollowers.clear();
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("users");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject userJSON = jsonArray.getJSONObject(i);
                                    User user = new GsonBuilder().create().fromJson(userJSON.toString(), User.class);
                                    listUserFollowers.add(user);
                                }

                                Log.d(TAG, "listUserFollowers size : " + listUserFollowers.size());
                                CacheHelper.setFollowers(UserFollowers.this,listUserFollowers);
                                userFollowersRVAdapter.notifyDataSetChanged();
                                rlProgress.setVisibility(View.GONE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                getFollowersTryAgain();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            getFollowersTryAgain();
                        }

                    });
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    getFollowersTryAgain();
                }
            });
        }else{
            listUserFollowers.clear();
            listUserFollowers.addAll(CacheHelper.getFollowers(this));
            Log.d(TAG, "listUserFollowers size : " + listUserFollowers.size());
            userFollowersRVAdapter.notifyDataSetChanged();
            rlProgress.setVisibility(View.GONE);
        }
    }

    private void getFollowersTryAgain(){
        rlProgress.setVisibility(View.GONE);
        UTils.show2OptionsDialoge(UserFollowers.this, getString(R.string.error_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getFollowerList();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        },getString(R.string.try_again),getString(R.string.cancel));
    }
}

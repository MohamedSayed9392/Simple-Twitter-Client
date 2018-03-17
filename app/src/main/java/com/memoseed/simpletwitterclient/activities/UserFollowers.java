package com.memoseed.simpletwitterclient.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.GsonBuilder;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.TWParameters;
import com.memoseed.simpletwitterclient.adapters.UserFollowersRVAdapter;
import com.memoseed.simpletwitterclient.generalUtils.CacheHelper;
import com.memoseed.simpletwitterclient.generalUtils.UTils;
import com.memoseed.simpletwitterclient.interfaces.OnBottomReachedListener;
import com.memoseed.simpletwitterclient.twitterApi.MyTwitterApiClient;
import com.twitter.sdk.android.core.Twitter;
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

    TWParameters p;

    private TwitterSession twitterSession;

    UserFollowersRVAdapter userFollowersRVAdapter;
    List<User> listUserFollowers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = new TWParameters(this);
        UTils.changeLocale(this, getResources().getStringArray(R.array.languages_tag)[p.getInt("language", 0)]);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeLanguage) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.change_language));
            builder.setItems(getResources().getStringArray(R.array.languages), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    p.setInt(which, "language");
                    dialog.cancel();
                    UTils.recreateActivityCompat(UserFollowers.this);
                }
            });
            builder.show();
        } else if (id == R.id.logout) {
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
            startActivity(new Intent(this, Login_.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    RecyclerView rView;
    @ViewById
    RelativeLayout rlProgress;

    @AfterViews
    void afterViews() {
        setTitle(getString(R.string.followers));
        userFollowersRVAdapter = new UserFollowersRVAdapter(listUserFollowers, this);
        if (UTils.getScreenOrientation(this) == Configuration.ORIENTATION_PORTRAIT) {
            rView.setLayoutManager(new LinearLayoutManager(this));
        } else if (UTils.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            rView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        rView.setAdapter(userFollowersRVAdapter);

        getFollowerList();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cursor = -1;
                listUserFollowers.clear();
                userFollowersRVAdapter.notifyDataSetChanged();

                getFollowerList();
            }
        });

        userFollowersRVAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (!isGetFollowerListRunning && position != 0 && cursor!=0) {
                    bottomPosition = position;
                    getFollowerList();
                }

            }
        });
    }

    int bottomPosition;
    long cursor = -1;
    private boolean isGetFollowerListRunning = false;

    private void getFollowerList() {
        isGetFollowerListRunning = true;
        rlProgress.setVisibility(View.VISIBLE);
        if (UTils.isOnline(this)) {
            Call<User> userCall = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, true, false);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
                    myTwitterApiClient.getCustomService().list(response.body().id,cursor).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String result = "";
                            try {
                                result = response.body().string();
                                Log.d(TAG, "result : " + result);

                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("users");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject userJSON = jsonArray.getJSONObject(i);
                                    User user = new GsonBuilder().create().fromJson(userJSON.toString(), User.class);
                                    listUserFollowers.add(user);
                                }

                                Log.d(TAG, "listUserFollowers size : " + listUserFollowers.size());
                                CacheHelper.setFollowers(UserFollowers.this, listUserFollowers);
                                userFollowersRVAdapter.notifyDataSetChanged();
                                try{rView.smoothScrollToPosition(bottomPosition+1);}catch (IndexOutOfBoundsException e){e.printStackTrace();}
                                rlProgress.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);

                                cursor = jsonObject.getLong("next_cursor");
                                Log.d(TAG,"next_cursor : "+cursor);
                            } catch (IOException e) {
                                e.printStackTrace();
                                getFollowersTryAgain(getString(R.string.error_try_again));
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                getFollowersTryAgain(getString(R.string.error_try_again));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                getFollowersTryAgain(getString(R.string.error_try_again));
                            }

                            isGetFollowerListRunning = false;
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            getFollowersTryAgain(getString(R.string.error_try_again));
                        }

                    });
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                    getFollowersTryAgain(getString(R.string.error_try_again));
                }
            });
        } else {
            if (cursor == -1) getFollowersOffline();
            else getFollowersTryAgain(getString(R.string.no_internet));

        }
    }

    private void getFollowersTryAgain(String message) {
        rlProgress.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        isGetFollowerListRunning = false;
        UTils.show2OptionsDialoge(UserFollowers.this, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getFollowerList();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(cursor == -1) getFollowersOffline();
                dialogInterface.cancel();
            }
        }, getString(R.string.try_again), getString(R.string.offline));
    }

    private void getFollowersOffline(){
        cursor = 0;
        listUserFollowers.clear();
        listUserFollowers.addAll(CacheHelper.getFollowers(this));
        Log.d(TAG, "listUserFollowers size : " + listUserFollowers.size());
        userFollowersRVAdapter.notifyDataSetChanged();
        rlProgress.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        isGetFollowerListRunning = false;
    }
}

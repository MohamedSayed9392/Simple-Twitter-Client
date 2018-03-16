package com.memoseed.simpletwitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.memoseed.simpletwitterclient.R;
import com.memoseed.simpletwitterclient.generalUtils.UTils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_login)
public class Login extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    private TwitterAuthClient twitterAuthClient;
    private TwitterSession twitterSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            Log.d(TAG,"userId : " + twitterSession.getUserId());
            startActivity(new Intent(this,UserFollowers_.class));finish();
        }
    }

    @ViewById
    CircularProgressView pBar;

    @Click
    void linLogin() {
        if(UTils.isOnline(this)) {
            pBar.setVisibility(View.VISIBLE);
            twitterAuthClient = new TwitterAuthClient();
            twitterAuthClient.authorize(this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    Log.d(TAG, "twitterSessionResult : " + twitterSessionResult.data.getUserName());
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                    pBar.setVisibility(View.GONE);
                    toast(getString(R.string.error_try_again));
                }
            });
        }else{
            toast(getString(R.string.no_internet));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private void toast(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
}

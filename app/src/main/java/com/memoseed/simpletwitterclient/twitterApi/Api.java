package com.memoseed.simpletwitterclient.twitterApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mohamed Sayed on 3/16/2018.
 */

public interface Api {
    @GET("/1.1/followers/list.json?count=200")
    Call<ResponseBody> list(@Query("user_id") long id);
}

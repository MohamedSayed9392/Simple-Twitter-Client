package com.memoseed.simpletwitterclient.twitterApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Mohamed Sayed on 3/16/2018.
 */

public interface Api {
    @GET("/1.1/followers/list.json?include_user_entities=false&skip_status=true&count=20")
    Call<ResponseBody> list(@Query("user_id") long id,@Query("cursor") long cursor);

    @GET("/1.1/statuses/user_timeline.json?count=10")
    Call<ResponseBody> user_timeline(@Query("user_id") long id);
}

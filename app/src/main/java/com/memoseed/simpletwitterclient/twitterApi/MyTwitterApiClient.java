package com.memoseed.simpletwitterclient.twitterApi;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Mohamed Sayed on 3/16/2018.
 */

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public Api getCustomService() {
        return getService(Api.class);
    }
}
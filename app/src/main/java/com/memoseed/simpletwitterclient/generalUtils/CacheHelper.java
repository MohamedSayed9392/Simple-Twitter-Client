package com.memoseed.simpletwitterclient.generalUtils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.memoseed.simpletwitterclient.TWParameters;
import com.twitter.sdk.android.core.models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Sayed on 10/23/2017.
 */
public class CacheHelper {

    public static void setFollowers(Context context, List<User> list) {
        TWParameters p = new TWParameters(context);
        Type listsType = new TypeToken<List<User>>() {}.getType();
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(list, listsType);
        JsonArray jsonArray = element.getAsJsonArray();
        Log.d("followers", jsonArray.toString());
        p.setString(jsonArray.toString(), "followers");
    }

    public static List<User> getFollowers(Context context) {
        TWParameters p = new TWParameters(context);
        List<User> list = new ArrayList<>();
        Type listsType = new TypeToken<List<User>>() {}.getType();
        Gson gson = new Gson();
        String followersList = p.getString("followers");
        if (!followersList.isEmpty()) {
            Log.d("followers", followersList);
            list = gson.fromJson(followersList, listsType);
            Log.d("followers", Integer.toString(list.size()));
        }
        return list;
    }
}

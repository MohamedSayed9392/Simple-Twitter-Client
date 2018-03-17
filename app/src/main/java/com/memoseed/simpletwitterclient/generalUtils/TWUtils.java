package com.memoseed.simpletwitterclient.generalUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mohamed Sayed on 3/17/2018.
 */

public class TWUtils {
    public static Date parseTwitterUTC(String date) throws ParseException {

        String twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";

        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setTimeZone(TimeZone.getDefault());

        return sf.parse(date);
    }
}

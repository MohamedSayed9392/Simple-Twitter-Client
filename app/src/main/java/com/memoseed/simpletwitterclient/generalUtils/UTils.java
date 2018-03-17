package com.memoseed.simpletwitterclient.generalUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.LocaleList;
import android.view.Display;

import java.util.Locale;

/**
 * Created by Mohamed Sayed on 10/31/2016.
 */
public class UTils {

    public static boolean isOnline(Context context) {
        boolean connected = false;
//		boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    connected = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    connected = true;
        }
        return connected;
    }


    public static int getScreenOrientation(Activity activity)
    {
        Display getOrient = activity.getWindowManager().getDefaultDisplay();
        int orientation;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    public static void changeLocale(Context activity, String language) {
        final Resources res = activity.getResources();
        final Configuration conf = res.getConfiguration();
        if (language == null || language.length() == 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.setLocales(new LocaleList(Locale.getDefault()));
            }else{
                conf.locale = Locale.getDefault();
            }
        } else {
            final int idx = language.indexOf('-');
            if (idx != -1) {
                final String[] split = language.split("-");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    conf.setLocales(new LocaleList(new Locale(split[0], split[1].substring(1))));
                }else{
                    conf.locale = new Locale(split[0], split[1].substring(1));
                }
            } else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    conf.setLocales(new LocaleList(new Locale(language)));
                }else{
                    conf.locale = new Locale(language);
                }

            }
        }
        res.updateConfiguration(conf, null);
    }

    public static final void recreateActivityCompat(final Activity a) {

            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);

    }

    public static void showProgressDialog(String title, String message, ProgressDialog progressDialog) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog progressDialog) {
        progressDialog.hide();
    }

    public static void show2OptionsDialoge(Activity activity, String msg, DialogInterface.OnClickListener listenerPos, DialogInterface.OnClickListener listenerNeg, String txtbtnP, String txtbtnN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(txtbtnP, listenerPos)
                .setNegativeButton(txtbtnN, listenerNeg);
        AlertDialog alert = builder.create();
        alert.show();
    }

}

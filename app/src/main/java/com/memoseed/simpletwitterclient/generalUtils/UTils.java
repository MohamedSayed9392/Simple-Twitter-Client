package com.memoseed.simpletwitterclient.generalUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Michael Tien on 2016/3/30.
 */
public class Utils {
    public static BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        Drawable vectorDrawable;
        if ( Build.VERSION.SDK_INT < 21) {
            vectorDrawable = ContextCompat.getDrawable(context, id);
        } else
            vectorDrawable = context.getDrawable(id);
        int h = (int) convertDpToPixel(40, context);
        int w = (int) convertDpToPixel(40, context);
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    public static class NetworkDialogFragment extends DialogFragment {
        private static final String TITLE = "title";
        private static final String MESSAGE = "message";
        public static NetworkDialogFragment newInstance(int title, int message) {
            NetworkDialogFragment frag = new NetworkDialogFragment();
            Bundle args = new Bundle();
            args.putInt(TITLE, title);
            args.putInt(MESSAGE, message);
            frag.setArguments(args);
            return frag;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt(TITLE);
            int message = getArguments().getInt(MESSAGE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
//                    .setTitle(R.string.network_error_title)
//                    .setMessage(R.string.network_error_message)
                    .setPositiveButton(R.string.dialog_network_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            return builder.create();
        }
    }
    static private boolean isServiceAvailable(Context context,
                      GoogleApiClient mGoogleApiClient,
                      boolean mRequestingLocationUpdates) {
        return Utils.isOnline(context) && mRequestingLocationUpdates;
    }
    public static boolean checkNetworkAvailable(Activity activity) {
        if ( Utils.isOnline(activity)) return true;
        Utils.NetworkDialogFragment.newInstance(
                    R.string.network_error_title,
                    R.string.network_error_message)
                .show(activity.getFragmentManager(), "NetworkDialog");
        return false;
    }
    public static  boolean checkServicesAvailable(Activity activity,
                                  GoogleApiClient mGoogleApiClient,
                                  boolean mRequestingLocationUpdates) {
        if ( isServiceAvailable(activity, mGoogleApiClient, mRequestingLocationUpdates)) return true;
        Utils.NetworkDialogFragment dialog;
        if (!Utils.isOnline((activity))) {
            dialog = Utils.NetworkDialogFragment.newInstance(
                    R.string.network_error_title,
                    R.string.network_error_message);
        } else {
            dialog = Utils.NetworkDialogFragment.newInstance(
                    R.string.location_service_error_title,
                    R.string.location_service_error_message);
        }
        dialog.show(activity.getFragmentManager(), "NetworkDialog");
        return false;
    }
    public static String getTrimmedHtml( String htmlText) {
        String text;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(htmlText, 0).toString().trim();
        } else {
            text = Html.fromHtml(htmlText).toString().trim();
        }
        // remove extra Linefeed
        text = text.replaceAll("\n\n", "\n");
        return text;
    }
}

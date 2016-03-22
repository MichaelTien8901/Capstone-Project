package com.ymsgsoft.michaeltien.hummingbird;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ymsgsoft.michaeltien.hummingbird.geofencing.Constants;

public class GeofenceReceiver extends BroadcastReceiver {
    public NavigateActivity mActivity;
    public GeofenceReceiver(){
    }
    public GeofenceReceiver(NavigateActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int position = intent.getIntExtra("position", -1);
        Toast.makeText(mActivity, "position:" + String.valueOf(position), Toast.LENGTH_SHORT).show();
        mActivity.navigationForward(); // simple try
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NAVIGATION_NOTIFY_ID);
    }
}

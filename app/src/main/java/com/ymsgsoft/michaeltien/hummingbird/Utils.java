package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

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


}

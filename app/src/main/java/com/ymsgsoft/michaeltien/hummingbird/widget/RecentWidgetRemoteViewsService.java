package com.ymsgsoft.michaeltien.hummingbird.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ymsgsoft.michaeltien.hummingbird.MapsActivity;
import com.ymsgsoft.michaeltien.hummingbird.PlaceObject;
import com.ymsgsoft.michaeltien.hummingbird.R;
import com.ymsgsoft.michaeltien.hummingbird.data.HistoryColumns;
import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;



/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RecentWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = RecentWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }
            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // latest places

                final long identityToken = Binder.clearCallingIdentity();
                Uri dataUri = RoutesProvider.History.CONTENT_URI;
                String sortArgs = HistoryColumns.QUERY_TIME + " DESC LIMIT 10";
                 data = getContentResolver().query(dataUri,
                         null,
                         null,
                         null,
                        sortArgs );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_recent_list_item);

                String place_name = data.getString(data.getColumnIndex(HistoryColumns.PLACE_NAME));
                String place_id = data.getString(data.getColumnIndex(HistoryColumns.PLACE_ID));
                PlaceObject placeObject = new PlaceObject(place_name, place_id);
                views.setTextViewText(R.id.widget_list_item_place, place_name);
                Long query_time = data.getLong(data.getColumnIndex(HistoryColumns.QUERY_TIME));
                DateFormat formatter = new SimpleDateFormat("MMM/dd/yyyy HH:mm");
                String time_formatted = formatter.format(query_time);
                views.setTextViewText(R.id.widget_list_item_time, time_formatted);

                String description = place_name;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                // no need for fillInIntent
                final Intent fillInIntent = new Intent();
//                Uri uri = DatabaseContract.ScoreEntry.buildScoreWithDateString( data.getString(INDEX_DATE));
//                fillInIntent.setData(uri); // setup date time
                fillInIntent.putExtra(MapsActivity.PLACE_PARAM, placeObject);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_list_item, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_recent_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(HistoryColumns.ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

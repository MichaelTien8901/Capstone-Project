package com.ymsgsoft.michaeltien.hummingbird;

import com.ymsgsoft.michaeltien.hummingbird.playservices.HistoryRecyclerViewAdapter;

/**
 * Created by Michael Tien on 2016/4/1.
 */
public interface OnHistoryItemClickListener {
    void OnItemClick(HistoryRecyclerViewAdapter.HistoryObject data, int position);
}

package com.ymsgsoft.michaeltien.hummingbird;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymsgsoft.michaeltien.hummingbird.data.RoutesProvider;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DetailRouteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected long mRouteId = -1;
    protected DetailRouteRecyclerViewAdapter mAdapter;
    public static final int ROUTE_LOADER =1;
    private OnListFragmentInteractionListener mListener;

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(getActivity(),
//                RoutesProvider.Legs.CONTENT_URI,
//                null,
//                LegColumns.ID + "=?",
//                new String[] {String.valueOf(mRouteId)},
//                null);
        return new CursorLoader(getActivity(),
                RoutesProvider.Legs.CONTENT_URI,
                null,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailRouteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_detail_route, container, false);
        mAdapter = new DetailRouteRecyclerViewAdapter( R.layout.list_item_detail_route, null);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
            recyclerView.setAdapter(mAdapter);
        }
        if ( savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                final String ARG_ROUTE_KEY_ID = getString(R.string.intent_route_key);
                if ( arguments.containsKey(ARG_ROUTE_KEY_ID))
                    mRouteId = arguments.getLong(ARG_ROUTE_KEY_ID);
            }
        }
        getLoaderManager().initLoader(ROUTE_LOADER, null, this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onListFragmentInteraction(DummyItem item);
    }
}

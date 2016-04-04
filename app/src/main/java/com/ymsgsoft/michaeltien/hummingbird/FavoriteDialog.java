package com.ymsgsoft.michaeltien.hummingbird;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Michael Tien on 2016/3/28.
 */
 public class FavoriteDialog extends DialogFragment {
    public static final String SUGGEST_NAME="SUGGEST_NAME";
    public interface FavoriteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String saveName);
    }

    // Use this instance of the interface to deliver action events
    FavoriteDialogListener mListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FavoriteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FavoriteDialogListener");
        }
    }
    protected EditText mText;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String id_name = getArguments().getString(SUGGEST_NAME, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_favorite, null);
        mText = (EditText) view.findViewById(R.id.favor_name);
        mText.setText(id_name, TextView.BufferType.NORMAL);
        builder.setTitle(R.string.dialog_favorite_title)
                .setView(view)
                .setPositiveButton(R.string.dialog_favorite_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(FavoriteDialog.this, mText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_favorite_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }
}

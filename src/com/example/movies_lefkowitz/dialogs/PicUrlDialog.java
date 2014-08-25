package com.example.movies_lefkowitz.dialogs;

import java.util.ArrayList;

import com.example.movies_lefkowitz.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PicUrlDialog extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PicUrlDialogListener {
        public void onPicUrlDialogPositiveClick(DialogFragment dialog, String url);
        public void onPicUrlDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    PicUrlDialogListener mListener;
	
	public static final String EXTRA_ADD_URL = "com.example.www.movies_lefkowitz.add_url";
	protected static final int REQUEST_URL = 0;
	
	public static PicUrlDialog newInstance(String url) {
		Bundle args = new Bundle();
		PicUrlDialog fragment = new PicUrlDialog();
		if (url != null && url.length()>0) {
			args.putString(EXTRA_ADD_URL, url);
			fragment.setArguments(args);
		}
		return fragment;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PicUrlDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PicUrlDialogListener");
        }
    }
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.pic_url_dialog, null);
		final EditText urlET = (EditText) view.findViewById(R.id.pic_url_dialog_url);
		if (getArguments() != null) {
			String url = getArguments().getString(EXTRA_ADD_URL);
			urlET.setText(url);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);
		builder.setPositiveButton("Load", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    	String url = urlET.getText().toString().trim();
                        mListener.onPicUrlDialogPositiveClick(PicUrlDialog.this, url);
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mListener.onPicUrlDialogNegativeClick(PicUrlDialog.this);
                    }
                });
		return	builder.create();
	}
}
package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AddMovieDialogFragment extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddMovieDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogNeutralClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    AddMovieDialogListener mListener;
	
	public static final String EXTRA_ADD_MOVIE = "com.example.www.movies_lefkowitz.add_dialog";
	protected static final int REQUEST_SEARCH = 0;
	protected static final int REQUEST_MANUAL = 1;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddMovieDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddMovieDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle("Add Movie")
				.setMessage(
						"Would you like to add movie manually or from the internet?")
				.setPositiveButton("Internet", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener.onDialogPositiveClick(AddMovieDialogFragment.this);
					}
				})
				.setNegativeButton("Manual", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener.onDialogNegativeClick(AddMovieDialogFragment.this);
					}
				})
				.setNeutralButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onDialogNeutralClick(AddMovieDialogFragment.this);
					}
				})
				.create();
	}
}
package com.example.movie_lefkowitz.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class DeleteAllDialogFragment extends DialogFragment {
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeleteAllDialogListener {
        public void onDeleteAllDialogPositiveClick(DialogFragment dialog);
        public void onDeleteAllDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    DeleteAllDialogListener mListener;
	
	public static final String EXTRA_DELETE_ALL_MOVIE = "com.example.www.movies_lefkowitz.delete_all_dialog";
	protected static final int REQUEST_DELETE= 0;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeleteAllDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteAllDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle("Delete All")
				.setMessage(
						"Are you sure you want to delete all movies?")
				.setPositiveButton("Yes", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener.onDeleteAllDialogPositiveClick(DeleteAllDialogFragment.this);
					}
				})
				.setNegativeButton("No", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener.onDeleteAllDialogNegativeClick(DeleteAllDialogFragment.this);
					}
				})
				.create();
	}
}
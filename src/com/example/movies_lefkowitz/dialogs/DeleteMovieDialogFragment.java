package com.example.movies_lefkowitz.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.example.movies_lefkowitz.model.Movie;

public class DeleteMovieDialogFragment extends DialogFragment {
	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface DeleteMovieDialogListener {
		public void onDeleteMovieDialogPositiveClick(DialogFragment dialog, Movie movie);
		public void onDeleteMovieDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	DeleteMovieDialogListener mListener;

	public static final String EXTRA_DELETE_MOVIE = "com.example.www.movies_lefkowitz.delete_movie_dialog";
	public static final String EXTRA_MOVIE = "movie";
	protected static final int REQUEST_DELETE = 0;

	public static DeleteMovieDialogFragment newInstance(Movie movie) {
		Bundle args = new Bundle();
		DeleteMovieDialogFragment fragment = new DeleteMovieDialogFragment();
		if (movie != null) {
			args.putSerializable(EXTRA_MOVIE, movie);
			fragment.setArguments(args);
		}
		return fragment;
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (DeleteMovieDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement DeleteMovieDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Movie movie = (Movie) getArguments().getSerializable(EXTRA_MOVIE);
		
		return new AlertDialog.Builder(getActivity()).setTitle("Delete")
				.setMessage("Are you sure you want to delete this movie?")
				.setPositiveButton("Yes", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener
								.onDeleteMovieDialogPositiveClick(DeleteMovieDialogFragment.this, movie);
					}
				}).setNegativeButton("No", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						mListener
								.onDeleteMovieDialogNegativeClick(DeleteMovieDialogFragment.this);
					}
				}).create();
	}
}
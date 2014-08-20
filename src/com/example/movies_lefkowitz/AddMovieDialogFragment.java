package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class AddMovieDialogFragment extends DialogFragment {

	public static final String EXTRA_ADD_MOVIE = "com.example.www.movies_lefkowitz.add_dialog";
	
	protected static final int REQUEST_SEARCH = 0;
	protected static final int REQUEST_MANUAL = 1;
	
//	private boolean mMovieAdded = false;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle("Add Movie")
				.setMessage(
						"Would you like to add movie manually or from the internet?")
				.setPositiveButton("Internet", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent searchActivityIntent = new Intent(getActivity(),
								InternetSearchActivity.class);
						startActivityForResult(searchActivityIntent, REQUEST_SEARCH);
					}
				})
				.setNegativeButton("Manual", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent editActivityIntent = new Intent(getActivity(),
								Add_Edit_Activity.class);
						startActivityForResult(editActivityIntent, REQUEST_MANUAL);
					}
				})
				.setNeutralButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_SEARCH || requestCode == REQUEST_MANUAL) {
			//mMovieAdded = (boolean) data.getBooleanExtra("movie_added", false);
			getActivity().setResult(Activity.RESULT_OK);
			sendResult(resultCode);
		}
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		//i.putExtra(EXTRA_ADD_MOVIE, mMovieAdded);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}
}
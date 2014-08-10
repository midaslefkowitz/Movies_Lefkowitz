package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RatingPickerFragment extends DialogFragment {
	
	public static final String EXTRA_RATINGS = "com.example.www.movies_lefkowitz.ratings";
	private String[] mRatingsArray;
	private String mRating;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mRatingsArray = getResources().getStringArray(R.array.ratings);
		
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.genre_fragment_title)
			.setItems(R.array.ratings,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mRating = mRatingsArray[which];
						sendResult(Activity.RESULT_OK);
					}
				})
			.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
			.create();
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		i.putExtra(EXTRA_RATINGS, mRating);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}
}
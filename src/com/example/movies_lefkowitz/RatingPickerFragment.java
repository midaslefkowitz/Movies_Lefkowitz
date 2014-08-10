package com.example.movies_lefkowitz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RatingPickerFragment extends DialogFragment {
	
	public static final String EXTRA_RATINGS = "com.example.www.movies_lefkowitz.ratings";
	private String[] mRatingsArray;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.genre_fragment_title)
			.setItems(R.array.ratings,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
					}
				})
			.create();
	}
}
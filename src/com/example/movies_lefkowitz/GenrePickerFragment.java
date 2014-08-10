package com.example.movies_lefkowitz;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GenrePickerFragment extends DialogFragment {
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	public static final String EXTRA_GENRES = "com.example.www.movies_lefkowitz.genres";
	private String[] mGenresArray;
	private boolean[] mSelected;

	public static GenrePickerFragment newInstance(
			ArrayList<String> selectedItems) {
		Bundle args = new Bundle();
		GenrePickerFragment fragment = new GenrePickerFragment();
		if (selectedItems != null) {
			args.putStringArrayList(EXTRA_GENRES, selectedItems);
			fragment.setArguments(args);
		}
		return fragment;
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		i.putExtra(EXTRA_GENRES, mSelectedItems);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Resources res = getResources();
		mGenresArray = res.getStringArray(R.array.genres);
		mSelected = new boolean[mGenresArray.length];
		// There are selected genres
		if (getArguments() != null) {
			mSelectedItems = getArguments().getStringArrayList(EXTRA_GENRES);
			// Add true to selected [] boolean
			for (int i = 0; i < mGenresArray.length; i++) {
				mSelected[i] = mSelectedItems.contains(mGenresArray[i]);
			}
		} else {
			// Nothing already checked so fill list with false
			for (int i = 0; i < mSelected.length; i++) {
				mSelected[i] = false;
			}
		}

		// Use the Builder class for convenient dialog construction
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.genre_fragment_title)
				.setMultiChoiceItems(R.array.genres, // Specify the list array,
						mSelected, // the items to be selected by default 
						// and the listener through which to receive callbacks
						// when items are selected
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									// If the user checked the item, add it to
									// the selected items
									mSelectedItems.add(mGenresArray[which]);
								} else if (mSelectedItems
										.contains(mGenresArray[which])) {
									// Else, if the item is already in the
									// array, remove it
									mSelectedItems.remove(mGenresArray[which]);
								}
							}
						})
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendResult(Activity.RESULT_OK);
							}
						}).setNegativeButton(R.string.cancel, null).create();
	}
}
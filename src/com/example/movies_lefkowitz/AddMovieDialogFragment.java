package com.example.movies_lefkowitz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class AddMovieDialogFragment extends DialogFragment {
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
						startActivityForResult(searchActivityIntent,
								MainFragment.REQUEST_SEARCH);
					}
				}).setNegativeButton("Manual", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent editActivityIntent = new Intent(getActivity(),
								Add_Edit_Activity.class);
						startActivityForResult(editActivityIntent,
								MainFragment.REQUEST_MANUAL);
					}
				}).setNeutralButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				}).create();
	}
}
package com.example.movies_lefkowitz.dialogs;

import java.lang.reflect.Field;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class YearPickerDialogFragment extends DialogFragment {
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    
	
	/*
	public interface YearDialogListener {
        public void onYearPickerDialogPositiveClick(DialogFragment dialog);
        public void onYearPickerDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    YearDialogListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (YearDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement YearDialogListener");
        }
    }
	*/
	
	public static final String EXTRA_PICK_YEAR = "com.example.www.movies_lefkowitz.year_dialog";
	protected static final int REQUEST_YEAR = 0;
	
	// This will be the handler we will attach to the dialog 
	OnDateSetListener dateDialog = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, year);
		}
	};
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();

		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dpd = new DatePickerDialog(getActivity(),
				dateDialog, mYear, mMonth, mDay);
		try {

			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName())
								|| "mDaySpinner".equals(datePickerField
										.getName())
								|| "mMonthSpinner".equals(datePickerField
										.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
							Object monthPicker = new Object();
							monthPicker = datePickerField.get(datePicker);
							((View) monthPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
		}
		return dpd;
	}
	
	
	
	
}
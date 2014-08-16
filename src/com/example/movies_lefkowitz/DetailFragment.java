package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.example.movies_lefkowitz.model.Movie;

public class DetailFragment extends Fragment {
	private Movie mMovie;
	private ImageView mThumbnailIV;
	
	public DetailFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the message from the intent
	    Intent intent = getActivity().getIntent();
	    mMovie = (Movie) intent.getSerializableExtra("movie");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View detailsView = inflater.inflate(R.layout.fragment_details,
				container, false);

		mThumbnailIV = (ImageView) detailsView
				.findViewById(R.id.detail_item_thumb);
		ImageView watchCheckIV = (ImageView) detailsView
				.findViewById(R.id.detail_item_check);
		TextView titleTV = (TextView) detailsView
				.findViewById(R.id.detail_item_title);
		TextView descriptionTV = (TextView) detailsView
				.findViewById(R.id.detail_item_title);
		TextView rt_ratingTV = (TextView) detailsView
				.findViewById(R.id.detail_item_title);
		TextView my_ratingTV = (TextView) detailsView
				.findViewById(R.id.detail_item_title);

		mThumbnailIV.setImageResource(R.drawable.thumb);
		
		titleTV.setMovementMethod(LinkMovementMethod.getInstance());
		titleTV.setText(getTitleYearSpan(
					getActivity(), 
					mMovie.getTitle(),
					mMovie.getYear() ), 
				BufferType.SPANNABLE);
		
		
		return detailsView;
	}

	public static SpannableStringBuilder getTitleYearSpan(Context ctx, String title, int y) {
		// Text color attribute id
		int[] attrs = {android.R.attr.textColor};

		// Parse year_text style
		TypedArray ta = ctx.obtainStyledAttributes(R.style.year_text, attrs);
		
		// get the color from the style attributes     
		int textColor = ta.getColor(0, Color.DKGRAY);

		SpannableStringBuilder title_year = new SpannableStringBuilder(title);
		String year = "";
		
		if (y > 0) { 
			year = "(" + Integer.toString(y) + ")";
			title_year.append(" " + year);
		}
		
		int span_end = title_year.length();
		int span_start = span_end - year.length();
		
		title_year.setSpan(new TextAppearanceSpan(ctx, android.R.style.TextAppearance_Small, textColor), span_start,
				span_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ta.recycle();
		return title_year;
	}
	
	
}

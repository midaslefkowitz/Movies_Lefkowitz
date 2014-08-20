/*
 * TODO: 
 * 1. Need to implement saving in onPause and loading in onResume 
 * 2. Context menus
 * 3. Change add_movie icon to show always
 * 4. Side by side fragments of movies and details
 */

package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.example.movies_lefkowitz.model.Movie;
import com.example.movies_lefkowitz.model.MovieHolder;
import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class MainFragment extends Fragment 
	implements AddMovieDialogFragment.AddMovieDialogListener {

	private MoviesDBAdapter mMyDb;
	private ListView mListview;
	private Cursor mMovieCursor;
	private MyCursorAdapter mAdapter;
	private View mRootView;
	private ImageView mThumbnailIV;
	
	private static final int TARGET_HEIGHT = 120;

	protected static final int REQUEST_ADD = 0;
	protected static final int REQUEST_SEARCH = 0;
	protected static final int REQUEST_MANUAL = 1;

	public MainFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_main, container, false);

		openDB();
		populateListViewFromDB();
		return mRootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.movies_main_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) { 
		case R.id.action_add:
			// Create an instance of the dialog fragment and show it
			DialogFragment dialog = new AddMovieDialogFragment();
			dialog.setTargetFragment(this, REQUEST_ADD);
		    dialog.show(getFragmentManager(), "AddMovieDialogFragment");
			break;
		case R.id.action_settings:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMovieCursor.close();
	}

	private void openDB() {
		mMyDb = new MoviesDBAdapter(getActivity());
	}

	private void populateListViewFromDB() {
		mMovieCursor = mMyDb.getAllMovies();

		mListview = (ListView) mRootView.findViewById(R.id.main_movie_listview);
		mAdapter = new MyCursorAdapter(getActivity(), mMovieCursor);
		mListview.setAdapter(mAdapter);
		//registerForContextMenu(mListview);
	}

	@Override
    public void onDialogPositiveClick(DialogFragment dialog) {
		Intent searchActivityIntent = new Intent(getActivity(),
				InternetSearchActivity.class);
		startActivityForResult(searchActivityIntent, REQUEST_SEARCH);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    	Intent editActivityIntent = new Intent(getActivity(),
				Add_Edit_Activity.class);
		startActivityForResult(editActivityIntent, REQUEST_MANUAL);
    }
    
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
    	return;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == REQUEST_MANUAL || requestCode == REQUEST_SEARCH) { 
			mMovieCursor = mMyDb.getAllMovies();
			mAdapter.swapCursor(mMovieCursor);
		}
		
	}

	private class MyCursorAdapter extends CursorAdapter {
		MovieHolder holder;
		
		public MyCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor, true);
		}

		@Override
		public void bindView(View movieView, Context context, Cursor cursor) {
			Movie movie = new Movie(getActivity(), cursor);

			holder = (MovieHolder) movieView.getTag();
			holder.setMovie(movie);
			
			mThumbnailIV = (ImageView) movieView
					.findViewById(R.id.list_item_thumb);
			ImageView watchCheckIV = (ImageView) movieView
					.findViewById(R.id.list_item_check);
			TextView titleTV = (TextView) movieView
					.findViewById(R.id.list_item_title);
			TextView descriptionTV = (TextView) movieView
					.findViewById(R.id.list_item_description);
			TextView rt_ratingTV = (TextView) movieView
					.findViewById(R.id.list_item_rt_rating);
			TextView my_ratingTV = (TextView) movieView
					.findViewById(R.id.list_item_my_rating);
			
			/* Load the image to the thumbnail */
			mThumbnailIV.setImageResource(R.drawable.thumb);
			MainActivity.GetImage.download(movie.getPic(),
					getActivity(), mThumbnailIV, TARGET_HEIGHT);

			/*
			 * Displayed checkmark image (green/grey) depends if user has seen
			 * the movie
			 */
			if (movie.getWatched() == Movie.UNWATCHED) {
				watchCheckIV.setImageResource(R.drawable.checkmark_grey);
			} else {
				watchCheckIV.setImageResource(R.drawable.checkmark_green);
			}

			/* Display the title textview with title and year span */
			titleTV.setMovementMethod(LinkMovementMethod.getInstance());
			titleTV.setText(DetailFragment.getTitleYearSpan(getActivity(),
					movie.getTitle(),
					movie.getYear()),
					BufferType.SPANNABLE);

			/* Display description */
			descriptionTV.setText(movie.getDescription());

			/* Display Rotten Rating */
			rt_ratingTV.setText(Double.toString(movie.getRt_rating()));

			/* Display User Rating */
			my_ratingTV.setText(Double.toString(movie.getUser_rating()));
			
			movieView.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), DetailsActivity.class);
					Movie movie = holder.getMovie();
					intent.putExtra("movie", movie);
					startActivity(intent);
				}
			});
		}

		@Override
		public View newView(Context context, Cursor arg1, ViewGroup group) {
			MovieHolder holder = new MovieHolder();
			View itemView = null;

			itemView = getActivity().getLayoutInflater().inflate(
					R.layout.item_layout, null);
			
			itemView.setTag(holder);
			return itemView;
		}
	}
}
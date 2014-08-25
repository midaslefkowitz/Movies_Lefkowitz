package com.example.movies_lefkowitz;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.example.movies_lefkowitz.dialogs.AddMovieDialogFragment;
import com.example.movies_lefkowitz.dialogs.DeleteAllDialogFragment;
import com.example.movies_lefkowitz.dialogs.DeleteMovieDialogFragment;
import com.example.movies_lefkowitz.model.Movie;
import com.example.movies_lefkowitz.model.MovieHolder;
import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class MainFragment extends Fragment 
	implements AddMovieDialogFragment.AddMovieDialogListener, 
	DeleteAllDialogFragment.DeleteAllDialogListener,
	DeleteMovieDialogFragment.DeleteMovieDialogListener {

	private MoviesDBAdapter mMyDb;
	private ListView mListview;
	private Cursor mMovieCursor;
	private MyCursorAdapter mAdapter;
	private View mRootView;
	private ImageView mThumbnailIV;
	
	private static final int TARGET_HEIGHT = 120;

	protected static final int REQUEST_ADD = 0;
	protected static final int REQUEST_DELETE_ALL = 1;
	protected static final int REQUEST_SEARCH = 2;
	protected static final int REQUEST_MANUAL = 3;
	protected static final int REQUEST_DELETE_MOVIE = 4;
	protected static final int REQUEST_EDIT = 5;

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
	public void onResume() {
		super.onResume();
		Intent intent = getActivity().getIntent();
		boolean saved = intent.getBooleanExtra("saved", false) ;
		if (saved) {
			openDB();
			mMovieCursor = mMyDb.getAllMovies();
			mAdapter.swapCursor(mMovieCursor);
		}
	}

	private void openDB() {
		mMyDb = new MoviesDBAdapter(getActivity());
	}

	private void populateListViewFromDB() {
		mMovieCursor = mMyDb.getAllMovies();

		mListview = (ListView) mRootView.findViewById(R.id.main_movie_listview);
		mAdapter = new MyCursorAdapter(getActivity(), mMovieCursor);
		mListview.setAdapter(mAdapter);
		
		/* Set Click Handlers */
		
		/* Short Click */
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), DetailsActivity.class);
				MovieHolder holder = (MovieHolder) view.getTag();
				Movie movie = holder.getMovie();
				intent.putExtra("source", "MainFragment");
				intent.putExtra("movie", movie);
				startActivity(intent);
			}
		});
		
		/* Long Click */
		registerForContextMenu(mListview);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.main_context, menu);
		super.onCreateContextMenu(menu, view, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		View view = info.targetView;

		MovieHolder holder = (MovieHolder) view.getTag();
		Movie movie = holder.getMovie();
		switch (item.getItemId()) {
			case R.id.main_context_delete:
				deleteMovie(movie);
				return true;
			case R.id.main_context_details:
				getMovieDetails(movie);
				return true;
			case R.id.main_context_edit:
				editMovie(movie);
				return true;
			case R.id.main_context_cancel:
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	private void deleteMovie(Movie movie) {
		DialogFragment deleteDialog = DeleteMovieDialogFragment.newInstance(movie);
		deleteDialog.setTargetFragment(this, REQUEST_DELETE_MOVIE);
		deleteDialog.show(getFragmentManager(), "DeleteMovieDialogFragment");
	}
	
	private void getMovieDetails(Movie movie) {
		Intent intent = new Intent(getActivity(), DetailsActivity.class); 
		intent.putExtra("source", "MainFragment");
		intent.putExtra("movie", movie);
		startActivity(intent);
	}
	
	private void editMovie(Movie movie) {
		Intent intent = new Intent(getActivity(), Add_Edit_Activity.class); 
		intent.putExtra("isNew", false);
		intent.putExtra("movie", movie);
		startActivityForResult(intent, REQUEST_EDIT);
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
			addMovie();
			break;
		case R.id.action_delete_all:
			deleteAllMovies();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addMovie() {
		// Create an instance of the dialog fragment and show it
		DialogFragment addDialog = new AddMovieDialogFragment();
		addDialog.setTargetFragment(this, REQUEST_ADD);
		addDialog.show(getFragmentManager(), "AddMovieDialogFragment");
	}
	
	private void deleteAllMovies() {
		DialogFragment deleteAllDialog = new DeleteAllDialogFragment();
		deleteAllDialog.setTargetFragment(this, REQUEST_DELETE_ALL);
		deleteAllDialog.show(getFragmentManager(), "DeleteAllMovieDialogFragment");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mMovieCursor.close();
	}

	@Override
    public void onAddMovieDialogPositiveClick(DialogFragment dialog) {
		Intent searchActivityIntent = new Intent(getActivity(),
				InternetSearchActivity.class);
		startActivityForResult(searchActivityIntent, REQUEST_SEARCH);
    }

    @Override
    public void onAddMovieDialogNegativeClick(DialogFragment dialog) {
    	Intent editActivityIntent = new Intent(getActivity(),
				Add_Edit_Activity.class);
    	editActivityIntent.putExtra("isNew", true);
		startActivityForResult(editActivityIntent, REQUEST_MANUAL);
    }
    
    @Override
    public void onAddMovieDialogNeutralClick(DialogFragment dialog) {
    	return;
    }
	
    @Override
	public void onDeleteAllDialogPositiveClick(DialogFragment dialog) {
		mMyDb.deleteAll();
		mMovieCursor = mMyDb.getAllMovies();
		mAdapter.swapCursor(mMovieCursor);
	}

	@Override
	public void onDeleteAllDialogNegativeClick(DialogFragment dialog) {
		return;		
	}
	
    @Override
	public void onDeleteMovieDialogPositiveClick(DialogFragment dialog, Movie movie) {
		mMyDb.deleteMovie(movie.getDbID());
		mMovieCursor = mMyDb.getAllMovies();
		mAdapter.swapCursor(mMovieCursor);
	}

	@Override
	public void onDeleteMovieDialogNegativeClick(DialogFragment dialog) {
		return;		
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == REQUEST_MANUAL || requestCode == REQUEST_SEARCH  || requestCode == REQUEST_EDIT) { 
			mMovieCursor = mMyDb.getAllMovies();
			mAdapter.swapCursor(mMovieCursor);
		}
	}

	private class MyCursorAdapter extends CursorAdapter {
		
		public MyCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor, true);
		}

		@Override
		public void bindView(View movieView, Context context, Cursor cursor) {
			Movie movie = new Movie(getActivity(), cursor);

			final MovieHolder holder = (MovieHolder) movieView.getTag();
			holder.setMovie(movie);
			
			mThumbnailIV = (ImageView) movieView
					.findViewById(R.id.list_item_thumb);
			ImageView watchCheckIV = (ImageView) movieView
					.findViewById(R.id.list_item_check);
			ProgressBar progressBarPB = (ProgressBar) movieView
					.findViewById(R.id.list_item_pb);
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
					getActivity(), mThumbnailIV, TARGET_HEIGHT, progressBarPB);

			/* Displayed checkmark image (green/grey) depends if user has seen the movie */
			if (movie.getWatched() == Movie.UNWATCHED) {
				watchCheckIV.setImageResource(R.drawable.checkmark_grey);
			} else {
				watchCheckIV.setImageResource(R.drawable.checkmark_green);
			}
			
			
			

			/* Display the title textview with title and year span */
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
package com.example.movies_lefkowitz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movie_lefkowitz.dialogs.GenrePickerFragment;
import com.example.movie_lefkowitz.dialogs.RatingPickerFragment;
import com.example.movies_lefkowitz.model.Movie;
import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class Add_Edit_Activity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Intent intent = getIntent();
		boolean isNew = intent.getBooleanExtra("isNew", true);
		Bundle args = new Bundle();
		args.putBoolean("isNew", isNew);
		if (!isNew) {
			Movie movie = (Movie) intent.getSerializableExtra("movie");
			args.putSerializable("movie", movie);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private static final String GENRE_DIALOG_TAG = "genre dialog";
		private static String RATING_DIALOG_TAG = "rating dialog";	
		//private static String EXTRA_ADD_EDIT = "com.example.www.movies_lefkowitz.add_edit";
		private static final int REQUEST_GENRE = 0;
		private static final int REQUEST_RATING = 1;
		private static final int TARGET_HEIGHT = 140;
		
		private View mRootView;
		private ImageView mThumbnail;
		private TextView mGenreTV;
		private TextView mRatingTV;
		private TextView mMyRatingTV;
		private SeekBar sb;
		private EditText eUrl;

		private ArrayList<String> mGenreArray;
		private String mGenre;
		private String mRating = "";
		private String mUrl = "";
		private boolean mHasRated = false;
		private double mMyRating;
		private Movie mMovie = null;
		private boolean isNew = true;
		
		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_add_edit, container,
					false);
			
			isNew = getArguments().getBoolean("isNew");
			if (!isNew) {
				mMovie = (Movie) getArguments().getSerializable("movie");
			}
			
			setLoadPicFromUrlHandler();
			setGenreHandler();
			setRatingHandler();
			setSeekBarHandler();
			setCancelHandler();
			setSaveHandler();
			
			
			if (!isNew) {
				setWatched(mMovie);
				setUrlTV(mMovie);
				setTitle(mMovie);
				setYear(mMovie);
				setGenreTV(mMovie);
				setRatingTV(mMovie);
				setRuntime(mMovie);
				setDescription(mMovie);
				setUserRating(mMovie);
				setCast(mMovie);
				setDirectors(mMovie);
			}
			
			return mRootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if (requestCode == REQUEST_GENRE) {
				mGenreArray = (ArrayList<String>) data
						.getStringArrayListExtra(GenrePickerFragment.EXTRA_GENRES);
				setGenreTV();
			}
			if (requestCode == REQUEST_RATING) {
				mRating = (String) data.getStringExtra(RatingPickerFragment.EXTRA_RATINGS);
				setRatingTV();
			}
		}

		private void setWatched(Movie movie) {
			CheckBox watchedCB = (CheckBox) mRootView.findViewById(R.id.add_edit_watched);
			watchedCB.setSelected(movie.getWatched() == Movie.WATCHED);
		}
		
		private void setUrlTV(Movie movie){
			EditText picEditText = (EditText) mRootView
					.findViewById(R.id.add_edit_pic);
			String url = movie.getPic();
			picEditText.setText(url);
			setThumbnail(url);
		}
		
		private void setThumbnail (String url){
			MainActivity.GetImage.download(url, 
					getActivity(), 
					mThumbnail, 
					TARGET_HEIGHT);
		}
		
		private void setTitle(Movie movie) {
			EditText titleEditText = (EditText) mRootView.findViewById(R.id.add_edit_title);
			titleEditText.setText(movie.getTitle());
		}
		
		private void setYear(Movie movie) {
			EditText yearEditText = (EditText) mRootView.findViewById(R.id.add_edit_year);
			yearEditText.setText( Integer.toString(movie.getYear() ) );
		}
		
		private void setGenreTV(Movie movie) {
			mGenreTV = (TextView) mRootView.findViewById(R.id.add_edit_genre);
			mGenreTV.setText(movie.getGenre());
		}
		
		private void setRatingTV(Movie movie) {
			mRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_rating_select);
			mRatingTV.setText(movie.getMpaa_rating());
		}
		
		private void setRuntime(Movie movie) {
			EditText runtimeEditText = (EditText) mRootView.findViewById(R.id.add_edit_runtime);
			runtimeEditText.setText(Integer.toString(movie.getRuntime() ) );
		}
		
		private void setDescription(Movie movie) {
			EditText dscrptnEditText = (EditText) mRootView.findViewById(R.id.add_edit_description);
			dscrptnEditText.setText(movie.getDescription());
		}
		
		private void setUserRating(Movie movie) {
			double userRating = movie.getUser_rating();
			sb = (SeekBar) mRootView.findViewById(R.id.add_edit_my_rating_SB);
			sb.setProgress((int) (userRating * 10));
			mMyRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_my_rating_TV2);
			mMyRatingTV.setText(Double.toString(userRating));
		}
		
		private void setCast(Movie movie) {
			EditText castEditText = (EditText) mRootView.findViewById(R.id.add_edit_cast);
			castEditText.setText(movie.getCast());
		}
		
		private void setDirectors(Movie movie) {
			EditText dirEditText = (EditText) mRootView.findViewById(R.id.add_edit_director);
			dirEditText.setText(movie.getDirector());
		}
		
		private void setGenreTV() {
			mGenreTV = (TextView) mRootView.findViewById(R.id.add_edit_genre);
			if (mGenreArray != null && mGenreArray.size() > 0) {
				mGenre = GenrePickerFragment.genreArrayToString(mGenreArray);
				mGenreTV.setText(mGenre);
			} else {
				mGenre = getString(R.string.add_edit_genre);
				mGenreTV.setHint(mGenre);
			}
		}

		private void setRatingTV() {
			mRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_rating_select);
			if (mRating == null) {
				mRatingTV.setHint(R.string.add_edit_rating_select);
			} else {
				mRatingTV.setText("Rating: " + mRating);
			}
		}

		private void setLoadPicFromUrlHandler() {
			eUrl = (EditText) mRootView
					.findViewById(R.id.add_edit_pic);
			
			mThumbnail = (ImageView) mRootView
					.findViewById(R.id.add_edit_thumb);
			Button previewButton = (Button) mRootView.findViewById(R.id.add_edit_preview);
			previewButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mUrl = eUrl.getText().toString().trim();
					MainActivity.GetImage.download(mUrl, 
							getActivity(), 
							mThumbnail, 
							TARGET_HEIGHT);
				}
			});
			
		}

		private void setGenreHandler() {
			mGenreTV = (TextView) mRootView.findViewById(R.id.add_edit_genre);
			mGenreTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					GenrePickerFragment dialog = GenrePickerFragment
							.newInstance(mGenreArray);
					dialog.setTargetFragment(PlaceholderFragment.this,
							REQUEST_GENRE);
					dialog.show(fm, GENRE_DIALOG_TAG);
				}
			});
		}

		private void setRatingHandler() {
			mRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_rating_select);
			mRatingTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					RatingPickerFragment dialog = new RatingPickerFragment();
					//RatingPickerFragment dialog = RatingPickerFragment.newInstance(mRating);
					dialog.setTargetFragment(PlaceholderFragment.this,
							REQUEST_RATING);
					dialog.show(fm, RATING_DIALOG_TAG);
				}
			});
		}

		private void setSeekBarHandler() {
			sb = (SeekBar) mRootView.findViewById(R.id.add_edit_my_rating_SB);
			mMyRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_my_rating_TV2);
			sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					mHasRated = true;
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					mMyRating = (double) progress/10;
					mMyRatingTV.setText(mMyRating + "/" + (sb.getMax()/10));
				}
			});
		}
		
		private void setCancelHandler() {
			Button cancel = (Button) mRootView
					.findViewById(R.id.add_edit_cancel);
			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
			});
		}

		public void setSaveHandler() {
			Button save = (Button) mRootView.findViewById(R.id.add_edit_save);
			save.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//get title and check if entered
					EditText titleEditText = (EditText) mRootView
							.findViewById(R.id.add_edit_title);
					String title = titleEditText.getText().toString().trim();
					if (title == null || title.length()==0) {
						Toast.makeText( 
							 	getActivity(), 
							 	"You need to enter a title", 
							 	Toast.LENGTH_LONG)
							 .show();
						return;
					}
					Movie movie = new Movie(getActivity(), title);
					// get watched
					CheckBox watchedCB = (CheckBox) mRootView.findViewById(R.id.add_edit_watched);
					movie.setWatched(watchedCB.isChecked() ? Movie.WATCHED : Movie.UNWATCHED);
					// get pic URL
					EditText picEditText = (EditText) mRootView
							.findViewById(R.id.add_edit_pic);
					String pic = picEditText.getText().toString().trim();
					
					movie.setPic(pic);
					// get year
					EditText yearEditText = (EditText) mRootView.findViewById(R.id.add_edit_year);
					String yearText = yearEditText.getText().toString().trim();
					movie.setYear((yearText.length()>0) ? Integer.parseInt(yearText) : 0);
					// get genre
					movie.setGenre(mGenre.equals(getString(R.string.add_edit_genre)) ? "" : mGenre); //change to null?
					// get mpaa rating
					movie.setMpaa_rating(mRating.equals(getString(R.string.add_edit_rating_select)) ? "" : mRating);
					// get runtime
					EditText runtimeEditText = (EditText) mRootView.findViewById(R.id.add_edit_runtime);
					String runtimeText = runtimeEditText.getText().toString().trim();
					movie.setRuntime((runtimeText.length()>0) ? Integer.parseInt(runtimeText) : 0);
					// get description
					EditText dscrptnEditText = (EditText) mRootView.findViewById(R.id.add_edit_description);
					movie.setDescription(dscrptnEditText.getText().toString().trim());
					// get User Rating
					movie.setUser_rating((!mHasRated && mMyRating==0) ? 0 : mMyRating);
					// get cast
					EditText castEditText = (EditText) mRootView.findViewById(R.id.add_edit_cast);
					movie.setCast(castEditText.getText().toString().trim());
					// get director
					EditText dirEditText = (EditText) mRootView.findViewById(R.id.add_edit_director);
					movie.setDirector(dirEditText.getText().toString().trim());
					// Tomato rating
					movie.setRt_rating(0);
					
					MoviesDBAdapter db = new MoviesDBAdapter(getActivity());
					db.addMovie(movie);
					getActivity().setResult(RESULT_OK);
					getActivity().finish();
				}
			});
		}
	}
}
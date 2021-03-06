/*

Add checkmark click handler
			watchCheckIV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (movie.getWatched() == Movie.UNWATCHED) {
						watchCheckIV.setImageResource(R.drawable.checkmark_green);
						movie.setWatched(Movie.WATCHED);
					} else {
						watchCheckIV.setImageResource(R.drawable.checkmark_grey);
						movie.setWatched(Movie.UNWATCHED);
					}
				}
			}); 
*/



package com.example.movies_lefkowitz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies_lefkowitz.dialogs.GenrePickerFragment;
import com.example.movies_lefkowitz.dialogs.PicUrlDialog;
import com.example.movies_lefkowitz.dialogs.RatingPickerFragment;
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
	public static class PlaceholderFragment extends Fragment 
	implements PicUrlDialog.PicUrlDialogListener {
		private static final String GENRE_DIALOG_TAG = "genre dialog";
		private static String RATING_DIALOG_TAG = "rating dialog";	
		private static final int REQUEST_GENRE = 0;
		private static final int REQUEST_RATING = 1;
		private static final int REQUEST_URL = 2;
		private static final int TARGET_HEIGHT = 140;
		
		private View mRootView;
		private ImageView mThumbnail;
		private ImageView mWatchCheckIV;
		private ProgressBar mProgressBarPB;
		private TextView mGenreTV;
		private TextView mRatingTV;
		private TextView mMyRatingTV;
		private SeekBar sb;

		private ArrayList<String> mGenreArray;
		private String mGenre;
		private String mRating = "";
		private String mUrl = "";
		private boolean mHasRated = false;
		private double mMyRating;
		private Movie mMovie = null;
		private boolean mIsNew = true;
		private boolean watched = false;
		
		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_add_edit, container,
					false);
			mProgressBarPB = (ProgressBar) mRootView
						.findViewById(R.id.add_edit_pb);
			mThumbnail = (ImageView) mRootView.findViewById(R.id.add_edit_thumb);
			mWatchCheckIV = (ImageView) mRootView.findViewById(R.id.add_edit_check);
			Intent intent = getActivity().getIntent();
			mIsNew = intent.getBooleanExtra("isNew", true);			
			if (mIsNew) {
				getActivity().setTitle(getResources()
						.getString(R.string.title_activity_add));
			} else {
				mMovie = (Movie) intent.getSerializableExtra("movie");
				getActivity().setTitle(getResources()
						.getString(R.string.edit) + " \"" + mMovie.getTitle() + " \"");
				mUrl = mMovie.getPic();
				setGenreArray(mMovie);
				setWatched(mMovie);
				setThumbnail(mUrl);
				set_title(mMovie);
				setYear(mMovie);
				setGenreTV(mMovie);
				setRatingTV(mMovie);
				setRuntime(mMovie);
				setDescription(mMovie);
				setUserRating(mMovie);
				setCast(mMovie);
				setDirectors(mMovie);
			}

			setGenreTV();
			setGetPicUrlHandler();
			setWatchedHandler();
			setGenreHandler();
			setRatingHandler();
			setSeekBarHandler();
			setCancelHandler();
			setSaveHandler();
			
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

		private void setGenreArray(Movie movie) {
			String genres = movie.getGenre();
			mGenreArray = GenrePickerFragment.genreStringToArray(genres);
		}
		
		private void setWatched(Movie movie) {
			if (movie.getWatched() == Movie.UNWATCHED) {
				mWatchCheckIV.setImageResource(R.drawable.checkmark_grey);
				watched = false;
			} else {
				mWatchCheckIV.setImageResource(R.drawable.checkmark_green);
				watched = true;
			}
		}
		
		private void setThumbnail (String url){
			MainActivity.GetImage.download(url, 
					getActivity(), 
					mThumbnail, 
					TARGET_HEIGHT, 
					mProgressBarPB);
		}
		
		private void set_title(Movie movie) {
			EditText titleEditText = (EditText) mRootView.findViewById(R.id.add_edit_title);
			titleEditText.setText(movie.getTitle());
		}
		
		private void setYear(Movie movie) {
			EditText yearET = (EditText) mRootView.findViewById(R.id.add_edit_year);
			yearET.setText( Integer.toString(movie.getYear() ) );
			
		}
		
		private void setGenreTV(Movie movie) {
			mGenreTV = (TextView) mRootView.findViewById(R.id.add_edit_genre);
			mGenreTV.setText(movie.getGenre());
		}
		
		private void setRatingTV(Movie movie) {
			mRatingTV = (TextView) mRootView.findViewById(R.id.add_edit_rating_select);
			mRatingTV.setText("Rating: " + movie.getMpaa_rating());
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
			if (mRating == null || mRating.length()<=0) {
				mRatingTV.setHint(R.string.add_edit_rating_select);
			} else {
				mRatingTV.setText("Rating: " + mRating);
			}
		}

		private void setGetPicUrlHandler() {
			mThumbnail = (ImageView) mRootView
					.findViewById(R.id.add_edit_thumb);
			Button loadButton = (Button) mRootView.findViewById(R.id.add_edit_reload);
			loadButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					PicUrlDialog dialog = PicUrlDialog.newInstance(mUrl);
					dialog.setTargetFragment(PlaceholderFragment.this, REQUEST_URL);
					dialog.show(fm, "pic_url_dialog");
				}
			});
		}
		
		private void setWatchedHandler() {
			mWatchCheckIV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mMovie.getWatched() == Movie.UNWATCHED) {
						mWatchCheckIV.setImageResource(R.drawable.checkmark_green);
						mMovie.setWatched(Movie.WATCHED);
						watched = true;
					} else {
						mWatchCheckIV.setImageResource(R.drawable.checkmark_grey);
						mMovie.setWatched(Movie.UNWATCHED);
						watched = false;
					}					
				}
			});
		}

		@Override
		public void onPicUrlDialogPositiveClick(DialogFragment dialog, String url) {
			mUrl = url;
			setThumbnail(url);
		}

		@Override
		public void onPicUrlDialogNegativeClick(DialogFragment dialog) {
			return;			
		}
		
		/*
		private void setYearHandler() {
			mYearTV = (TextView) mRootView.findViewById(R.id.add_edit_year);
			mYearTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					Calendar c = Calendar.getInstance();

					c.get(Calendar.YEAR);
					c.get(Calendar.MONTH);
					c.get(Calendar.DAY_OF_MONTH);

					DatePickerDialog dialog = this.customDatePicker();
					dialog.show();
				}

				OnDateSetListener dateDialog = new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar c = Calendar.getInstance();

						c.set(Calendar.YEAR, year);

						mYearTV.setText("" + year);

						//yearMovie = year;
					}
				};

				public DatePickerDialog customDatePicker() {

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
								Field datePickerFields[] = datePickerDialogField
										.getType().getDeclaredFields();
								for (Field datePickerField : datePickerFields) {
									if ("mDayPicker".equals(datePickerField
											.getName())
											|| "mDaySpinner".equals(datePickerField
													.getName())
											|| "mMonthSpinner"
													.equals(datePickerField
															.getName())) {
										datePickerField.setAccessible(true);
										Object dayPicker = new Object();
										dayPicker = datePickerField.get(datePicker);
										((View) dayPicker).setVisibility(View.GONE);
										Object monthPicker = new Object();
										monthPicker = datePickerField
												.get(datePicker);
										((View) monthPicker)
												.setVisibility(View.GONE);
									}
								}
							}

						}
					} catch (Exception ex) {
					}
					return dpd;
				}

			});
		}
		*/
		
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
					if (mIsNew) {
						mMovie = new Movie(getActivity(), title);
					} else {
						mMovie.setTitle(title);
					}
					// get watched
					mMovie.setWatched((watched) ? Movie.WATCHED : Movie.UNWATCHED);
					// get pic URL
					mMovie.setPic(mUrl);
					// get year
					TextView yearEditText = (TextView) mRootView.findViewById(R.id.add_edit_year);
					String yearText = yearEditText.getText().toString().trim();
					mMovie.setYear((yearText.length()>0) ? Integer.parseInt(yearText) : 0);
					// get genre
					mMovie.setGenre(mGenre.equals(getString(R.string.add_edit_genre)) ? "" : mGenre); //change to null?
					// get mpaa rating
					mMovie.setMpaa_rating(mRating.equals(getString(R.string.add_edit_rating_select)) ? "" : mRating);
					// get runtime
					EditText runtimeEditText = (EditText) mRootView.findViewById(R.id.add_edit_runtime);
					String runtimeText = runtimeEditText.getText().toString().trim();
					mMovie.setRuntime((runtimeText.length()>0) ? Integer.parseInt(runtimeText) : 0);
					// get description
					EditText dscrptnEditText = (EditText) mRootView.findViewById(R.id.add_edit_description);
					mMovie.setDescription(dscrptnEditText.getText().toString().trim());
					// get User Rating
					mMovie.setUser_rating((!mHasRated && mMyRating==0) ? 0 : mMyRating);
					// get cast
					EditText castEditText = (EditText) mRootView.findViewById(R.id.add_edit_cast);
					mMovie.setCast(castEditText.getText().toString().trim());
					// get director
					EditText dirEditText = (EditText) mRootView.findViewById(R.id.add_edit_director);
					mMovie.setDirector(dirEditText.getText().toString().trim());
					
					MoviesDBAdapter db = new MoviesDBAdapter(getActivity());
					if (mIsNew) {
						// No tomato rating so set to 0
						mMovie.setRt_rating(0);
						db.addMovie(mMovie);
					} else {
						db.updateMovie(mMovie);
					}
					getActivity().setResult(RESULT_OK);
					getActivity().finish();
				}
			});
		}
	}
}
// TODO: rework fragment flow depending on if came 
// from internet search or mainfragment

package com.example.movies_lefkowitz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.example.movies_lefkowitz.dialogs.GenrePickerFragment;
import com.example.movies_lefkowitz.dialogs.RatingPickerFragment;
import com.example.movies_lefkowitz.model.Movie;
import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class DetailFragment extends Fragment {
	private static final int TARGET_HEIGHT = 140;
	
	private View mDetailsView;
	private Movie mMovie;
	private String mSource;
	private ImageView mThumbnailIV;
	private ImageView mWatchCheckIV;
	private TextView mTitleTV;
	private TextView mGenreTV;
	private ImageView mMpaaIV;
	private TextView mRuntimeTV;
	private TextView mRt_ratingTV;
	private TextView mMy_ratingTV;
	private TextView mDescriptionTV;
	private TextView mCastTV;
	private TextView mDirectorTV;
	private Button mSaveBtn;
	private Button mCancelBtn;

	public DetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the movie from the intent
		Intent intent = getActivity().getIntent();
		mMovie = (Movie) intent.getSerializableExtra("movie");
		mSource = intent.getStringExtra("source");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDetailsView = inflater.inflate(R.layout.fragment_details, container,
				false);
		
		mThumbnailIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_thumb);
		mWatchCheckIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_check);
		mTitleTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_title);
		mGenreTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_genre);
		mMpaaIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_mpaa);
		mRuntimeTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_runtime);
		mRt_ratingTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_rt_rating);
		mMy_ratingTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_my_rating);
		mDescriptionTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_description);
		mCastTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_cast);
		mDirectorTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_director);
		mSaveBtn = (Button) mDetailsView
				.findViewById(R.id.detail_item_save);
		mCancelBtn = (Button) mDetailsView
				.findViewById(R.id.detail_item_cancel);

		if (mSource.equalsIgnoreCase("InternetSearchActivity")) {
			mSaveBtn.setVisibility(android.view.View.VISIBLE);
			mCancelBtn.setVisibility(android.view.View.VISIBLE);
			setSaveHandler();
			setCancelHandler();
		}
		

		/* Set placeholder thumbnail before try to download */
		mThumbnailIV.setImageResource(R.drawable.thumb);

		/* Try to download the thumbnail */
		MainActivity.GetImage.download(mMovie.getPic(), 
				getActivity(), 
				mThumbnailIV, 
				TARGET_HEIGHT);
	
		/*
		 * Displayed checkmark image (green/grey) depends if user has seen the
		 * movie
		 */
		if (mMovie.getWatched() == Movie.UNWATCHED) {
			mWatchCheckIV.setImageResource(R.drawable.checkmark_grey);
		} else {
			mWatchCheckIV.setImageResource(R.drawable.checkmark_green);
		}

		/* Set title and year TV */
		mTitleTV.setMovementMethod(LinkMovementMethod.getInstance());
		mTitleTV.setText(
				getTitleYearSpan(getActivity(), mMovie.getTitle(),
						mMovie.getYear()), BufferType.SPANNABLE);

		/* Display genres */
		String g = mMovie.getGenre();
		mGenreTV.setText(g);

		/* Display relevant MPAA rating icon */
		int mpaa_icon_reference = RatingPickerFragment.getMPAAicon(mMovie.getMpaa_rating());
		if (mpaa_icon_reference>0) {
			mMpaaIV.setImageResource(mpaa_icon_reference);
		} else {
			mMpaaIV.setImageResource(android.R.color.transparent);
		}

		/* Display runtime */
		int rt = mMovie.getRuntime();
		int h = rt / 60;
		int m = rt % 60;
		String hours = ((h > 0) ? h + " hrs " : "");
		String runtime = hours + ((m > 0) ? m + " mins" : "");
		mRuntimeTV.setText(runtime);

		/* Display Rotten Rating */
		double rt_rating = mMovie.getRt_rating();
		mRt_ratingTV.setText(rt_rating > 0 ? Double.toString(rt_rating) : "");

		/* Display User Rating */
		double user_rating = mMovie.getUser_rating();
		mMy_ratingTV
				.setText(user_rating > 0 ? Double.toString(user_rating) : "");

		/* Display description */
		mDescriptionTV.setText(mMovie.getDescription());

		/* Display the cast */
		mCastTV.setText(mMovie.getCast());

		/* Display the director(s) */
		mDirectorTV.setText(mMovie.getDirector());
		
		if (mMovie.getRottenID()>0) {
			GetMoviesTask movieTask = new GetMoviesTask(getActivity());
			movieTask.execute(Long.toString(mMovie.getRottenID() ) );
		} 
		
		return mDetailsView;
	}

	private void setCancelHandler() {
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
	}

	private void setSaveHandler() {
		mSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoviesDBAdapter db = new MoviesDBAdapter(getActivity());
				db.addMovie(mMovie);
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("saved", true);
				startActivity(intent);
			}
		}) ;
	}

	public static SpannableStringBuilder getTitleYearSpan(Context ctx,
			String title, int y) {
		// Text color attribute id
		int[] attrs = { android.R.attr.textColor };

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

		title_year.setSpan(new TextAppearanceSpan(ctx,
				android.R.style.TextAppearance_Small, textColor), span_start,
				span_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		ta.recycle();
		return title_year;
	}

	private class GetMoviesTask extends AsyncTask<String, Void, Movie> {

		/* Constants */
		private final String LOG_TAG = GetMoviesTask.class.getSimpleName();
		private final String MOVIE_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/movies/";
		private final String API_PARAM = "apikey";
		private final String MY_API = "smbffqgh98ztd8vq2b4b394a";

		/* Fields */
		private Activity activity;

		/* Constructor to get Activity */
		public GetMoviesTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Movie doInBackground(String... params) {
			// Get URL for query
			URL url = getMovieURL(params[0]);
			// go to url and get JSON
			String moviesJSON = getJSONString(url);
			// get movie list from moviesjson
			return parseJSON(moviesJSON);
		}

		private URL getMovieURL(String param) {
			// Construct the URL for the rotten tomato query
			Uri builtUri = Uri.parse(MOVIE_BASE_URL + param + ".json?")
					.buildUpon().appendQueryParameter(API_PARAM, MY_API)
					.build();
			URL url = null;

			try {
				url = new URL(builtUri.toString());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			return url;
		}

		private String getJSONString(URL url) {
			// These two need to be declared outside the try/catch
			// so that they can be closed in the finally block.
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			// Will contain the raw JSON response as a string.
			String moviesJsonStr = null;

			try {
				// Create the request to Rotten Tomatoes,
				// and open the connection
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				// Read the input stream into a String
				InputStream inputStream = urlConnection.getInputStream();
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					// Nothing to do.
					return null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line + "\n");
				}

				if (buffer.length() == 0) {
					// Stream was empty. No point in parsing.
					return null;
				}
				moviesJsonStr = buffer.toString();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error ", e);
				// If the code didn't successfully get the movie data,
				// there's no point in attempting
				// to parse it.
				moviesJsonStr = null;
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						Log.e(LOG_TAG, "Error closing stream", e);
					}
				}
			}
			if (moviesJsonStr != null) {
				return moviesJsonStr;
			}
			return null;
		}

		private Movie parseJSON(String jsonString) {
			Movie movie = null;
			try {
				JSONObject js = new JSONObject(jsonString);
				
				if (js.has("title")) {
					String title = js.getString("title");
					movie = new Movie(activity, title);
				}
				
				if (js.has("year")) {
					String year = js.getString("year");
					movie.setYear(Integer.parseInt(year));
				}
				
				if (js.has("id")) {
					movie.setRottenID(Integer.parseInt(js.getString("id") ) );
				}
				
				if (js.has("posters")) {
					JSONObject posters = js.getJSONObject("posters");
					String pic = posters.getString("original").replace("tmb", "ori");
					movie.setPic(pic);
				}
				
				movie.setWatched(mMovie.getWatched());
				
				if (js.has("genres")) {
					JSONArray genres = js.getJSONArray("genres");
					ArrayList<String> genreArrayList = new ArrayList<String>();
					for (int j = 0; j < genres.length(); j++) {
						genreArrayList.add(genres.getString(j));
					}
					String genre = GenrePickerFragment
							.genreArrayToString(genreArrayList);
					movie.setGenre(genre);
				}

				if (js.has("mpaa_rating")) {
					String mpaa_rating = js.getString("mpaa_rating");
					movie.setMpaa_rating(mpaa_rating);
				}
				
				if (js.has("ratings")) {
					String rating = js.getJSONObject("ratings").getString("audience_score");
					movie.setRt_rating((Double.parseDouble(rating))/10); 
				}
				
				if (js.has("runtime")) {
					String runtime = js.getString("runtime");
					if (runtime.length() > 0) {
						movie.setRuntime(Integer.parseInt(runtime));
					}
				}
				
				if (js.has("synopsis")) {
					String description = js.getString("synopsis");
					movie.setDescription(description);
				}

				if (js.has("abridged_cast")) {
					JSONArray actors = js.getJSONArray("abridged_cast");
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < actors.length(); j++) {
						JSONObject actor = (JSONObject) actors.get(j);
						if (actor.has("name")) {
							sb.append(actor.get("name") + ", ");
						}
					}
					if (sb.length() > 0) {
						movie.setCast(sb.substring(0, sb.length() - 2)
								.toString());
					}
				}

				if (js.has("abridged_directors")) {
					JSONArray directors = js.getJSONArray("abridged_directors");
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < directors.length(); j++) {
						JSONObject dir = (JSONObject) directors.get(j);
						if (dir.has("name")) {
							sb.append(dir.get("name") + ", ");
						}
					}
					movie.setDirector(sb.substring(0, sb.length() - 2)
							.toString());
				}
				
			} catch (JSONException e) {
				// some sort of json error no point in parsing
				return null;
			}
			return movie;
		}

		@Override
		protected void onPostExecute(Movie movie) {
			super.onPostExecute(movie);
			
			if (movie == null) {
				Toast.makeText(activity, "Error loading movie details",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			/* Display Thumbnail to new one */
			String oldPic = mMovie.getPic();
			String newPic = movie.getPic();
			if (!(oldPic.equalsIgnoreCase(newPic) ) ) { // have a new pic url 
				MainActivity.GetImage.download(newPic, 
						getActivity(), 
						mThumbnailIV, 
						TARGET_HEIGHT);
			}
				
			/*
			 * Displayed checkmark image (green/grey) depends if user has seen
			 * the movie
			 */
			if (movie.getWatched() == Movie.UNWATCHED) {
				mWatchCheckIV.setImageResource(R.drawable.checkmark_grey);
			} else {
				mWatchCheckIV.setImageResource(R.drawable.checkmark_green);
			}

			/* Display title and year to new one */
			String oldTitle = mMovie.getTitle();
			String newTitle = movie.getTitle();
			String oldYear = Integer.toString(mMovie.getYear());
			String newYear = Integer.toString(movie.getYear());
			if (!(oldTitle.equalsIgnoreCase(newTitle)) ||
				!(oldYear.equalsIgnoreCase(newYear)) ) {
				mTitleTV.setMovementMethod(LinkMovementMethod.getInstance());
				mTitleTV.setText(
						DetailFragment.getTitleYearSpan(getActivity(),
								movie.getTitle(), movie.getYear()),
						BufferType.SPANNABLE);
			}
			
			/* Display description */
			String oldDescript = mMovie.getDescription();
			String newDescript = movie.getDescription();
			if (!(oldDescript.equalsIgnoreCase(newDescript))) {
				mDescriptionTV.setText(newDescript);
			}

			/* Display Rotten Rating */
			double oldRottenRating = mMovie.getRt_rating();
			double newRottenRating = movie.getRt_rating();
			if (oldRottenRating != newRottenRating) {
				mRt_ratingTV.setText(Double.toString(movie.getRt_rating()));
			}

			/* Display User Rating */
			mMy_ratingTV.setText(Double.toString(mMovie.getUser_rating()));

			/* Display genres */
			String oldGenres = mMovie.getGenre();
			String newGenres = movie.getGenre();
			if (!(oldGenres.equalsIgnoreCase(newGenres))) {
				mGenreTV.setText(mMovie.getGenre());
			}

			/* Display relevant MPAA rating icon */
			String oldMpaa = mMovie.getMpaa_rating();
			String newMpaa  = movie.getMpaa_rating();
			if (!(oldMpaa.equalsIgnoreCase(newMpaa))) {
				mMpaaIV.setImageResource(RatingPickerFragment.getMPAAicon(movie
						.getMpaa_rating()));
			}

			/* Display runtime */
			int oldRt = mMovie.getRuntime();
			int newRt = movie.getRuntime();
			if (oldRt != newRt ) {
				int h = newRt / 60;
				int m = newRt % 60;
				String hours = ((h > 0) ? h + " hrs " : "");
				String runtime = hours + ((m > 0) ? m + " mins" : "");
				mRuntimeTV.setText(runtime);
			}

			/* Display the cast */
			String oldCast = mMovie.getCast();
			String newCast = movie.getCast();
			if (!(oldCast.equalsIgnoreCase(newCast))) {
				mCastTV.setText(newCast);
			}

			/* Display the director(s) */
			String oldDir = mMovie.getDirector();
			String newDir = movie.getDirector();
			if (!(oldDir.equalsIgnoreCase(newDir))) {
				mDirectorTV.setText(newDir);
			}
			
			mMovie = movie;
		}
	}
}

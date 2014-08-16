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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.example.movies_lefkowitz.model.Movie;

public class DetailFragment extends Fragment {
	private View mDetailsView;
	private Movie mMovie;
	private ImageView mThumbnailIV;

	public DetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the movie from the intent
		Intent intent = getActivity().getIntent();
		mMovie = (Movie) intent.getSerializableExtra("movie");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDetailsView = inflater.inflate(R.layout.fragment_details, container,
				false);

		mThumbnailIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_thumb);
		ImageView watchCheckIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_check);
		TextView titleTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_title);
		TextView genreTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_genre);
		ImageView mpaaIV = (ImageView) mDetailsView
				.findViewById(R.id.detail_item_mpaa);
		TextView runtimeTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_runtime);
		TextView rt_ratingTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_title);
		TextView my_ratingTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_title);
		TextView descriptionTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_title);
		TextView castTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_cast);
		TextView directorTV = (TextView) mDetailsView
				.findViewById(R.id.detail_item_director);

		/* Set placeholder thumbnail before try to download */
		mThumbnailIV.setImageResource(R.drawable.thumb);

		/*
		 * Displayed checkmark image (green/grey) depends if user has seen the
		 * movie
		 */
		if (mMovie.getWatched() == Movie.UNWATCHED) {
			watchCheckIV.setImageResource(R.drawable.checkmark_grey);
		} else {
			watchCheckIV.setImageResource(R.drawable.checkmark_green);
		}

		/* Set title and year TV */
		titleTV.setMovementMethod(LinkMovementMethod.getInstance());
		titleTV.setText(
				getTitleYearSpan(getActivity(), mMovie.getTitle(),
						mMovie.getYear()), BufferType.SPANNABLE);

		/* Display genres */
		genreTV.setText(mMovie.getGenre());

		/* Display relevant MPAA rating icon */
		mpaaIV.setImageResource(RatingPickerFragment.getMPAAicon(mMovie
				.getMpaa_rating()));

		/* Display runtime */
		int rt = mMovie.getRuntime();
		int h = rt / 60;
		int m = rt % 60;
		String hours = ((h > 0) ? h + " hrs " : "");
		String runtime = hours + ((m > 0) ? m + " mins" : "");
		runtimeTV.setText(runtime);

		/* Display Rotten Rating */
		double rt_rating = mMovie.getRt_rating();
		rt_ratingTV.setText(rt_rating > 0 ? Double.toString(rt_rating) : "");

		/* Display User Rating */
		double user_rating = mMovie.getUser_rating();
		my_ratingTV
				.setText(user_rating > 0 ? Double.toString(user_rating) : "");

		/* Display description */
		descriptionTV.setText(mMovie.getDescription());

		/* Display the cast */
		castTV.setText(mMovie.getCast());

		/* Display the director(s) */
		directorTV.setText(mMovie.getDirector());

		return mDetailsView;
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

			try {
				JSONObject js = new JSONObject(jsonString);
				if (js.has("genres") && mMovie.getGenre().length() == 0) {
					JSONArray genres = js.getJSONArray("genres");
					ArrayList<String> genreArrayList = new ArrayList<String>();
					for (int j = 0; j < genres.length(); j++) {
						genreArrayList.add(genres.getString(j));
					}
					String genre = GenrePickerFragment
							.genreArrayToString(genreArrayList);
					mMovie.setGenre(genre);
				}

				if (js.has("mpaa_rating")
						&& mMovie.getMpaa_rating().length() == 0) {
					String mpaa_rating = js.getString("mpaa_rating");
					mMovie.setMpaa_rating(mpaa_rating);
				}

				if (js.has("abridged_cast") && mMovie.getCast().length() == 0) {
					JSONArray actors = js.getJSONArray("abridged_cast");
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < actors.length(); j++) {
						JSONObject actor = (JSONObject) actors.get(j);
						if (actor.has("name")) {
							sb.append(actor.get("name") + ", ");
						}
					}
					if (sb.length() > 0) {
						mMovie.setCast(sb.substring(0, sb.length() - 2)
								.toString());
					}
				}

				if (js.has("abridged_directors")
						&& mMovie.getDirector().length() == 0) {
					JSONArray directors = js.getJSONArray("abridged_directors");
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < directors.length(); j++) {
						JSONObject actor = (JSONObject) directors.get(j);
						if (actor.has("name")) {
							sb.append(actor.get("name") + ", ");
						}
					}
					mMovie.setDirector(sb.substring(0, sb.length() - 2)
							.toString());
				}

				if (js.has("runtime") && mMovie.getRuntime() == 0) {
					String runtime = js.getString("runtime");
					if (runtime.length() > 0) {
						mMovie.setRuntime(Integer.parseInt(runtime));
					}
				}

			} catch (JSONException e) {
				// some sort of json error no point in parsing
				return null;
			}
			return mMovie;
		}

		@Override
		protected void onPostExecute(Movie movies) {
			super.onPostExecute(movies);

			if (movies == null) {
				Toast.makeText(activity, "Error loading movie details",
						Toast.LENGTH_LONG).show();
				return;
			}

			ImageView mThumbnailIV = (ImageView) mDetailsView
					.findViewById(R.id.list_item_thumb);
			ImageView watchCheckIV = (ImageView) mDetailsView
					.findViewById(R.id.list_item_check);
			TextView titleTV = (TextView) mDetailsView
					.findViewById(R.id.list_item_title);
			TextView descriptionTV = (TextView) mDetailsView
					.findViewById(R.id.list_item_description);
			TextView rt_ratingTV = (TextView) mDetailsView
					.findViewById(R.id.list_item_rt_rating);
			TextView my_ratingTV = (TextView) mDetailsView
					.findViewById(R.id.list_item_my_rating);

			mThumbnailIV.setImageResource(R.drawable.thumb);
			new ImageLoader(getActivity(), mThumbnailIV).execute(mMovie
					.getPic());

			/*
			 * Displayed checkmark image (green/grey) depends if user has seen
			 * the movie
			 */
			if (mMovie.getWatched() == Movie.UNWATCHED) {
				watchCheckIV.setImageResource(R.drawable.checkmark_grey);
			} else {
				watchCheckIV.setImageResource(R.drawable.checkmark_green);
			}

			/* Display title */
			titleTV.setMovementMethod(LinkMovementMethod.getInstance());
			titleTV.setText(
					DetailFragment.getTitleYearSpan(getActivity(),
							mMovie.getTitle(), mMovie.getYear()),
					BufferType.SPANNABLE);

			/* Display description */
			descriptionTV.setText(mMovie.getDescription());

			/* Display Rotten Rating */
			rt_ratingTV.setText(Double.toString(mMovie.getRt_rating()));

			/* Display User Rating */
			my_ratingTV.setText(Double.toString(mMovie.getUser_rating()));

		}
	}
}

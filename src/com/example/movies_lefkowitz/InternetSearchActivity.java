package com.example.movies_lefkowitz;

// TODO: Click listener for movie
// TODO: ProgressDialog
// TODO: Save image to disk so that doesnt need to reload thumbnail

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.example.movies_lefkowitz.model.Movie;
import com.example.movies_lefkowitz.model.MovieHolder;

public class InternetSearchActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_internet_search);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.internet_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		View mRootView;
		private ListView mListview;
		private ArrayAdapter<Movie> mMoviesAdapter;

		// private Dialog progressDialog;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_internet_search,
					container, false);
			mListview = (ListView) mRootView
					.findViewById(R.id.internet_search_listView);
			setSearchClickHandler();
			return mRootView;
		}

		private void setSearchClickHandler() {
			Button search = (Button) mRootView
					.findViewById(R.id.internet_search_button);
			search.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String userInput = getUserInput();
					getRottenTomatoJSON(userInput);
				}
			});
		}

		private String getUserInput() {
			EditText inputET = (EditText) mRootView
					.findViewById(R.id.internet_search_user_input);
			return inputET.getText().toString();
		}

		private void getRottenTomatoJSON(String input) {
			GetMoviesTask movies = new GetMoviesTask(getActivity());
			movies.execute(input);
		}

		private class GetMoviesTask extends
				AsyncTask<String, Void, List<Movie>> {

			/* Constants */
			private final String LOG_TAG = GetMoviesTask.class.getSimpleName();
			private final String MOVIES_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?";
			private final String PAGE_LIMIT_PARAM = "page_limit";
			private final String NUM_MOVIES = "50";
			private final String API_PARAM = "apikey";
			private final String MY_API = "smbffqgh98ztd8vq2b4b394a";
			private final String QUERY_PARAM = "q";

			/* Fields */
			private Activity activity;

			/* Constructor to get Activity */
			public GetMoviesTask(Activity activity) {
				this.activity = activity;
			}

			@Override
			protected List<Movie> doInBackground(String... params) {
				// Get URL for query
				URL url = getMoviesURL(params[0]);
				// go to url and get JSON
				String moviesJSON = getJSONString(url);
				// get movie list from moviesjson
				return parseJSON(moviesJSON);
			}

			private URL getMoviesURL(String param) {

				// Construct the URL for the rotten tomato query
				Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
						.appendQueryParameter(API_PARAM, MY_API)
						.appendQueryParameter(QUERY_PARAM, param)
						.appendQueryParameter(PAGE_LIMIT_PARAM, NUM_MOVIES)
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
					reader = new BufferedReader(new InputStreamReader(
							inputStream));

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

			private List<Movie> parseJSON(String jsonString) {
				List<Movie> movies = new ArrayList<Movie>();

				// Convert String to JSON array of movies
				JSONArray movieArray = stringToJsonArray(jsonString, "movies");

				// iterate over JsonArray and add each movie to movie array
				for (int i = 0; i < movieArray.length(); i++) {
					
					JSONObject js;
					try {
						// get movie from the list
						js = (JSONObject) movieArray.get(i);
						Movie movie = null;
						
						if (js.has("title")) {
							String title = js.getString("title");
							movie = new Movie(activity, title);
						}
						
						if (js.has("id")) {
							movie.setRottenID(Integer.parseInt(js.getString("id") ) );
						}
					
						if (js.has("year")) {
							String year = js.getString("year");
							movie.setYear(Integer.parseInt(year));
						}
											
						if (js.has("posters")) {
							JSONObject posters = js.getJSONObject("posters");
							String pic = posters.getString("original").replace("tmb", "ori");
							movie.setPic(pic);
						}

						if (js.has("synopsis")) {
							String description = js.getString("synopsis");
							movie.setDescription(description);
						}

						if (js.has("ratings")) {
							String rating = js.getJSONObject("ratings").getString("audience_score");
							movie.setRt_rating((Double.parseDouble(rating))/10); 
						}
											
						movie.setWatched(0);
						movies.add(movie);
						
					} catch (JSONException e) {
						// some sort of json error no point in parsing
						return null;
					}
				}
				return movies;
			}

			private JSONArray stringToJsonArray(String jsonString, String key) {
				JSONObject json;
				try {
					json = new JSONObject(jsonString);
					return json.getJSONArray(key);
				} catch (JSONException e) {
					e.printStackTrace(); // TODO: Make a better catch statement
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Movie> movies) {
				super.onPostExecute(movies);
				
				if(movies == null) {
					Toast.makeText(activity, "Error during search movie", Toast.LENGTH_LONG).show();
					return;
				}
				
				mMoviesAdapter = new ArrayAdapter<Movie>(activity, -1, movies) {

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						Movie movie = getItem(position);
						View movieView = convertView;
						
						if(movieView == null) {
							movieView = activity.getLayoutInflater().inflate(R.layout.item_layout, null);
							MovieHolder holder = new MovieHolder();  
							movieView.setTag(holder);
						}

						final MovieHolder holder = (MovieHolder)movieView.getTag();
						holder.setMovie(movie);

						ImageView thumbnailIV = (ImageView) movieView
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
						
						thumbnailIV.setImageResource(R.drawable.thumb);
						new ImageLoader(getActivity(), thumbnailIV).execute(movie.getPic());
						
						/* Displayed checkmark image (green/grey) depends if user has seen the movie */
						if (movie.getWatched() == Movie.UNWATCHED) {
							watchCheckIV.setImageResource(R.drawable.checkmark_grey);
						} else {
							watchCheckIV.setImageResource(R.drawable.checkmark_green);
						}
						
						/* Display title */
						titleTV.setMovementMethod(LinkMovementMethod.getInstance());
						titleTV.setText(DetailFragment.getTitleYearSpan(
									getActivity(), 
									movie.getTitle(),
									movie.getYear() ), 
								BufferType.SPANNABLE);
						
						/* Display description */
						descriptionTV.setText(movie.getDescription());
						
						/* Display Rotten Rating */
						double rt_rating = movie.getRt_rating();
						rt_ratingTV.setText(rt_rating>0 ? Double.toString(rt_rating) : "");
						
						/* Display User Rating */
						double user_rating = movie.getUser_rating();
						my_ratingTV.setText(user_rating>0 ? Double.toString(user_rating) : "");
						
						/* Add Click Listener */
						movieView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getActivity(), DetailsActivity.class);
								Movie movie = holder.getMovie();
								intent.putExtra("isNew",false);
								intent.putExtra("movie", movie);
								startActivity(intent);
								//startActivityForResult(intent, SAVE_MOVIE_REQUEST_CODE);
							}
						});
						
						return movieView;
					}
				};		    		
				mListview.setAdapter(mMoviesAdapter);
			}
		}
	}
}
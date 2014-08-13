package com.example.movies_lefkowitz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
		private ArrayAdapter<String> mMoviesAdapter;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_internet_search,
					container, false);
			mListview = (ListView) mRootView.findViewById(R.id.internet_search_listView);
			setSearchClickHandler();
			return mRootView;
		}

		private void setSearchClickHandler() {
			Button search = (Button) mRootView.findViewById(R.id.internet_search_button);
			search.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String userInput = getUserInput();
					getRottenTomatoJSON(userInput);
					//populateLVfromJSON();
					
				}
			});
		}

		private String getUserInput() {
			EditText inputET = (EditText) mRootView.findViewById(R.id.internet_search_user_input);
			return inputET.getText().toString();
		}
		
		private void getRottenTomatoJSON(String input) {
			GetMovieTask movies = new GetMovieTask();
			movies.execute(input);
		}
		
		private class GetMovieTask extends AsyncTask <String, Void, String> {
            private final String LOG_TAG = GetMovieTask.class.getSimpleName();
            private final String MOVIES_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?";
            private final String API_PARAM = "apikey";
            private final String MY_API = "smbffqgh98ztd8vq2b4b394a";
            private final String QUERY_PARAM = "q";
            private final String PAGE_LIMIT_PARAM = "page_limit";
            private final String NUM_MOVIES = "50";

    		@Override
            protected String doInBackground(String... params) {
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String moviesJsonStr = null;

                try {
                    // Construct the URL for the rotten tomato query
                	Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                				.appendQueryParameter(API_PARAM, MY_API)
                				.appendQueryParameter(QUERY_PARAM, params[0])
                				.appendQueryParameter(PAGE_LIMIT_PARAM, NUM_MOVIES)
                				.build();
                				
                	URL url = new URL(builtUri.toString());

                    // Create the request to Rotten Tomatoes, and open the connection
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
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    moviesJsonStr = buffer.toString();
                } catch (IOException e)  {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the movie data, there's no point in attempting
                    // to parse it.
                    moviesJsonStr = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e)  {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                if (moviesJsonStr != null) {
                	return moviesJsonStr;
                }
                return null;
            }
    		
    		/*
    		@Override
    		protected void onPostExecute(String result) {
    			if(result != null) {
    				mMoviesAdapter.clear();
    		        for(String dayForecastStr : result) {
    		        	mMoviesAdapter.add(dayForecastStr);
    		        }
    		    }
    		}
    		*/
        }
    }
		
}
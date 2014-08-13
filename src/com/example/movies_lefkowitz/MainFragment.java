/*
 * TODO: 
 * 1. Do the post execute in internet search
 * 2. Details fragment
 * 3. Context menus
 * 4. Scale thumbnails to fit imageviews
 * 			http://stackoverflow.com/questions/4837715/how-to-resize-a-bitmap-in-android
 * 			http://stackoverflow.com/questions/14546922/android-bitmap-resize
 * 5. Set actionbar even if menu key on device
 * 6. Side by side fragments of movies and details
 * 
 * 
 */

package com.example.movies_lefkowitz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies_lefkowitz.Add_Edit_Activity.PlaceholderFragment;
import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class MainFragment extends Fragment {

	private MoviesDBAdapter mMyDb;
	private ListView mListview;
	private Cursor mMovieCursor;
	private MyCursorAdapter mAdapter;
	private View mRootView;
	private ImageView mThumbnailIV;

	protected static final int REQUEST_SEARCH = 0;
	protected static final int REQUEST_MANUAL = 1;
	
	public MainFragment() {
	}

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
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Add Movie").setMessage(
					"Would you like to add movie manually or from the internet?");

				builder.setPositiveButton("Internet", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent searchActivityIntent = new Intent(
							getActivity(), InternetSearchActivity.class);
					startActivityForResult(searchActivityIntent, REQUEST_SEARCH);
				}
			});

			builder.setNegativeButton("Manual", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent editActivityIntent = new Intent(getActivity(),
							Add_Edit_Activity.class);
					startActivityForResult(editActivityIntent, REQUEST_MANUAL);
				}
			});

			builder.setNeutralButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					return;
				}
			});

			builder.create().show();
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
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
			}
		});
		registerForContextMenu(mListview);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_MANUAL) {
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

			mThumbnailIV = (ImageView) movieView
					.findViewById(R.id.list_item_thumb);
			ImageView watchCheckIV = (ImageView) movieView
					.findViewById(R.id.list_item_check);
			TextView titleTV = (TextView) movieView
					.findViewById(R.id.list_item_title);
			TextView yearTV = (TextView) movieView
					.findViewById(R.id.list_item_year);
			TextView descriptionTV = (TextView) movieView
					.findViewById(R.id.list_item_description);
			TextView rt_ratingTV = (TextView) movieView
					.findViewById(R.id.list_item_rt_rating);
			TextView my_ratingTV = (TextView) movieView
					.findViewById(R.id.list_item_my_rating);

			mThumbnailIV.setImageResource(R.drawable.thumb);
			new ImageLoader().execute(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_PIC)));
			
			/* Displayed checkmark image (green/grey) depends if user has seen the movie */
			if (cursor.getInt(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_WATCHED)) == 0) {
				watchCheckIV.setImageResource(R.drawable.checkmark_grey);
			} else {
				watchCheckIV.setImageResource(R.drawable.checkmark_green);
			}
			titleTV.setText(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_TITLE)));
			/* Displayed year depends on if database has a year */
			int year = cursor.getInt(cursor.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_YEAR));
			if (year <= 0) {
				yearTV.setText("");
			} else {
				yearTV.setText(Integer.toString(year));
			}
			/* Display description */
			descriptionTV.setText(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_DESCRIPTION)));
			
			rt_ratingTV.setText(Double.toString(cursor.getDouble(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_RT_RATING))));
			
			my_ratingTV.setText(Double.toString(cursor.getInt(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_USER_RATING))));
		}

		@Override
		public View newView(Context context, Cursor arg1, ViewGroup group) {
			View itemView = null;

			itemView = getActivity().getLayoutInflater().inflate(R.layout.item_layout,
					null);
			return itemView;
		}
	}

	private class ImageLoader extends AsyncTask<String, String, Bitmap> {
		Bitmap bitmap;

		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap image = downloadImage(urls[0]);
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap image) {
			if (image != null) {
				mThumbnailIV.setImageBitmap(image);
			}
		}

		private Bitmap downloadImage(String urlString) {
			URL url;
			try {
				url = new URL(urlString);
				HttpURLConnection httpCon = (HttpURLConnection) url
						.openConnection();

				InputStream is = httpCon.getInputStream();
				int fileLength = httpCon.getContentLength();

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead = 0;
				int totalBytesRead = 0;
				byte[] data = new byte[2048];

				// Read the image bytes in chunks of 2048 bytes
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
					totalBytesRead += nRead;
				}

				buffer.flush();
				byte[] image = buffer.toByteArray();

				Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,
						image.length);
				return bitmap;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}

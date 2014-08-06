package com.example.movies_lefkowitz;

import java.io.InputStream;
import java.net.URL;

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

import com.example.movies_lefkowitz.model.MoviesDBAdapter;

public class MainFragment extends Fragment {

	private MoviesDBAdapter mMyDb;
	private ListView mListview;
	private Cursor mMovieCursor;
	private MyCursorAdapter mAdapter;
	private View mRootView;
	private ImageView thumbnail;

	protected final static int SEARCH_MOVIE_REQUEST_CODE = 1234;
	protected final static int MANUALLY_ADD_MOVIE_REQUEST_CODE = 6789;
	
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
					startActivityForResult(searchActivityIntent,
							SEARCH_MOVIE_REQUEST_CODE);

				}
			});

			builder.setNegativeButton("Manual", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent editActivityIntent = new Intent(getActivity(),
							Add_Edit_Activity.class);
					startActivityForResult(editActivityIntent,
							MANUALLY_ADD_MOVIE_REQUEST_CODE);

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
	}

	private void openDB() {
		mMyDb = new MoviesDBAdapter(getActivity());
	}

	private void populateListViewFromDB() {
		mMovieCursor = mMyDb.getAllMovies();

		// Allow activity to manage lifetime of the cursor.
		getActivity().startManagingCursor(mMovieCursor);

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

	private class MyCursorAdapter extends CursorAdapter {
		public MyCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor, true);
		}

		@Override
		public void bindView(View movieView, Context context, Cursor cursor) {

			thumbnail = (ImageView) movieView
					.findViewById(R.id.list_item_thumb);
			ImageView watchCheck = (ImageView) movieView
					.findViewById(R.id.list_item_check);
			TextView title = (TextView) movieView
					.findViewById(R.id.list_item_title);
			TextView year = (TextView) movieView
					.findViewById(R.id.list_item_year);
			TextView description = (TextView) movieView
					.findViewById(R.id.list_item_description);
			TextView rt_rating = (TextView) movieView
					.findViewById(R.id.list_item_rt_rating);
			TextView my_rating = (TextView) movieView
					.findViewById(R.id.list_item_my_rating);

			thumbnail.setImageResource(R.drawable.thumb);
			new ImageLoader().execute(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_PIC)));
			watchCheck.setImageResource(R.drawable.checkmark_grey);
			title.setText(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_TITLE)));
			year.setText(Integer.toString(cursor.getInt(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_YEAR))));
			description.setText(cursor.getString(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_DESCRIPTION)));
			rt_rating.setText(Double.toString(cursor.getDouble(cursor
					.getColumnIndex(MoviesDBAdapter.KEY_MOVIE_RT_RATING))));
			my_rating.setText(Integer.toString(cursor.getInt(cursor
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
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(
						args[0]).getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap image) {
			if (image != null) {
				thumbnail.setImageBitmap(image);
			}
		}
	}
}
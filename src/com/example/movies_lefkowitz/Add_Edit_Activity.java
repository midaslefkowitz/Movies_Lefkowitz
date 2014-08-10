package com.example.movies_lefkowitz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
		private static String GENRE_DIALOG_TAG = "genre dialog";
		private static int REQUEST_GENRE = 0;
		private static int REQUEST_RATING = 1;
		private View mRootView;
		private String mGenre;
		private ArrayList<String> mGenreArray;
		private String mRating;
		private ImageView mThumbnail;
		private boolean mValidUrl;
		private TextView mGenreTV;
		private TextView mRatingTV;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.fragment_add_edit, container,
					false);
			setGenreTV();
			setLoadPicFromUrlHandler();
			setGenreHandler();
			setRatingHandler();
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
		}

		private void setGenreTV() {
			if (mGenreArray != null) {
				StringBuilder sb = new StringBuilder();
				mGenre = "";
				for (int i = 0, len = mGenreArray.size(); i < len; i++) {
					sb.append(mGenreArray.get(i));
					sb.append(", ");
				}
				mGenre = sb.substring(0, sb.length() - 2).toString();
			} else {
				mGenre = getString(R.string.add_edit_genre);
			}
			mGenreTV = (TextView) mRootView.findViewById(R.id.add_edit_genre);
			mGenreTV.setText(mGenre);
		}

		private void setLoadPicFromUrlHandler() {
			EditText eUrl = (EditText) mRootView
					.findViewById(R.id.add_edit_pic);
			String url = eUrl.getText().toString().trim();
			mThumbnail = (ImageView) mRootView
					.findViewById(R.id.add_edit_thumb);
			new ImageLoader().execute(url);
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
							REQUEST_GENRE);
					dialog.show(fm, GENRE_DIALOG_TAG);
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
					// get pic URL
					EditText ePic = (EditText) mRootView
							.findViewById(R.id.add_edit_pic);
					String pic = null;
					if (mValidUrl) { // ensure that saving a valid url
						pic = ePic.getText().toString().trim();
					}
					// get title
					EditText eTitle = (EditText) mRootView
							.findViewById(R.id.add_edit_title);
					String title = eTitle.getText().toString().trim();
					// get year
					EditText eYear = (EditText) mRootView
							.findViewById(R.id.add_edit_year);
					int year = Integer.parseInt(eYear.getText().toString()
							.trim());
					// get genre
				}
			});
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
					mThumbnail.setImageBitmap(image);
					mValidUrl = true;
				} else {
					mValidUrl = false;
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
}
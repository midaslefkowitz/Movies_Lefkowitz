package com.example.movies_lefkowitz;

// TODO: progress dialog 

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask<String, String, Bitmap> {

	/* Fields */
	Activity activity;
	ImageView mThumbnailIV;
	
	/* Constructor */	
	public ImageLoader(Activity activity, ImageView iv) {
		this.activity = activity;
		this.mThumbnailIV = iv;
	}
	

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

			return BitmapFactory.decodeByteArray(image, 0,
					image.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
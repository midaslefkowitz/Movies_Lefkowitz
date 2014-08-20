package com.example.movies_lefkowitz;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

public class MainActivity extends ActionBarActivity {
	public final static ImageDownloader GetImage = new ImageDownloader();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}
		try {
		    ViewConfiguration config = ViewConfiguration.get(MainActivity.this);
		    Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		    if (menuKeyField != null) {
		        menuKeyField.setAccessible(true);
		        menuKeyField.setBoolean(config, false);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}	
}
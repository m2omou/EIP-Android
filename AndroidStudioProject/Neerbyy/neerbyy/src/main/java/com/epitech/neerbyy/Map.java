package com.epitech.neerbyy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * This is an temporary debugging test class
 * @author Seb
 *
 */
public class Map extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}

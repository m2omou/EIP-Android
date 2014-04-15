package com.epitech.neerbyy;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * This class describe the temporary menu of each views
 * All views extend this class instead the class Activity
 * @author Seb
 *
 */
public class MainMenu extends Activity {
	     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		TextView infoUser = (TextView)findViewById(R.id.menuTxtInfoUser);
		if (Network.USER == null)
			infoUser.setText("Please Login");
		else
			infoUser.setText("Hello " + Network.USER.username);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.layout.menu, menu);
		menu.getItem(2).getSubMenu().setHeaderIcon(R.drawable.ic_launcher);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
        switch (item.getItemId()) {
           case R.id.LocateMe:
              //intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.fr"));
              intent = new Intent(this, Geoloc.class);
              startActivity(intent);
              return true;
           case R.id.getUser:
               intent = new Intent(this, EditInfoUser.class);
			   startActivity(intent);
               return true;
           case R.id.User:
               return true;
           case R.id.Login:
        	   intent = new Intent(this, Login.class);
			   startActivity(intent);
        	   return true;
           case R.id.CreateAccount:
        	   intent = new Intent(this, CreateAccount.class);
			   startActivity(intent);
        	   return true;
           case R.id.Menu:
        	   intent = new Intent(this, MainMenu.class);
			   startActivity(intent);
        	   return true;
           case R.id.quit:
              finish();
              return true;
           case R.id.testToken:
        	   intent = new Intent(this, GetUserById.class);
			   startActivity(intent);
			   break;   //  ou return true ??
           case R.id.mapView:
        	   intent = new Intent(this, MapView.class);
			   startActivity(intent);
			   break;
        }
        return super.onOptionsItemSelected(item);
    }

}

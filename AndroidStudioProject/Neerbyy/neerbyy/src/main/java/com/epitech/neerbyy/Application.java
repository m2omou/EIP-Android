package com.epitech.neerbyy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.PushService;
import com.parse.SaveCallback;


public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
	//Parse.initialize(this, "YOUR_APP_ID", "YOUR_CLIENT_KEY");
  	
    //Parse.initialize(this, "LODDgzzvB7dSRAyu9ECAfAX3RfsiItWJZFHXRUIN", "Xz2zzkjiJvCWU8biVqYcY18Lb5lx9Ez9xFgSo6QR");


	// Specify an Activity to handle all pushes by default.
	//PushService.setDefaultPushCallback(this, Login.class);
  	//PushService.setDefaultPushCallback(this, Login.class);

  	
  /*	ParsePush.subscribeInBackground("", new SaveCallback() {
  	  @Override
  	  public void done(ParseException e) {
  	    if (e == null) {
  	      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
  	    } else {
  	      Log.e("com.parse.push", "failed to subscribe for push", e);
  	    }
  	  }
  	});*/
  }
  
 /* protected void onPushReceive(Context context, Intent intent) {
	 // Intent intent2 = new Intent(Login.this, LostPassword.class);
		//startActivity(intent2);
	  Log.w("toto", "toto");
  }
  */
}

package com.epitech.neerbyy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.PushService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class represent the view for logging an user.
 * Each field must be check for error with the MyRegex class
 *@see User
 *@see MyRegex
 */
public class Login extends Activity {

	 public static final String EXTRA_MESSAGE = "message";
	    public static final String PROPERTY_REG_ID = "registration_id";
	    private static final String PROPERTY_APP_VERSION = "appVersion";
	    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	    /**
	     * Substitute you own sender ID here. This is the project number you got
	     * from the API Console, as described in "Getting Started."
	     */
	    String SENDER_ID = "618589947764";

	    /**
	     * Tag used on log messages.
	     */
	    static final String TAG = "GCMDemo";
	    GoogleCloudMessaging gcm;
	    String regid;
	    AtomicInteger msgId = new AtomicInteger();
	    SharedPreferences prefs;
	    Context context;


	    
	//------------------------------------------------
	    
	Button createAccount;
	Button login;
	Button map;
	EditText loginMail; 
	EditText password; 
	//TextView info;
	TextView lostPassword;
	
	TextView btnLostPassword;
	User user;
	
	List<EditText> list;
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	private MenuItem item_loading;
	
	
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(Login.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login2);
		
		
		 // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
		
		  context = getApplicationContext();
		  
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

    
            if (regid.isEmpty()) {
                registerInBackground(); 
                }
            else
            {
            	 Log.w("PUSH DEJA REGISTER", "Device registered, registration ID = " + regid);
            }
     	
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    
/////////////////////////////////////////////////////////////////////////////////////////
		  
		createAccount = (Button)findViewById(R.id.btnGoCreateAccount2);
		login = (Button)findViewById(R.id.btnLogin2);
		map = (Button)findViewById(R.id.btnLoginMapView);
		loginMail = (EditText)findViewById(R.id.txtLoginMail2);
		password = (EditText)findViewById(R.id.txtLoginPassword2);
		

		btnLostPassword = (TextView)findViewById(R.id.txtLoginLostPasswordMail2);
		//info = (TextView)findViewById(R.id.txtLoginInfo2);
		
		list = new ArrayList<EditText>();
		list.add(loginMail);
		list.add(password);
		
		//loginLostPassword.setVisibility(View.INVISIBLE);
		//btnLostPassword.setVisibility(View.INVISIBLE);
		
		/*lostPassword.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				loginLostPassword.setVisibility(View.VISIBLE);
				btnLostPassword.setVisibility(View.VISIBLE);
			}
		});*/
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		  
		createAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this, CreateAccount.class);
				startActivity(intent);
			}
		});
		map.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this, MapView.class);
				startActivity(intent);
			}
		});
		
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!checkFormu())
			    	return;
				
			//	mProgressDialog = ProgressDialog.show(Login.this, "Please wait",
				//		"Long operation starts...", true);
				
	    		
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/sessions.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("connection[email]", loginMail.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("connection[password]", password.getText().toString()));
		            	if (regid != null) {
		            		nameValuePairs.add(new BasicNameValuePair("connection[device_token]", regid));
			            	nameValuePairs.add(new BasicNameValuePair("connection[platform_id]", "3"));
		            	}
		            	
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.LOGIN.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.POST, nameValuePairs);
						if (input == null)
							messageBundle.putInt("error", 1);
						else
						{	
							Reader readerResp = new InputStreamReader(input);
							String ret = Network.checkInputStream(readerResp);
							
							if (ret.charAt(0) != '{' && ret.charAt(0) != '[')
							{
								messageBundle.putInt("error", 3);
								messageBundle.putString("msgError", ret);
							}
							else
							{
								try {
									rep = gson.fromJson(ret, ResponseWS.class);
									user = rep.getValue(User.class);
								}
								catch(JsonParseException e)
							    {
							        System.out.println("Exception n3 in check_exitrestrepWSResponse::"+e.toString());
							    }
								
								if (rep.responseCode == 1)
								{
									messageBundle.putInt("error", 2);
									messageBundle.putString("msgError", rep.responseMessage);
								}
								else		  	                   
									messageBundle.putSerializable("user", (Serializable) user);	
							}
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                myHandler.sendMessage(msgPb);
	                }
					catch (Exception e) {
		                e.printStackTrace();}
				}};
			thread1.start();			
			}
		});
		
		//-----------------------------------------------------------------
		
		btnLostPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this, LostPassword.class);
				startActivity(intent);
			}
		});
	}
	
	//-------------------------------------------------
	
	
	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}

	
	private boolean checkFormu()
	{
		boolean error = false;
		
		for (EditText field : list)
		{
			if (!MyRegex.checkById(field)) {
		    	field.setText("");
		    	field.setHintTextColor(Color.RED);
		    	//field.setTextColor(Color.RED);
		    	//field.setHint("Please enter a valid " + mail.getHint());
		    	//field.setBackgroundColor(R.color.ErrorBackground);
		    	error = true;
		    }
		}
		if (error)
		{
			//info.setTextColor(Color.RED);
	    	//info.setText("Entrez une valeur correcte");
			Toast.makeText(getApplicationContext(), "Ces valeurs sont incorrectes", Toast.LENGTH_LONG).show();
	    	return false;
		}
		else
			return true;
	}

	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	/*switch (msg.what) {
	        case 0:   //  begin
	            if (mProgressDialog.isShowing()) {
	                mProgressDialog.setMessage(((String) msg.obj));
	                //return;
	            }
	            break;
	        case 1:  //  finish
	        	if (mProgressDialog.isShowing()) {
	                mProgressDialog.dismiss();
	                //return;
	        	}
	        	break;
	        default: // should never happen
	            break;
	    	}*/
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case LOGIN:
			    	if (Error == 1)
			    		Toast.makeText(getApplicationContext(), "Erreur de connection avec le WebService", Toast.LENGTH_LONG).show();
			    	else if (Error == 2)
			    	{
			    		//Toast.makeText(getApplicationContext(), "Erreur : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    		Toast.makeText(getApplicationContext(), "Erreur : Mot de passe ou login incorrecte.", Toast.LENGTH_LONG).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	else
			    	{
			    		user = (User)pack.getSerializable("user");   //  utile ??????
			    		login.setEnabled(false);   		
			    		Network.USER = user;
			    				    		
			    		Intent intent = new Intent(Login.this, Menu2.class);
						startActivity(intent);
						
			    	}
			    	item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    		item_loading.setVisible(false);
			    	break;
			    	
		    	case RESET_PASSWORD:
		    		if (Error == 1)
			    		Toast.makeText(getApplicationContext(), "Erreur de connection avec le WebService", Toast.LENGTH_LONG).show();
			    	else if (Error == 2)
			    	{
			    		Toast.makeText(getApplicationContext(), "Erreur : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	else
			    	{
			    		Toast.makeText(getApplicationContext(), "Vérifier vos emails", Toast.LENGTH_LONG).show();
			    	}
			    	break;
	    	} 	
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.login, menu);
	    item_loading = menu.findItem(R.id.loading_zone);
		item_loading.setVisible(false);

	    return true;
	  }
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	   /* switch (item.getItemId()) {
	    case R.id.logo_menu:
	      logo_menu = item;
	      logo_menu.setActionView(R.layout.progressbar);
	      logo_menu.expandActionView();
	      TestTask task = new TestTask();
	      task.execute("test");
	      break;
	    default:
	      break;
	    }*/
	    return true;
	  }
	 
	 private class TestTask extends AsyncTask<String, Void, String> {

		    @Override
		    protected String doInBackground(String... params) {
		      // Simulate something long running
		      try {
		        Thread.sleep(2000);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		      return null;
		    }

		    @Override
		    protected void onPostExecute(String result) {
		    	item_loading.collapseActionView();
		    	item_loading.setActionView(null);
		    }
		  };
		  
		  
		  
		  /**
		   * Registers the application with GCM servers asynchronously.
		   * <p>
		   * Stores the registration ID and app versionCode in the application's
		   * shared preferences.
		   */
		  @SuppressWarnings({ "rawtypes", "unchecked" })
		private void registerInBackground() {
			  Log.w("PUSH", "asy appeler");
		      new AsyncTask() {
		    	  @Override
		          protected Object doInBackground(Object... params) {
		              String msg = "";
		              try {
		                  if (gcm == null) {
		                      gcm = GoogleCloudMessaging.getInstance(context);
		                  }
		                  regid = gcm.register(SENDER_ID);
		                  msg = "Device registered, registration ID=" + regid;

		                  // You should send the registration ID to your server over HTTP,
		                  // so it can use GCM/HTTP or CCS to send messages to your app.
		                  // The request to your server should be authenticated if your app
		                  // is using accounts.
		                  sendRegistrationIdToBackend();

		                  // For this demo: we don't need to send it because the device
		                  // will send upstream messages to a server that echo back the
		                  // message using the 'from' address in the message.

		                  // Persist the regID - no need to register again.
		                  storeRegistrationId(context, regid);
		              } catch (IOException ex) {
		                  msg = "Error :" + ex.getMessage();
		                  // If there is an error, don't just keep trying to register.
		                  // Require the user to click a button again, or perform
		                  // exponential back-off.
		              }
		              return msg;
		          }

		    	  @Override
		          protected void onPostExecute(Object msg) {
		             // mDisplay.append(msg + "\n");
		        	  Log.w("PUSHonExecute", msg.toString());
		          }

				
		      }.execute(null, null, null);
		      
		  }
		  
		  
		  
		  /**
		   * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
		   * or CCS to send messages to your app. Not needed for this demo since the
		   * device sends upstream messages to a server that echoes back the message
		   * using the 'from' address in the message.
		   */
		  private void sendRegistrationIdToBackend() {
		      // Your implementation here.
			  Log.w("PUSH", "Device registered, registration ID = " + regid);
		  }
		  
		  /**
		   * Stores the registration ID and app versionCode in the application's
		   * {@code SharedPreferences}.
		   *
		   * @param context application's context.
		   * @param regId registration ID
		   */
		  private void storeRegistrationId(Context context, String regId) {
		      final SharedPreferences prefs = getGCMPreferences(context);
		      int appVersion = getAppVersion(context);
		      Log.i(TAG, "Saving regId on app version " + appVersion);
		      SharedPreferences.Editor editor = prefs.edit();
		      editor.putString(PROPERTY_REG_ID, regId);
		      editor.putInt(PROPERTY_APP_VERSION, appVersion);
		      editor.commit();
		  }
		  
		  
		  
		  
		  
}

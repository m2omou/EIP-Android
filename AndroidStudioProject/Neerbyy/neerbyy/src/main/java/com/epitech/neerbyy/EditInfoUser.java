package com.epitech.neerbyy;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.internal.gt;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ipaulpro.afilechooser.utils.FileUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class stock temporary the informations of the user. It is use for debugging case
 *@see User
 */
public class EditInfoUser extends Activity {
	
	Button btnOk;
	Button btnChangePass;
	TextView btnDelete;
	TextView txtUsername;
	EditText username; 
	EditText firstname;
	EditText lastname; 
	EditText mail;
	ImageView avatar;
	String  passAvatar;
	
	Bitmap bitmap;
	
	User user;
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	List<EditText> list;
	
	private MenuItem item_loading;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info_user);
		
		btnOk = (Button)findViewById(R.id.btnInfoUserValid);
		btnChangePass = (Button)findViewById(R.id.btnInfoUserChangePass);
		btnDelete = (TextView)findViewById(R.id.btnInfoUserDelete);
		txtUsername = (TextView)findViewById(R.id.txtUserInfoViewUsername);
		username = (EditText)findViewById(R.id.EditTextInfoUserUsername);
		firstname = (EditText)findViewById(R.id.EditTextInfoUserFirsname);
		lastname = (EditText)findViewById(R.id.EditTextInfoUserLastname);
		mail = (EditText)findViewById(R.id.EditTextInfoUserMail);
		avatar = (ImageView)findViewById(R.id.imgUserInfoAvatar);
		
		list = new ArrayList<EditText>();
		list.add(username);
		list.add(firstname);
		list.add(lastname);
		list.add(mail);
			
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		
		if (Network.USER == null)
		{
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);	
			return;
		}
		
		user = Network.USER;
		
		username.setText(user.username);
		firstname.setText(user.firstname);
		lastname.setText(user.lastname);
		mail.setText(user.mail);
		
		txtUsername.setText(user.username);
		
		btnChangePass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditInfoUser.this, LostPassword.class);
				startActivity(intent);
			}
		});
		
		avatar.setAdjustViewBounds(true);
		avatar.setMaxWidth(100);
		avatar.setMaxHeight(100);
		avatar.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
		 	Intent getContentIntent = FileUtils.createGetContentIntent();
		    Intent intent = Intent.createChooser(getContentIntent, "Choisissez un fichier");
		    startActivityForResult(intent, 1234);  //private static final int REQUEST_CHOOSER = 1234;
			}	
		});
		
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (!checkFormu())
			    	return;
				//mProgressDialog = ProgressDialog.show(EditInfoUser.this, "Please wait",
					//	"Long operation starts...", true);
				
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread thread1 = new Thread(){
			        public void run(){
			        	
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/users/" + user.id + ".json";
		            	//String url = Network.URL + Network.PORT + "/users/21.json";
		            	
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
  	
		            	nameValuePairs.add(new BasicNameValuePair("user[username]", username.getText().toString()));           	
		            	nameValuePairs.add(new BasicNameValuePair("user[firstname]", firstname.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[lastname]", lastname.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[email]", mail.getText().toString()));
		            	//nameValuePairs.add(new BasicNameValuePair("user[password]", password.getText().toString()));
		            	 	
		            	File extStore = Environment.getExternalStorageDirectory();
		            	//File[] imageDirs = extStore.listFiles(filterForImageFolders);
		                    	
		            	//nameValuePairs.add(new BasicNameValuePair("user[avatar]", extStore.getAbsolutePath() + "/ic.png"));
		            	 if (passAvatar != null)
		            		 nameValuePairs.add(new BasicNameValuePair("user[avatar]", passAvatar));

		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.EDIT_USER.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input;
				        if (passAvatar != null)
				        	input = Network.retrieveStream(url, METHOD.UPLOAD_PUT, nameValuePairs);
				        else
				        	input = Network.retrieveStream(url, METHOD.PUT, nameValuePairs);
				        
						if (input == null)
						{
							messageBundle.putInt("error", 1);
						}
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
								Network.USER = user;	
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
		
		
	
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditInfoUser.this);
		 
					// set title
					alertDialogBuilder.setTitle("Voulez vous vraiment supprimer votre compte ?");
		 
					// set dialog message
					alertDialogBuilder
						.setMessage("Cette action est irréversible !!")
						.setCancelable(false)
						.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
					
								//mProgressDialog = ProgressDialog.show(EditInfoUser.this, "Please wait",
									//	"Long operation starts...", true);
								item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
								item_loading.setVisible(true);
								
								Thread thread1 = new Thread(){
							        public void run(){
							        	
									try {	
						            	Gson gson = new Gson();
						            	String url = Network.URL + Network.PORT + "/users/" + user.id + ".json";
				     	
						            	Message myMessage, msgPb;
						            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
						                myHandler.sendMessage(msgPb);
						                
										Bundle messageBundle = new Bundle();
										messageBundle.putInt("action", ACTION.DELETE_USER.getValue());
								        myMessage = myHandler.obtainMessage();	
				   		        
								        InputStream input = Network.retrieveStream(url, METHOD.DELETE, null);
								        
										if (input == null)
										{
											messageBundle.putInt("error", 1);
										}
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
											//else		  	                   
												//Network.USER = user;	
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
						  })
						.setNegativeButton("Non",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
		 
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
			}
				
		});
		
		//mProgressDialog = ProgressDialog.show(EditInfoUser.this, "Please wait",
			//	"Long operation starts...", true);
		new ThreadDownloadImage(EditInfoUser.this).start();
	}
	
	private boolean checkFormu()
	{
		boolean error = false;
		
		for (EditText field : list)
		{
			if (!MyRegex.check(field)) {
				if (field.getId() != R.id.EditTextInfoUserFirsname && field.getId() != R.id.EditTextInfoUserLastname) {
					field.setText("");
					field.setHint("Valeur incorrecte");
					field.setHintTextColor(Color.RED);
					//field.setTextColor(Color.RED);
					//field.setHint("Please enter a valid " + mail.getHint());
					//field.setBackgroundColor(R.color.ErrorBackground);
					error = true;
				}
				else {
					if (field.getText().toString() == null || field.getText().toString() == "") {
						error = false;
					}
				}
		    }
		}
		if (error)
		{
			Toast.makeText(getApplicationContext(), "Svp entrez des valeurs corrects", Toast.LENGTH_SHORT).show();
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
	    /*	switch (msg.what) {
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
		    	case EDIT_USER:    		
		    		if (Error == 1)
			    		Toast.makeText(getApplicationContext(), "Erreur de connection avec le WebService", Toast.LENGTH_LONG).show();
			    	else if (Error == 2)
			    	{
			    		Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour de vos informations: " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	else
			    	{
			    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    		//Toast.makeText(getApplicationContext(), "Update account success", Toast.LENGTH_SHORT).show();
			    		Intent intent = new Intent(EditInfoUser.this, Menu2.class);
						startActivity(intent);	    		
			    	}
			    break;
		    	case UPDATE_IMG_INFO_USER:
		    		
		    		avatar.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(bitmap, avatar.getHeight()));
		    		avatar.setAdjustViewBounds(true);
		    		avatar.setMaxWidth(100);
		    		avatar.setMaxHeight(100);
		    		//Toast.makeText(getApplicationContext(), "Update avatar success", Toast.LENGTH_SHORT).show();
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    		item_loading.setVisible(false);
		    	break;
		    	
		    	case DELETE_USER:
		    		if (Error == 1)
			    		Toast.makeText(getApplicationContext(), "Erreur de connection avec le WebService", Toast.LENGTH_LONG).show();
			    	else if (Error == 2)
			    	{
			    		Toast.makeText(getApplicationContext(), "Erreur, votre compte n'a pas pu être supprimé" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	else
			    	{
			    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    		
			    		Network.USER = null;
			    		//Toast.makeText(getApplicationContext(), "Delete account success", Toast.LENGTH_SHORT).show();
			    		Intent intent = new Intent(EditInfoUser.this, Login.class);
			    		startActivity(intent);
			    	}
		    	break;
	    	}
	    }
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case 1234:     //private static final int REQUEST_CHOOSER = 1234;
	            if (resultCode == RESULT_OK) {

	                final Uri uri = data.getData();

	                // Get the File path from the Uri
	                String path = FileUtils.getPath(this, uri);
	                passAvatar = path;
	                // Alternatively, use FileUtils.getFile(Context, Uri)
	                if (path != null && FileUtils.isLocal(path)) {
	                    File file = new File(path);
	                }
	            }
	            break;
	    }
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_info_user, menu);
	    item_loading = menu.findItem(R.id.loading_zone);
		item_loading.setVisible(false);
		
		//item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//item_loading.setVisible(true);
		
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.logo_menu:
	    	item_loading = item;
	    	//item_loading.setActionView(R.layout.progressbar);
	    	//item_loading.expandActionView();
	    	//TestTask task = new TestTask();
	    	//task.execute("test");
	    	
	    	Intent intent;
	    	if (Network.USER == null)
	    		intent = new Intent(EditInfoUser.this, Login.class);
	    	else
	    		intent = new Intent(EditInfoUser.this, Menu2.class);
	    	startActivity(intent);
			break;
	    default:
	    	break;
	    }
	    return true;
	}
}

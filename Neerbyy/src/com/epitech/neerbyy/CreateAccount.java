package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.epitech.neerbyy.Network.METHOD;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
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
 * This class represent the view for create a new user account.
 * Each field must be check for error with the MyRegex class
 *@see User
 *@see MyRegex
 */

public class CreateAccount extends MainMenu {
	
	Button btnOk;
	EditText username; 
	
	//EditText firstname;
	//EditText lastname; 
	//EditText avatar;
	
	EditText mail;
	EditText confirmMail;
	EditText password;
	EditText confirmPassword;
	//TextView info;
	List<EditText> list;
	
	ProgressDialog mProgressDialog;
	
	private MenuItem item_loading;
	
	ResponseWS rep;
	
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account2);
		
		btnOk = (Button)findViewById(R.id.btnGoCreateAccount2);		
		username = (EditText)findViewById(R.id.txtCreateUsername2);
		
	//	firstname = (EditText)findViewById(R.id.txtCreateFirstname);
	//	lastname = (EditText)findViewById(R.id.txtCreateLastname);
	//	avatar= (EditText)findViewById(R.id.txtUsername);
		
		mail = (EditText)findViewById(R.id.txtCreateMail2);
		confirmMail = (EditText)findViewById(R.id.txtCreateConfirmMail2);
		password = (EditText)findViewById(R.id.txtCreatePassword2);
		confirmPassword = (EditText)findViewById(R.id.txtCreateConfirmPassword2);
		//info = (TextView)findViewById(R.id.txtCreateAccountInfo2);
		
		list = new ArrayList<EditText>();
		list.add(username);
		list.add(mail);
		list.add(confirmMail);
		list.add(password);
		list.add(confirmPassword);
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
			    if (!checkFormu())
			    	return;
				
			    //mProgressDialog = ProgressDialog.show(CreateAccount.this, "Please wait", "Long operation starts...", true);
				
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
			    
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {		
						Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/users.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            		            	
		            	nameValuePairs.add(new BasicNameValuePair("user[email]", mail.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[password]", password.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[username]", username.getText().toString()));           	
		            	
		            	//nameValuePairs.add(new BasicNameValuePair("user[firstname]", firstname.getText().toString()));
		            	//nameValuePairs.add(new BasicNameValuePair("user[lastname]", lastname.getText().toString()));    	
		            
		            	
		            	//Bitmap image = = BitmapFactory.
		            	
		            	//nameValuePairs.add(new BasicNameValuePair("user[avatar]", ));

		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Try Connecting");	 
		                myHandler.sendMessage(msgPb);
		                 	
						Bundle messageBundle = new Bundle();
				        myMessage=myHandler.obtainMessage();					        
				        messageBundle.putInt("action", Network.ACTION.CREATE_ACCOUNT.getValue());	
				        
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
								//user = gson.fromJson(readerResp, User.class);
								//user = gson.fromJson(ret, User.class);
									
								//Log.d("RET", "RET = " + ret);
								rep = gson.fromJson(ret, ResponseWS.class);
								//Log.d("REP", "REP = " + rep.responseMessage + " ::: " + rep.result.toString());
								user = rep.getValue(User.class);
								}
								catch(JsonParseException e)
							    {
							        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
							        
							        msgPb = myHandler.obtainMessage(1, (Object) "Echec");
					                myHandler.sendMessage(msgPb);
							        return;
							    }
								if (user == null)
								{
									messageBundle.putInt("error", 2);
									String err = "";
									
									err = rep.responseMessage;
									/*
									for (String s : user.errors) {
									    err += s;
									    err += "/n";
									}*/
									messageBundle.putString("msgError", err);
								}
								else		  	                   
									messageBundle.putSerializable("user", (Serializable) user);	
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                myHandler.sendMessage(msgPb);
						}
	                }
					catch (Exception e) {
		                e.printStackTrace();}
				}};
			thread1.start();				
			}
		});	
	}

	private boolean checkFormu()
	{
		boolean error = false;
		
		for (EditText field : list)
		{
			if (!MyRegex.check(field)) {
		    	field.setText("");
		    	field.setHintTextColor(Color.RED);
		    	//field.setTextColor(Color.RED);
		    	//field.setHint("Please enter a valid " + mail.getHint());
		    	//field.setBackgroundColor(R.color.ErrorBackground);
		    	error = true;
		    }
		}
		if (!MyRegex.checkIfIdent(mail, confirmMail))
		{
	    	mail.setHintTextColor(Color.RED);
	    	confirmMail.setHintTextColor(Color.RED);
	    	//info.setTextColor(Color.RED);
	    	//info.setText("Les adresses emails ne correspondent pas");
    		Toast.makeText(getApplicationContext(), "Les adresses mails ne correspondent pas", Toast.LENGTH_SHORT).show();

	    	return false;
		}
		if (!MyRegex.checkIfIdent(password, confirmPassword))
		{
			password.setHintTextColor(Color.RED);
	    	confirmPassword.setHintTextColor(Color.RED);
	    	//info.setTextColor(Color.RED);
	    	//info.setText("Les mot de passe ne correspondent pas");
    		Toast.makeText(getApplicationContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();

	    	return false;
		}
		if (error)
		{
			//info.setTextColor(Color.RED);
	    	//info.setText("Please enter valid values");
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
	        default: // should never happen
	            break;
	    	}*/
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case CREATE_ACCOUNT:    		
		    		if (Error == 1)
			    		Toast.makeText(getApplicationContext(), "Erreur de connection avec le WebService", Toast.LENGTH_LONG).show();
			    	else if (Error == 2)
			    	{
			    		Toast.makeText(getApplicationContext(), "Erreur lors de la création du compte :" + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService : " + pack.getString("msgError"), Toast.LENGTH_LONG).show();
			    	else
			    	{
			    		//info.setText("Creating account success");
			    		User user = (User)pack.getSerializable("user"); 
			    		Toast.makeText(getApplicationContext(), "Votre compte a bien été crée ;)", Toast.LENGTH_SHORT).show();
			    		Network.USER = user;
			    		Intent intent = new Intent(CreateAccount.this, Login.class);
						startActivity(intent);
			    	}
	    	} 	
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.create_account, menu);
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
	    		intent = new Intent(CreateAccount.this, Login.class);
	    	else
	    		intent = new Intent(CreateAccount.this, Menu2.class);
	    	startActivity(intent);
			break;
	    default:
	    	break;
	    }
	    return true;
	}
}

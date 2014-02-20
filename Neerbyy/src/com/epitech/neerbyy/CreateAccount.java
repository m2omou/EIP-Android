package com.epitech.neerbyy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccount extends Activity {
	
	Button btnOk;
	EditText username; 
	
	//EditText firstname;
	//EditText lastname; 
	//EditText avatar;
	
	EditText mail;
	EditText confirmMail;
	EditText password;
	EditText confirmPassword;
	TextView info;
	List<EditText> list;
	
	ProgressDialog mProgressDialog;
	
	ResponseWS rep;
	
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		btnOk = (Button)findViewById(R.id.btnValidInscr);		
		username = (EditText)findViewById(R.id.txtCreateUsername);
		
	//	firstname = (EditText)findViewById(R.id.txtCreateFirstname);
	//	lastname = (EditText)findViewById(R.id.txtCreateLastname);
	//	avatar= (EditText)findViewById(R.id.txtUsername);
		
		mail = (EditText)findViewById(R.id.txtCreateMail);
		confirmMail = (EditText)findViewById(R.id.txtCreateConfirmMail);
		password = (EditText)findViewById(R.id.txtCreatePassword);
		confirmPassword = (EditText)findViewById(R.id.txtCreateConfirmPassword);
		info = (TextView)findViewById(R.id.txtCreateAccountInfo);
		
		list = new ArrayList<EditText>();
		list.add(username);
		list.add(mail);
		list.add(confirmMail);
		list.add(password);
		list.add(confirmPassword);
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
			    if (!checkFormu())
			    	return;
				
			    mProgressDialog = ProgressDialog.show(CreateAccount.this, "Please wait", "Long operation starts...", true);
				
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
		            	//nameValuePairs.add(new BasicNameValuePair("user[avatar]", "default.jpg"));

		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Try Connecting");	 
		                myHandler.sendMessage(msgPb);
		                 	
						Bundle messageBundle = new Bundle();
				        myMessage=myHandler.obtainMessage();					        
				        messageBundle.putInt("action", Network.CREATE_ACCOUNT);	
				        
				        InputStream input = Network.retrieveStream(url, 1, nameValuePairs);
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
								rep = gson.fromJson(ret, ResponseWS.class);
								user = rep.getValue(User.class, 1);
								}
								catch(JsonParseException e)
							    {
							        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
							    }
								if (user.errors != null)
								{
									messageBundle.putInt("error", 2);
									String err = "";
									for (String s : user.errors) {
									    err += s;
									    err += "/n";
									}
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
	    	info.setTextColor(Color.RED);
	    	info.setText("The mail/confirm mail doesn't match");
	    	return false;
		}
		if (!MyRegex.checkIfIdent(password, confirmPassword))
		{
			password.setHintTextColor(Color.RED);
	    	confirmPassword.setHintTextColor(Color.RED);
	    	info.setTextColor(Color.RED);
	    	info.setText("The password/confirm password doesn't match");
	    	return false;
		}
		if (error)
		{
			info.setTextColor(Color.RED);
	    	info.setText("Please enter valid values");
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
	    	switch (msg.what) {
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
	    	}
	    	
	    	Bundle pack = msg.getData();
	    	switch (pack.getInt("action"))
	    	{
		    	case Network.CREATE_ACCOUNT:    		
		    		info.setText("");
		    		User user = (User)pack.getSerializable("user");   	
			    	int Error = pack.getInt("error");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 3)
			    		info.setText("Login error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		//info.setText("Creating account success");
			    		msg.obj = user;
			    		Toast.makeText(getApplicationContext(), "Creating account success", Toast.LENGTH_SHORT).show();
			    		Network.USER = user;
			    		Intent intent = new Intent(CreateAccount.this, Login.class);
						startActivity(intent);
			    	}
	    	} 	
	    }
	};
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.layout.menu, menu);
		menu.getItem(2).getSubMenu().setHeaderIcon(R.drawable.ic_launcher);
		return super.onCreateOptionsMenu(menu);
	}
	
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
        }
        return super.onOptionsItemSelected(item);
    }

}

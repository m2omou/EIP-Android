package com.epitech.neerbyy;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.epitech.neerbyy.Network;

public class Login extends Activity {

	Button createAccount;
	Button login;
	EditText loginMail; 
	EditText password; 
	TextView info;
	TextView lostPassword;
	
	EditText loginLostPassword;
	Button btnLostPassword;
	User user;
	
	ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		createAccount = (Button)findViewById(R.id.btnGoCreateAccount);
		login = (Button)findViewById(R.id.btnLogin);
		loginMail = (EditText)findViewById(R.id.txtLoginMail);
		password = (EditText)findViewById(R.id.txtPassword);
		
		lostPassword = (TextView)findViewById(R.id.txtLostPassword);		
		loginLostPassword = (EditText)findViewById(R.id.txtLoginLostPasswordMail);
		btnLostPassword = (Button)findViewById(R.id.btnLoginLostPasswordMail);
		
		//loginLostPassword.setEnabled(false);	
		//btnLostPassword.setEnabled(false);
		loginLostPassword.setVisibility(View.INVISIBLE);
		btnLostPassword.setVisibility(View.INVISIBLE);
		
		lostPassword.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				//loginLostPassword.setEnabled(true);
				//btnLostPassword.setEnabled(true);
				loginLostPassword.setVisibility(View.VISIBLE);
				btnLostPassword.setVisibility(View.VISIBLE);
			}
		});

		createAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this, CreateAccount.class);
				startActivity(intent);
			}
		});
		
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mProgressDialog = ProgressDialog.show(Login.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson(); // http://neerbyy.com/webservice-login
		            	String url = Network.URL + Network.PORT + "/sessions.json?email=" + loginMail.getText() + "&password=" + password.getText();
		            	//Log.w("Error", "jenvoie  :::::::::::::::::::::::::::::: " + url);
		            	
		            	 Message myMessage, msgPb;
		            	 msgPb = myHandler.obtainMessage(0, (Object) "Try Connecting");	 
		                 myHandler.sendMessage(msgPb);
		              	
						InputStream input = Network.retrieveStream(url, 1, null);
						
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", Network.LOGIN);
				        myMessage = myHandler.obtainMessage();	
   		        
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
							    ResponseWS rep;
								//user = gson.fromJson(ret, User.class);
								rep = gson.fromJson(ret, ResponseWS.class);
								System.out.println("jai recup " + rep.result );
								return;
								
								}
								catch(JsonParseException e)
							    {
							        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
							    }
								
								messageBundle.putSerializable("user", (Serializable) user);
							
								if (user.error == 1)
									messageBundle.putInt("error", 2);
								else		  	                   
				                    messageBundle.putInt("error", 0);	
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
	}

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
		    	case Network.LOGIN:    		
		    		info = (TextView)findViewById(R.id.txtLoginInfo);
		    		info.setText("");	
			    	int Error = pack.getInt("error");
			    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		user = (User)pack.getSerializable("user");
			    		info.setText("Login error :\n" + user.errorMsg);
			    	}
			    	else if (Error == 3)
			    		info.setText("Login error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		user = (User)pack.getSerializable("user");
			    		info.setText("Login success with id : " + user.id + " and token : " + user.token);
			    		login.setEnabled(false);
			    		msg.obj = user;
			    		
			    		Network.USER = user;
			    	}
	    	} 	
	    }
	};
}

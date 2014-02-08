package com.epitech.neerbyy;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GetUserById extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_user_by_id);
				
		Button b = (Button)findViewById(R.id.btnGetLogin);
		final EditText login = (EditText)findViewById(R.id.txtLogin);
		
		if (Network.USER != null)
			login.setText(String.valueOf(Network.USER.id));
		
		b.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {		
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/users.json?user_id=" + login.getText();
						InputStream input = Network.retrieveStream(url, 0, null);
						
						Bundle messageBundle = new Bundle();
				        Message myMessage;
				        myMessage=myHandler.obtainMessage();	
				        
				        messageBundle.putInt("action", Network.GET_USER);			        
						if (input == null)
							messageBundle.putInt("error", 1);
						else
						{
							Reader readerResp = new InputStreamReader(input);
							
							Network.checkInputStream(readerResp);
							
							User user = gson.fromJson(readerResp, User.class);
					        messageBundle.putSerializable("user", (Serializable) user);	                   
		                    messageBundle.putInt("error", 0);       
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);	
	                }
					catch (Exception e) {
		                e.printStackTrace();}
				}};
			thread1.start();
	}});}
			
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
	    	Bundle pack = msg.getData();
	    	switch (pack.getInt("action"))
	    	{
		    	case Network.GET_USER:
		    		TextView error = (TextView)findViewById(R.id.txtInfoError);   	    	
			    	TextView username = (TextView)findViewById(R.id.txtInfoUsername);
			    	TextView firstname = (TextView)findViewById(R.id.txtInfoFirstname);
			    	TextView lastname = (TextView)findViewById(R.id.txtInfoLastname);
			    	TextView mail = (TextView)findViewById(R.id.txtInfoMail);	
			    	User user = (User)pack.getSerializable("user");
			    	int Error = pack.getInt("error");
			    	if (Error == 1)
			    	{
			    		error.setText("Error: user unknown");
				    	username.setText("");
					    firstname.setText("");
					    lastname.setText("");
					    mail.setText("");		
			    	}
			    	else
			    	{
				    	//Log.w(getClass().getSimpleName(), "On a " + user.username);	    	
				    	error.setText("");
					    username.setText(user.username);
					    firstname.setText(user.firstname);
					    lastname.setText(user.lastname);
					    mail.setText(user.mail);
					    msg.obj = user;
			    	}
	    	} 	
	    }
	};
}

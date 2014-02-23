package com.epitech.neerbyy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import com.google.gson.Gson;

public class GetUserById extends MainMenu {
	
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

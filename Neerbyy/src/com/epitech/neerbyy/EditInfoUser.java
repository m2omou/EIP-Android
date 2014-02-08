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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditInfoUser extends Activity {
	
	Button btnOk;
	TextView info;
	EditText username; 
	EditText firstname;
	EditText lastname; 
	EditText mail;
	EditText password;
	User user;
	//EditText avatar; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info_user);
		
		btnOk = (Button)findViewById(R.id.btnEditOk);
		username = (EditText)findViewById(R.id.txtEditUsername);
		firstname = (EditText)findViewById(R.id.txtEditFirstname);
		lastname = (EditText)findViewById(R.id.txtEditLastname);
		mail = (EditText)findViewById(R.id.txtEditMail);
		password = (EditText)findViewById(R.id.txtEditPassword);
		
		Bundle b  = this.getIntent().getExtras();		
		if (b == null)
		{
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);	
			return;
		}
		
		user = (User)b.getSerializable("user");
		
		username.setText(user.username);
		firstname.setText(user.firstname);
		lastname.setText(user.lastname);
		mail.setText(user.mail);
		password.setText(user.password);

		
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/users/" + user.id + ".json";
		            	Log.w("Error", "jenvoie  :::::::::::::::::::::::::::::: " + url);
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
  	
		            	nameValuePairs.add(new BasicNameValuePair("user[username]", username.getText().toString()));           	
		            	nameValuePairs.add(new BasicNameValuePair("user[firstname]", firstname.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[lastname]", lastname.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[email]", mail.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[password]", password.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("user[avatar]", "dorothee.jpg"));
		            	
						InputStream input = Network.retrieveStream(url, 2, nameValuePairs);
						
						Bundle messageBundle = new Bundle();
				        Message myMessage;
				        myMessage=myHandler.obtainMessage();	
				        
				        messageBundle.putInt("action", Network.EDIT_USER);			        
						if (input == null)  //  diff  null ans error WS
						{
							messageBundle.putInt("error", 1);
							//Toast.makeText(getApplicationContext(), "Modified account success", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Reader readerResp = new InputStreamReader(input);				
							User user = gson.fromJson(readerResp, User.class);
							messageBundle.putSerializable("user", (Serializable) user);
							if (user.error == 1)
								messageBundle.putInt("error", 2);
							else                   
			                    messageBundle.putInt("error", 0);
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);	
	                }
					catch (Exception e) {
		                e.printStackTrace();}
				}};
			thread1.start();				
			}
		});
	}
	
	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	Bundle pack = msg.getData();
	    	switch (pack.getInt("action"))
	    	{
		    	case Network.EDIT_USER:    		
		    		info = (TextView)findViewById(R.id.txtInfoEditUser);
		    		info.setText("");
		    		user = (User)pack.getSerializable("user");
			    	
			    	int Error = pack.getInt("error");
			    	if (Error == 1)
			    	{
			    		//Toast.makeText(getApplicationContext(), "Modified account success", Toast.LENGTH_SHORT).show();
			    		info.setText("Modified account success");
			    	}
			    	else if (Error == 2)
			    		info.setText("Modify info user error : " + user.errorMsg);
			    	else
			    	{
			    		info.setText("Modify info user success for : " + user.username);
			    		msg.obj = user;	    		
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

package com.epitech.neerbyy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class stock temporary the informations of the user. It is use for debugging case
 *@see User
 */
public class EditInfoUser extends MainMenu {
	
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
		//firstname = (EditText)findViewById(R.id.txtEditFirstname);
		//lastname = (EditText)findViewById(R.id.txtEditLastname);
		mail = (EditText)findViewById(R.id.txtEditMail);
	//	password = (EditText)findViewById(R.id.txtEditPassword);
		
		//Bundle b  = this.getIntent().getExtras();		
		if (Network.USER == null)
		{
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);	
			return;
		}
		
		//user = (User)b.getSerializable("user");
		user = Network.USER;
		
		username.setText(user.username);
		//firstname.setText(user.firstname);
		//lastname.setText(user.lastname);
		mail.setText(user.mail);
//		password.setText(user.password);

		
		/*btnOk.setOnClickListener(new View.OnClickListener() {

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
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);	
	                }
					catch (Exception e) {
		                e.printStackTrace();}
				}};
			thread1.start();				
			}
		});*/
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
			    	else
			    	{
			    		info.setText("Modify info user success for : " + user.username);
			    		msg.obj = user;	    		
			    	}
	    	} 	
	    }
	};
}

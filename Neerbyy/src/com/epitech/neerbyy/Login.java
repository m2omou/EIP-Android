package com.epitech.neerbyy;

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
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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
public class Login extends MainMenu {

	Button createAccount;
	Button login;
	EditText loginMail; 
	EditText password; 
	TextView info;
	TextView lostPassword;
	
	Button btnLostPassword;
	User user;
	
	List<EditText> list;
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login2);
		
		createAccount = (Button)findViewById(R.id.btnGoCreateAccount2);
		login = (Button)findViewById(R.id.btnLogin2);
		loginMail = (EditText)findViewById(R.id.txtLoginMail2);
		password = (EditText)findViewById(R.id.txtLoginPassword2);
		

		btnLostPassword = (Button)findViewById(R.id.btnLoginLostPasswordMail2);
		info = (TextView)findViewById(R.id.txtLoginInfo2);
		
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
				
				if (!checkFormu())
			    	return;
				
				mProgressDialog = ProgressDialog.show(Login.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/sessions.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("connection[email]", loginMail.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("connection[password]", password.getText().toString()));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
		                Log.w("PATH", "ICI1");
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.LOGIN.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.POST, nameValuePairs);
						if (input == null)
							messageBundle.putInt("error", 1);
						else
						{	
							Log.w("PATH", "ICI2");
							Reader readerResp = new InputStreamReader(input);
							String ret = Network.checkInputStream(readerResp);
							Log.w("PATH", "ICI3");
							
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
	        	break;
	        default: // should never happen
	            break;
	    	}
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case LOGIN:
		    		info.setText("");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Login error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		user = (User)pack.getSerializable("user");   //  utile ??????
			    		info.setText("Login success with : " + user.username);
			    		login.setEnabled(false);   		
			    		Network.USER = user;
			    	}
			    	break;
		    	case RESET_PASSWORD:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Reset password error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		Toast.makeText(getApplicationContext(), "Please check your email", Toast.LENGTH_LONG).show();
			    	}
			    	break;
	    	} 	
	    }
	};
}

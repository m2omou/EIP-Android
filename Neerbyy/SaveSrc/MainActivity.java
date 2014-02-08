package com.epitech.neerbyy;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class MainActivity extends Activity {
private Network net;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		net = new Network();
		
		Button b = (Button)findViewById(R.id.btnGetLogin);
		final EditText login = (EditText)findViewById(R.id.txtLogin);
		final EditText result = (EditText)findViewById(R.id.txtInfo);
		
		/*b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Thread thread1 = new Thread(){
			        public void run(){
			        	
				runOnUiThread(new Runnable() {@Override public void run()
				 {	        
					try {
		            	Gson gson = new Gson();
						InputStream input = net.retrieveStream("http://eip.callumoz.com:3000/users/13.json");
						Reader readerResp = new InputStreamReader(input);
						
						//Toast.makeText(input., text, duration)
						
						char[] buf = new char[512];
						 User user = gson.fromJson(readerResp, User.class);
						readerResp.read(buf);
						//Log.w(getClass().getSimpleName(), "Fini avec : " + buf);
						result.setText(user.username);	            
		            } catch (Exception e) {
		                e.printStackTrace();}	
				        
				       
				 }});
				}};
			        thread1.start();
			        
				/*final User user = null;
				 Thread thread1 = new Thread(){
				        public void run(){
				            try {
				            	Gson gson = new Gson();
								InputStream input = net.retrieveStream("http://eip.callumoz.com:3000/users/13.json");
								Reader readerResp = new InputStreamReader(input);
								
								//Toast.makeText(input., text, duration)
								
								char[] buf = new char[512];
								 User user = gson.fromJson(readerResp, User.class);
								readerResp.read(buf);
								//Log.w(getClass().getSimpleName(), "Fini avec : " + buf);
								result.setText(user.username);		            
				            } catch (Exception e) {
				                e.printStackTrace();
				            }
				        }
				 };
				 thread1.start();
				 
				 result.setText(user.username);
				    
				//Reader readerResp = new InputStreamReader(input);
				
				//Toast.makeText(input., text, duration)
				
				//User user = gson.fromJson(readerResp, User.class);
				
				//result.setText(user.username);		
			}*/
	}});}
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
		
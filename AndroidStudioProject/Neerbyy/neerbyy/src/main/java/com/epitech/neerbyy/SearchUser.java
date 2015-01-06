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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ipaulpro.afilechooser.utils.FileUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
public class SearchUser extends MainMenu {
	
	Button btnOk;
	EditText username; 
	
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	Users users; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_user);
		
		btnOk = (Button)findViewById(R.id.btnSearchUserOk);
		username = (EditText)findViewById(R.id.editTextSearchUserSearch);
		
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				//mProgressDialog = ProgressDialog.show(SearchUser.this, "Please wait",
					//	"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){
			        	
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/search/users.json?query=" + username.getText().toString();	       

		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.SEARCH_USER.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.GET, null);
				        
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
								users = rep.getValue(Users.class);
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
	
	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	switch (msg.what) {
	        case 0:
	     //       if (mProgressDialog.isShowing())
	        	{
	       //         mProgressDialog.setMessage(((String) msg.obj));
	            }
	            break;
	        case 1:
	        //	if (mProgressDialog.isShowing()) 
	        	{
	          //      mProgressDialog.dismiss();
	        	}
	        	break;
	        default:
	            break;
	    	}
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case SEARCH_USER:    		
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Search user error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		Toast.makeText(getApplicationContext(), "Search user success", Toast.LENGTH_SHORT).show();
			    					    		
			    		if (users.list.length == 0)
			    		{
    			    		Toast.makeText(getApplicationContext(), "list vide !!!!", Toast.LENGTH_SHORT).show();
			    			return;
			    		}
			    		
			    		List<CharSequence> charSequences = new ArrayList<CharSequence>();
			    		for (int i = 0; i < users.list.length; i++) {
			    			String tmp = new String(users.list[i].username + ":\n");
			    			if (users.list[i].firstname != null)
			    				tmp += users.list[i].firstname;
			    			if (users.list[i].lastname != null)
			    				tmp += users.list[i].lastname;
			    			
			    			charSequences.add(tmp);
			    		}
			    		
			    		final CharSequence[] charSequenceArray = charSequences.toArray(new
			    			    CharSequence[charSequences.size()]);
			    		
			    		Toast.makeText(getApplicationContext(),"Charsequence a " + charSequenceArray.length, Toast.LENGTH_SHORT).show();

			    		
						AlertDialog.Builder builder = new AlertDialog.Builder(SearchUser.this);
						builder.setTitle("Qui Cherchez vous ?");
						builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
						          
							@Override
								public void onClick(DialogInterface dialog, int item) {
						                Toast.makeText(getApplicationContext(), charSequenceArray[item], Toast.LENGTH_SHORT).show();
						                Intent intent = new Intent(SearchUser.this, ViewMessages.class);
			    						Bundle b = new Bundle();		    					
			    						//b.putSerializable("conv", (Serializable)listConv.list[position]);
			    						b.putInt("convId", -1);
			    						b.putInt("recipientId", users.list[item].id);
			    						intent.putExtras(b);					
			    						startActivity(intent);
			    						return;
						          	}
						        });
						AlertDialog alert = builder.create();
						alert.show();			
			    }	
			    break;    	
	    	}
	    }
	};
}

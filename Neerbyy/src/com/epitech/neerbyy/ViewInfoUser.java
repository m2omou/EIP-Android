package com.epitech.neerbyy;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.epitech.neerbyy.Conversations.Conversation;
import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.internal.gt;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class stock temporary the informations of the user. It is use for debugging case
 *@see User
 */
public class ViewInfoUser extends MainMenu {
	
	ImageButton btnConv;
	Button btnShowMemory;
	Button btnShowPlace;
	
	TextView username;
	ImageView avatar;
	
	Bitmap bitmap;
	
	User user;
	int userId;
	public Post listPost;
	Thread threadGetPost;
	
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	ListView listView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_info_user);
		
		btnConv = (ImageButton)findViewById(R.id.imgUserViewConv);
		btnShowMemory = (Button)findViewById(R.id.btnViewUserMemory);
		btnShowPlace = (Button)findViewById(R.id.btnViewUserPlace);
		username = (TextView)findViewById(R.id.TxtViewUserUsername);
		avatar = (ImageView)findViewById(R.id.imgUserViewAvatar);
		
		listView = (ListView)findViewById(R.id.listViewUserList);
		//listView.clearChoices();
		
		avatar.setAdjustViewBounds(true);
		avatar.setMaxWidth(100);
		avatar.setMaxHeight(100);
		
		
		Bundle b  = this.getIntent().getExtras();
		userId = b.getInt("userId");
		
	
		btnConv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (Network.USER == null)
				{
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(ViewInfoUser.this, ViewMessages.class);
				Bundle b = new Bundle();		    					
				b.putInt("convId", -1);		 
				b.putInt("recipientId", user.id);
				intent.putExtras(b);	
				startActivity(intent);	
				return;							
			}
		});	
		
		Thread getInfoUser = new Thread(){
	        public void run(){	        	      
			try {	
            	Gson gson = new Gson();
            	String url = Network.URL + Network.PORT + "/users/" + userId + ".json";
            	
            	Message myMessage, msgPb;
            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
                myHandler.sendMessage(msgPb);
                
				Bundle messageBundle = new Bundle();
				messageBundle.putInt("action", ACTION.GET_INFO_USER.getValue());
		        myMessage = myHandler.obtainMessage();	
	        
		        InputStream input = Network.retrieveStream(url, METHOD.GET, null);
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
	
		
		threadGetPost = new Thread(){
	        public void run(){	        	      
	                Log.w("THREAD", "DEBUT THREAD POSTS USER");
	            	try {
	                	Gson gson = new Gson();
	                	String url;
	                	url = Network.URL + Network.PORT + "/publications.json?user_id=" + user.id;        	
	                	
	                	Message myMessage, msgPb;
	                	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
	                	myHandler.sendMessage(msgPb);
	                
	                	Bundle messageBundle = new Bundle();
	        			messageBundle.putInt("action", ACTION.UPDATE_POST.getValue());
	        	        myMessage = myHandler.obtainMessage();	
	               
	        	        InputStream input = Network.retrieveStream(url, METHOD.GET, null);
	                	
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
	        						rep = gson.fromJson(ret, ResponseWS.class);
	        						listPost = rep.getValue(Post.class);
	        						
	        					}
	        					catch(JsonParseException e)
	        				    {
	        				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
	        				    }
	        					if (rep.responseCode == 1)
	        					{
	        						messageBundle.putInt("error", 2);
	        						messageBundle.putString("msgError", rep.responseMessage);
	        					}
	        					else
	        						Log.w("RECUP", "JAI RECUP " + listPost.list.length + " posts");
	        					
	        				}
	        			}
	        	       
	        	        myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
	                    myHandler.sendMessage(msgPb);
	            	}
	            	catch (Exception e) {
	                    e.printStackTrace();}
	            	Log.w("THREAD", "FIN THREAD UPDATE POST USER");
	            }
		};
		
	mProgressDialog = ProgressDialog.show(ViewInfoUser.this, "Please wait",
			"Long operation starts...", true);		
		
	getInfoUser.start();

}
	
			
	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    /*	switch (msg.what) {
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
	    	}*/
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case GET_INFO_USER:    		
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Get info user error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		Toast.makeText(getApplicationContext(), "Get info user success", Toast.LENGTH_SHORT).show();
			    		username.setText(user.username);
			    		new ThreadDownloadImage(ViewInfoUser.this).start();
			    		
			    		//threadGetPost.start();  bug

			    		
			    	}
			    break;
		    	case UPDATE_IMG_INFO_USER:
		    		
		    		avatar.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(bitmap, bitmap.getHeight() * bitmap.getWidth()));
		    		avatar.setAdjustViewBounds(true);
		    		avatar.setMaxWidth(100);
		    		avatar.setMaxHeight(100);
		    		Toast.makeText(getApplicationContext(), "Update avatar success", Toast.LENGTH_SHORT).show();
		    	break;
		    	case UPDATE_AVATAR:
		    		
		    		mSchedule = new SimpleAdapter (ViewInfoUser.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		//ImageView dd = (ImageView)findViewById(R.id.avatar);
			    	//dd.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(dd.getDrawingCache(), 100));
		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);
		    	    
		    	    
		    		break;
		    	case UPDATE_POST:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Update post user error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else {
		    			Toast.makeText(getApplicationContext(), "Update post user success", Toast.LENGTH_SHORT).show();
			    	
			    	
		    			//Création de la ArrayList qui nous permettra de remplir la listView
			            listItem = new ArrayList<HashMap<String, Object>>();
			            listView.removeAllViewsInLayout();
			            //On déclare la HashMap qui contiendra les informations pour un item
			            
			    		
			    		String[] listStrings = new String[listPost.list.length] ;
			    		if (listPost.list.length > 0)
			    		{
			    			for (int i = 0; i < listPost.list.length; i++) {
			    				listStrings[i] = listPost.list[i].content;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				new ThreadDownloadImage(ViewInfoUser.this, i, listView, listPost, listItem, map).start();
			    			}
			    			
			    			mSchedule = new SimpleAdapter (ViewInfoUser.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);
				    	    
			    			
			    	        listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    				     
			    			    	Intent intent = new Intent(ViewInfoUser.this, ViewMemory.class);
			    					Bundle b = new Bundle();		    					
			    					b.putSerializable("post", (Serializable)listPost.list[position]);
			    					b.putString("Place_id", listPost.list[position].place.id);
			    			    	Log.w("LIKE", "dislike = " + listPost.list[position].downvotes);
			    						
			    					intent.putExtras(b);					
			    					startActivity(intent);
			    					return;  
			    			    }
			    			});
			    		}    	
			    	}
	    	}
	    }
	};   
}

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
import com.epitech.neerbyy.Place.PlaceInfo;
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
import android.app.ActionBar;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class ViewInfoUser extends Activity {
	
	ImageButton btnConv;
	//Button btnShowMemory;
	//Button btnShowPlace;
	
	TextView username;
	ImageView avatar;
	
	Bitmap bitmap;
	
	User user;
	int userId;
	public Post listPost;
	public Place places;
	
	Thread threadGetPost;
	Thread threadGetFeed;
	
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	private MenuItem item_loading;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	ListView listView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_info_user);
		
		btnConv = (ImageButton)findViewById(R.id.imgUserViewConv);
		//btnShowMemory = (Button)findViewById(R.id.btnViewUserMemory);
		//btnShowPlace = (Button)findViewById(R.id.btnViewUserPlace);
		username = (TextView)findViewById(R.id.txtUserInfoViewUsername);
		avatar = (ImageView)findViewById(R.id.imgUserViewAvatar);
		
		listView = (ListView)findViewById(R.id.listViewUserList);
		//listView.clearChoices();
		
		avatar.setAdjustViewBounds(true);
		avatar.setMaxWidth(100);
		avatar.setMaxHeight(100);
		
		
		Bundle b  = this.getIntent().getExtras();
		userId = b.getInt("userId");
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
	
		btnConv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (Network.USER == null)
				{
					Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(ViewInfoUser.this, ViewMessages.class);
				Bundle b = new Bundle();		    					
				b.putInt("convId", -1);		 
				b.putInt("recipientId", user.id);
                b.putInt("withId", userId);
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
	                Log.w("THREAD", "DEBUT THREAD FOLLOW POST");
	            	try {
	                	Gson gson = new Gson();
	                	String url;
	                	url = Network.URL + Network.PORT + "/followed_places.json?user_id=" + user.id;        	
	                	
	                	Message myMessage, msgPb;
	                	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
	                	myHandler.sendMessage(msgPb);
	                
	                	Bundle messageBundle = new Bundle();
	        			messageBundle.putInt("action", ACTION.GET_FOLLOW.getValue());
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
	        						places = rep.getValue(Place.class);
	        						
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
	        						Log.w("RECUP", "JAI RECUP " + places.list.length + " follow place");       					
	        				}
	        			}
	        	       
	        	        myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
	                    myHandler.sendMessage(msgPb);
	            	}
	            	catch (Exception e) {
	                    e.printStackTrace();}
	            	Log.w("THREAD", "FIN THREAD UPDATE FOLLOW POST");
	            }
		};
		
		threadGetFeed = new Thread(){
	        public void run(){	        	      
	                Log.w("THREAD", "DEBUT THREAD FEED USER");
	            	try {
	            		Gson gson = new Gson();
	                	String url = Network.URL + Network.PORT + "/feed.json";
	                	
	                	Message myMessage, msgPb;
	                	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
	                	myHandler.sendMessage(msgPb);
	                
	                	Bundle messageBundle = new Bundle();
	        			messageBundle.putInt("action", ACTION.GET_FEED.getValue());
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
	        					if (listPost == null)
	        					{
	        						messageBundle.putInt("error", 2);
	        						messageBundle.putString("msgError", rep.responseMessage);
	        					}
	        					else
	        						Log.w("RECUP", "JAI RECUP DES FEED ");
	        					//else		  	                   
	        						//messageBundle.putSerializable("post", (Serializable) vp.listPost);
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
		
	//mProgressDialog = ProgressDialog.show(ViewInfoUser.this, "Please wait",
		//	"Long operation starts...", true);		
		
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
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des informations utilisateur: " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		//Toast.makeText(getApplicationContext(), "Get info user success", Toast.LENGTH_SHORT).show();
			    		username.setText(user.username);
			    		new ThreadDownloadImage(ViewInfoUser.this).start();
			    		
			    		threadGetPost.start();  //  followPlace
			    		//threadGetFeed.start();
			    	}
			    break;
			    
		    	case UPDATE_IMG_INFO_USER:	    		
		    		avatar.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(bitmap, bitmap.getHeight()));
		    		avatar.setAdjustViewBounds(true);
		    		avatar.setMaxWidth(100);
		    		avatar.setMaxHeight(100);
		    		//Toast.makeText(getApplicationContext(), "Update avatar success", Toast.LENGTH_SHORT).show();
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    	item_loading.setVisible(false);
		    		break;
		    	
		    	case UPDATE_AVATAR:
		    		
		    		mSchedule = new SimpleAdapter (ViewInfoUser.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		//ImageView dd = (ImageView)findViewById(R.id.avatar);
			    	//dd.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(dd.getDrawingCache(), 100));
		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);
		    	    
		    	    item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    		item_loading.setVisible(false);
		    		break;
		    		
		    	case GET_FOLLOW:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des abonnements utilisateur: " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
		    			Toast.makeText(getApplicationContext(), "Update post follow", Toast.LENGTH_SHORT).show();
			    	    	
		    			//Création de la ArrayList qui nous permettra de remplir la listView
			            listItem = new ArrayList<HashMap<String, Object>>();
			            listView.removeAllViewsInLayout();
			            //On déclare la HashMap qui contiendra les informations pour un item
			            		    		
			    		String[] listStrings = new String[places.list.length] ;
			    		if (places.list.length > 0)
			    		{
			    			for (int i = 0; i < places.list.length; i++) {
			    				listStrings[i] = places.list[i].name;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				
			    
			    					String us;
			    					String con;
			    					String da;
			    					if (places.list[i].name != null)
			    						us = places.list[i].name;
			    					else
			    						us = "";
			    					
			    					if (places.list[i].address != null){
			    						con = places.list[i].address;
			    						if (places.list[i].city != null)
			    							con += "\n";
			    					}
			    					else
			    						con = "";
			    					
			    					if (places.list[i].city != null)
			    						con += places.list[i].city;
			    				
			    					
			    					if (places.list[i].cp != null)
			    					{
			    						da = places.list[i].cp;
			    						if (places.list[i].country != null)
			    							da += ", ";
			    					}
			    					else
			    						da = "";
			    					
			    					if (places.list[i].country != null)
			    						da += places.list[i].country;
			    					
			    					
			    					
			    			     	map.put("username", us);
			    				    map.put("content", con);
			    				    map.put("date", da);
			    				    map.put("avatar", String.valueOf(R.drawable.avatar_place));
			    				
			    				
			    				//new ThreadDownloadImage(ViewInfoUser.this, i, listView, places, listItem, map).start();
			    			}
			    			
			    			mSchedule = new SimpleAdapter (ViewInfoUser.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);


                            listView.setOnItemClickListener(new OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    Intent intent = new Intent(ViewInfoUser.this, ViewPost.class);
                                    Bundle b = new Bundle();
                                   // PlaceInfo pi = getPlaceFromMarker(m.getId());

                                    if (places.list[position] == null)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error find Place", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    //b.putSerializable("placeInfo", pi);
                                    int idFallowed = 0;
                                  //  if (places.list[position].followed_place_id != 0)
                                    //    idFallowed = places.list[position].followed_place_id;   //  bug a corriger, sinon ont suit toutes les places des gens
                                    b.putString("placeId", places.list[position].id);
                                    b.putString("placeName", places.list[position].name);
                                    b.putDouble("latitude", places.list[position].lat);
                                    b.putDouble("longitude", places.list[position].lon);
                                    b.putInt("isFallowed", idFallowed);
                                    intent.putExtras(b);
                                    startActivity(intent);
                                    return;

                                }
                            });

				    	    
				    	    listView.setBackgroundResource(R.drawable.my_listview);
			    			
			    	       /* listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    				     // ///////////////A REMETTRE//////////////////////////
			    			    	/*Intent intent = new Intent(ViewInfoUser.this, ViewMemory.class);
			    					Bundle b = new Bundle();		    					
			    					b.putSerializable("post", (Serializable)listPost.list[position]);
			    					b.putString("Place_id", listPost.list[position].place.id);
			    			    	Log.w("LIKE", "dislike = " + listPost.list[position].downvotes);
			    						
			    					intent.putExtras(b);					
			    					startActivity(intent);
			    					return;  
			    			    }
			    			});*/
			    		}    	
			    	}
		    		break;
		    		
		    	case GET_FEED:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des publications :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		//listPost = (Post)pack.getSerializable("post");   //  utile ?????? 
			    		//Log.w("PATH", "LAAA");
			    		//List listStrings = new ArrayList<String>() ;//= {"France","Allemagne","Russie"};
			    		String[] listStrings = new String[listPost.list.length] ;//= {"France","Allemagne","Russie"};
			    		
			    		
		
			            listItem = new ArrayList<HashMap<String, Object>>();
			            listView.removeAllViewsInLayout();
			         		    		
			    		if (listPost.list.length > 0)
			    		{
			    			Log.d("FEED", "YA DEJA DES FEED !!");
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
			    			    	
			    			    	/*Intent intent = new Intent(ViewFeed.this, ViewPost.class); au cas ou aller vers publication dune place
			    					Bundle b = new Bundle();
			    					PostInfos pi = listPost.list[position];
			    					//b.putSerializable("placeInfo", pi);
			    					int idFallowed = 0;
			    					pi.
			    					if (pi.followed_place_id != 0)
			    						idFallowed = pi.followed_place_id;
			    					b.putString("placeId", pi.id);
			    					b.putString("placeName", pi.name);
			    					b.putDouble("latitude", pi.lat);
			    					b.putDouble("longitude", pi.lon);
			    					b.putInt("isFallowed", idFallowed);
			    		    		intent.putExtras(b);					
			    					startActivity(intent);*/		    			    	
	
			    				    Intent intent = new Intent(ViewInfoUser.this, ViewMemory.class);
			    					Bundle b = new Bundle();		    					
			    					b.putSerializable("post", (Serializable)listPost.list[position]);
			    					b.putString("Place_id", "");
			    			    	Log.w("LIKE", "dislike = " + listPost.list[position].downvotes);
			    					
			    					intent.putExtras(b);					
			    					startActivity(intent);
			    					return;
			    			    }
			    			});
			    		
			            Toast.makeText(getApplicationContext(), "Update feed success", Toast.LENGTH_LONG).show();
			    	}		    		
	    	}
		    item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    item_loading.setVisible(false);
		    break;
	    }
	  }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_info_user, menu);
	    item_loading = menu.findItem(R.id.loading_zone);
		item_loading.setVisible(false);
		
		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item_loading.setVisible(true);
		
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.logo_menu:
	    	item_loading = item;
	    	//item_loading.setActionView(R.layout.progressbar);
	    	//item_loading.expandActionView();
	    	//TestTask task = new TestTask();
	    	//task.execute("test");
	    	
	    	Intent intent;
	    	if (Network.USER == null)
	    		intent = new Intent(ViewInfoUser.this, Login.class);
	    	else
	    		intent = new Intent(ViewInfoUser.this, Menu2.class);
	    	startActivity(intent);
			break;
	    default:
	    	break;
	    }
	    return true;
	}
}

package com.epitech.neerbyy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.R.color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;

/**
 * This class represent the view associate to a place, and allow to list all posts of this place.
 * @author Seb
 */
public class ViewPost extends MainMenu {

	private Button btnFallow;
	private TextView sendButton;
	private TextView info;
	private TextView placeName;
	private EditText editPost;
	private ListView listView;
	// Button report;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Post listPost;
	public PlaceInfo place;
	public String placeId;
	public double lat;
	public double lon;
	
	public Bitmap img;
	public int idFallowed;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	
	/*protected void onListIemClick(ListView lv , View v, int position, long id){
		//super.(lv, v, position, id);
	  //   Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
	     Toast.makeText(this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
	}*/
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_post);
	
		btnFallow = (Button)findViewById(R.id.btnFallowPost2);
		sendButton = (TextView)findViewById(R.id.postSendPost);
		info = (TextView)findViewById(R.id.postTextInfo);
		placeName = (TextView)findViewById(R.id.postNamePlace);
		editPost = (EditText)findViewById(R.id.postEditPost);
		listView = (ListView)findViewById(R.id.postViewListPost);
		//report = (Button)findViewById(R.id.btnPostReport);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
	//	place = (PlaceInfo)b.getSerializable("placeInfo");
	
		placeId = b.getString("placeId");
		lat = b.getDouble("latitude");
		lon = b.getDouble("longitude");
	//	placeId = place.id;
		idFallowed = b.getInt("isFallowed");
		placeName.setText(b.getString("placeName"));  
	//	placeName.setText(place.name); 

//		b.getSerializable(key)
		
		/*report.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewPost.this, Report_pub.class);		
				Bundle b = new Bundle();
				b.putInt("pub_id", this.);	
	    		intent.putExtras(b);
				startActivity(intent);		
			}
		});*/

		
		
		View.OnClickListener fallowPlace =  new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadFallow = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/followed_places.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	    	
		            	nameValuePairs.add(new BasicNameValuePair("followed_place[place_id]", placeId));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.FALLOW_PLACE.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.POST, nameValuePairs);
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
			threadFallow.start();
			}
		};
		
		View.OnClickListener unFallowPlace =  new OnClickListener() {	
			@Override
			public void onClick(View v) {
		
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
				mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
						"Long operation starts...", true);
				Thread thread1 = new Thread(){
			        public void run(){
			        	
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/fallowed_places/" + idFallowed + ".json";
     	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.UNFALLOW.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.DELETE, null);
				        
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
							//else		  	                   
								//Network.USER = user;	
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
		};
		
		if (idFallowed == 0)
			btnFallow.setOnClickListener(fallowPlace);
		else
		{
			btnFallow.setOnClickListener(unFallowPlace);
			btnFallow.setText("Ne plus suivre ce lieu");
    		//btnFallow.setBackgroundColor(R.color.orangeNeerbyy);
    		btnFallow.setBackgroundColor(color.holo_orange_dark);	}
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
				mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/publications.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("publication[user_id]", Integer.toString(Network.USER.id)));
		            	nameValuePairs.add(new BasicNameValuePair("publication[place_id]", placeId));
		            	nameValuePairs.add(new BasicNameValuePair("publication[latitude]", Double.toString(lat)));
		            	nameValuePairs.add(new BasicNameValuePair("publication[longitude]", Double.toString(lon)));
		            	
		            	
		            	nameValuePairs.add(new BasicNameValuePair("publication[link]", ""));
		            	
		            	
		            	nameValuePairs.add(new BasicNameValuePair("publication[title]", ""));
		            	nameValuePairs.add(new BasicNameValuePair("publication[content]", editPost.getText().toString()));
		            	nameValuePairs.add(new BasicNameValuePair("publication[file]", null));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.CREATE_POST.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.POST, nameValuePairs);
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
									//user = rep.getValue(Post.class, 1);
								}
								catch(JsonParseException e)
							    {
							        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
							    }
								
								if (rep.responseCode == 1)
								{
									messageBundle.putInt("error", 2);
									messageBundle.putString("msgError", rep.responseMessage);
									Log.w("COORD", "lat: " + lat + " Lon: " + lon);
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
			thread1.start();
			
			}
		});
		
		mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
				"Long operation starts...", true);
		
		new ThreadUpdatePost(ViewPost.this).start();
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
		    	case CREATE_POST:
		    		info.setText("");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Post error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		editPost.setText("");
			    		info.setText("Post send success" );
			    		//mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
			    			//	"Long operation starts...", true);
			    		new ThreadUpdatePost(ViewPost.this).start();
			    	}
			    	break;
		    	case UPDATE_POST:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Update post error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		//Cr�ation de la ArrayList qui nous permettra de remplir la listView
			            listItem = new ArrayList<HashMap<String, Object>>();
			            listView.removeAllViewsInLayout();
			            //On d�clare la HashMap qui contiendra les informations pour un item
			            
			    		
			    		String[] listStrings = new String[listPost.list.length] ;
			    		if (listPost.list.length > 0)
			    		{
			    			Log.d("POST", "YA DEJA DES POSTS !!");
			    			for (int i = 0; i < listPost.list.length; i++) {
			    				listStrings[i] = listPost.list[i].content;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				new ThreadDownloadImage(ViewPost.this, i, listView, listPost, listItem, map).start();
			    			}
			    		 
			    			//Cr�ation d'un SimpleAdapter qui se chargera de mettre les items pr�sents dans notre list (listItem) dans la vue affichageitem
			    	       /* mSchedule = new SimpleAdapter (ViewPost.this, listItem, R.layout.view_item_list,
			    	               new String[] {"avatar", "username", "content"}, new int[] {R.id.avatar, R.id.username, R.id.content});
			    	         
			    	        mSchedule.setViewBinder(new MyViewBinder());
			    	        listView.setAdapter(mSchedule);*/
			    			
			    			
			    			mSchedule = new SimpleAdapter (ViewPost.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);
				    	    
			    			
			    	        listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    				     
			    			    	Intent intent = new Intent(ViewPost.this, ViewMemory.class);
			    					Bundle b = new Bundle();		    					
			    					b.putSerializable("post", (Serializable)listPost.list[position]);
			    					b.putString("Place_id", placeId);
			    			    	Log.w("LIKE", "dislike = " + listPost.list[position].downvotes);
			    						
			    					intent.putExtras(b);					
			    					startActivity(intent);
			    					return;  
			    			    }
			    			});
			    		}
			    		
			    		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
							@Override
							public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
										
								final CharSequence[] items = {"Signaler", "Supprimer"};

								AlertDialog.Builder builder = new AlertDialog.Builder(ViewPost.this);
								builder.setTitle("Que voulez vous faire ?");
								builder.setItems(items, new DialogInterface.OnClickListener() {
								          
										public void onClick(DialogInterface dialog, int item) {
								                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
								               switch (item) {
								               case 0:
								            	   if (Network.USER == null) {
								   					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
								   					//Intent intent = new Intent(ViewPost.this, Login.class);
								   					//startActivity(intent);
								   					return;
								   				}
								            	   	Intent intent = new Intent(ViewPost.this, Report_pub.class);		
													Bundle b = new Bundle();
													b.putInt("com_id", listPost.list[position].id);	
										    		intent.putExtras(b);
													startActivity(intent);	
													break;
								               
								               case 1:
								            	   if (Network.USER == null) {
								   					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
								   					//Intent intent = new Intent(ViewPost.this, Login.class);
								   					//startActivity(intent);
								   					return;
								   				}
								            	   mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
															"Long operation starts...", true);
													Thread thread1 = new Thread(){
												        public void run(){
												        	
														try {	
											            	Gson gson = new Gson();
											            	String url = Network.URL + Network.PORT + "/publications/" + listPost.list[position].id + ".json";
									     	
											            	Message myMessage, msgPb;
											            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
											                myHandler.sendMessage(msgPb);
											                
															Bundle messageBundle = new Bundle();
															messageBundle.putInt("action", ACTION.DELETE_PUB.getValue());
													        myMessage = myHandler.obtainMessage();	
									   		        
													        InputStream input = Network.retrieveStream(url, METHOD.DELETE, null);
													        
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
																}
																catch(JsonParseException e)
															    {
															        System.out.println("Exception n3 in check_exitrestrepWSResponse::"+e.toString());
															    }
																
																if (rep.responseCode == 1 || rep.responseCode == -1)   //  ERREUR DU WS  -1
																{
																	messageBundle.putInt("error", 2);
																	messageBundle.putString("msgError", rep.responseMessage);
																}
																//else		  	                   
																	//Network.USER = user;	
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
	
								            	break;
								               }
								          	}
								        });
								AlertDialog alert = builder.create();
								alert.show();
								return false;							
							}
						});   		
	    				
			    		//new ThreadDownloadImage(listView, listPost, ViewPost.this).start();			    		
			    		Toast.makeText(getApplicationContext(), "Update post success", Toast.LENGTH_LONG).show();
			    	}
			    	break;
			    	
		    	case FALLOW_PLACE:
		    		info.setText("");	
		    		
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Fallow Place error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{		
			    		Toast.makeText(getApplicationContext(), "Fallow Place success", Toast.LENGTH_LONG).show();
			    		btnFallow.setText("Ne plus suivre ce lieu");
			    		btnFallow.setBackgroundColor(R.color.orangeNeerbyy);
			    	}
			    	break;
		    	case UPDATE_AVATAR:
		    		
		    		mSchedule = new SimpleAdapter (ViewPost.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);
		    	    
		    		//Bundle pack2 = msg.getData();
			    	//int pos = pack2.getInt("pos");
			    	
		    		//Toast.makeText(getApplicationContext(), "update img", Toast.LENGTH_SHORT).show();
			    	//view.findViewById(R.id.avatar);
			    	
			    	//image.setImageBitmap(bitmap);
		    		
		    		
		    	        
		    		
		    		/*if (mSchedule != null)
		    		{
		    			listView.removeAllViewsInLayout();
		    			mSchedule.notifyDataSetChanged();
		    		}*/
		    		break;
		    		
		    	case DELETE_PUB:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Delete pub error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{		
			    		Toast.makeText(getApplicationContext(), "Delete Comm success", Toast.LENGTH_LONG).show();
			    		new ThreadUpdatePost(ViewPost.this).start();
			    	}
			    	break;
			    	
		    	case UNFALLOW:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Unfallow place error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		Toast.makeText(getApplicationContext(), "Unfallow place success", Toast.LENGTH_SHORT).show();
			    		btnFallow.setText("Suivre ce lieu");
			    		btnFallow.setBackgroundColor(R.color.greenNeerbyy);
			    	}
		    	break;
	    	} 	
	    }
	};	
}


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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

	private ImageButton btnFallow;
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
	
	/*protected void onListIemClick(ListView lv , View v, int position, long id){
		//super.(lv, v, position, id);
	  //   Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
	     Toast.makeText(this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
	}*/
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_post);
	
		btnFallow = (ImageButton)findViewById(R.id.btnFallowPost);
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
		
		btnFallow.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
		
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
		});
		
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
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
								}
							}
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                   // msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                //myHandler.sendMessage(msgPb);
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
			    		//listPost = (Post)pack.getSerializable("post");   //  utile ?????? 
			    		//Log.w("PATH", "LAAA");
			    		//List listStrings = new ArrayList<String>() ;//= {"France","Allemagne","Russie"};
			    		
			    		
			    		  //Création de la ArrayList qui nous permettra de remplir la listView
			            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			     
			            //On déclare la HashMap qui contiendra les informations pour un item
			            HashMap<String, String> map;
			    		
			    		
			    		String[] listStrings = new String[listPost.list.length] ;//= {"France","Allemagne","Russie"};
			    		if (listPost.list.length > 0)
			    		{
			    			Log.d("POST", "YA DEJA DES POSTS !!");
			    			for (int i = 0; i < listPost.list.length; i++) {
			    				listStrings[i] = listPost.list[i].content;
			    				
			    				 //Création d'une HashMap pour insérer les informations du premier item de notre listView
					            map = new HashMap<String, String>();
					            //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
					            map.put("username", listPost.list[i].user.username + " :");
					            //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
					            map.put("content", listPost.list[i].content);
					            //on insère la référence à l'image (converti en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
					            map.put("avatar", String.valueOf(R.drawable.avatar));
					            //enfin on ajoute cette hashMap dans la arrayList
					            
					            
					            
					            /*if (listPost.list[i].url != null) {
					            	try {
										img = BitmapFactory.decodeStream(new URL(listPost.list[i].url).openStream());
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            	
					            	//final ImageView imgView = (ImageView)findViewById(R.id.img);
					            	//imgView.setImageBitmap(img);
					            	map.put("img", String.valueOf(img));   	
					            }*/
					            
					            listItem.add(map);
			    				
			    			}
			    		 
			    			//Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
			    	        SimpleAdapter mSchedule = new SimpleAdapter (ViewPost.this, listItem, R.layout.view_item_list,
			    	               new String[] {"avatar", "username", "content"}, new int[] {R.id.avatar, R.id.username, R.id.content});
			    	 
			    	        //On attribue à notre listView l'adapter que l'on vient de créer
			    	        listView.setAdapter(mSchedule);
			    	 
			    	        
			    			//listView.setAdapter(new ArrayAdapter<String>(ViewPost.this, android.R.layout.simple_list_item_1, listStrings));	
			    			listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			    	 //Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
			    				     //Toast.makeText(ViewPost.this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
			    				     
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
			    		btnFallow.setImageResource(R.drawable.iconmortel_f);
			    	}
			    	break;
	    	} 	
	    }
	};	
	
}


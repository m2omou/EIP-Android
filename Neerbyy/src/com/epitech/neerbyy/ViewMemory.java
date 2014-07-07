package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class ViewMemory extends MainMenu {

	private TextView sendButton;
	private TextView info;
	private EditText editPost;
	private ListView listView;
	private TextView memoryContent;
	
	private ImageButton btnLike;
	private ImageButton btnDislike;
	private ImageButton btnFallow;
	private TextView viewLike;
	private TextView viewDislike;
	
	private Button report;
	
	private Votes votes;
	public Thread threadGetLike;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Commentary listComm;
	public Post.PostInfos memory;
	
	public String place_id;
	
	public int actionVote;
	
	public Thread threadCancelLike;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_memory);
	
		btnLike = (ImageButton)findViewById(R.id.commBtnLike);
		btnDislike = (ImageButton)findViewById(R.id.commBtnDislike);
		btnFallow = (ImageButton)findViewById(R.id.btnCreateComm);
		
		btnFallow.setEnabled(false);
		
		viewLike = (TextView)findViewById(R.id.commViewLike);                   
		viewDislike = (TextView)findViewById(R.id.commViewDislike);
		
		sendButton = (TextView)findViewById(R.id.postSendCommentary);
		info = (TextView)findViewById(R.id.commTextInfo);
		editPost = (EditText)findViewById(R.id.postEditCommentary);
		listView = (ListView)findViewById(R.id.postViewListCommentary);
		
		report = (Button)findViewById(R.id.btnCommReport);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
		memory = (Post.PostInfos)b.getSerializable("post");
		memoryContent = (TextView)findViewById(R.id.commContentMemory);
		place_id = (String)b.getString("Place_id");  //  inut  deja dans mem.pla
		
		if (memory.vote == null)
			actionVote = -1;
		else if(memory.vote.value) 
			actionVote = 1;
		else
			actionVote = 0;
		//if (Integer.getInteger(viewLike.getText().toString()) == 0 && Integer.getInteger(viewDislike.getText().toString()) == 0) {
			viewLike.setText(Integer.toString(memory.upvotes));
			viewDislike.setText(Integer.toString(memory.downvotes));
		//}
			
		/*report.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ViewMemory.this, Report_pub.class);		
				Bundle b = new Bundle();
				b.putInt("pub_id", memory.id);	
	    		intent.putExtras(b);
				startActivity(intent);		
			}
		});*/
			
		btnFallow.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadFallow = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/followed_places.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	    	
		            	nameValuePairs.add(new BasicNameValuePair("followed_place[place_id]", place_id));
		            	
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
		
		btnLike.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadSendLike = new Thread(){
			        public void run(){	        	      
					try {
						
						if (actionVote == 1) {
							actionVote = -2;
							threadCancelLike.start();
							return;
						}
						actionVote = 1;
						
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/votes.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	    	
		            	nameValuePairs.add(new BasicNameValuePair("vote[user_id]", Integer.toString(Network.USER.id)));
		            	nameValuePairs.add(new BasicNameValuePair("vote[publication_id]", Integer.toString(memory.id)));
		            	nameValuePairs.add(new BasicNameValuePair("vote[value]", "1"));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.SEND_VOTE.getValue());
						messageBundle.putInt("type", 1);  //  for like
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
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                myHandler.sendMessage(msgPb);
	                }
					catch (Exception e) {
		                e.printStackTrace();}
					
				
					
				}};
			threadSendLike.start();
			}
		});
			
		threadCancelLike = new Thread(){
	        public void run(){
	        	if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
			try {	
            	Gson gson = new Gson();
            	String url = Network.URL + Network.PORT + "/votes/" + memory.vote.id + ".json";
            	
            	Message myMessage, msgPb;
            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
                myHandler.sendMessage(msgPb);
		
				Bundle messageBundle = new Bundle();
				messageBundle.putInt("action", ACTION.CANCEL_VOTE.getValue());
		        myMessage = myHandler.obtainMessage();	
	        
		        InputStream input = Network.retrieveStream(url, METHOD.DELETE, null);
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
                
                msgPb = myHandler.obtainMessage(1, (Object) "Success");
                myHandler.sendMessage(msgPb);
            }
			catch (Exception e) {
                e.printStackTrace();}			
		}};
	
		
		btnDislike.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadSendDislike = new Thread(){
			        public void run(){	        	      
					try {	
						
						if (actionVote == 0) {
							actionVote = -3;
							threadCancelLike.start();
							
							return;
						}
						actionVote = 0;
						
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/votes.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	    	
		            	nameValuePairs.add(new BasicNameValuePair("vote[user_id]", Integer.toString(Network.USER.id)));
		            	nameValuePairs.add(new BasicNameValuePair("vote[publication_id]", Integer.toString(memory.id)));
		            	nameValuePairs.add(new BasicNameValuePair("vote[value]", "0"));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.SEND_VOTE.getValue());
						messageBundle.putInt("type", 0);  //  for Dislike
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
	                    
	                   msgPb = myHandler.obtainMessage(1, (Object) "Success");
		               myHandler.sendMessage(msgPb);
	                }
					catch (Exception e) {
		                e.printStackTrace();}
					
					
				}};
			threadSendDislike.start();
			}
		});
		
		
		
	threadGetLike = new Thread(){
	        public void run(){	        	      
			try {	
            	Gson gson = new Gson();
            	String url = Network.URL + Network.PORT + "/votes.json&publication_id=" + memory.id;
            	
            	Message myMessage, msgPb;
            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
            	myHandler.sendMessage(msgPb);
            
            	Bundle messageBundle = new Bundle();
    			messageBundle.putInt("action", ACTION.GET_VOTES.getValue());
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
    						votes = rep.getValue(Votes.class);
    						
    					}
    					catch(JsonParseException e)
    				    {
    				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
    				    }
    					if (votes == null)
    					{
    						messageBundle.putInt("error", 2);
    						messageBundle.putString("msgError", rep.responseMessage);
    					}
    					else
    						Log.w("RECUP", "JAI RECUP DES VOTES ");
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
        	Log.w("THREAD", "FIN THREAD UPDATE COMM");
			
		}};
	//threadGetLike.start();
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Veuillez d'abord vous identifier", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/comments.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("comment[user_id]", Integer.toString(Network.USER.id)));
		            	nameValuePairs.add(new BasicNameValuePair("comment[publication_id]", Integer.toString(memory.id)));
		            	nameValuePairs.add(new BasicNameValuePair("comment[content]", editPost.getText().toString()));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.CREATE_COMM.getValue());
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
	                    
	                    //msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                //myHandler.sendMessage(msgPb);
	                }
					catch (Exception e) {
		                e.printStackTrace();}
					
				}};
			thread1.start();
			}
		});
		
		
		
		/*OnClickListener cc = new OnClickListener() {
			//  IMPLEMENTER POUR LES DEUX 
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		};*/
		
		mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
				"Long operation starts...", true);
		
	new ThreadUpdateComm(ViewMemory.this).start();
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
		    	case CREATE_COMM:
		    		info.setText("");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Comm error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		editPost.setText("");
			    		info.setText("Commentary send success" );
			    		//mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
			    			//	"Long operation starts...", true);
			    		new ThreadUpdateComm(ViewMemory.this).start();
			    	}
			    	break;
		    	case UPDATE_COMM:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Update comm error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		//listPost = (Post)pack.getSerializable("post");   //  utile ?????? 
			    		//Log.w("PATH", "LAAA");
			    		//List listStrings = new ArrayList<String>() ;//= {"France","Allemagne","Russie"};
			    		String[] listStrings = new String[listComm.list.length];
			    		
			    		
			    		//Création de la ArrayList qui nous permettra de remplir la listView
			            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			     
			            //On déclare la HashMap qui contiendra les informations pour un item
			            HashMap<String, String> map;
			    		
			            
			    		//listStrings[0] = memory.content;
			    		memoryContent.setText(memory.content);
			    		
			    		if (listComm.list.length > 0)
			    		{
			    			Log.d("COMM", "YA DEJA DES COMM !!, vec first comment = " + memory.content);
			    			for (int i = 0; i < listComm.list.length; i++) {
			    				listStrings[i] = listComm.list[i].content; 	
			    				
			    				 //Création d'une HashMap pour insérer les informations du premier item de notre listView
					            map = new HashMap<String, String>();
					            //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
					            //map.put("username", listComm.list[i].user.username + " :");
					            map.put("username", "bugbugbug :");
					            //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
					            map.put("content", listComm.list[i].content);
					            //on insère la référence à l'image (converti en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
					            map.put("avatar", String.valueOf(R.drawable.avatar));
					            //enfin on ajoute cette hashMap dans la arrayList
					            listItem.add(map);
			    				
			    			}
			    			
			    			//Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
			    	        SimpleAdapter mSchedule = new SimpleAdapter (ViewMemory.this, listItem, R.layout.view_item_list,
			    	               new String[] {"avatar", "username", "content"}, new int[] {R.id.avatar, R.id.username, R.id.content});
			    	 
			    	        //On attribue à notre listView l'adapter que l'on vient de créer
			    	        listView.setAdapter(mSchedule);
			    	        
			    			//listView.setAdapter(new ArrayAdapter<String>(ViewMemory.this, android.R.layout.simple_list_item_1, listStrings));
			    		}
			    		
			    		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
							@Override
							public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
										
								final CharSequence[] items = {"Signaler", "Supprimer"};

								AlertDialog.Builder builder = new AlertDialog.Builder(ViewMemory.this);
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
								            	   	Intent intent = new Intent(ViewMemory.this, Report_com.class);		
													Bundle b = new Bundle();
													b.putInt("com_id", listComm.list[position].id);	
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
								            	   mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
															"Long operation starts...", true);
													Thread thread1 = new Thread(){
												        public void run(){
												        	
														try {	
											            	Gson gson = new Gson();
											            	String url = Network.URL + Network.PORT + "/comments/" + listComm.list[position].id + ".json";
									     	
											            	Message myMessage, msgPb;
											            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
											                myHandler.sendMessage(msgPb);
											                
															Bundle messageBundle = new Bundle();
															messageBundle.putInt("action", ACTION.DELETE_COMM.getValue());
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
			    		
			           // listView.getAdapter().getView(0, null, listView).setBackgroundColor(getResources().getColor(R.color.greenNeerbyy));	
			    		
			    		/*listView.setOnItemClickListener(new OnItemClickListener() {
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
		    		}*/
			    		
			    		
			    		
			    					    		
			    		Toast.makeText(getApplicationContext(), "Update comm success", Toast.LENGTH_LONG).show();
			    		//threadGetLike.start();
			    	}
			    	break;
			    	
		    	case GET_VOTES:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Get votes error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		viewLike.setText(Integer.toString(memory.upvotes));
			    		viewDislike.setText(Integer.toString(memory.downvotes));	    		
			    	}
			    	break;
			    	
		    	case SEND_VOTE:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Send vote error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{    		
			    		Toast.makeText(getApplicationContext(), "Send vote success", Toast.LENGTH_LONG).show();
			    		if (actionVote == 1)
			    			viewLike.setText(Integer.toString(memory.upvotes + 1));   //  verif si auth
			    		else if(actionVote == 0)
			    			viewDislike.setText(Integer.toString(memory.downvotes + 1));   //  verif si auth
			    		/*else if(actionVote == -2)
			    			viewDislike.setText(Integer.toString(memory.upvotes - 1));
			    		else if(actionVote == -3)
			    			viewDislike.setText(Integer.toString(memory.downvotes - 1));*/
			    		threadGetLike.start();
			    	}
			    	break;
			    	
		    	case CANCEL_VOTE:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Cancel vote error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{   		
			    		Toast.makeText(getApplicationContext(), "Cancel vote success " + actionVote, Toast.LENGTH_LONG).show();
			    		if(actionVote == -2)
			    			viewLike.setText(Integer.toString(memory.upvotes - 1));
			    		else if(actionVote == -3)
			    			viewDislike.setText(Integer.toString(memory.downvotes - 1));
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
		    	case DELETE_COMM:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Error: connection with WS fail", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Delete comm error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Ws error :\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{		
			    		Toast.makeText(getApplicationContext(), "Delete Comm success", Toast.LENGTH_LONG).show();
			    		new ThreadUpdateComm(ViewMemory.this).start();
			    	}
			    	break;
	    	} 	
	    }
	};
}


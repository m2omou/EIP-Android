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
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
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
public class ViewMemory extends Activity {

	//private TextView sendButton;
	//private TextView info;
	//private EditText editPost;
	private ListView listView;
	private TextView memoryContent;
	
	private ImageView btnLike;
	private ImageView btnDislike;
	//private ImageButton btnFallow;
	private ImageView imgMemoryImg;
	private TextView viewLike;
	private TextView viewDislike;
	
	//private Button btnSendComm;
	
	public Votes votes;
	//public Thread threadGetLike;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Commentary listComm;
	public Post.PostInfos memory;
	
	public String place_id;
	
	public int actionVote;
	
	//public Thread threadCancelLike;
	
	public Bitmap imgPlace;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	
	private MenuItem item_loading;
	private MenuItem addComm;
	OnMenuItemClickListener addCommThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_memory2);
	
		btnLike = (ImageView)findViewById(R.id.commBtnLike2);
		btnDislike = (ImageView)findViewById(R.id.commBtnDislike2);
		//btnFallow = (ImageButton)findViewById(R.id.btnCreateComm);
		
		viewLike = (TextView)findViewById(R.id.commViewLike2);                   
		viewDislike = (TextView)findViewById(R.id.commViewDislike2);
		
		imgMemoryImg = (ImageView)findViewById(R.id.imgMemoryImg);

		
	//	sendButton = (TextView)findViewById(R.id.postSendCommentary);
		//info = (TextView)findViewById(R.id.commTextInfo2);
	//	editPost = (EditText)findViewById(R.id.postEditCommentary);
		listView = (ListView)findViewById(R.id.postViewListCommentary2);
		
		//btnSendComm = (Button)findViewById(R.id.btnMemorySendMessage);
		
		memoryContent = (TextView)findViewById(R.id.commContentMemory2);
		//listView.removeAllViews();
		listView.clearChoices();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

		
		Bundle b  = this.getIntent().getExtras();
		memory = (Post.PostInfos)b.getSerializable("post");
		memoryContent.setText(memory.content);
		
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
			

			addCommThread = new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(ViewMemory.this);

				alert.setTitle("Ajouter un message");
				alert.setMessage("Ajouter votre message");
				
				// Set an EditText view to get user input 
				final EditText input = new EditText(ViewMemory.this);
				alert.setView(input);

				alert.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					final String value = input.getText().toString();
				  	Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
				
					if (Network.USER == null) {
						Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_LONG).show();
						//Intent intent = new Intent(ViewPost.this, Login.class);
						//startActivity(intent);
						return;
					}
					//mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						//	"Long operation starts...", true);
					
					item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					item_loading.setVisible(true);
					
					Thread thread1 = new Thread(){
				        public void run(){	        	      
						try {	
			            	Gson gson = new Gson();
			            	String url = Network.URL + Network.PORT + "/comments.json";
			            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			            	
			            	nameValuePairs.add(new BasicNameValuePair("comment[user_id]", Integer.toString(Network.USER.id)));
			            	nameValuePairs.add(new BasicNameValuePair("comment[publication_id]", Integer.toString(memory.id)));
			            	//nameValuePairs.add(new BasicNameValuePair("comment[content]", editPost.getText().toString()));
			            	nameValuePairs.add(new BasicNameValuePair("comment[content]", value));
			            	
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

				alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
				alert.show();
				return false;
			}	
			
		};
	
		
		btnLike.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				//mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
					//	"Long operation starts...", true);
				
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread threadSendLike = new Thread(){
			        public void run(){	        	      
					try {
						
						if (actionVote == 1) {
							actionVote = -2;
							new ThreadCancelLike(ViewMemory.this).start();
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
		
		btnDislike.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Network.USER == null) {
					Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(ViewPost.this, Login.class);
					//startActivity(intent);
					return;
				}
		
				//mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
					//	"Long operation starts...", true);
				
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread threadSendDislike = new Thread(){
			        public void run(){	        	      
					try {	
						
						if (actionVote == 0) {
							actionVote = -3;
							new ThreadCancelLike(ViewMemory.this).start();
							
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
	
		new ThreadUpdateComm(ViewMemory.this).start();
	}
	

	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	/*switch (msg.what) {
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
		    	case CREATE_COMM:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur : votre commentaire n'a pas été pris en compte : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		//Toast.makeText(getApplicationContext(), "Commentary send success", Toast.LENGTH_SHORT).show(); 

			    		//mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
			    			//	"Long operation starts...", true);
			    		new ThreadUpdateComm(ViewMemory.this).start();
			    	}
			    	break;
			    	
		    	case UPDATE_COMM:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des commentaires : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{  		
			    		//////////////////////////////////////////DETECT TYPE///////////////////////////
			    		if (memory.type == 2) {
				    		//Toast.makeText(getApplicationContext(), "DETECT UNE IMAGE", Toast.LENGTH_SHORT).show();
			    			new ThreadDownloadImage(ViewMemory.this).start();
			    		}			    		
			    		///////////////////////////////////////////////////////////////
			    		
			    		listItem = new ArrayList<HashMap<String, Object>>();
				        listView.removeAllViewsInLayout();
			    		
			    		String[] listStrings = new String[listComm.list.length];
			    		if (listComm.list.length > 0)
			    		{
			    			for (int i = 0; i < listComm.list.length; i++) {
			    				listStrings[i] = listComm.list[i].content;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				new ThreadDownloadImage(ViewMemory.this, i, listView, listComm, listItem, map).start();
			    			}
			    		 	    			
			    			mSchedule = new SimpleAdapter (ViewMemory.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);
				    	    
				    	    

/////////////////////////////////////////////////////////////////
			    		
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
								   					Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_LONG).show();
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
								            	  // mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
													//		"Long operation starts...", true);
								            	   	item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
								   					item_loading.setVisible(true);
								            	   
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
			    		
			    		//Toast.makeText(getApplicationContext(), "Update comm success", Toast.LENGTH_LONG).show();
			    		//threadGetLike.start();
			    		
			    	}
			    	else
		    			Toast.makeText(getApplicationContext(), "Il n'y a encore aucun commentaires, soyez le premier ;)", Toast.LENGTH_LONG).show();
			    
			    	if (memory.type == 0) {
			    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    	}
			    }
			    break;
			    	
		    	case GET_VOTES:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des votes : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		viewLike.setText(Integer.toString(memory.upvotes));
			    		viewDislike.setText(Integer.toString(memory.downvotes));	    		
			    	}
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
					item_loading.setVisible(false);
			    	break;
			    	
		    	case SEND_VOTE:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur : Votre vote n'a pas été pris en compte : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
		    		{    		
			    		Toast.makeText(getApplicationContext(), "Votre vote a bien été pris en compte", Toast.LENGTH_LONG).show();
			    		if (actionVote == 1)
			    			viewLike.setText(Integer.toString(memory.upvotes + 1));   //  verif si auth
			    		else if(actionVote == 0)
			    			viewDislike.setText(Integer.toString(memory.downvotes + 1));   //  verif si auth
			    		/*else if(actionVote == -2)
			    			viewDislike.setText(Integer.toString(memory.upvotes - 1));
			    		else if(actionVote == -3)
			    			viewDislike.setText(Integer.toString(memory.downvotes - 1));*/
			    		
			    		new ThreadUpdateLike(ViewMemory.this).start();
			    	}
			    	break;
			    	
		    	case CANCEL_VOTE:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur : Votre annulation de vote n'a pas été pris en compte : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{   		
			    		Toast.makeText(getApplicationContext(), "Votre annulation de vote a bien été pris en compte", Toast.LENGTH_LONG).show();
			    		if(actionVote == -2)
			    			viewLike.setText(Integer.toString(memory.upvotes - 1));
			    		else if(actionVote == -3)
			    			viewDislike.setText(Integer.toString(memory.downvotes - 1));
			    	}
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
					item_loading.setVisible(false);
			    	break;
			    	
		    	case DELETE_COMM:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur : Votre vote na pas été annulé : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{		
			    		Toast.makeText(getApplicationContext(), "Votre vote a bien été annulé ", Toast.LENGTH_LONG).show();
			    		new ThreadUpdateComm(ViewMemory.this).start();
			    	}
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
					item_loading.setVisible(false);
			    	break;
			    	
		    	case UPDATE_IMG_MEMORY:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des avatars : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{		
			    		//Toast.makeText(getApplicationContext(), "Update img success", Toast.LENGTH_LONG).show();
			    		
			    		//imgMemoryImg.setAdjustViewBounds(true);
			    		//imgMemoryImg.setMaxWidth(100);
			    		
			    		
			    		
			    		//imgMemoryImg.setMaxHeight(150);
			    		
			    		
			    		imgMemoryImg.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(imgPlace, 30));	    		
			    		//imgMemoryImg.setImageBitmap(imgPlace);
			    		
			    		//imgMemoryImg.setImageBitmap(new CreateCircleBitmap(imgPlace, 50));
			    	}
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
					item_loading.setVisible(false);
			    	break;
			    	
		    		case UPDATE_AVATAR:		    		
			    		mSchedule = new SimpleAdapter (ViewMemory.this, listItem, R.layout.view_item_list,
			    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
			    	        
			    		mSchedule.setViewBinder(new MyViewBinder());
			    		
			    		//ImageView dd = (ImageView)findViewById(R.id.avatar);
				    	//dd.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(dd.getDrawingCache(), 100));		    		
			    		listView.requestLayout();
			    	    listView.setAdapter(mSchedule);
			    	    
			    	    item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
						item_loading.setVisible(false);
			    		break;
	    	}
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_memory, menu);
	    item_loading = menu.findItem(R.id.loading_zone);
		item_loading.setVisible(false);
		
		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item_loading.setVisible(true);
		
		addComm = menu.findItem(R.id.addComm);
		addComm.setOnMenuItemClickListener(addCommThread);
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
	    		intent = new Intent(ViewMemory.this, Login.class);
	    	else
	    		intent = new Intent(ViewMemory.this, Menu2.class);
	    	startActivity(intent);
			break;
	    default:
	    	break;
	    }
	    return true;
	}
}

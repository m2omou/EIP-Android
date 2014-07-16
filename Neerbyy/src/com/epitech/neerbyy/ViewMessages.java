package com.epitech.neerbyy;

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
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
 * This class describe the view who list all messages associate to a given conversation.
 * @author Seb
 * @see Messages
 * @see Conversations
 */
public class ViewMessages extends Activity {

	//private TextView info;
	private EditText editMessage;
	private ImageView imgSendMessage;
	private ListView listView;
	
	private Conversation conv;
	
	//private Thread threadGetMessages;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Messages listMessages;
	public int conv_id;
	public int recipientId;
	
	private MenuItem item_loading;

	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_messages);
	
		//info = (TextView)findViewById(R.id.messagesTextInfo);
		editMessage = (EditText)findViewById(R.id.textViewMessagesEditMessage);
		imgSendMessage = (ImageView)findViewById(R.id.imgMessagesSendMess);
		
		listView = (ListView)findViewById(R.id.messagesViewList);
		
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		
		Bundle b  = this.getIntent().getExtras();
		conv = (Conversation)b.getSerializable("conv");
		conv_id = b.getInt("convId");
		recipientId = b.getInt("recipientId");
		
		imgSendMessage.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
		
				//mProgressDialog = ProgressDialog.show(ViewMessages.this, "Please wait",
					//	"Long operation starts...", true);
				
				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/messages.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	if (conv_id != -1)
		            		nameValuePairs.add(new BasicNameValuePair("message[recipient_id]", Integer.toString(conv.recipient.id)));
		            	else
		            		nameValuePairs.add(new BasicNameValuePair("message[recipient_id]", Integer.toString(recipientId)));
		            	nameValuePairs.add(new BasicNameValuePair("message[content]", editMessage.getText().toString()));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.POST_MESSAGE.getValue());
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
									if (conv_id == -1)
										conv = rep.getValue(Conversation.class);
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
			thread1.start();
			}
		});
		if (conv_id != -1) {
			//mProgressDialog = ProgressDialog.show(ViewMessages.this, "Please wait",
				//	"Long operation starts...", true);
			new ThreadUpdateMessages(ViewMessages.this).start();
		}
	}
	
	/*Message message = new Message();
	Callback callback = new Callback() {
	    public boolean handleMessage(Message msg) {
	    	new ThreadUpdateMessages(ViewMessages.this).start();
	    }
	};

	Handler handler = new Handler(callback);
	handler.sendMessage(message);*/
	
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
		    	case GET_MESSAGES:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des messages : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		listItem = new ArrayList<HashMap<String, Object>>();
				        listView.removeAllViewsInLayout();
			    		
			    		String[] listStrings = new String[listMessages.list.length];
			    		if (listMessages.list.length > 0)
			    		{
			    			for (int i = 0; i < listMessages.list.length; i++) {
			    				listStrings[i] = listMessages.list[i].content;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				
			    				new ThreadDownloadImage(ViewMessages.this, i, listView, listMessages, listItem, map).start();
			    			}
			    		 	    			
			    			mSchedule = new SimpleAdapter (ViewMessages.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);			    	    
				    	    ////////////////////////////////////////////   	
					            /*map.put("username", listMessages.list[i].sender.username + " :");
					            map.put("content", listMessages.list[i].content);*/
					         
			    			listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			    	 //Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
			    				     //Toast.makeText(ViewPost.this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
			    				     
			    				     	/*Intent intent = new Intent(ViewConv.this, ViewMemory.class);
			    						Bundle b = new Bundle();		    					
			    						b.putSerializable("post", (Serializable)listPost.list[position]);
			    						b.putString("Place_id", placeId);
			    			    		Log.w("LIKE", "dislike = " + listPost.list[position].downvotes);
			    						
			    						intent.putExtras(b);					
			    						startActivity(intent);
			    						return;  */
			    			    }
			    			});
			    		}
			            Toast.makeText(getApplicationContext(), "Update messages success", Toast.LENGTH_LONG).show();
			            item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    	}
			    	break;
			    	
		    	case UPDATE_AVATAR:		    		
		    		mSchedule = new SimpleAdapter (ViewMessages.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		//ImageView dd = (ImageView)findViewById(R.id.avatar);
			    	//dd.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(dd.getDrawingCache(), 100));		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);
		    	break;
			    	
		    	case POST_MESSAGE:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de l'envoie du message : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		editMessage.setText("");
			    		if (conv_id == -1)
			    			conv_id = conv.id;
			    		
			    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			    		item_loading.setVisible(true);
			    		
			    		new ThreadUpdateMessages(ViewMessages.this).start();
			    	}
			    	break;
	    	} 	
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_message, menu);
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
	    		intent = new Intent(ViewMessages.this, Login.class);
	    	else
	    		intent = new Intent(ViewMessages.this, Menu2.class);
			startActivity(intent);
	      break;
	    default:
	      break;
	    }
	    return true;
	  }
}

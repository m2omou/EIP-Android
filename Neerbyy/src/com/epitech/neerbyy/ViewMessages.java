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
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewMessages extends MainMenu {

	private TextView info;
	private EditText editMessage;
	private Button btnSendMessage;
	private ListView listView;
	
	private Conversation conv;
	
	//private Thread threadGetMessages;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Messages listMessages;
	public int conv_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_messages);
	
		info = (TextView)findViewById(R.id.messagesTextInfo);
		editMessage = (EditText)findViewById(R.id.textViewMessagesEditMessage);
		btnSendMessage = (Button)findViewById(R.id.btnMessagesSendMess);
		
		listView = (ListView)findViewById(R.id.messagesViewList);
		
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
		conv = (Conversation)b.getSerializable("conv");
		conv_id = b.getInt("convId");
		
		btnSendMessage.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
		
				mProgressDialog = ProgressDialog.show(ViewMessages.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/messages.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("message[recipient_id]", Integer.toString(conv.recipient.id)));
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
			thread1.start();
			}
		});
		
		mProgressDialog = ProgressDialog.show(ViewMessages.this, "Please wait",
				"Long operation starts...", true);
		new ThreadUpdateMessages(ViewMessages.this).start();
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
		    	case GET_MESSAGES:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Update messages error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		//listPost = (Post)pack.getSerializable("post");   //  utile ?????? 
			    		//Log.w("PATH", "LAAA");
			    		//List listStrings = new ArrayList<String>() ;//= {"France","Allemagne","Russie"};
			    		String[] listStrings = new String[listMessages.list.length] ;//= {"France","Allemagne","Russie"};
			    		
			    		
			    		//Création de la ArrayList qui nous permettra de remplir la listView
			            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			     
			            //On déclare la HashMap qui contiendra les informations pour un item
			            HashMap<String, String> map;
			    		
			    		
			    		if (listMessages.list.length > 0)
			    		{
			    			Log.d("MESSAGES", "YA DEJA DES MESSAGES !!");
			    			for (int i = 0; i < listMessages.list.length; i++) {
			    				listStrings[i] = listMessages.list[i].content;
			    				
			    				 //Création d'une HashMap pour insérer les informations du premier item de notre listView
					            map = new HashMap<String, String>();
					            //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
					            map.put("username", listMessages.list[i].sender.username + " :");
					            //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
					            map.put("content", listMessages.list[i].content);
					            //on insère la référence à l'image (converti en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
					            map.put("avatar", String.valueOf(R.drawable.avatar));
					            //enfin on ajoute cette hashMap dans la arrayList
					            listItem.add(map);
			    				
			    			}
			    			
			    			//Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
			    	        SimpleAdapter mSchedule = new SimpleAdapter (ViewMessages.this, listItem, R.layout.view_item_list,
			    	               new String[] {"avatar", "username", "content"}, new int[] {R.id.avatar, R.id.username, R.id.content});
			    	 
			    	        //On attribue à notre listView l'adapter que l'on vient de créer
			    	        listView.setAdapter(mSchedule);
			    	        
			    		 
			    			//listView.setAdapter(new ArrayAdapter<String>(ViewFeed.this, android.R.layout.simple_list_item_1, listStrings));	
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
			    	}
			    	break;
		    	case POST_MESSAGE:
		    		info.setText("");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Message error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		editMessage.setText("");
			    		info.setText("Message send success" );
			    		new ThreadUpdateMessages(ViewMessages.this).start();
			    		//mProgressDialog = ProgressDialog.show(ViewPost.this, "Please wait",
			    			//	"Long operation starts...", true);
			    	}
			    	break;
	    	} 	
	    }
	};
}

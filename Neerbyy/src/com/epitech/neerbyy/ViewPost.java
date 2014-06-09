package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;

/**
 * This class represent the view associate to a place, and allow to list all posts of this place.
 * @author Seb
 */
public class ViewPost extends Activity {

	private ImageButton btnCreatePost;
	private TextView sendButton;
	private TextView info;
	private TextView placeName;
	private EditText editPost;
	private ListView listView;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Post listPost;
	public PlaceInfo place;
	public String placeId;
	
	/*protected void onListIemClick(ListView lv , View v, int position, long id){
		//super.(lv, v, position, id);
	  //   Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
	     Toast.makeText(this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
	}*/
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_post);
	
		btnCreatePost = (ImageButton)findViewById(R.id.btnCreatePost);
		sendButton = (TextView)findViewById(R.id.postSendPost);
		info = (TextView)findViewById(R.id.postTextInfo);
		placeName = (TextView)findViewById(R.id.postNamePlace);
		editPost = (EditText)findViewById(R.id.postEditPost);
		listView = (ListView)findViewById(R.id.postViewListPost);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
	//	place = (PlaceInfo)b.getSerializable("placeInfo");
		placeId = b.getString("placeId");
		placeName.setText(b.getString("placeName"));

//		b.getSerializable(key)
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
			    		String[] listStrings = new String[listPost.list.length] ;//= {"France","Allemagne","Russie"};
			    		if (listPost.list.length > 0)
			    		{
			    			Log.d("POST", "YA DEJA DES POSTS !!");
			    			for (int i = 0; i < listPost.list.length; i++) {
			    				listStrings[i] = listPost.list[i].content;
			    				
			    			}
			    		 
			    			listView.setAdapter(new ArrayAdapter<String>(ViewPost.this, android.R.layout.simple_list_item_1, listStrings));	
			    			listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			    	 //Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
			    				     //Toast.makeText(ViewPost.this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
			    				     
			    				     Intent intent = new Intent(ViewPost.this, ViewMemory.class);
			    						Bundle b = new Bundle();		    					
			    						b.putSerializable("post", (Serializable)listPost.list[position]);
			    						//listPost.list[position].nbLike;
			    			    		intent.putExtras(b);					
			    						startActivity(intent);
			    						return;  
			    			    }
			    			});
			    		}
			            Toast.makeText(getApplicationContext(), "Update post success", Toast.LENGTH_LONG).show();
			    	}
			    	break;
	    	} 	
	    }
	};	
	
}


package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.IntToString;
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
public class ViewMemory extends Activity {

	private TextView sendButton;
	private TextView info;
	private EditText editPost;
	private ListView listView;
	private TextView memoryContent;
	
	private ImageButton btnLike;
	private ImageButton btnDislike;
	private TextView viewLike;
	private TextView viewDislike;
	
	private Votes votes;
	public Thread threadGetLike;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Commentary listComm;
	public Post.PostInfos memory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_memory);
	
		btnLike = (ImageButton)findViewById(R.id.commBtnLike);
		btnDislike = (ImageButton)findViewById(R.id.commBtnDislike);
		viewLike = (TextView)findViewById(R.id.commViewLike);                   
		viewDislike = (TextView)findViewById(R.id.commViewDislike);
		
		sendButton = (TextView)findViewById(R.id.postSendCommentary);
		info = (TextView)findViewById(R.id.commTextInfo);
		editPost = (EditText)findViewById(R.id.postEditCommentary);
		listView = (ListView)findViewById(R.id.postViewListCommentary);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
		memory = (Post.PostInfos)b.getSerializable("post");
		memoryContent = (TextView)findViewById(R.id.commContentMemory);
		
		
		btnLike.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadSendLike = new Thread(){
			        public void run(){	        	      
					try {	
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
		
				mProgressDialog = ProgressDialog.show(ViewMemory.this, "Please wait",
						"Long operation starts...", true);
				
				Thread threadSendDislike = new Thread(){
			        public void run(){	        	      
					try {	
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
	threadGetLike.start();
		
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
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
			    		
			    		//listStrings[0] = memory.content;
			    		memoryContent.setText(memory.content);
			    		
			    		if (listComm.list.length > 0)
			    		{
			    			Log.d("COMM", "YA DEJA DES COMM !!, vec first comment = " + memory.content);
			    			for (int i = 0; i < listComm.list.length; i++) {
			    				listStrings[i] = listComm.list[i].content; 				
			    			}
			    			listView.setAdapter(new ArrayAdapter<String>(ViewMemory.this, android.R.layout.simple_list_item_1, listStrings));
			    		}
			    		
			           // listView.getAdapter().getView(0, null, listView).setBackgroundColor(getResources().getColor(R.color.greenNeerbyy));
			    		
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
			    		
			    		viewLike.setText(Integer.toString(memory.nbLike));
			    		viewDislike.setText(Integer.toString(memory.nbDislike));	    		
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
			    		//threadGetLike.start();
			    	}
			    	break;
	    	} 	
	    }
	};	
	
}


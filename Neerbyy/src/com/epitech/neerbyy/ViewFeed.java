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
 * This class represent the feed associate to a user.
 * @author Seb
 */
public class ViewFeed extends Activity {

	private ImageButton btnCreatePost;
	private TextView info;
	private TextView placeName;
	private ListView listView;
	
	private Thread threadGetFeed;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Post listPost;
	public PlaceInfo place;
	public String placeId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_feed);
	
		btnCreatePost = (ImageButton)findViewById(R.id.btnCreatePost);
		info = (TextView)findViewById(R.id.feedTextInfo);
		placeName = (TextView)findViewById(R.id.feedNamePlace);
		listView = (ListView)findViewById(R.id.feedViewListFeed);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
		Bundle b  = this.getIntent().getExtras();
	//	place = (PlaceInfo)b.getSerializable("placeInfo");
	
	//	placeId = b.getString("placeId");
	//	placeId = place.id;
	//	placeName.setText(b.getString("placeName"));  
	//	placeName.setText(place.name); 

//		b.getSerializable(key)
	
		threadGetFeed = new Thread(){
	        public void run(){	        	      
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
        	Log.w("THREAD", "FIN THREAD UPDATE FEED");
			
		}};
		mProgressDialog = ProgressDialog.show(ViewFeed.this, "Please wait",
				"Long operation starts...", true);
	threadGetFeed.start();	
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
		    	case GET_FEED:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Update feed error :\n" + pack.getString("msgError"));
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
			    			Log.d("FEED", "YA DEJA DES FEED !!");
			    			for (int i = 0; i < listPost.list.length; i++) {
			    				listStrings[i] = listPost.list[i].content;
			    				
			    			}
			    		 
			    			listView.setAdapter(new ArrayAdapter<String>(ViewFeed.this, android.R.layout.simple_list_item_1, listStrings));	
			    			listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			    	 //Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
			    				     //Toast.makeText(ViewPost.this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
			    				     
			    				     Intent intent = new Intent(ViewFeed.this, ViewMemory.class);
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
			            Toast.makeText(getApplicationContext(), "Update feed success", Toast.LENGTH_LONG).show();
			    	}
			    	break;
	    	} 	
	    }
	};	
}


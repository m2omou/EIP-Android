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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;
import com.epitech.neerbyy.Post.PostInfos;

/**
 * This class describe the view to see the feed associate to a user.
 * @author Seb
 * 
 */
public class ViewFeed extends Activity {

	//private ImageButton btnCreatePost;
	//private TextView info;
	private TextView placeName;
	private ListView listView;
	
	private Thread threadGetFeed;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Post listPost;
	public PlaceInfo place;
	public String placeId;
	
	private MenuItem item_loading;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_feed);
	
		//btnCreatePost = (ImageButton)findViewById(R.id.btnCreatePost);
		//info = (TextView)findViewById(R.id.feedTextInfo);
		placeName = (TextView)findViewById(R.id.feedNamePlace);
		placeName.setText("Flux de " + Network.USER.username);
		
		listView = (ListView)findViewById(R.id.feedViewListFeed);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		
		
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
		//mProgressDialog = ProgressDialog.show(ViewFeed.this, "Please wait",
			//	"Long operation starts...", true);
	threadGetFeed.start();	
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
			    				new ThreadDownloadImage(ViewFeed.this, i, listView, listPost, listItem, map).start();
			    			}
			    			
			    			mSchedule = new SimpleAdapter (ViewFeed.this, listItem, R.layout.view_item_list,
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
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    		item_loading.setVisible(false);
			    	break;
			    	
		    	case UPDATE_AVATAR:
		    		mSchedule = new SimpleAdapter (ViewFeed.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);     
		    		break;
	    	} 	
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_feed, menu);
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
	    		intent = new Intent(ViewFeed.this, Login.class);
	    	else
	    		intent = new Intent(ViewFeed.this, Menu2.class);
			startActivity(intent);
	      break;
	    default:
	      break;
	    }
	    return true;
	  }
}


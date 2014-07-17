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

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.epitech.neerbyy.Place.PlaceInfo;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class describe the view to see the list of conversations associate to a user.
 * @author Seb
 * 
 */
public class ViewConv extends Activity {

	//private TextView info;
	private ListView listView;
	
	private Thread threadGetConv;
	
	private ImageView newConv;
	
	private MenuItem item_loading;
	
	Users users;
	
	SimpleAdapter mSchedule = null;
	ArrayList<HashMap<String, Object>> listItem;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Conversations listConv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_conv);
	
		//info = (TextView)findViewById(R.id.convTextInfo);
		listView = (ListView)findViewById(R.id.convViewList);
		
		newConv = (ImageView)findViewById(R.id.convViewNewConv);

		listView.clearChoices();
		
		
	//	Bundle b  = this.getIntent().getExtras();
	//	place = (PlaceInfo)b.getSerializable("placeInfo");
	
	//	placeId = b.getString("placeId");
	//	placeId = place.id;
	//	placeName.setText(b.getString("placeName"));  
	//	placeName.setText(place.name); 

    //	b.getSerializable(key)
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
	
		newConv.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder alert = new AlertDialog.Builder(ViewConv.this);

				alert.setTitle("Rechercher un utilisateur");
				alert.setMessage("Entrer le nom d'un utilisateur");
				
				// Set an EditText view to get user input 
				final EditText input = new EditText(ViewConv.this);
				alert.setView(input);

				alert.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					final String value = input.getText().toString();
				  	//Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
				
					if (Network.USER == null) {
						Toast.makeText(getApplicationContext(), "Cette fonctionalité nécessite un compte Neerbyy", Toast.LENGTH_LONG).show();
						//Intent intent = new Intent(ViewPost.this, Login.class);
						//startActivity(intent);
						return;
					}
					
					item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
					item_loading.setVisible(true);
					
					Thread thread1 = new Thread(){
				        public void run(){
				        	
						try {	
			            	Gson gson = new Gson();
			            	String url = Network.URL + Network.PORT + "/search/users.json?query=" + value;	       

			            	Message myMessage, msgPb;
			            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
			                myHandler.sendMessage(msgPb);
			                
							Bundle messageBundle = new Bundle();
							messageBundle.putInt("action", ACTION.SEARCH_USER.getValue());
					        myMessage = myHandler.obtainMessage();	
	   		        
					        InputStream input = Network.retrieveStream(url, METHOD.GET, null);
					        
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
									users = rep.getValue(Users.class);
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

				alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				    // Canceled.
				  }
				});
				alert.show();
			
			}	
				
				//Intent intent = new Intent(ViewConv.this, SearchUser.class);
				//startActivity(intent);
		});
		
		
		threadGetConv = new Thread(){
	        public void run(){	        	      
			try {	
            	Gson gson = new Gson();
            	String url = Network.URL + Network.PORT + "/conversations.json";
            	
            	Message myMessage, msgPb;
            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");
            	myHandler.sendMessage(msgPb);
            
            	Bundle messageBundle = new Bundle();
    			messageBundle.putInt("action", ACTION.GET_CONV.getValue());
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
    						listConv = rep.getValue(Conversations.class);
    						
    					}
    					catch(JsonParseException e)
    				    {
    				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
    				    }
    					if (listConv == null)
    					{
    						messageBundle.putInt("error", 2);
    						messageBundle.putString("msgError", rep.responseMessage);
    					}
    					else
    						Log.w("RECUP", "JAI RECUP DES CONV ");
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
        	Log.w("THREAD", "FIN THREAD UPDATE CONV");
			
		}};
		//mProgressDialog = ProgressDialog.show(ViewConv.this, "Please wait",
			//	"Long operation starts...", true);
	threadGetConv.start();	
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
		    	case GET_CONV:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la mise à jour des conversations:\n" + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		listItem = new ArrayList<HashMap<String, Object>>();
				        listView.removeAllViewsInLayout();
			    		
			    		String[] listStrings = new String[listConv.list.length];
			    		if (listConv.list.length > 0)
			    		{
			    			for (int i = 0; i < listConv.list.length; i++) {
			    				listStrings[i] = listConv.list[i].messages[0].content;
			    				HashMap<String, Object> map = new HashMap<String, Object>();			    				
			    				listItem.add(map);
			    				int lastMessageIndice = listConv.list[i].messages.length - 1;
			    				if (lastMessageIndice < 0)
			    					lastMessageIndice = 0;
			    				new ThreadDownloadImage(ViewConv.this, i, listView, listConv, listItem, map, lastMessageIndice).start();
			    			}
			    		 	    			
			    			mSchedule = new SimpleAdapter (ViewConv.this, listItem, R.layout.view_item_list,
				    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
				    	        
				    		mSchedule.setViewBinder(new MyViewBinder());
				    		
				    		listView.requestLayout();
				    	    listView.setAdapter(mSchedule);
				    	    
				    	    listView.setBackgroundResource(R.drawable.my_listview);
				    	    	
			    			listView.setOnItemClickListener(new OnItemClickListener() {
			    			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			    	 //Toast.makeText(this, "Id: " + lv.getAdapter().get(position), Toast.LENGTH_LONG).show();
			    				     //Toast.makeText(ViewPost.this, "Id: " + listPost.list[position].id, Toast.LENGTH_LONG).show();
			    				     
			    				     	Intent intent = new Intent(ViewConv.this, ViewMessages.class);
			    						Bundle b = new Bundle();		    					
			    						b.putSerializable("conv", (Serializable)listConv.list[position]);
			    						b.putInt("convId", listConv.list[position].id);			    						
			    						intent.putExtras(b);					
			    						startActivity(intent);
			    						return;
			    			    }
			    			});
			    		}
			            //Toast.makeText(getApplicationContext(), "Update conv success", Toast.LENGTH_LONG).show();
			            item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    	}
			    	break;
			    	
		    	case UPDATE_AVATAR:		    		
		    		mSchedule = new SimpleAdapter (ViewConv.this, listItem, R.layout.view_item_list,
		    				new String[] {"avatar", "username", "content", "date"}, new int[] {R.id.avatar, R.id.username, R.id.content, R.id.date});
		    	        
		    		mSchedule.setViewBinder(new MyViewBinder());
		    		
		    		//ImageView dd = (ImageView)findViewById(R.id.avatar);
			    	//dd.setImageBitmap(CreateCircleBitmap.getRoundedCornerBitmap(dd.getDrawingCache(), 100));		    		
		    		listView.requestLayout();
		    	    listView.setAdapter(mSchedule);
		    	break;
		    	
		    	case SEARCH_USER:    		
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la recherche utilisateur : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		//Toast.makeText(getApplicationContext(), "Search user success", Toast.LENGTH_SHORT).show();
			    					    		
			    		if (users.list.length == 0)
			    		{
    			    		Toast.makeText(getApplicationContext(), "list vide !!!!", Toast.LENGTH_SHORT).show();
			    			return;
			    		}
			    		
			    		List<CharSequence> charSequences = new ArrayList<CharSequence>();
			    		for (int i = 0; i < users.list.length; i++) {
			    			String tmp = new String(users.list[i].username + "\n");
			    			if (users.list[i].firstname != null)
			    				tmp += users.list[i].firstname;
			    			if (users.list[i].lastname != null)
			    				tmp += users.list[i].lastname;
			    			
			    			charSequences.add(tmp);
			    		}
			    		
			    		final CharSequence[] charSequenceArray = charSequences.toArray(new
			    			    CharSequence[charSequences.size()]);
			    		
			    		//Toast.makeText(getApplicationContext(),"Charsequence a " + charSequenceArray.length, Toast.LENGTH_SHORT).show();

			    		
						AlertDialog.Builder builder = new AlertDialog.Builder(ViewConv.this);
						builder.setTitle("Qui Cherchez vous ?");
						builder.setItems(charSequenceArray, new DialogInterface.OnClickListener() {
						          
							@Override
								public void onClick(DialogInterface dialog, int item) {
						               // Toast.makeText(getApplicationContext(), charSequenceArray[item], Toast.LENGTH_SHORT).show();
						               
						                /////////////SOIT VERS PROFIL USER SOIT MESSAGE DIRECT
						                
						                Intent intent;
						            	/*if (Network.USER != null && listPost.list[position].user.id == Network.USER.id)
						            	   intent = new Intent(ViewPost.this, EditInfoUser.class);
						            	else*/
						            	   intent = new Intent(ViewConv.this, ViewInfoUser.class);
						            	Bundle b = new Bundle();
						            	b.putInt("userId", users.list[item].id);	
						            	intent.putExtras(b);
						            	startActivity(intent);	
						                
						                
						                
						                /*Intent intent = new Intent(ViewConv.this, ViewMessages.class);
			    						Bundle b = new Bundle();		    					
			    						//b.putSerializable("conv", (Serializable)listConv.list[position]);
			    						b.putInt("convId", -1);
			    						b.putInt("recipientId", users.list[item].id);
			    						intent.putExtras(b);					
			    						startActivity(intent);*/
			    						return;
						          	}
						        });
						AlertDialog alert = builder.create();
						alert.show();			
			    }
		    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		    		item_loading.setVisible(false);
			    break;
	    	} 	
	    }
	};

	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_conv, menu);
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
	    		intent = new Intent(ViewConv.this, Login.class);
	    	else
	    		intent = new Intent(ViewConv.this, Menu2.class);
			startActivity(intent);
	      break;
	    default:
	      break;
	    }
	    return true;
	  }
}

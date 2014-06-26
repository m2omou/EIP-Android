package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewConv extends MainMenu {

	private TextView info;
	private ListView listView;
	
	private Thread threadGetConv;
	
	ResponseWS rep;
	ProgressDialog mProgressDialog;
	public Conversations listConv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_conv);
	
		info = (TextView)findViewById(R.id.convTextInfo);
		listView = (ListView)findViewById(R.id.convViewList);
		
		//listView.removeAllViews();
		listView.clearChoices();
		
		
	//	Bundle b  = this.getIntent().getExtras();
	//	place = (PlaceInfo)b.getSerializable("placeInfo");
	
	//	placeId = b.getString("placeId");
	//	placeId = place.id;
	//	placeName.setText(b.getString("placeName"));  
	//	placeName.setText(place.name); 

//		b.getSerializable(key)
	
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
		mProgressDialog = ProgressDialog.show(ViewConv.this, "Please wait",
				"Long operation starts...", true);
	threadGetConv.start();	
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
		    	case GET_CONV:
		    		info.setText("");		    	
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Update conv error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		//listPost = (Post)pack.getSerializable("post");   //  utile ?????? 
			    		//Log.w("PATH", "LAAA");
			    		//List listStrings = new ArrayList<String>() ;//= {"France","Allemagne","Russie"};
			    		String[] listStrings = new String[listConv.list.length] ;//= {"France","Allemagne","Russie"};
			    		
			    		
			    		//Création de la ArrayList qui nous permettra de remplir la listView
			            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
			     
			            //On déclare la HashMap qui contiendra les informations pour un item
			            HashMap<String, String> map;
			    		
			    		
			    		if (listConv.list.length > 0)
			    		{
			    			Log.d("CONV", "YA DEJA DES CONV !!");
			    			for (int i = 0; i < listConv.list.length; i++) {
			    				listStrings[i] = listConv.list[i].messages[0].content;
			    				int lastMessageIndice = listConv.list[i].messages.length - 1;
			    				if (lastMessageIndice < 0)
			    					lastMessageIndice = 0;
			    				 //Création d'une HashMap pour insérer les informations du premier item de notre listView
					            map = new HashMap<String, String>();
					            //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
					            map.put("username", listConv.list[i].messages[lastMessageIndice].sender.username + " :");
					            //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
					            map.put("content", listConv.list[i].messages[lastMessageIndice].content);
					            //on insère la référence à l'image (converti en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
					            map.put("avatar", String.valueOf(R.drawable.avatar));
					            //enfin on ajoute cette hashMap dans la arrayList
					            listItem.add(map);
			    				
			    			}
			    			
			    			//Création d'un SimpleAdapter qui se chargera de mettre les items présents dans notre list (listItem) dans la vue affichageitem
			    	        SimpleAdapter mSchedule = new SimpleAdapter (ViewConv.this, listItem, R.layout.view_item_list,
			    	               new String[] {"avatar", "username", "content"}, new int[] {R.id.avatar, R.id.username, R.id.content});
			    	 
			    	        //On attribue à notre listView l'adapter que l'on vient de créer
			    	        listView.setAdapter(mSchedule);
			    	        
			    		 
			    			//listView.setAdapter(new ArrayAdapter<String>(ViewFeed.this, android.R.layout.simple_list_item_1, listStrings));	
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
			            Toast.makeText(getApplicationContext(), "Update conv success", Toast.LENGTH_LONG).show();
			    	}
			    	break;
	    	} 	
	    }
	};

}

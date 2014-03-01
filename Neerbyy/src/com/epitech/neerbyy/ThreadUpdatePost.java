package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

	
public class ThreadUpdatePost extends Thread {
	private ViewPost vp;
	
    public ThreadUpdatePost(ViewPost vp) {
        super();
        this.vp = vp;
    }
    
    public void run() {
        Log.w("THREAD", "DEBUT THREAD POSTS");
    	try {
        	Gson gson = new Gson();
        	String url;
        	url = Network.URL + Network.PORT + "/publications.json/?place_id=" + vp.placeId;        	

        	Message myMessage;
        	Bundle messageBundle = new Bundle();
			messageBundle.putInt("action", Network.UPDATE_POST);
	        myMessage = vp.myHandler.obtainMessage();	
       
	        InputStream input = Network.retrieveStream(url, 0, null);
        	
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
						vp.rep = gson.fromJson(ret, ResponseWS.class);
						vp.listPost = vp.rep.getValue(Post.class, 0);
						Log.w("RECUP", "JAI RECUP " + vp.listPost.list.length + " posts");
					}
					catch(JsonParseException e)
				    {
				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
				    }
					if (vp.listPost == null)
					{
						messageBundle.putInt("error", 2);
						messageBundle.putString("msgError", vp.rep.responseMessage);
					}
					//else		  	                   
						//messageBundle.putSerializable("post", (Serializable) vp.listPost);
				}
			}
	        myMessage.setData(messageBundle);
            vp.myHandler.sendMessage(myMessage);
    	}
    	catch (Exception e) {
            e.printStackTrace();}
    	Log.w("THREAD", "FIN THREAD PLACES");
    }
}

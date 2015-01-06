package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * this class can launch a request for Places around the user. 
 *This class can be instantiated as a Thread
 * @see Place
 */
public class ThreadPlaces extends Thread {
	/**
	 * mv represent the instance of the view "MapView",
	 * and allow this thread to communicate with it handler
	 */
	public MapView mv;
	
	/**
	 * locat represent the actual position of the user
	 */
	private LatLng locat;
	
    public ThreadPlaces(MapView Mv, LatLng L) {
        super();
        mv = Mv;
        locat = L;
    }
    
    public void run() {
        Log.w("THREAD", "DEBUT THREAD PLACES");
    	try {
        	Gson gson = new Gson();
        	String url;
        	if (locat != null){
        		url = Network.URL + Network.PORT + "/places.json?latitude=" + locat.latitude + "&longitude=" + locat.longitude
        				+ "&limit=" + mv.limit + "&radius=" + mv.radius;
        		if (mv.categorieId != null)
        			url += "&category_id=" + mv.categorieId;
        	}
        	else {        		
        		url = Network.URL + Network.PORT + "/places.json?latitude=45.75&longitude=-0.633333";
        	}
        	Message myMessage;
        	Bundle messageBundle = new Bundle();
			messageBundle.putInt("action", ACTION.GET_PLACES.getValue());
	        myMessage = mv.myHandler.obtainMessage();	
       
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
						mv.rep = gson.fromJson(ret, ResponseWS.class);
						mv.places = mv.rep.getValue(Place.class);
						
					}
					catch(JsonParseException e)
				    {
				        System.out.println("Exception n6 in check_exitrestrepWSResponse::"+e.toString());
				    }
					if (mv.places == null)
					{
						messageBundle.putInt("error", 2);
						messageBundle.putString("msgError", mv.rep.responseMessage);
					}
					Log.w("RECUP", "JAI RECUP " + mv.places.list.length + " places");
					//else		  	                   
						//messageBundle.putSerializable("places", (Serializable) mv.places);
				}
			}
	        myMessage.setData(messageBundle);
            mv.myHandler.sendMessage(myMessage);
    	}
    	catch (Exception e) {
            e.printStackTrace();}
    	Log.w("THREAD", "FIN THREAD PLACES");
    }
}


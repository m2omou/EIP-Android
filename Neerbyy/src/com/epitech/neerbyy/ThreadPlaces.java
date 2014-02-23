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

	
public class ThreadPlaces extends Thread {
	public MapView mv;
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
        	}
        	else {        		
        		url = Network.URL + Network.PORT + "/places.json?latitude=45.75&longitude=-0.633333";
        	}
        	Message myMessage;
        	Bundle messageBundle = new Bundle();
			messageBundle.putInt("action", Network.GET_PLACES);
	        myMessage = mv.myHandler.obtainMessage();	
       
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
					/*if (ret.charAt(0) == '[')  //   ENLEVE  BLOC  INUTILE  !!!!!
					{
						ret = ret.substring(1);
						ret = ret.substring(0, ret.length() - 1);
						Log.w("TRANSFORM", "NEW RET =  " + ret);
					}*/
					try {		    
						mv.rep = gson.fromJson(ret, ResponseWS.class);
						//mv.places = mv.rep.getTabValue(Place.class);
						mv.places = mv.rep.getValue(Place.class, 0);
						//Log.w("RECUP", "JAI RECUP " + mv.places.list.length + " places");
					}
					catch(JsonParseException e)
				    {
				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
				    }
					if (mv.places == null)
					{
						messageBundle.putInt("error", 2);
						messageBundle.putString("msgError", mv.rep.responseMessage);
					}
					else		  	                   
						messageBundle.putSerializable("places", (Serializable) mv.places);
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


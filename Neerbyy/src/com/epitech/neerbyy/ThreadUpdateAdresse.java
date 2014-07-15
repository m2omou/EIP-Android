package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 *This class can launch a request for obtain list of Commentary from a given place. 
 *This class can be instantiated as a Thread
 * @see Commentary
 */
public class ThreadUpdateAdresse extends Thread {
	/**
	 *vm represent the instance of the view "ViewMemory",
	 *and allow this thread to communicate with it handler
	 *@see ViewMemory
	 */
	private MapView mv;
	private String add;
	
    public ThreadUpdateAdresse(MapView mv_, String add_) {
        super();
        this.mv = mv_;
        add = add_;
    }
    
    public void run() {
        Log.w("THREAD", "DEBUT THREAD COMM");
        try {	
        	Gson gson = new Gson();
    
        	String url;
        	if (mv.locat != null)
        		url = Network.URL + Network.PORT + "/search/places.json?query=" +add + "&user_latitude=" + mv.locat.getLatitude() + "&user_longitude=" + mv.locat.getLongitude();
        	else
        		url = Network.URL + Network.PORT + "/search/places.json?query=" + add;
        	
        	Message myMessage, msgPb;
        	msgPb = mv.myHandler.obtainMessage(0, (Object) "Please wait");
        	mv.myHandler.sendMessage(msgPb);
        
        	Bundle messageBundle = new Bundle();
			messageBundle.putInt("action", ACTION.GET_SEARCH_PLACE.getValue());
	        myMessage = mv.myHandler.obtainMessage();	
       
	        mv.searchPlaces = null;
	        
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
						Log.w("DETECT", "111");
						
						mv.rep = gson.fromJson(ret, ResponseWS.class); 
						
						Log.w("DETECT", "222");
						
						mv.searchPlaces = mv.rep.getValue(Place.class);         						
					}
					catch(JsonParseException e)
				    {
						Log.w("DETECT", "MERDE " + e.toString());
				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
				    }
					if (mv.searchPlaces == null)
					{
						messageBundle.putInt("error", 2);
						messageBundle.putString("msgError", mv.rep.responseMessage);
					}
					else
						Log.w("RECUP", "JAI RECUP DES PLACES SEARCH ");
				}
			}     
	        myMessage.setData(messageBundle);
            mv.myHandler.sendMessage(myMessage);
            
            msgPb = mv.myHandler.obtainMessage(1, (Object) "Success");
            mv.myHandler.sendMessage(msgPb);                       
    	}
    	catch (Exception e) {
            e.printStackTrace();}
    	Log.w("THREAD", "FIN THREAD SEARCH PLACES");      			
    	
    }
}

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
 *This class can launch a request for obtain list of posts from a given place. 
 *This class can be instantiated as a Thread
 * @see Post
 */
public class ThreadUpdateComm extends Thread {
	/**
	 *vp represent the instance of the view "ViewPost",
	 *and allow this thread to communicate with it handler
	 *@see ViewPost
	 */
	private ViewMemory vm;
	
    public ThreadUpdateComm(ViewMemory vm) {
        super();
        this.vm = vm;
    }
    
    public void run() {
        Log.w("THREAD", "DEBUT THREAD COMM");
    	try {
        	Gson gson = new Gson();
        	String url;
     
        	url = Network.URL + Network.PORT + "/comments.json?publication_id=" + vm.memory.id;        	
        	
        	Message myMessage, msgPb;
        	msgPb = vm.myHandler.obtainMessage(0, (Object) "Please wait");
        	vm.myHandler.sendMessage(msgPb);
        
        	Bundle messageBundle = new Bundle();
			messageBundle.putInt("action", ACTION.UPDATE_COMM.getValue());
	        myMessage = vm.myHandler.obtainMessage();	
       
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
						vm.rep = gson.fromJson(ret, ResponseWS.class);
						vm.listComm = vm.rep.getValue(Commentary.class);
						
					}
					catch(JsonParseException e)
				    {
				        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
				    }
					if (vm.listComm == null)
					{
						messageBundle.putInt("error", 2);
						messageBundle.putString("msgError", vm.rep.responseMessage);
					}
					else
						Log.w("RECUP", "JAI RECUP " + vm.listComm.list.length + " comm");
					//else		  	                   
						//messageBundle.putSerializable("post", (Serializable) vp.listPost);
				}
			}
	        
	        
	        myMessage.setData(messageBundle);
            vm.myHandler.sendMessage(myMessage);
            
            msgPb = vm.myHandler.obtainMessage(1, (Object) "Success");
            vm.myHandler.sendMessage(msgPb);
            
    	}
    	catch (Exception e) {
            e.printStackTrace();}
    	Log.w("THREAD", "FIN THREAD UPDATE COMM");
    	
    }
}

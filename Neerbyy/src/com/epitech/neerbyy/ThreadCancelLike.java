package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
public class ThreadCancelLike extends Thread {
	/**
	 *vp represent the instance of the view "ViewPost",
	 *and allow this thread to communicate with it handler
	 *@see ViewPost
	 */
	private ViewMemory vm;
	
    public ThreadCancelLike(ViewMemory vm_) {
        super();
        this.vm = vm_;
    }
    
    public void run(){	        	      
    	
	try {	
    	Gson gson = new Gson();
    	String url = Network.URL + Network.PORT + "/votes/" + vm.memory.vote.id + ".json";
    	
    	Message myMessage, msgPb;
    	msgPb = vm.myHandler.obtainMessage(0, (Object) "Please wait");	 
        vm.myHandler.sendMessage(msgPb);

		Bundle messageBundle = new Bundle();
		messageBundle.putInt("action", ACTION.CANCEL_VOTE.getValue());
        myMessage = vm.myHandler.obtainMessage();	
    
        InputStream input = Network.retrieveStream(url, METHOD.DELETE, null);
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
					//user = rep.getValue(Post.class, 1);
				}
				catch(JsonParseException e)
			    {
			        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
			    }
				
				if (vm.rep.responseCode == 1)
				{
					messageBundle.putInt("error", 2);
					messageBundle.putString("msgError", vm.rep.responseMessage);
				}
			}
		}						
		myMessage.setData(messageBundle);
        vm.myHandler.sendMessage(myMessage);
        
        msgPb = vm.myHandler.obtainMessage(1, (Object) "Success");
        vm.myHandler.sendMessage(msgPb);
	    }
		catch (Exception e) {
	        e.printStackTrace();     
	    }
    } 
}


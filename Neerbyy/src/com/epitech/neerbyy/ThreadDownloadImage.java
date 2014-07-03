package com.epitech.neerbyy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.ls.LSInput;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class ThreadDownloadImage extends Thread {
	
	int pos;
	Post listPost;
	Bitmap bitmap;
	HashMap<String, Object> map;
	ArrayList<HashMap<String, Object>> listItem;
	ArrayList<Bitmap> listAvatar;
	
	ListView listView;
	ViewPost vp;
	
	public ThreadDownloadImage(ViewPost vp_, int pos_, ListView listView_, Post listPost_, ArrayList<HashMap<String, Object>> listItem_, HashMap<String, Object> map_) {
        super();
        pos = pos_;
        listView = listView_;
        listPost = listPost_;
        listItem = listItem_;
        map = map_;
        vp = vp_;
    }
	
	public ThreadDownloadImage(ListView listView_, Post listPost_, ViewPost vp_) {
        super();
        listView = listView_;
        listPost = listPost_;
        vp = vp_;
	}
	
	public void run() {	 
		 
		Message myMessage;
		//for (int i = 0; i < listPost.list.length; i++) {
	     		
		//Log.w("MAP", listPost.list[pos].user.avatar_thumb);
		if (listPost.list[pos].user == null) {
			map.put("username", "Unknown :");
			map.put("content", listPost.list[pos].content);
			map.put("avatar", String.valueOf(R.drawable.avatar));
			return;
		}
		
	     		map.put("username", listPost.list[pos].user.username + " :");
		        map.put("content", listPost.list[pos].content);
		        //map.put("avatar", String.valueOf(R.drawable.avatar));
	     		
	     		
			 	//URL urll = new URL(listPost.list[i].user.avatar_thumb);
	     		//InputStream in2 = (InputStream) urll.openConnection().getInputStream();
				//bitmap = BitmapFactory.decodeStream(in2);
	     		//bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
	     		
	     		//listAvatar.add(bitmap);   	
		//}
		 
		URL pictureURL = null;
     	try {
     			pictureURL = new URL(listPost.list[pos].user.avatar_thumb);
     		}
     	catch (MalformedURLException e){
     		e.printStackTrace();
     	}
     	try {
		 	//URL urll = new URL(listPost.list[i].user.avatar_thumb);
     		//InputStream in2 = (InputStream) urll.openConnection().getInputStream();
			//bitmap = BitmapFactory.decodeStream(in2);
     		bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
     	} 
     	catch (IOException e) {
     		e.printStackTrace();
     	}
     	map.put("avatar",bitmap);
     	
     	
	    //listItem.add(pos, map);
	    
	    
	    Bundle messageBundle = new Bundle();
    	messageBundle.putInt("action", ACTION.UPDATE_AVATAR.getValue());
		//messageBundle.putInt("pos", i);
        myMessage = vp.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        vp.myHandler.sendMessage(myMessage);
	}
}

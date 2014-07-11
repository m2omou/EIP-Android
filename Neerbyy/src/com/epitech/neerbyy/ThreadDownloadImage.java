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
	
	int mode;                   // 0 = avatar listview  / 1 bitmap editInfoUser / 2 ViewFeed / 3 view memory
	
	EditInfoUser eiu;
	int pos;
	Post listPost;
	Bitmap bitmap;
	HashMap<String, Object> map;
	ArrayList<HashMap<String, Object>> listItem;
	ArrayList<Bitmap> listAvatar;
	
	ListView listView;
	ViewPost vp;
	ViewFeed vf;
	ViewMemory vm;
	MapView mv;
	int indiceMarker;
	Message myMessage, msgPb;
	
	public ThreadDownloadImage(ViewPost vp_, int pos_, ListView listView_, Post listPost_, ArrayList<HashMap<String, Object>> listItem_, HashMap<String, Object> map_) {
        super();
        pos = pos_;
        listView = listView_;
        listPost = listPost_;
        listItem = listItem_;
        map = map_;
        vp = vp_;
        
        mode = 0;
    }
	
	public ThreadDownloadImage(ViewFeed vf_, int pos_, ListView listView_, Post listPost_, ArrayList<HashMap<String, Object>> listItem_, HashMap<String, Object> map_) {
        super();
        pos = pos_;
        listView = listView_;
        listPost = listPost_;
        listItem = listItem_;
        map = map_;
        vf = vf_;
        
        mode = 2;
    }
	
	public ThreadDownloadImage(ListView listView_, Post listPost_, ViewPost vp_) {
        super();
        listView = listView_;
        listPost = listPost_;
        vp = vp_;
        
        mode = 0;
	}
	
	public ThreadDownloadImage(EditInfoUser eiu_) {
		super();
		eiu = eiu_;
		
		mode = 1;
	}
	
	public ThreadDownloadImage(ViewMemory vm_) {
	
		vm = vm_;
		
		mode = 3;
	}

	public ThreadDownloadImage(MapView mv_, int i) {
		mv = mv_;
		indiceMarker = i;
	
		mode = 4;
	}

	public void run() {	 
		
		switch (mode){
		case 0:
			downloadMode0();
			break;
		case 1:
			downloadMode1();
			break;
		case 2:
			downloadMode2();
			break;
		case 3:
			downloadMode3();
			break;
		case 4:
			downloadMode4();
			break;
		}	
	}
	
	private void downloadMode0() {
		//for (int i = 0; i < listPost.list.length; i++) {
	     		
		Log.w("DATE", "jai " + listPost.list[pos].created_at);
		if (listPost.list[pos].user == null) {
			map.put("username", "Unknown user :");
			map.put("content", listPost.list[pos].content);
			map.put("avatar", String.valueOf(R.drawable.avatar));
			map.put("date", "\n" + formatDate(listPost.list[pos].created_at) + ", " + formatHour(listPost.list[pos].created_at));
		}
		else
		{
	     	map.put("username", listPost.list[pos].user.username + " :");
	        map.put("content", listPost.list[pos].content);
			map.put("date", "\n" + formatDate(listPost.list[pos].created_at) + ", " + formatHour(listPost.list[pos].created_at));
		}
		URL pictureURL = null;
     	try {
     			pictureURL = new URL(listPost.list[pos].user.avatar_thumb);
     		}
     	catch (MalformedURLException e){
     		e.printStackTrace();
     	}
     	try {
     		bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
     	} 
     	catch (IOException e) {
     		e.printStackTrace();
     	}
     	//map.put("avatar", bitmap);
     	map.put("avatar", CreateCircleBitmap.getRoundedCornerBitmap(bitmap, 100));
     	
	    Bundle messageBundle = new Bundle();
    	messageBundle.putInt("action", ACTION.UPDATE_AVATAR.getValue());
        myMessage = vp.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        vp.myHandler.sendMessage(myMessage);
	}
	
	private void downloadMode1() {
		
		URL pictureURL = null;
     	try {
     			pictureURL = new URL(Network.USER.avatar);
     			//pictureURL = new URL("http://api.neerbyy.com/uploads/user/avatar/21/thumb_user_avatar_.png");
     		}
     	catch (MalformedURLException e){
     		e.printStackTrace();
     	}
     	try {
     		bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
     	} 
     	catch (IOException e) {
     		e.printStackTrace();
     	}
		eiu.bitmap = bitmap;	
	    Bundle messageBundle = new Bundle();
    	messageBundle.putInt("action", ACTION.UPDATE_IMG_INFO_USER.getValue());
        myMessage = eiu.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        eiu.myHandler.sendMessage(myMessage);
        
        msgPb = eiu.myHandler.obtainMessage(1, (Object) "Success");
        eiu.myHandler.sendMessage(msgPb);
	}
	
	private void downloadMode2() {
		//for (int i = 0; i < listPost.list.length; i++) {
	     		
		//Log.w("MAP", listPost.list[pos].user.avatar_thumb);
		if (listPost.list[pos].user == null) {
			map.put("username", "Unknown :");
			map.put("content", listPost.list[pos].content);
			map.put("avatar", String.valueOf(R.drawable.avatar));
			map.put("date", "\n" + listPost.list[pos].created_at);
		}
		else
		{
	     	map.put("username", listPost.list[pos].user.username + " :");
		    map.put("content", listPost.list[pos].content);
		    map.put("date", "\n" + listPost.list[pos].created_at);
		    //map.put("avatar", String.valueOf(R.drawable.avatar));
		}		
	     		
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
        myMessage = vf.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        vf.myHandler.sendMessage(myMessage);
	}
	
	private void downloadMode3() {
		URL pictureURL = null;
     	try {
     			pictureURL = new URL(vm.memory.url);
     		}
     	catch (MalformedURLException e){
     		e.printStackTrace();
     	}
     	try {
     		bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
     	} 
     	catch (IOException e) {
     		e.printStackTrace();
     	}
     	
        vm.imgPlace = bitmap;
 
	    Bundle messageBundle = new Bundle();
    	messageBundle.putInt("action", ACTION.UPDATE_IMG_MEMORY.getValue());
        myMessage = vm.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        vm.myHandler.sendMessage(myMessage);
	}
	
	private void downloadMode4() {
		URL pictureURL = null;
     	try {
     			pictureURL = new URL(mv.places.list[indiceMarker].icon);
     		}
     	catch (MalformedURLException e){
     		e.printStackTrace();
     	}
     	try {
     		bitmap = BitmapFactory.decodeStream(pictureURL.openStream());
     	} 
     	catch (IOException e) {
     		e.printStackTrace();
     	}
     	
     	mv.places.list[indiceMarker].bitmap = bitmap;
 
	    Bundle messageBundle = new Bundle();
    	messageBundle.putInt("action", ACTION.UPDATE_ICON_MARKER.getValue());
    	messageBundle.putInt("indicePost", indiceMarker);
        myMessage = mv.myHandler.obtainMessage();
        myMessage.setData(messageBundle);
        mv.myHandler.sendMessage(myMessage);
	}
	
	public String formatDate(String date) {
		String newDate;
		
		newDate = date.substring(0, 10);
		return newDate;
	}
	
	public String formatHour(String date) {
		String newHour;
		
		newHour = date.substring(12, 16);
		return newHour;
	}
	
}

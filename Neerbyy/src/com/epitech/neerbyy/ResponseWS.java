package com.epitech.neerbyy;

import java.lang.reflect.Type;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

//{"resposeCode":0 ou 1 si error, "responseMessage":"... success ou error", "result":{...classic}}

public class ResponseWS {
	@SerializedName("responseCode")
	public int responseCode;
	@SerializedName("responseMessage")
	public String responseMessage;
	@SerializedName("result")
	public Object result;
	
	public <T> T getValue(Class<T> obj)
	{
		try {
			Gson gson = new Gson();
			if (responseCode != 1 || obj.getName().contains("com.epitech.neerbyy.User"))
				{
				String gg = "";
					if (obj.getName().contains("com.epitech.neerbyy.User"))
							{
								gg = gson.toJson(result);
								gg = gg.substring(8,gg.length() - 1);
								//gg = gg.substring(0, gg.length() - 1);
								Log.w("TRANSFORM", "NEW USER =  " + gg);
								Log.w("RECUS OBJ", "receiveOBJ vec code = " + this.responseCode + " nameClass " + obj.getName() + "::::::::::::::::::::::::: " + gson.toJson(result));
								return gson.fromJson(gg, obj);
							}
					else
						return gson.fromJson(gson.toJson(result), obj);
					
				}
			}
		catch(JsonParseException e)
		{
			System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
		}
		if (obj.getName() == "com.epitech.neerbyy.User")
			responseMessage = "Invalid email or password";
		return null;
	}
	
	public <T> List<T> getTabValue(Class<T> obj)
	{
		try {
			Gson gson = new Gson();
			if (responseCode != 1 )
				{
					Log.w("RECUS OBJ", "receiveOBJ =  ::::::::::::::::::::::::: " + gson.toJson(result));
				
					Type collectionType = new TypeToken<List<T>>(){}.getType();
					List<T> lcs = (List<T>)	new Gson().fromJson(gson.toJson(result) , collectionType);
					return lcs;
			
					
			//		T[] mcArray = gson.fromJson(gson.toJson(result), T[].class);
			//		return mcArray;
					
					//return gson.fromJson(gson.toJson(result), collectionType);
				}
			}
		catch(JsonParseException e)
		{
			System.out.println("Exception in synthaxe Json :: "+e.toString());
		}		
		return null;
	}
}


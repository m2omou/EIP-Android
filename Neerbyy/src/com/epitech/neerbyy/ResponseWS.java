package com.epitech.neerbyy;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

//{"resposeCode":0 ou 1 si error, "responseMessage":"... success ou error", "result":{...classic}}

public class ResponseWS {
	@SerializedName("resposeCode")
	public int responseCode;
	@SerializedName("responseMessage")
	public String responseMessage;
	@SerializedName("result")
	public Object result;
	
	public <T> T getValue(Class<T> obj)
	{
		try {
			Gson gson = new Gson();
			if (responseCode != 1 )
				{
					//Log.w("RECUS OBJ", "receiveOBJ =  ::::::::::::::::::::::::: " + gson.toJson(result));
					return gson.fromJson(gson.toJson(result), obj);
				}
			}
		catch(JsonParseException e)
		{
			System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
		}		
		return null;
	}
}


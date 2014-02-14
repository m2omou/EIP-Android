package com.epitech.neerbyy;

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
		Gson gson = new Gson();
		if (responseCode != 1 )
			return gson.fromJson(responseMessage, obj);
		return null;
	}
}

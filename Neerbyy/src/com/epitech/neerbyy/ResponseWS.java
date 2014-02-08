package com.epitech.neerbyy;

import com.google.gson.annotations.SerializedName;

//{"resposeCode":0 ou 1 si error, "responseMessage":"... success ou error", "result":{...classic}}

public class ResponseWS {
	@SerializedName("resposeCode")
	public int responseCode;
	@SerializedName("responseMessage")
	public String responseMessage;
	@SerializedName("result")
	public String result;
}

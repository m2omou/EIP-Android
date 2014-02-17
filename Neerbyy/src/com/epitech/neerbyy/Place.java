package com.epitech.neerbyy;

import com.google.gson.annotations.SerializedName;

public class Place {
	@SerializedName("id")
	public String id;
	@SerializedName("longitude")
	public double lon;
	@SerializedName("latitude")
	public double lat;
	@SerializedName("name")
	public String name;
	@SerializedName("postcode")
	public int cp;
	@SerializedName("city")
	public String city;
	@SerializedName("address")
	public String address;
	@SerializedName("country")
	public String country;
	@SerializedName("icon")
	public String icon;
}

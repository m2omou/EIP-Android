package com.epitech.neerbyy;

import java.io.Serializable;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.annotations.SerializedName;

public class Place implements Serializable {

	private static final long serialVersionUID = 3112717925409026343L;
	@SerializedName("places") 
    public PlaceInfo[] list;
    
    public class PlaceInfo implements Serializable{
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
    	public Marker marker;
    }
}


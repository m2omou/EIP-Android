package com.epitech.neerbyy;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent a list of users register in Neerbyy.
 * This class is automatically instanced by the Json library (GSON)
 * @author Seb
 */
public class Users implements Serializable{
	private static final long serialVersionUID = 2546416138675349316L;

	@SerializedName("users")
	public User[] list;
}




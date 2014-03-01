package com.epitech.neerbyy;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Post implements Serializable {
	private static final long serialVersionUID = -7494458459431032428L;

	@SerializedName("publications") 
    public PostInfos[] list;
    
	public class PostInfos {
    	@SerializedName("id")
    	public int id;
    	@SerializedName("user_id")
    	public int user_id;
    	@SerializedName("title")
    	public String title; 
    	@SerializedName("content")
    	public String content;
    	@SerializedName("create_at")
    	public String create_at;
    	@SerializedName("updated_at")
    	public String updated_at;
    	@SerializedName("file")
    	public FileInfos files;
    	
    	private class FileInfos{
    		@SerializedName("url")
        	public String url;
    	}
    }
}

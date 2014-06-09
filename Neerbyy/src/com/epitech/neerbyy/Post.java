package com.epitech.neerbyy;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/** This class represent the list of post send by the WebService.
 * Each of this post are associate to the same place.
 * @see Place
 * @author Seb
 */
public class Post implements Serializable {
	private static final long serialVersionUID = -7494458459431032428L;

	@SerializedName("publications") 
    public PostInfos[] list;
    
	/** This class represent the data of one post 
	 * @author Seb
	 */
	public class PostInfos implements Serializable {
    	@SerializedName("id")
    	public int id;
    	@SerializedName("user_id")
    	public int user_id;
    	@SerializedName("like")
    	public int nbLike;
    	@SerializedName("dislike")
    	public int nbDislike;
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
    	/**
    	 * FileInfos represent data for an upload Media
    	 * @author Seb
    	 *
    	 */
    	private class FileInfos{
    		@SerializedName("url")
        	public String url;
    	}
    }
}

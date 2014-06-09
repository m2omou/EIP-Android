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
    	@SerializedName("content")
    	public String content;
    	@SerializedName("create_at")
    	public String create_at;
    	@SerializedName("updated_at")
    	public String updated_at;
    	@SerializedName("longitude")
    	public double longitude;
    	@SerializedName("latitude")
    	public double latitude;
    	@SerializedName("type")
    	public int type;
    	@SerializedName("url")
    	public String url;
    	@SerializedName("thumb_url")
    	public String thumb_url;
    	@SerializedName("comments")
    	public int comments;
    	@SerializedName("upvotes")
    	public int upvotes;
    	@SerializedName("downvotes")
    	public int downvotes;
    	@SerializedName("user")
    	public User user;                 //  or  InfoUserPost
    	@SerializedName("vote")
    	public Votes vote;                //  or  InfoVotePost
    	@SerializedName("place")
    	public Place place;               //  or  InfoPlacePost
    	
    	private class InfoUserPost {
    		@SerializedName("id")
        	public int id;
        	@SerializedName("username")
        	public String username;
        	@SerializedName("avatar")
        	public String avatar;
        	@SerializedName("avatar_thumb")
        	public String avatar_thumb;
    	}
    	private class InfoPlacePost {
    		@SerializedName("id")
        	public String id;
        	@SerializedName("name")
        	public String name;
    	}
    	private class InfoVotePost {
    		@SerializedName("id")
        	public String id;
        	@SerializedName("value")
        	public boolean value;
    	}
    }
}

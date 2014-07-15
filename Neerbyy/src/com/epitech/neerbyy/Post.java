package com.epitech.neerbyy;

import java.io.Serializable;

import com.epitech.neerbyy.Votes.VoteInfo;
import com.google.gson.annotations.SerializedName;

/** This class represent the list of post send by the WebService.
 * Each post is associate to the same place.
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
    	@SerializedName("created_at")
    	public String created_at;
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
    	public VoteInfo vote;                //  or  InfoVotePost
    	@SerializedName("place")
    	public InfoPlacePost place;               //  or  Place
    	
    	/** This class represent the data of the author of this post 
    	 * @author Seb
    	 */
    	public class InfoUserPost implements Serializable{
    		@SerializedName("id")
        	public int id;
        	@SerializedName("username")
        	public String username;
        	@SerializedName("avatar")
        	public String avatar;
        	@SerializedName("avatar_thumb")
        	public String avatar_thumb;
    	}
    	/** This class represent the data of the place associate to the publication 
    	 * @author Seb
    	 */
    	public class InfoPlacePost implements Serializable {
    		@SerializedName("id")
        	public String id;
        	@SerializedName("name")
        	public String name;
    	}
    	/** This class represent the statue of the current user about he have already vote or not 
    	 * for the current Post 
    	 * @author Seb
    	 * @see Post
    	 */
    	public class InfoVotePost implements Serializable{
    		@SerializedName("id")
        	public String id;
        	@SerializedName("value")
        	public boolean value;
    	}
    }
}

package com.epitech.neerbyy;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/** This class represent the list of messages in each conversation.
 * @author Seb
 */
public class Messages implements Serializable {
	private static final long serialVersionUID = -7494458452231032428L;

	@SerializedName("messages") 
    public Message[] list;
	@SerializedName("message") 
    public Message message;
    
	/** This class represent the data of one Message
	 * @author Seb
	 */
	@SuppressWarnings("serial")
	public class Message implements Serializable {
    	@SerializedName("id")
    	public int id;
    	@SerializedName("content")
    	public String content;
    	@SerializedName("create_at")
    	public String create_at;
    	@SerializedName("sender")
    	public InfoUserMessage sender; 
    	
    	public class InfoUserMessage implements Serializable{
    		@SerializedName("id")
        	public int id;
        	@SerializedName("username")
        	public String username;
        	@SerializedName("avatar")
        	public String avatar;
        	@SerializedName("avatar_thumb")
        	public String avatar_thumb;
        	@SerializedName("settings")
        	public Settings settings;
    	}
    	
    	public class Settings implements Serializable{
    		@SerializedName("allow_messages")
        	public boolean allow_messages;
    	}
    }
}

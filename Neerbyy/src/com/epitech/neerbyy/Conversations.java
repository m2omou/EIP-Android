package com.epitech.neerbyy;

import java.io.Serializable;

import com.epitech.neerbyy.Messages.Message;
import com.google.gson.annotations.SerializedName;

/** This class represent a conversation.
 * @author Seb
 */
public class Conversations implements Serializable {
	private static final long serialVersionUID = -7494458457231032428L;

	@SerializedName("conversations") 
    public Conversation[] list;
	@SerializedName("conversation") 
    public Conversation conv;
    
	/** This class represent the data of one Conversation
	 * @author Seb
	 */
	@SuppressWarnings("serial")
	public class Conversation implements Serializable {
    	@SerializedName("id")
    	public int id;
    	@SerializedName("messages")
    	public Message[] messages;
    	@SerializedName("recipient")
    	public InfoUserConversation recipient; 
    	
    	public class InfoUserConversation implements Serializable{
    		@SerializedName("id")
        	public int id;
        	@SerializedName("username")
        	public String username;
        	@SerializedName("avatar")
        	public String avatar;
        	@SerializedName("avatar_thumb")
        	public String avatar_thumb;
    	}
    }
}

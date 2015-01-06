package com.epitech.neerbyy;
import java.io.Serializable;

import com.epitech.neerbyy.Messages.Message.Settings;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent data of one user register in Neerbyy.
 * This class is automatically instanced by the Json library (GSON)
 * @author Seb
 */
public class User implements Serializable{
	private static final long serialVersionUID = 2546606138675349316L;
/**
 * Id of the user in the database
 */
	@SerializedName("id")
	public int id;
	/**
	 * The username of the user
	 */
	@SerializedName("username")
	public String username;
	@SerializedName("firstname")
	public String firstname;
	@SerializedName("lastname")
	public String lastname;
	@SerializedName("email")
	public String mail;
	@SerializedName("avatar")
	public String avatar;
	@SerializedName("auth_token")
	public String token;
	@SerializedName("avatar_thumb")
	public String avatar_thumb;
	
	//   NO MORE USE  ??
	@SerializedName("created_at")	
	public String created_at;
	@SerializedName("updated_at")
	public String updated_at;
	@SerializedName("setting")
	public Settings settings;
	
	/** This class describe the parameters of a user.
	 * @author Seb
	 */
	public class Settings implements Serializable{
		@SerializedName("id")
		public int id;
		@SerializedName("allow_messages")
		public boolean allow_messages;
		@SerializedName("send_notification_for_comments")
		public boolean send_notification_for_comments;
		@SerializedName("send_notification_for_messages")
		public boolean send_notification_for_messages;
	}
}




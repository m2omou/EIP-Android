package com.epitech.neerbyy;
import java.io.Serializable;
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
	public String avatar;   //  Av  class Ava
	
	@SerializedName("created_at")	
	public String created_at;
	@SerializedName("updated_at")
	public String updated_at;
	/**
	 * This token is attributed by the WebService when the User connects to the app.
	 * It should be allow this user to post new data.
	 */
	@SerializedName("auth_token")
	public String token;
	/**
	 * Temporary debug variable
	 */
	@SerializedName("error")
	public String error;
	/**
	 * Temporary debug variable
	 */
	@SerializedName("errors")
	public String[] errors;
	
	@SerializedName("avatar_thumb")
	public String avatar_thumb;

	/**
	 * This internal class represent the image of the user, and the path to image on the
	 * Neerbyy server.
	 * @author Seb
	 *
	 */
	public class Avatar {
		@SerializedName("url")
		public String url;
		@SerializedName("thumb")
		public Thumb thumb;
	}
	
	/**
	 * This internal class represent the path to the Thumb image
	 * on the Neerbyy server
	 * @author Seb
	 *
	 */
	public class Thumb {
		@SerializedName("url")
		public String url;
	}
}

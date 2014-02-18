package com.epitech.neerbyy;
import java.io.Serializable;
import java.net.PasswordAuthentication;

import android.text.method.PasswordTransformationMethod;

import com.google.gson.annotations.SerializedName;

/**
 * Class representant les informations d'un utilisateur enregistrer chez Neerbyy
 * <p>Cette classe est automatiquement instancier par la librairie GSON en vue des informations
 * retour du WS</p>
 * @author Seb
 *
 */
public class User implements Serializable{
	@SerializedName("id")
	public int id;
	@SerializedName("username")
	public String username;
	@SerializedName("firstname")
	public String firstname;
	@SerializedName("lastname")
	public String lastname;
	@SerializedName("email")
	public String mail;
	@SerializedName("avatar")
	public Avatar avatar;
	@SerializedName("created_at")
	public String created_at;
	@SerializedName("updated_at")
	public String updated_at;
	@SerializedName("auth_token")
	public String token;
	@SerializedName("error")
	public String error;
	
	public class Avatar {
		@SerializedName("url")
		public String url;
		@SerializedName("thumb")
		public Thumb thumb;
	}
	
	public class Thumb {
		@SerializedName("url")
		public String url;
	}
}

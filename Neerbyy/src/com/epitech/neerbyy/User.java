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
	@SerializedName("password")
	public String password;
	//@SerializedName("avatar")
	//public String avatar;
	@SerializedName("error")
	public int error;
	@SerializedName("message")
	public String errorMsg;
	@SerializedName("auth_token")
	public String token;
	
	static public double lat = 39.948518;
	static public double lon = 116.3371423;
	static public float alt;
	
	
	
	public User(int ID, String U, String F,String L,String M,String P,String A, int E, String ME)
	{
		super();
		id = ID;
		username = U;
		firstname = F;
		lastname = L;
		mail = M;
		password = P;
		//avatar = A;
		error = E;
		errorMsg = ME;
		token = null;
	}
	
}

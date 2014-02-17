package com.epitech.neerbyy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

/**
 * <b>Network est une classe static permettant d'effectuer des taches réseau.</b>
 *<p>Elle possede une liste des differentes actions reseaux disponible 
 *pour les vues:
 *<ul>
 *<li>GET_UNTRUC : operation quelconque (pour debugging)
 *<li>GET_USER : s'apprete à recevoir les infos d'un utilisateur par le WS
 *<li>LOGIN : s'apprete à recevoir la confirmation du Login par le WS
 *<li>CREATE_ACCOUNT : s'apprete à recevoir la confirmation de la création du compte utilisateur par le WS
 *<li>EDIT_USER : s'apprete à recevoir les information d'un utilisateur par le WS en vue de les modifier
 *</ul>
 *</p>
 *@see User
 */

public class Network {

	static final public int GET_UNTRUC = 0;
	static final public int GET_USER = 1;
	static final public int GET_INFO = 2;
	static final public int LOGIN = 3;
	static final public int CREATE_ACCOUNT = 4;
	static final public int EDIT_USER = 5;
	static final public int GET_PLACES = 6;
	
	static final public String URL = "http://neerbyy.com:";  //  keep : !?
	static final public int PORT = 80;
	
	static public User USER = null;
	
	/**
	 * 
	 * @param url
	 * L'url de la requete http
	 * @param mode
	 * Le type de la requete : 0 GET, 1 POST, 2 PUT
	 * @param nameValuePairs
	 * Liste des variables à envoyer pour les requetes de type POST et PUT
	 * @return
	 * L'InputStream contenant la réponse éventuelle du WS
	 * @see InputStream
	 * @throws UnsupportedEncodingException
	 * Permet de gerer les exception non gérer par le bloc Catch habituel
	 */
	static public InputStream retrieveStream(final String url, int mode, List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException { // mode 0 = getMethode  1 = postMethode 2 = putMethode with key pair data value
		
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet getRequestGet = new HttpGet(url);
	        HttpPost getRequestPost = new HttpPost(url);
	        HttpPut getRequestPut = new HttpPut(url);
	        
	        try {
	        	if (nameValuePairs != null)
	        	{        		
		        	getRequestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        	getRequestPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        	}
		        if (USER != null)
		        {	
		        	getRequestPost.setHeader("Authorization", "Token token="+ USER.token);
		        	getRequestGet.setHeader("Authorization", "Token token="+ USER.token);
		        	getRequestPut.setHeader("Authorization", "Token token=" + USER.token);  
		        }
	        	        	
	        	HttpResponse getResponse;
	        	if (mode == 0)
	        		Log.w("Network ", "JENVOIE : " + getRequestGet.getMethod() + " -- " + getRequestGet.getRequestLine());
	        	else if(mode == 2)
	        		Log.w("Network ", "JENVOIE : " + getRequestPost.getMethod() + " -- " + getRequestPost.getRequestLine());
	        	
	        	if (mode == 0)
	        		getResponse = client.execute(getRequestGet);
	        	else if(mode == 1)
	        		getResponse = client.execute(getRequestPost);
	        	else
	        		getResponse = client.execute(getRequestPut);
	           
	        	final int statusCode = getResponse.getStatusLine().getStatusCode();
	            if (statusCode < 200 || statusCode > 226) {  // before HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED
	            	Log.w("Network ", "Error " + statusCode + " for URL " + url);  // before = getClass().getSimpleName()
	            	if (statusCode != 422)  //  Enlever ca plus tard !!!  erreur webservice connection
	            		return null;
	            }
	       
	           if (mode == 2)
	        	   return null;
	           HttpEntity getResponseEntity = getResponse.getEntity();
	           return getResponseEntity.getContent();	     
	        }
	        catch (IOException e) {
	        	if (mode == 0)
	        		getRequestGet.abort();
	        	else if(mode == 1)
	        		getRequestPost.abort();
	        	else
	        		getRequestPut.abort();
	           Log.w("Network ", "Exeption Error for URL " + url, e);
	        }
	        return null;	
	     }
	  
	/**
	 * <p>Affiche le contenu de la réponse retour du WS</p>
	 * @param readerResp
	 * Reader contenant le InputStream de retour du WS
	 * @see Reader
	 * @see InputStream
	 * @see retrieveStream
	 * 
	 */
	public static String checkInputStream(Reader readerResp) throws IOException
	{
		BufferedReader br = new BufferedReader(readerResp);
		String ligne, str2 = "";
		while ((ligne = br.readLine())!=null){
			System.out.println(ligne);
			str2 += ligne + "\n";
		}
		//Log.w("RECUS", "receive =  ::::::::::::::::::::::::: " + str2);
		br.close();
		return str2;
	}
}

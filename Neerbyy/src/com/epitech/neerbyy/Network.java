package com.epitech.neerbyy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * <b>Network is a static a class for performing network tasks.</b>
 *<p>It has a list of different network shares available for views:
 *<ul>
 *<li>GET_UNTRUC : any operation (for debugging)
 *<li>GET_USER : prepares to to receive information from a user by WS
 *<li>LOGIN : prepares to to receive confirmation Login by WS
 *<li>CREATE_ACCOUNT : prepares to receive confirmation of the user account creation by WS
 *<li>EDIT_USER : prepares to receive information from a user by the WS to change
 *<li>GET_PLACES : prepares to receive information from the places around the user
 *<li>RESET_PASSWORD : prepares to receive confirmation about the modification's user password 
 *<li>CREATE_POST : prepares to receive confirmation about the new created post
 *<li>UPDATE_POST : prepares to receive information about list of post from a place
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
	static final public int RESET_PASSWORD = 7;
	static final public int CREATE_POST = 8;
	static final public int UPDATE_POST = 9;
	
	 /**
	  * The URL of the Web Service
     */
	static final public String URL = "http://api.neerbyy.com:";  //  keep : !?
	/**
	  * The default port of the Web Service 
    */
	static final public int PORT = 80;
	
	/**
	  * USER will be set when the user will be logged. It will contain all data
	  *  of this user in a static way for allow access to other views. 
   */
	static public User USER = null;
	
	/**
	 * 
	 * @param url
	 * URL of the HTTP request
	 * @param mode
	 * type of the request : 0 GET, 1 POST, 2 PUT
	 * @param nameValuePairs
	 * List of variables to send to the applications of POST and PUT
	 * @return
	 * The InputStream containing the possible response of the WS
	 * @see InputStream
	 * @throws UnsupportedEncodingException
	 * Allows you to manage the unhandled by the usual exception Catch block
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
		        	Log.w("TOKEN USER", "TOKEN: " + USER.token);
		        	getRequestPost.setHeader("Authorization", "Token =" + USER.token);
		        	getRequestGet.setHeader("Authorization", "Token =" + USER.token);
		        	getRequestPut.setHeader("Authorization", "Token =" + USER.token);
		        	
		        }
	        	        	
	        	HttpResponse getResponse;
	        	if (mode == 0)
	        		Log.w("Network ", "JENVOIE : " + getRequestGet.getMethod() + " -- " + getRequestGet.getRequestLine());
	        	else if(mode == 1)
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
	            	//if (statusCode != 422)  //  Enlever ca plus tard !!!  erreur webservice connection
	            	//	return null;
	            	if (statusCode == 401)   //  pour erreur token
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
	 * <p>Displays the contents of the return response of the WS on the log, and return a String data</p>
	 * @param readerResp
	 * Reader containing the InputStream returned from WS
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
	
	public static Bitmap DownloadImage(String URL)
	{        
	    Bitmap bitmap = null;
	    InputStream in = null;        
	    try {
	        in = OpenHttpConnection(URL);
	        bitmap = BitmapFactory.decodeStream(in);
	        in.close();
	    } catch (IOException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    }
	    return bitmap;                
	}
	
	/**
	 * <p>Creates an asynchronous connection to handle
	 * download certaint graphic items listed such as user avatars for exemple</p>
	 * @param urlString
	 * URL element to download.
	 * @return
	 * The InputStream object containing the data sent by the Web Service.
	 * @see InputStream
	 */
	private static InputStream OpenHttpConnection(String urlString) 
	        throws IOException
	        {
	            InputStream in = null;
	            int response = -1;

	            URL url = new URL(urlString); 
	            URLConnection conn = url.openConnection();

	            if (!(conn instanceof HttpURLConnection))                    
	                throw new IOException("Not an HTTP connection");
	            try{
	                HttpsURLConnection httpConn = (HttpsURLConnection) conn;
	                httpConn.setAllowUserInteraction(false);
	                httpConn.setInstanceFollowRedirects(true);
	                httpConn.setRequestMethod("GET");
	                httpConn.connect(); 

	                response = httpConn.getResponseCode();                 
	                if (response == HttpURLConnection.HTTP_OK) {
	                    in = httpConn.getInputStream();                                 
	                }                     
	            }
	            catch (Exception ex)
	            {
	                throw new IOException("Error connecting");            
	            }
	            return in;     
	        }
}

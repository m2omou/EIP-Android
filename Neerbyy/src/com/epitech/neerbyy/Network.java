package com.epitech.neerbyy;

import java.io.BufferedReader;
import java.io.File;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

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
 *<li>GET_VOTES : Obtain the list of votes for one publication
 *<li>SEND_VOTE : Send a vote for an  existing publication
 *<li>FALLOW_PLACE : Allow to follow the current place in the user view
 *<li>GET_FEED : Obtain the list of the user's following places.
 *<li>CANCEL_VOTE : Cancel an existing vote
 *<li>REPORT_PUB : Allow to report a publication
 *<li>REPORT_COM : Allow to report a commentary
 *<li>GET_CONV : Obtain the list of conversation for the current user. 
 *<li>GET_MESSAGES : Obtain the list of messages for a given conversation.
 *<li>POST_MESSAGE : Allow to send a new message in a given conversation.
 *</ul>
 *</p>
 *@see User
 *@see Messages
 *@see Conversations
 *@see Post
 *@see Place
 *@see Votes
 */

public class Network {

	static public enum ACTION {
		GET_UNTRUC(0),
		GET_USER(1),
		GET_INFO(2),
		LOGIN(3),
		CREATE_ACCOUNT(4),
		EDIT_USER(5),
		GET_PLACES(6),
		RESET_PASSWORD(7),
		CREATE_POST(8),
		UPDATE_POST(9),
		CREATE_COMM(10),
		UPDATE_COMM(11),
		GET_VOTES(12),
		SEND_VOTE(13),
		FALLOW_PLACE(14),
		GET_FEED(15),
		CANCEL_VOTE(16),
		REPORT_PUB(17),
		REPORT_COM(18),
		GET_CONV(19),
		GET_MESSAGES(20),
		POST_MESSAGE(21),
		UPDATE_AVATAR(22),
		UPDATE_IMG_INFO_USER(23),
		DECO(24),
		DELETE_USER(25),
		DELETE_COMM(26),
		DELETE_PUB(27),
		UNFALLOW(28),
		GET_SEARCH_PLACE(29),
		UPDATE_IMG_MEMORY(30),
		SEARCH_USER(31),
		UPDATE_ICON_MARKER(32),
		GET_INFO_USER(33);
		
		private final int value;
		
		private ACTION(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	static public enum METHOD {
		GET,
		POST,
		PUT,
		DELETE,
		UPLOAD_POST,
		UPLOAD_PUT,
	}
	 /**
	  * Define the connection settings
	  */
	static public enum PARAMS {
		CONNECTION_TIME_OUT(8000),   // def 0 for not taking
		SOCKET_TIME_OUT(8000);
	
		private final int value;
		
		private PARAMS(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	
	 /**
	  * The URL of the Web Service
	  */
	static final public String URL = "http://dev.neerbyy.com:";
	
	/**
	  * The default port of the Web Service 
	 */
	static final public int PORT = 80;
	
	/**
	  * USER will be set when the user will be logged. It will contain all data
	  *  of this user in a static way for allow access to other views. 
	 */
	
	static public User USER = null;
	
	//static MultiThreadedHttpConnectionManager connman = new MultiThreadedHttpConnectionManager();
	
	static DefaultHttpClient clientTmp = new DefaultHttpClient();
	static ClientConnectionManager cm = clientTmp.getConnectionManager();	
	static final HttpParams httpParameters = clientTmp.getParams();
	
	static DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, cm.getSchemeRegistry()), httpParameters);
	
	static boolean isInit = false; 
    static int statusCode = 0;
    static HttpResponse getResponse = null;
	
    static private void init() {
    	if (isInit)
			return;
		HttpConnectionParams.setConnectionTimeout(httpParameters, PARAMS.CONNECTION_TIME_OUT.getValue());
		HttpConnectionParams.setSoTimeout(httpParameters, PARAMS.SOCKET_TIME_OUT.getValue());
		isInit = true;
	}
	
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
    
	static public InputStream retrieveStream(final String url, METHOD mode, List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException { 
		
		HttpGet getRequestGet = new HttpGet(url);
	    HttpPost getRequestPost = new HttpPost(url);
	    HttpPut getRequestPut = new HttpPut(url);
	    HttpDelete getRequestDelete = new HttpDelete(url);
	     
	    //DefaultHttpClient client = new DefaultHttpClient();	    
	    //ClientConnectionManager cm = client.getConnectionManager();
	    
	    //String html = EntityUtils.toString(rp.getEntity() /*, Encoding */);
	    //EntityUtils.consume(rp.getEntity());
	    
	    if (!isInit)
			init();
	    
	    try {
	        if (USER != null)
	        	Log.w("TOKEN USER", "TOKEN: " + USER.token);
	        switch(mode) {
	        	case GET: {
	        		Log.w("Network ", "SENDING : " + getRequestGet.getMethod() + " -- " + getRequestGet.getRequestLine());	
	        		if (USER != null)
	    	        	getRequestGet.setHeader("Authorization", "Token token=" + USER.token);
	        		getResponse = client.execute(getRequestGet);
	        		break;
	        	}
	        	case POST: {
	        		Log.w("Network ", "SENDING : " + getRequestPost.getMethod() + " -- " + getRequestPost.getRequestLine());
	        		if (nameValuePairs != null)
	    	    		getRequestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        		if (USER != null)
	    	        	getRequestPost.setHeader("Authorization", "Token token=" + USER.token);
	        		getResponse = client.execute(getRequestPost);
	        		break;
	        	}
	        	case PUT: {
	        		Log.w("Network ", "SENDING : " + getRequestPut.getMethod() + " -- " + getRequestPut.getRequestLine());
	        		if (nameValuePairs != null)
	    	    		getRequestPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        		if (USER != null)
	    	        	getRequestPut.setHeader("Authorization", "Token token=" + USER.token);
	        		getResponse = client.execute(getRequestPut);
	        		break;
	        	}
	        	case DELETE: {
	        		Log.w("Network ", "SENDING : " + getRequestDelete.getMethod() + " -- " + getRequestDelete.getRequestLine());
	        		/*if (nameValuePairs != null)
	    	    		getRequestDelete.setEntity(new UrlEncodedFormEntity(nameValuePairs));*/
	        		if (USER != null)
	    	        	getRequestDelete.setHeader("Authorization", "Token token=" + USER.token);
	        		getResponse = client.execute(getRequestDelete);
	        		break;
	        	}        	
	        	case UPLOAD_POST: {
	        		Log.w("Network ", "SENDING : " + getRequestPost.getMethod() + " -- " + getRequestPost.getRequestLine());
	        		if (nameValuePairs != null) {
	    	    		//getRequestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        			
	        			MultipartEntityBuilder builder = MultipartEntityBuilder.create();        

	        			/* example for setting a HttpMultipartMode */
	        			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	        			/* example for adding an image part */
	        			//FileBody fileBody = new FileBody(image); //image should be a String
	        			//builder.addPart("my_file", fileBody); 
	        			//and so on

	    	            for(int index=0; index < nameValuePairs.size(); index++) {
	    	                if(nameValuePairs.get(index).getName().equalsIgnoreCase("publication[file]")) {
	    	                    // If the key equals to "image", we use FileBody to transfer the data 	
	    	                	Log.w("IMGGGG", nameValuePairs.get(index).getValue());
	    	                	builder.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
	    	                	//entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
	    	                } else {
	    	                    // Normal string data
	    	                	
	    	                	builder.addTextBody(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
	    	                	//builder.addTextBody("toto", "tata");

	    	                	//entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
	    	                }
	    	            }
	    	            HttpEntity entity = builder.build();  	            
	    	            getRequestPost.setEntity(entity);   
	        		}
	        		if (USER != null)
	    	        	getRequestPost.setHeader("Authorization", "Token token=" + USER.token);	
	        		getResponse = client.execute(getRequestPost);
	        		break;
	        	}
	        	
	        	case UPLOAD_PUT: {
	        		Log.w("Network ", "SENDING UPLOAD PUT : " + getRequestPut.getMethod() + " -- " + getRequestPut.getRequestLine());
	        		if (nameValuePairs != null) {
	    	    		//getRequestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        			
	        			MultipartEntityBuilder builder = MultipartEntityBuilder.create();        

	        			/* example for setting a HttpMultipartMode */
	        			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	        			/* example for adding an image part */
	        			//FileBody fileBody = new FileBody(image); //image should be a String
	        			//builder.addPart("my_file", fileBody); 
	        			//and so on

	    	            for(int index=0; index < nameValuePairs.size(); index++) {
	    	                if(nameValuePairs.get(index).getName().equalsIgnoreCase("user[avatar]")) {
	    	                    // If the key equals to "image", we use FileBody to transfer the data 	
	    	                	Log.w("IMGGGG", nameValuePairs.get(index).getValue());
	    	                	builder.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
	    	                	//entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
	    	                } else {
	    	                    // Normal string data
	    	                	
	    	                	builder.addTextBody(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
	    	                	//builder.addTextBody("toto", "tata");

	    	                	//entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
	    	                }
	    	            }
	    	            HttpEntity entity = builder.build();  	            
	    	            getRequestPut.setEntity(entity);		
	        		}
	        		if (USER != null)
	    	        	getRequestPut.setHeader("Authorization", "Token token=" + USER.token);	
	        		getResponse = client.execute(getRequestPut);
	        		break;
	        	}
	        }        
	        statusCode = getResponse.getStatusLine().getStatusCode();
	        
	        //before HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED
	        if (statusCode < 200 || statusCode > 226) {
	        	Log.w("Network ", "Error " + statusCode + " for URL " + url);  // before = getClass().getSimpleName()
	        	//if (statusCode != 422 || statusCode == 401)  //erreur webservice et erreur token
	        		//return null;
	        	//return null ?
	        }            
	        if (getResponse != null)
	        	return getResponse.getEntity().getContent();
	    }
	    catch (IOException e) {
	       	getRequestGet.abort();
	       	getRequestPost.abort();
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
			//System.out.println(ligne);
			str2 += ligne + "\n";
		}
		br.close();
		Log.w("RECEIVE", str2);
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
	
	public static InputStream OpenHttpConnection(String urlString) 
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

//-------------A  TEST BIEN
/*
 * new Thread(new Runnable() {
        public void run() {
            final Bitmap bitmap = loadImageFromNetwork("http://example.com/image.png");
            mImageView.post(new Runnable() {
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }
    }).start();
    */

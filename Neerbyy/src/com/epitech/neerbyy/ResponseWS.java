package com.epitech.neerbyy;

import java.io.Serializable;

import android.util.Log;
import android.widget.Toast;

import com.epitech.neerbyy.Categorie.CategorieInfos;
import com.epitech.neerbyy.Conversations.Conversation;
import com.epitech.neerbyy.Users;
import com.epitech.neerbyy.User;
import com.epitech.neerbyy.Messages.Message;
import com.epitech.neerbyy.Votes.VoteInfo;
import com.google.gson.annotations.SerializedName;
//import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * ResponseWs represent a generic object type that return the WebService for each request.
 *	
 * @author Seb
 *
 */
public class ResponseWS{
/**
 * responseCode is the error code number. 0 for no error, or 1.	 
 */
	@SerializedName("responseCode")
	public int responseCode;
	/**
	 * responseMessage is the string message error than the WebService return. It set to null on no error. 
	 */
	@SerializedName("responseMessage")
	public String responseMessage;
	/**
	 * result is a generic type class who depend the type of the client request.
	 * It allow the Gson library to instantiate the needed class.
	 */
	@SerializedName("result")
	//public Object result;
	public Result result;
	
	public class Result{
		
		@SerializedName("user")
		public User user;
		
		@SerializedName("users")
		public User[] users;
		
		@SerializedName("place")
		public Place.PlaceInfo place;
		
		@SerializedName("places")
		public Place.PlaceInfo[] places;
		
		@SerializedName("categories")
		public CategorieInfos[] categories;
		
		@SerializedName("publications")
		public Post.PostInfos[] postes;
	
		@SerializedName("comments")
		public Commentary.CommInfos[] comms;
		
		@SerializedName("votes")
		public VoteInfo[] votes;
		
		@SerializedName("conversations")
		//public Conversations conversations;
		public Conversation[] conversations;
		
		@SerializedName("conversation")
		//public Conversations conversations;
		public Conversation conversation;
		
		@SerializedName("messages")
		//public Conversations conversations;
		public Message[] messages;
		
		@SerializedName("settings")
		public User.Settings settings;
		
		//@SerializedName("error")
		//public User user;
	}
	
	/**
	 * Return the typed instanced object
	 * @param obj
	 * The type of Class need to be instanced
	 * @param mode
	 * mode is use to a temporary debugging test
	 * @return
	 * the typed instanced object
	 */
	public <T> T getValue(Class<T> obj)
	{
		try {
			//Gson gson = new Gson();
			Log.w("DETECT", "DEBUT DETECT");

					if (responseCode != 1)
							{
								if (obj == User.class) {
									Log.w("DETECT", "DETECT USER");
									return (T) result.user;
								}
								else if (obj == Users.class) {
									Log.w("DETECT", "DETECT USERS");
									Users users = new Users();
									users.list = result.users;
									return (T) users;
								}
								else if (obj == Place.class) {
									Log.w("DETECT", "DETECT PLACE");
									Place place = new Place();
									place.list = result.places;
									//return (T) result.places;
									return (T) place;
								}
								else if (obj == Place.PlaceInfo.class) {
									Log.w("DETECT", "DETECT ONE PLACE");
									return (T) result.place;
								}
								else if (obj == Post.class) {
									Log.w("DETECT", "DETECT POST_UPDATE");
									Post post = new Post();
									post.list = result.postes;
									//return (T) result.places;
									return (T) post;
								}
								else if (obj == Categorie.class) {
									Log.w("DETECT", "DETECT Categorie");
									Categorie cate = new Categorie();
									cate.list = result.categories;
									//return (T) result.places;
									return (T) cate;
								}
								else if (obj == Commentary.class) {
									Log.w("DETECT", "DETECT COMM_UPDATE");								
									Commentary comm = new Commentary();
									comm.list = result.comms;
									return (T) comm;
								}
								else if (obj == Votes.class) {
									Log.w("DETECT", "DETECT GET_VOTES");								
									Votes votes = new Votes();
									votes.list = result.votes;
									return (T) votes;
								}
								else if (obj == Conversations.class) {
									Log.w("DETECT", "DETECT GET_CONV");	
									Conversations conv = new Conversations();
									conv.list = result.conversations;
									return (T) conv;
								}
								else if (obj == Conversation.class) {
									Log.w("DETECT", "DETECT GET_CONV");	
									Conversations conv = new Conversations();
									conv.conv = result.conversation;
									return (T) conv.conv;
								}
								else if (obj == Messages.class) {
									Log.w("DETECT", "DETECT GET_MESSAGES");	
									Messages mess = new Messages();
									mess.list = result.messages;
									return (T) mess;   
								}
								else if (obj == User.Settings.class) {
									Log.w("DETECT", "DETECT GET_SETTINGS");	
									return (T) result.settings;
								}
							//return gson.fromJson(gg, obj);						
							}
					else
					{
						//return gson.fromJson(gson.toJson(result), obj);
						return null;
					}
				}
		catch(JsonParseException e)
		{
			Log.w("DETECT", "Exception nb 2 in check_exitrestrepWSResponse");
			//System.out.println("Exception nb 2 in check_exitrestrepWSResponse::"+e.toString());
			return null;
		}	
		return null;
	}
}


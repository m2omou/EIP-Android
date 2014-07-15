package com.epitech.neerbyy;
import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent the Vote object.
 * This class is automatically instanced by the Json library (GSON)
 * @author Seb
 */

public class Votes implements Serializable{
	private static final long serialVersionUID = 2546606138678349316L;

	public VoteInfo[] list;
	
	public class VoteInfo implements Serializable{
		@SerializedName("id")
		public int id;
		@SerializedName("publication_id")
		public int publicationId;
		@SerializedName("user_id")
		public int userId;
		@SerializedName("value")
		public boolean value;	
		@SerializedName("created_at")	
		public String created_at;
		@SerializedName("updated_at")
		public String updated_at;
	}
}

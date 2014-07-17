package com.epitech.neerbyy;

import java.io.Serializable;

import com.epitech.neerbyy.Votes.VoteInfo;
import com.google.gson.annotations.SerializedName;

/** This class represent the categorie send by the WebService.
 * @author Seb
 */
public class Categorie implements Serializable {
	private static final long serialVersionUID = -7497458459431032428L;

	@SerializedName("publications") 
    public CategorieInfos[] list;
    
	/** This class represent the data of one categorie 
	 * @author Seb
	 */
	public class CategorieInfos implements Serializable {
    	@SerializedName("id")
    	public String id;
    	@SerializedName("name")
    	public String name;
    }
}

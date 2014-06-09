package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

/**
 * This class allow to check all the user's Input, and check the syntax with a predefine pattern.
 * @author Seb
 */
public class MyRegex {
	
	private static Pattern pattMail = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Za-z]{2,4}$");  //  ptete mettre en min
	//private static Pattern pattMail = Pattern.compile("^[_a-z0-9-]+(\\.[_wwwww-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$");
	private static Pattern pattName = Pattern.compile("^[ A-Za-z0-9]+$");   //  enleverChVerifCreateAccount
	private static Pattern pattLogin = Pattern.compile("^[ A-Za-z0-9]+$");
	private static Pattern pattPassword = Pattern.compile("^[ A-Za-z0-9-_]+$");
	//Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	
	static private Matcher m;
	
	/**
	 * check method will check what is the type of the EditText, and validate or not the input 
	 * in compare with the associate pattern.
	 * @param text
	 * The field text to be checked.
	 * @return
	 * Return true if the text is correct
	 */
	public static boolean check(EditText text)
	{
		switch (text.getInputType())
		{
		case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
			return forMail(text.getText().toString());
		case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME:			
			return forName(text.getText().toString());
		case InputType.TYPE_CLASS_TEXT:
			return forLogin(text.getText().toString());
		case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD:
			return forPassword(text.getText().toString());
		default:
			Log.w("REGEX ", "Error to find InputType " + text.getInputType());
			break;	
		}
		return false;
	}
	
	/**
	 * This method compare if two fields are different or not
	 * @param text
	 * the first field
	 * @param text2
	 * the second field
	 * @return
	 * Return true if fields are different.
	 */
	public static boolean checkIfIdent(EditText text, EditText text2)
	{
		return text.getText().toString().contentEquals(text2.getText().toString());
	}
	
	/**
	 * Check if the mail text is correct
	 */
	private static boolean forMail(String text)
	{
		m = pattMail.matcher(text);
		return m.matches();
		
		//return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();		
	}
	
	/**
	 * Check if the name or firstname or lastname text is correct
	 */
	private static boolean forName(String text)
	{
		m = pattName.matcher(text);
		return m.matches();
	}
	
	/**
	 * Check if the username text is correct
	 */
	private static boolean forLogin(String text)
	{
		m = pattLogin.matcher(text);
		return m.matches();
	}
	
	/**
	 * Check if the password text is correct
	 */
	private static boolean forPassword(String text)
	{
		m = pattPassword.matcher(text);
		return m.matches();
	}
}

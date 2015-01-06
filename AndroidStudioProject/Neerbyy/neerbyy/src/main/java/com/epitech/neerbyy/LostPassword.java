package com.epitech.neerbyy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.epitech.neerbyy.Network.ACTION;
import com.epitech.neerbyy.Network.METHOD;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class describe the view for regenere a new password.
 * The field must be check for error with the MyRegex class
 *@see User
 *@see MyRegex
 */
public class LostPassword extends Activity {

	//TextView info;
	
	List<EditText> list;	
	EditText LostPassword;
	Button btnLostPassword;
	
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	
	private MenuItem item_loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostpassword);
				
		LostPassword = (EditText)findViewById(R.id.txtLostPasswordMail);
		btnLostPassword = (Button)findViewById(R.id.btnLostPasswordMail);
	//	info = (TextView)findViewById(R.id.txtLostPasswordInfo);
		
		list = new ArrayList<EditText>();
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

		
		btnLostPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				list.clear();
				list.add(LostPassword);
				if (!checkFormu())
			    	return;
				
				//mProgressDialog = ProgressDialog.show(LostPassword.this, "Please wait",
					//	"Long operation starts...", true);
				

				item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				item_loading.setVisible(true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {	
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/password_resets.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("email", LostPassword.getText().toString()));

		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
				
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.RESET_PASSWORD.getValue());
				        myMessage = myHandler.obtainMessage();	
   		        
				        InputStream input = Network.retrieveStream(url, METHOD.POST, nameValuePairs);
						if (input == null)
							messageBundle.putInt("error", 1);
						else
						{	
							Reader readerResp = new InputStreamReader(input);
							String ret = Network.checkInputStream(readerResp);
							
							if (ret.charAt(0) != '{' && ret.charAt(0) != '[')
							{
								messageBundle.putInt("error", 3);
								messageBundle.putString("msgError", ret);
							}
							else
							{
								try {		    
									rep = gson.fromJson(ret, ResponseWS.class);
								}
								catch(JsonParseException e){
							        System.out.println("Exception in check_exitrestrepWSResponse::"+e.toString());
							    }
								
								if (rep.responseCode == 1)
								{
									messageBundle.putInt("error", 2);
									messageBundle.putString("msgError", rep.responseMessage);
								}	
							}
						}						
						myMessage.setData(messageBundle);
	                    myHandler.sendMessage(myMessage);
	                    
	                    msgPb = myHandler.obtainMessage(1, (Object) "Success");
		                myHandler.sendMessage(msgPb);
	                }
					catch (Exception e) {
		                e.printStackTrace();}				
				}};
			thread1.start();			
			}
		});
	}
	
	private boolean checkFormu()
	{
		boolean error = false;
		
		for (EditText field : list)
		{
			if (!MyRegex.check(field)) {
		    	field.setText("");
		    	field.setHintTextColor(Color.RED);
		    	error = true;
		    }
		}
		if (error)
		{
			//info.setTextColor(Color.RED);
	    	//info.setText("Please enter valid values");
			Toast.makeText(getApplicationContext(), "Votre adresse mail n'est pas valide", Toast.LENGTH_SHORT).show();
	    	return false;
		}
		return true;
	}

	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	/*switch (msg.what) {
	        case 0:   //  begin
	            if (mProgressDialog.isShowing()) {
	                mProgressDialog.setMessage(((String) msg.obj));
	                //return;
	            }
	            break;
	        case 1:  //  finish
	        	if (mProgressDialog.isShowing()) {
	                mProgressDialog.dismiss();
	                //return;
	        	}
	        	break;
	        default: // should never happen
	            break;
	    	}*/
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case RESET_PASSWORD:
		    		if (Error == 1)
		    			Toast.makeText(getApplicationContext(), "Erreur de connexion avec le WebService", Toast.LENGTH_SHORT).show();
			    	else if (Error == 2)
			    	{
		    			Toast.makeText(getApplicationContext(), "Erreur lors de la réinitialisation de votre mot de passe : " + pack.getString("msgError"), Toast.LENGTH_SHORT).show();
			    	}
			    	else if (Error == 3)
			    		Toast.makeText(getApplicationContext(), "Erreur du WebService :" + pack.getString("msgError"), Toast.LENGTH_SHORT).show(); 
			    	else
			    	{
			    		item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			    		item_loading.setVisible(false);
			    		Toast.makeText(getApplicationContext(), "Vérifiez votre boite mails ;)", Toast.LENGTH_LONG).show();
			    	}
			    break;
	    	}
	    }
	};
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.lost_pass, menu);
	    item_loading = menu.findItem(R.id.loading_zone);
		item_loading.setVisible(false);
		
		//item_loading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//item_loading.setVisible(true);
		
	    return true;
	  }
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.logo_menu:
	    	item_loading = item;
	    	//item_loading.setActionView(R.layout.progressbar);
	    	//item_loading.expandActionView();
	    	//TestTask task = new TestTask();
	    	//task.execute("test");
	    	
	    	Intent intent;
	    	if (Network.USER == null)
	    		intent = new Intent(LostPassword.this, Login.class);
	    	else
	    		intent = new Intent(LostPassword.this, Menu2.class);
			startActivity(intent);
			break;
		default:
			break;
	    }
	    return true;
	  }
}

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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** This class describe the view who allow the user to report a given publication
 * @see Post
 * @author Seb
 */
public class Report_pub extends MainMenu {
	
	public enum REASON {
		CUSTOM(0),
		COPYRIGHT(1),
		IMAGE_RIGHT(2),
		SEXUAL_CONTENT(3);
		private final int value;
		private REASON(int value) {
			this.value = value;
		}
		public int getValue() {
			return this.value;
		}
	}

	CheckBox[] checkb = new CheckBox[4]; //= {(CheckBox)findViewById(R.id.reportPubCheckBox1), (CheckBox)findViewById(R.id.reportPubCheckBox2), (CheckBox)findViewById(R.id.reportPubCheckBox3), (CheckBox)findViewById(R.id.reportPubCheckBox4)};
	EditText content;
	Button valid;
	TextView info;
	ProgressDialog mProgressDialog;
	ResponseWS rep;
	int publicationId;
	int reasonId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_pub);
		
		//checkb = new ArrayList<CheckBox>();
		checkb[0] = (CheckBox)findViewById(R.id.reportPubCheckBox1);
		checkb[1] = (CheckBox)findViewById(R.id.reportPubCheckBox2);
		checkb[2] = (CheckBox)findViewById(R.id.reportPubCheckBox3);
		checkb[3] = (CheckBox)findViewById(R.id.reportPubCheckBox4);
		content = (EditText)findViewById(R.id.editTextReportPub);
		valid = (Button)findViewById(R.id.btnReportPubValid);
		info = (TextView)findViewById(R.id.txtReportPubInfo);
		
		Bundle b  = this.getIntent().getExtras();
		publicationId = b.getInt("pub_id");
			
		valid.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				reasonId = getReason();
				if (reasonId == -1)
					return;
				
				mProgressDialog = ProgressDialog.show(Report_pub.this, "Please wait",
						"Long operation starts...", true);
				
				Thread thread1 = new Thread(){
			        public void run(){	        	      
					try {
						
		            	Gson gson = new Gson();
		            	String url = Network.URL + Network.PORT + "/report_publications.json";
		            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		            	
		            	nameValuePairs.add(new BasicNameValuePair("report_publication[publication_id]", String.valueOf(publicationId)));
		            	nameValuePairs.add(new BasicNameValuePair("report_publication[reason]", String.valueOf(reasonId)));
		            	nameValuePairs.add(new BasicNameValuePair("report_publication[content]", content.getText().toString()));
		            	
		            	Message myMessage, msgPb;
		            	msgPb = myHandler.obtainMessage(0, (Object) "Please wait");	 
		                myHandler.sendMessage(msgPb);
		                
						Bundle messageBundle = new Bundle();
						messageBundle.putInt("action", ACTION.REPORT_PUB.getValue());
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
								catch(JsonParseException e)
							    {
							        System.out.println("Exception n3 in check_exitrestrepWSResponse::"+e.toString());
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

	int getReason() {
		int count = 0;
		
		for (int i = 0; i < 4; i++) {
			if (checkb[i].isChecked())
				count++;
		}
		if (count == 0)
		{
			Toast.makeText(getApplicationContext(), "At least one reaon must be selected", Toast.LENGTH_SHORT).show();	
			return -1;
		}
		else if (count > 1)
		{
			Toast.makeText(getApplicationContext(), "Only one reaon must be selected", Toast.LENGTH_SHORT).show();
			return -1;
		}
		for (int i = 0; i < 4; i++) {
			if (checkb[i].isChecked()){
				return i;
			}
		}
		return -1;
	}

	Handler myHandler = new Handler()
	{
	    @Override 
	    public void handleMessage(Message msg)
	    {
	    	switch (msg.what) {
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
	    	}
	    	
	    	Bundle pack = msg.getData();
	    	int Error = pack.getInt("error");
	    	switch (Network.ACTION.values()[pack.getInt("action")])
	    	{
		    	case REPORT_PUB:
		    		info.setText("");
			    	if (Error == 1)
			    		info.setText("Error: connection with WS fail");
			    	else if (Error == 2)
			    	{
			    		info.setText("Report publication error :\n" + pack.getString("msgError"));
			    	}
			    	else if (Error == 3)
			    		info.setText("Ws error :\n" + pack.getString("msgError"));
			    	else
			    	{
			    		info.setText("Report publication success");
						Toast.makeText(getApplicationContext(), "Report publication success", Toast.LENGTH_SHORT).show();
						Report_pub.this.finish();
			    	}
			    	break;
	    	} 	
	    }
	};
}


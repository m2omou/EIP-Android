package com.epitech.neerbyy.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.TextView;

import com.epitech.neerbyy.Login;

public class NeerbyTestLogin extends ActivityInstrumentationTestCase2<Login> {

	private Login mActivity;
	private TextView logInfo;
    private EditText logUser;
    private EditText logPass;
    private String info;
	
    public NeerbyTestLogin(){
		super("com.epitech.neerbyy", Login.class);
	}
    
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		logInfo = (TextView) mActivity.findViewById(com.epitech.neerbyy.R.id.txtLoginInfo);
		logUser = (EditText) mActivity.findViewById(com.epitech.neerbyy.R.id.txtLoginMail);
		logPass = (EditText) mActivity.findViewById(com.epitech.neerbyy.R.id.txtPassword);
		info = mActivity.getString(com.epitech.neerbyy.R.string.loginDefaultInfo);
	}

	public void testPreconditions() {
	      assertNotNull(logInfo);
	    }
	
	public void testText() {
	      assertEquals(info,(String)logInfo.getText());
	    }
	
}

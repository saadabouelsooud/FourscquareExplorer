package com.saad.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FoursquareSession {
	private SharedPreferences sharedPref;
	private Editor editor;
	
	private static final String SHARED = "Foursquare_Preferences";
	private static final String FSQ_Access_code = "accesscode";
	private static final String FSQ_ACCESS_TOKEN = "access_token";
	
	public FoursquareSession(Context context) {
		sharedPref 	  = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		
		editor 		  = sharedPref.edit();
	}
	
	/**
	 * Save access token and user name
	 * 
	 * @param accessToken Access token
	 * @param username User name
	 */
	public void storeAccessToken(String accessToken) {
		editor.putString(FSQ_ACCESS_TOKEN, accessToken);
		
		editor.commit();
	}
	public void storeAccessCode(String accesscode) {
		editor.putString(FSQ_Access_code, accesscode);
		
		editor.commit();
	}
	
	/**
	 * Reset access token and user name
	 */
	public void resetAccessToken() {
		editor.putString(FSQ_ACCESS_TOKEN, null);
		
		editor.commit();
	}
	public void resetAccessCode() {
		editor.putString(FSQ_Access_code, null);
		
		editor.commit();
	}
	
	/**
	 * Get user name
	 * 
	 * @return User name
	 */
	public String getAccessCode() {
		return sharedPref.getString(FSQ_Access_code, null);
	}
	
	/**
	 * Get access token
	 * 
	 * @return Access token
	 */
	public String getAccessToken() {
		return sharedPref.getString(FSQ_ACCESS_TOKEN, null);
	}
}

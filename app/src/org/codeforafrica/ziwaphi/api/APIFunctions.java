package org.codeforafrica.ziwaphi.api;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.codeforafrica.ziwaphi.DefaultsActivity;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class APIFunctions {
	
	private JSONParser jsonParser;
	private static String registerURL = DefaultsActivity.site_url + "/api/users/register/";
	private static String updateURL = DefaultsActivity.site_url + "/api/users/editprofile/";
	private static String userURL = DefaultsActivity.site_url + "/api/users/user/";
	
	// constructor
	public APIFunctions(){
		jsonParser = new JSONParser();
	}
	
	
	public JSONObject registerUser(String username, String password, String email, String first_name, String last_name, String location, String phone_number){
		Log.d("passed", "passed" + email + username + password);
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("firstname", first_name));
		params.add(new BasicNameValuePair("lastname", last_name));
		params.add(new BasicNameValuePair("location", location));
		params.add(new BasicNameValuePair("phone_number", phone_number));
		params.add(new BasicNameValuePair("tag", "register"));

		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}
	public JSONObject newUser(String username, String password, String email, String operatorName, String deviceId, String serialNumber){
		Log.d("passed", "passed" + email + username + password);
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("tag", "register"));
		params.add(new BasicNameValuePair("operatorName", operatorName));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("serialNumber", serialNumber));

		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}

	public JSONObject updateUser(String vemail, String vfirst_name, String vlast_name,
			String vlocation, String vphone_number, String vaddress, String vuser_id) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", vuser_id));

		params.add(new BasicNameValuePair("first_name", vfirst_name));
		params.add(new BasicNameValuePair("last_name", vlast_name));
		params.add(new BasicNameValuePair("location", vlocation));
		params.add(new BasicNameValuePair("phone_number", vphone_number));
		params.add(new BasicNameValuePair("address", vaddress));
		if(vemail!=null){
			params.add(new BasicNameValuePair("email", vemail));

		}
		JSONObject json = jsonParser.getJSONFromUrl(updateURL, params);
		// return json
		return json;
	}
	
	public JSONObject getUser(String vuser_id) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", vuser_id));

		JSONObject json = jsonParser.getJSONFromUrl(userURL, params);
		// return json
		return json;
	}
	
}
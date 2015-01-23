package org.codeforafrica.ziwaphi.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.bican.wordpress.Category;

import org.codeforafrica.ziwaphi.R;
import org.codeforafrica.ziwaphi.AppConstants;
import org.codeforafrica.ziwaphi.BaseActivity;
import org.codeforafrica.ziwaphi.HomePanelsActivity;
import org.codeforafrica.ziwaphi.StoryMakerApp;
import org.codeforafrica.ziwaphi.api.APIFunctions;
import org.codeforafrica.ziwaphi.location.GPSTracker;
import org.codeforafrica.ziwaphi.model.Auth;
import org.codeforafrica.ziwaphi.model.AuthTable;

import android.app.Dialog;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.CheckBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
 
public class RegistrationActivity extends BaseActivity implements Runnable 
{
	
	private ImageView viewLogo;
	private TextView txtStatus;
	private EditText txtUser;
	private EditText txtPass;
	List<Category> categories;
	private boolean mPasswordVisible = false;
	//private ProgressBar pBLogin;
	
	String Vusername = "";
	String Vpassword = "";
	String Vcpassword = "";
	String Vemail = "";
	String Vfirst_name = "";
	String Vlast_name = "";
	String Vphone_number = "";
	
	private GPSTracker gpsT; 
	private boolean noGPS = false;
	private String fineLocation;
    
	private boolean registering = false;
	private boolean updatingProfile = false;
	
	// Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    
    
    LinearLayout lBasic_info;
	LinearLayout lMore_info;
	
	LinearLayout builtby;
	Button button_reg;
	EditText username;
	EditText rpassword;
	EditText cPassword;
	EditText email;
	EditText first_name;
	EditText last_name;
	EditText location;
	EditText phone_number;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_registration_full);
        
        button_reg = (Button)findViewById(R.id.btnRegister);
		
        username = (EditText) findViewById(R.id.registerUsername);
		rpassword = (EditText) findViewById(R.id.registerPassword);
		cPassword = (EditText) findViewById(R.id.confirmPassword);
		email = (EditText) findViewById(R.id.email);
		first_name = (EditText) findViewById(R.id.first_name);
		last_name = (EditText) findViewById(R.id.last_name);
		location = (EditText) findViewById(R.id.location);
		phone_number = (EditText) findViewById(R.id.phone_number);
		txtStatus = (TextView)findViewById(R.id.reg_error);
		
    	button_reg.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
													
				Vusername = "" + username.getText().toString();
	        	Vpassword = "" + rpassword.getText().toString();
	    		Vemail = "" + email.getText().toString();
	    		Vfirst_name = "" + first_name.getText().toString();
	    		Vlast_name = "" + last_name.getText().toString();
	    		Vphone_number = "" + phone_number.getText().toString();
	    		Vcpassword = "" + cPassword.getText().toString();
	    		
	    		
	    		if((Vusername=="")||(Vpassword=="")||(Vemail=="")||(Vfirst_name == "") || (Vlast_name == "") || (Vphone_number == "")){
		
					txtStatus.setText("All fields are required!");

	    			Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_LONG).show();

	    		}else{
	    			
	    			if(!isEmailValid(Vemail)){
		    			
	    				txtStatus.setText("Please provide a valid email address!");
	    				
	    				Toast.makeText(getApplicationContext(), "Please provide a valid email address!", Toast.LENGTH_LONG).show();

	    			}else if(!Vcpassword.equals(Vpassword)){
		    			
	    				txtStatus.setText("Your passwords do not match!");
	    				
		    			Toast.makeText(getApplicationContext(), "Your passwords do not match!", Toast.LENGTH_LONG).show();
		    		
		    		}else{
		    			
		    			
			    			registering = true;
				    		
		    			
		    				handleLogin();	
		    			
		    		}
		    		
	    		}
	    		}
			});
    	}
    
    
    private void handleLogin ()
    {
    	txtStatus.setText("Connecting to server...");
    	
    	new Thread(this).start();
    }
    private void saveCreds (String user, String pass)
    {   
        ArrayList<Auth> results = (new AuthTable()).getAuthsAsList(getApplicationContext(), Auth.STORYMAKER);
        for (Auth deleteAuth : results) {
        	// only a single username/password is stored at a time
        	deleteAuth.delete();
        }
		
        Auth storymakerAuth = new Auth(getApplicationContext(),
        		                       -1, // should be set to a real value by insert method
        		                       "StoryMaker.cc",
        		                       Auth.STORYMAKER,
        		                       user,
        		                       pass,
        		                       null,
        		                       new Date());
        storymakerAuth.save();
    } 
    
    private void getCreds()
    { 
        Auth storymakerAuth = (new AuthTable()).getAuthDefault(getApplicationContext(), Auth.STORYMAKER);
        if (storymakerAuth != null) {
        	txtUser.setText(storymakerAuth.getUserName());
        	//txtPass.setText(storymakerAuth.getCredentials());
        } 
    }
    public void run ()
    {
    	// creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
		//get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        
        if(!isInternetPresent){
        	
        	Message msgErr= mHandler.obtainMessage(1);
            msgErr.getData().putString("err","You have no internet connection!");
            mHandler.sendMessage(msgErr);
            
        }else{
        	
    	if(updatingProfile==true){
    		
    		//get user location
    		gpsT = new GPSTracker(RegistrationActivity.this);
    		// check if GPS enabled 
            if(gpsT.canGetLocation()){ 
            	txtStatus.setText("");
            	
                fineLocation = gpsT.getLatitude() + ", " + gpsT.getLongitude();
                
                APIFunctions userFunction = new APIFunctions();
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		       
                String Vuser_id = settings.getString("user_id","0");
                
                JSONObject json = userFunction.updateUser(null, Vfirst_name, Vlast_name, fineLocation, Vphone_number, "", Vuser_id);
    				try {
    						String res = json.getString("result"); 
    						if(res.equals("OK")){
    							mHandler.sendEmptyMessage(0);
    						}else{
    							Message msgErr= mHandler.obtainMessage(1);
    	                        msgErr.getData().putString("err",json.getString("message"));
    	                        mHandler.sendMessage(msgErr);
    						}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				
            }else{
            	noGPS = true;
            	Message msgErr= mHandler.obtainMessage(1);
                msgErr.getData().putString("err","loc");
                mHandler.sendMessage(msgErr);
            } 
    		
    		
    		
    	}else if(registering == true){
    	
    		
    		APIFunctions userFunction = new APIFunctions();
            JSONObject json = userFunction.newUser(Vusername, Vpassword, Vemail);
				try {
						String res = json.getString("result"); 
						if(res.equals("OK")){
							mHandler.sendEmptyMessage(0);
							
							saveUserID(json.getString("user_id"));
						}else{
							Message msgErr= mHandler.obtainMessage(1);
	                        msgErr.getData().putString("err",json.getString("message"));
	                        mHandler.sendMessage(msgErr);
						}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
    	}
        }
    }
    
    
    
    private Handler mHandler = new Handler ()
    {

		@Override
		public void handleMessage(Message msg) {
			//pBLogin.setVisibility(View.INVISIBLE);
			
				
			switch (msg.what)
			{
				case 0:
					loginSuccess();
					break;
				case 1:
					loginFailed(msg.getData().getString("err"));
					
					
				default:
			}
		}
    	
    };
    
    private void loginFailed (String err)
    {
    	if(noGPS==true && updatingProfile==true){
    		
    		txtStatus.setText("");
    		gpsT.showSettingsAlert();	
    	
    	}else{
    		txtStatus.setText(err);
    	
    		Toast.makeText(this, "Failed: " + err, Toast.LENGTH_LONG).show();
    	}
    }
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    private void loginSuccess ()
    {	
    	if(updatingProfile==true){
    		
    		updatingProfile = false;
    		
    		Toast.makeText(getApplicationContext(), "Registration completed successfully!", Toast.LENGTH_LONG).show();
    		
    		Intent intent = new Intent(this,HomePanelsActivity.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    		startActivity(intent);
	    	finish();
	    	
    		
    	}else if(registering==true){
    		
    		registering = false;
    		
			
        	updatingProfile = true;

        	handleLogin();
    	}
    }
    private void saveUserID(String user_id){
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString("user_id", user_id);
		editor.commit();
		
    }
    
    
}
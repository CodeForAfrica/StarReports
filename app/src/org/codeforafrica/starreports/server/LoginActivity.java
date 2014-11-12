package org.codeforafrica.starreports.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.bican.wordpress.Category;

import org.codeforafrica.starreports.AppConstants;
import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.HomePanelsActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.StoryMakerApp;
import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.model.Auth;
import org.codeforafrica.starreports.model.AuthTable;

import android.app.Dialog;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
 
public class LoginActivity extends BaseActivity implements Runnable 
{
	
	private ImageView viewLogo;
	private TextView txtStatus;
	private EditText txtUser;
	private EditText txtPass;
	List<Category> categories;
	private boolean mPasswordVisible = false;
	private ProgressBar pBLogin;
	
	String Vusername = "";
	String Vpassword = "";
	String Vcpassword = "";
	String Vemail = "";
	String Vfirst_name = "";
	String Vlast_name = "";
	String Vlocation = "";
	String Vphone_number = "";
	
	private boolean registering = false;
	private boolean initialReg = true;
	
	// Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    
    
    LinearLayout lBasic_info;
	LinearLayout lMore_info;
	Button button_reg;
	Dialog dialog_reg;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login_facebook);
        
        viewLogo = (ImageView)findViewById(R.id.logo);
        txtUser = (EditText)findViewById(R.id.login_username);
        txtPass = (EditText)findViewById(R.id.login_password);
        pBLogin = (ProgressBar)findViewById(R.id.pBLogin);
        getCreds();
        
        getSupportActionBar().hide();
        
        RelativeLayout lLayout = (RelativeLayout)findViewById(R.id.loginLayout);
        
        //lLayout.getBackground().setAlpha(90); 
        
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new OnClickListener ()
        {
			public void onClick(View v) {
				/*
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
				finish();
				*/
				showRegistrationDialog();
			}        	
        });
        initPasswordVisibilityButton((ImageView) findViewById(R.id.password_visibility), txtPass);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener ()
        {

			@Override
			public void onClick(View v) {
		        
				txtStatus = (TextView)findViewById(R.id.status);

				//Intent fl = new Intent(getApplicationContext(), FacebookLogin.class);
				//startActivity(fl);
				pBLogin.setVisibility(View.VISIBLE);
				handleLogin ();
				
			}
        	
        });
                
        
    }
    public void showRegistrationDialog(){
    	dialog_reg = new Dialog(this, R.style.DialogSlideAnim);
    	dialog_reg.setTitle("Registration");
    	dialog_reg.setContentView(R.layout.activity_registration_dialog);
		button_reg = (Button)dialog_reg.findViewById(R.id.btnRegister);

    	button_reg.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				lBasic_info = (LinearLayout)dialog_reg.findViewById(R.id.basic_info);
				lMore_info = (LinearLayout)dialog_reg.findViewById(R.id.more_info);
				
												
				EditText username = (EditText) dialog_reg.findViewById(R.id.registerUsername);
				EditText rpassword = (EditText) dialog_reg.findViewById(R.id.registerPassword);
				EditText cPassword = (EditText) dialog_reg.findViewById(R.id.confirmPassword);
				EditText email = (EditText) dialog_reg.findViewById(R.id.email);
				EditText first_name = (EditText) dialog_reg.findViewById(R.id.first_name);
				EditText last_name = (EditText) dialog_reg.findViewById(R.id.last_name);
				EditText location = (EditText) dialog_reg.findViewById(R.id.location);
				EditText phone_number = (EditText) dialog_reg.findViewById(R.id.phone_number);
	    						
				txtStatus = (TextView)dialog_reg.findViewById(R.id.reg_error);
				
				//
				Vusername = "" + username.getText().toString();
	        	Vpassword = "" + rpassword.getText().toString();
	    		Vemail = "" + email.getText().toString();
	    		Vfirst_name = "" + first_name.getText().toString();
	    		Vlast_name = "" + last_name.getText().toString();
	    		Vlocation = "" + location.getText().toString();
	    		Vphone_number = "" + phone_number.getText().toString();
	    		Vcpassword = "" + cPassword.getText().toString();
	    		
	    		
	    		if((Vusername=="")||(Vpassword=="")||(Vemail=="")){
	    			
	    			Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_LONG).show();

	    		}else{
	    			
		    		if(!Vcpassword.equals(Vpassword)){
		    			
		    			Toast.makeText(getApplicationContext(), "Your passwords do not match!", Toast.LENGTH_LONG).show();
		    		
		    		}else{
		    			
		    					    			
		    			if(initialReg == false){
			    			
			    			registering = true;
				    		
				    		handleLogin();
				    		
			    			initialReg = true;
			    			
				    		
			    		}else{
			    			
			    			initialReg = false;

			    		}
		    			
		    		}
		    		
		    		
	    		}
			}    	
        });
    	dialog_reg.show();
    }

    
    protected void initPasswordVisibilityButton(final ImageView passwordVisibilityToggleView,
            final EditText passwordEditText) {
		passwordVisibilityToggleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPasswordVisible = !mPasswordVisible;
				if (mPasswordVisible) {
					passwordVisibilityToggleView.setImageResource(R.drawable.dashicon_eye_open);
					passwordVisibilityToggleView.setColorFilter(v.getContext().getResources().getColor(R.color.lp_blue));
					passwordEditText.setTransformationMethod(null);
				} else {
					passwordVisibilityToggleView.setImageResource(R.drawable.dashicon_eye_closed);
					passwordVisibilityToggleView.setColorFilter(v.getContext().getResources().getColor(R.color.lp_red));
					passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
					passwordEditText.setSelection(passwordEditText.length());
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
        	
    	if(registering == true){
    		
    		APIFunctions userFunction = new APIFunctions();
            JSONObject json = userFunction.newUser(Vusername, Vpassword, Vemail);
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
	    	String username = txtUser.getText().toString();
	    	String password = txtPass.getText().toString();
	
	    	try {
				StoryMakerApp.getServerManager().connect(username, password);
	
				// only store username/password for a successful login
		    	saveCreds(username, password);
		    	
		    	categories = StoryMakerApp.getServerManager().getCategories();
		    	Log.d("cats", "cats:"+categories.toString());
				mHandler.sendEmptyMessage(0);
		         
			} catch (Exception e) {
				
				Message msgErr= mHandler.obtainMessage(1);
				msgErr.getData().putString("err",e.getLocalizedMessage());
				mHandler.sendMessage(msgErr);
				Log.e(AppConstants.TAG,"login err",e);
			}

    		}
        }
    }
    
    private Handler mHandler = new Handler ()
    {

		@Override
		public void handleMessage(Message msg) {
			pBLogin.setVisibility(View.INVISIBLE);
			
				
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
    	txtStatus.setText(err);
    	
    	Toast.makeText(this, "Login failed: " + err, Toast.LENGTH_LONG).show();
    }
    
    private void loginSuccess ()
    {	
    	if(registering==true){
    		
    		registering = false;
    		
    		lBasic_info.setVisibility(View.GONE);
			lMore_info.setVisibility(View.VISIBLE);
			

			dialog_reg.setTitle("Edit Profile");
			button_reg.setText("Update");
			
        	txtStatus.setText("Registration successfull!");

    	}else{
        	txtStatus.setText("Loading...");

	    	//login successful
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        Editor editor = settings.edit();
			editor.putString("logged_in", "1");
			
			editor.commit();
	    			
	    	updateCategories();
	    	Intent intent = new Intent(this,HomePanelsActivity.class);
	    	startActivity(intent);
	    	finish();
    	}
    }

    private void updateCategories(){
	
    	SharedPreferences prefs = PreferenceManager
		        .getDefaultSharedPreferences(getApplicationContext());
		JSONArray catsList = new JSONArray();

		for(int i=0;i<categories.size();i++){
			Category cat = categories.get(i);

			catsList.put(cat.getCategoryName());
		}
		
		Editor editor = prefs.edit();
		editor.putString("categories", catsList.toString());
		
		editor.commit();
    }
    
}
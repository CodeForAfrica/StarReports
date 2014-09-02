package org.codeforafrica.starreports.server;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.bican.wordpress.Category;

import org.codeforafrica.starreports.AppConstants;
import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.ReportsFragmentsActivity;
import org.codeforafrica.starreports.StoryMakerApp;
import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.api.SyncService;
import org.codeforafrica.starreports.encryption.EncryptionService;
import org.codeforafrica.starreports.export.Export2SDService;
import org.codeforafrica.starreports.model.Auth;
import org.codeforafrica.starreports.model.AuthTable;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
 
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
	String Vemail = "";
	String Vfirst_name = "";
	String Vlast_name = "";
	String Vlocation = "";
	String Vphone_number = "";
	
	private boolean registering = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login_facebook);
        
        viewLogo = (ImageView)findViewById(R.id.logo);
        txtStatus = (TextView)findViewById(R.id.status);
        txtUser = (EditText)findViewById(R.id.login_username);
        txtPass = (EditText)findViewById(R.id.login_password);
        pBLogin = (ProgressBar)findViewById(R.id.pBLogin);
        getCreds();
        
        getSupportActionBar().hide();
        
        RelativeLayout lLayout = (RelativeLayout)findViewById(R.id.loginLayout);
        
        lLayout.getBackground().setAlpha(120); 
        
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
				//Intent fl = new Intent(getApplicationContext(), FacebookLogin.class);
				//startActivity(fl);
				pBLogin.setVisibility(View.VISIBLE);
				handleLogin ();
				
			}
        	
        });
                
        
    }
    public void showRegistrationDialog(){
    	final Dialog dialog = new Dialog(this, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setTitle("Create new account");
    	dialog.setContentView(R.layout.activity_registration_dialog);
        dialog.findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				EditText username = (EditText) dialog.findViewById(R.id.registerUsername);
				EditText rpassword = (EditText) dialog.findViewById(R.id.registerPassword);
				EditText cPassword = (EditText) dialog.findViewById(R.id.confirmPassword);
				EditText email = (EditText) dialog.findViewById(R.id.email);
				EditText first_name = (EditText) dialog.findViewById(R.id.first_name);
				EditText last_name = (EditText) dialog.findViewById(R.id.last_name);
				EditText location = (EditText) dialog.findViewById(R.id.location);
				EditText phone_number = (EditText) dialog.findViewById(R.id.phone_number);
				
				//
				Vusername = username.getText().toString();
	        	Vpassword = rpassword.getText().toString();
	    		Vemail = email.getText().toString();
	    		Vfirst_name = first_name.getText().toString();
	    		Vlast_name = last_name.getText().toString();
	    		Vlocation = location.getText().toString();
	    		Vphone_number = phone_number.getText().toString();
	    			
	    		registering = true;
	    		
	    		handleLogin();
	    		
	    		dialog.dismiss();
			}    	
        });
        dialog.show();
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
    	if(registering == true){
    		
    		APIFunctions userFunction = new APIFunctions();
            JSONObject json = userFunction.registerUser(Vusername, Vpassword, Vemail, Vfirst_name, Vlast_name, Vlocation, Vphone_number);
				try {
						String res = json.getString("status"); 
						if(res.equals("ok")){
							mHandler.sendEmptyMessage(0);
						}else{
							Message msgErr= mHandler.obtainMessage(1);
	                        msgErr.getData().putString("err",json.getString("error"));
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
    	//Toast.makeText(this, "Login failed: " + err, Toast.LENGTH_LONG).show();
    }
    
    private void loginSuccess ()
    {	
    	if(registering==true){
    		
    		registering = false;
        	txtStatus.setText("Registration successfull, you can log in now!");

    	}else{
	    	//login successful
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        Editor editor = settings.edit();
			editor.putString("logged_in", "1");
			
			editor.commit();
	    			
	    	updateCategories();
	    	Intent intent = new Intent(this,ReportsFragmentsActivity.class);
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
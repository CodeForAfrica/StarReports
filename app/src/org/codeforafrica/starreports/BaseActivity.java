package org.codeforafrica.starreports;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.api.SyncService;
import org.codeforafrica.starreports.assignments.AssignmentsActivity;
import org.codeforafrica.starreports.encryption.EncryptionService;
import org.codeforafrica.starreports.export.Export2SDService;
import org.codeforafrica.starreports.facebook.FacebookLogin;
import org.codeforafrica.starreports.facebook.UpdateActivity;
import org.codeforafrica.starreports.server.LoginActivity;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Toast;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
//import com.google.analytics.tracking.android.EasyTracker;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;

public class BaseActivity extends Activity {

	public SlidingMenu mSlidingMenu;
	private static final String TAG=BaseActivity.class.getName();
    public Dialog dialog;

    /**
     * Gets reference to global Application
     * @return must always be type of ControlApplication! See AndroidManifest.xml
     */
public StoryMakerApp getApp()
{
    return (StoryMakerApp )this.getApplication();
}

@Override
public void onUserInteraction()
{
    super.onUserInteraction();
    getApp().touch();
    Log.d(TAG, "User interaction to "+this.toString());
}
	@Override
	public void onStart() {
		super.onStart();
//		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
//		EasyTracker.getInstance(this).activityStop(this);
	}
	
    public void initSlidingMenu ()
    {

        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setMenu(R.layout.fragment_drawer);

		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		
        mSlidingMenu.setOnClosedListener(new OnClosedListener() {

            @Override
            public void onClosed() {
                mSlidingMenu.requestLayout();

            }
        });
        
        final Activity activity = this;
        
        ImageButton btnDrawerQuickCaptureVideo = (ImageButton) findViewById(R.id.btnDrawerQuickCaptureVideo);
        ImageButton btnDrawerQuickCapturePhoto = (ImageButton) findViewById(R.id.btnDrawerQuickCapturePhoto);
        ImageButton btnDrawerQuickCaptureAudio = (ImageButton) findViewById(R.id.btnDrawerQuickCaptureAudio);
        
        Button btnDrawerHome = (Button) findViewById(R.id.btnDrawerHome);
        Button btnDrawerProjects = (Button) findViewById(R.id.btnDrawerProjects);
        Button btnDrawerLessons = (Button) findViewById(R.id.btnDrawerLessons);
        Button btnDrawerAccount = (Button) findViewById(R.id.btnDrawerAccount);
        Button btnDrawerSettings = (Button) findViewById(R.id.btnDrawerSettings);
        

       
        btnDrawerQuickCaptureVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 0);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);           
                 }
        });
        
        btnDrawerQuickCapturePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 2);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);           
                 }
        });
        
        btnDrawerQuickCaptureAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 1);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);           
                 }
        });
        
        btnDrawerHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	mSlidingMenu.showContent(true);
                
            	 Intent i = new Intent(activity, HomeActivity.class);
                 activity.startActivity(i);
            }
        });
        btnDrawerProjects.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	mSlidingMenu.showContent(true);
            	  Intent i = new Intent(activity, ProjectsActivity.class);
                  activity.startActivity(i);
            }
        });
        btnDrawerLessons.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	mSlidingMenu.showContent(true);
            	
                Intent i = new Intent(activity, LessonsActivity.class);
                activity.startActivity(i);
            }
        });
        
        btnDrawerAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	mSlidingMenu.showContent(true);
                Intent i = new Intent(activity, LoginActivity.class);
                activity.startActivity(i);
            }
        });
        
        btnDrawerSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mSlidingMenu.showContent(true);

                Intent i = new Intent(activity, SimplePreferences.class);
                activity.startActivity(i);
            }
        });
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_actionbar_menu, menu);
        
        return true;
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.menu_assignments)
        {	   		
    		Intent i = new Intent(getApplicationContext(),AssignmentsActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        }
    	if (item.getItemId() == R.id.menu_add_report)
        {	
        	   		
    		Intent i = new Intent(getApplicationContext(),ReportActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
        }
    	if (item.getItemId() == R.id.menu_sync_reports)
        {	
        	   		
    		dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_sync);
            dialog.findViewById(R.id.button_sync).setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isServiceRunning(SyncService.class)){
		  	          	Toast.makeText(getBaseContext(), "Syncing is already started!", Toast.LENGTH_LONG).show();
		        	}else if (isServiceRunning(EncryptionService.class)){
		  	          	Toast.makeText(getBaseContext(), "Please wait for encryption to finish!", Toast.LENGTH_LONG).show();
		        	}else if(isServiceRunning(Export2SDService.class)){
		  	          	Toast.makeText(getBaseContext(), "Please wait for exporting to finish!", Toast.LENGTH_LONG).show();
		        	}else{
			        	
			  	        	dialog.dismiss();
			  	        	startService(new Intent(getApplicationContext(),SyncService.class));
			  	   
		        	}
				}
            	
            });
            dialog.findViewById(R.id.button_export).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
		        	
		        	CheckBox cB = (CheckBox)dialog.findViewById(R.id.checkBox1);
		        	
		        	String includeExported;
		        	if(cB.isChecked()){
		        		includeExported = "1";
		        	}else{
		        		includeExported = "0";
		        	}
		        	
		        	if(isServiceRunning(Export2SDService.class)){
		  	          	Toast.makeText(getBaseContext(), "Export to SD is already started!", Toast.LENGTH_LONG).show();
		        	}else if (isServiceRunning(EncryptionService.class)){
		  	          	Toast.makeText(getBaseContext(), "Please wait for encryption to finish!", Toast.LENGTH_LONG).show();
		        	}else if(isServiceRunning(SyncService.class)){
		  	          	Toast.makeText(getBaseContext(), "Please wait for sync to finish!", Toast.LENGTH_LONG).show();
		        	}else{
		        		Intent eS = new Intent(getApplicationContext(),Export2SDService.class);
		        		eS.putExtra("includeExported", includeExported);
			  	        startService(eS); 
		        	}
				}
			});
            dialog.show();
        }
    	if (item.getItemId() == R.id.home)
        {
        	Intent intent = new Intent(getBaseContext(), HomePanelsActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }
        if (item.getItemId() == R.id.about)
        {	
        	   		
        	Intent intent = new Intent(getBaseContext(), AboutActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }
        if (item.getItemId() == R.id.profile)
        {	
        	   		
        	Intent intent = new Intent(getBaseContext(), UpdateActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }
        if (item.getItemId() == R.id.logout)
        {
        	//logout facebook
        	
        	callFacebookLogout(getApplicationContext());
    			
        	//nullify user
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		Editor editor = prefs.edit();
    		editor.putString("logged_in", "0");
        	editor.commit();
        	
        	Intent intent = new Intent(getBaseContext(), FacebookLogin.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        intent.putExtra("logout", "1");
	        startActivity(intent);
	        finish();
        }
       
		return true;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
        
        //(new Eula(this)).show();
    }
    
    @Override
	public void onPostCreate(Bundle savedInstanceState) {
		
		super.onPostCreate(savedInstanceState);
		//initSlidingMenu();
	}

    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
                //clear your preferences if saved

        }

    }
    private boolean isServiceRunning(Class<?> cls) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEncryptionRunning() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			       
        String encryption_running = settings.getString("encryption_running",null);
        
	        if (encryption_running == null){
	        	return false;
	        }else if(encryption_running.equals("end")){
	        	return false;
	        }else{
	        	return true;
	        }
	      
	    }
 
	private void detectCoachOverlay ()
    {
        try {
        	
        	if (this.getClass().getName().contains("SceneEditorActivity"))
        	{
        		showCoachOverlay("images/coach/coach_add.png");
        	}
        	else if (this.getClass().getName().contains("OverlayCameraActivity"))
        	{
        		showCoachOverlay("images/coach/coach_camera_prep.png");
        	}
        		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 
	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}	
**/
    
    private void showCoachOverlay (String path) throws IOException
    {
    	ImageView overlayView = new ImageView(this);
    	
    	overlayView.setOnClickListener(new OnClickListener () 
    	{

			@Override
			public void onClick(View v) {
				getWindowManager().removeView(v);
				
			}
    		
    	});
    	
    	AssetManager mngr = getAssets();
        // Create an input stream to read from the asset folder
           InputStream ins = mngr.open(path);

           // Convert the input stream into a bitmap
           Bitmap bmpCoach = BitmapFactory.decodeStream(ins);
           overlayView.setImageBitmap(bmpCoach);
           
    	WindowManager.LayoutParams params = new WindowManager.LayoutParams(
    	        WindowManager.LayoutParams.MATCH_PARENT,
    	        WindowManager.LayoutParams.MATCH_PARENT,
    	        WindowManager.LayoutParams.TYPE_APPLICATION,
    	        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
    	        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
    	        PixelFormat.TRANSLUCENT);

    	getWindowManager().addView(overlayView, params);
    }
}

package org.codeforafrica.ziwaphi;

import info.guardianproject.onionkit.ui.OrbotHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.bican.wordpress.Page;
import net.sqlcipher.database.SQLiteDatabase;

import org.codeforafrica.ziwaphi.R;
import org.codeforafrica.ziwaphi.api.XMLRPCSyncService;
import org.codeforafrica.ziwaphi.assignments.AssignmentsActivity;
import org.codeforafrica.ziwaphi.encryption.EncryptionBackground;
import org.codeforafrica.ziwaphi.encryption.EncryptionService;
import org.codeforafrica.ziwaphi.export.Export2SDService;
import org.codeforafrica.ziwaphi.model.Lesson;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.server.LoginActivity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;

import redstone.xmlrpc.XmlRpcFault;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomePanelsActivity extends BaseActivity implements OnClickListener{

    
    private ProgressDialog mLoading;
    private ArrayList<Lesson> mLessonsCompleted;
    private ArrayList<Project> mListProjects;

    RelativeLayout load_new_report_click;
    RelativeLayout load_lessons_click;
    RelativeLayout load_my_reports;
    RelativeLayout load_assignments;
    TextView assignTV;

    LinearLayout add_picture;
    LinearLayout add_video;
    LinearLayout add_audio;
    private Dialog dialog;
    
    List<Page> posts;
    
    //Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    String assignmentsCount;
   
    SharedPreferences prefs;
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
    	
        cd = new ConnectionDetector(getApplicationContext());

    	//Get constants
    	prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	
    	SQLiteDatabase.loadLibs(this);
        try {
            String pkg = getPackageName();
            String vers= getPackageManager().getPackageInfo(pkg, 0).versionName;
            setTitle(getTitle());
                    
        } catch (NameNotFoundException e) {
           
        }
        checkCreds();
    	//new getSectors().execute();
       // new getCategories().execute();
        
        
        // action bar stuff
       
         
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
                
        checkForTor();
        //check if relevant folders exist
        
        
        //start encryption
        if(!isServiceRunning(EncryptionBackground.class)){
        	startService(new Intent(HomePanelsActivity.this, EncryptionBackground.class));
        }
        setContentView(R.layout.activity_home_panels);
      	        
	        initSlidingMenu();
	        
		/*
        
        setContentView(R.layout.activity_home_panels);
        initSlidingMenu();
        
        load_new_report_click = (RelativeLayout)findViewById(R.id.load_new_report);
        load_new_report_click.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),Report_PageIndicatorActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
        */
	    
	    assignTV = (TextView)findViewById(R.id.assignmentsTv);
	    //set old value of assignments
	    assignmentsCount = prefs.getString("assignmentsCount", "0");
	    assignTV.setText(getResources().getString(R.string.title_assignments) + " (" + assignmentsCount + ")");
	    
	    
	    new GetAssignments().execute();
	    
	    add_picture = (LinearLayout)findViewById(R.id.add_picture);
	    add_picture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentStoryPhoto = new Intent(getApplicationContext(), StoryNewActivity.class);
			      intentStoryPhoto.putExtra("story_name", "Quick Story");
			      intentStoryPhoto.putExtra("storymode", 2);
			      intentStoryPhoto.putExtra("auto_capture", true);
			      intentStoryPhoto.putExtra("quickstory", 1);

			      startActivity(intentStoryPhoto);
			}
		});
	    add_video = (LinearLayout)findViewById(R.id.add_video);
	    add_video.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentStoryVideo = new Intent(getApplicationContext(), StoryNewActivity.class);
			      intentStoryVideo.putExtra("story_name", "Quick Story");
			      intentStoryVideo.putExtra("storymode", 0);
			      intentStoryVideo.putExtra("auto_capture", true);
			      intentStoryVideo.putExtra("quickstory", 1);
			      startActivity(intentStoryVideo);
			}
		});
	    add_audio = (LinearLayout)findViewById(R.id.add_audio);
	    add_audio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentStoryAudio = new Intent(getApplicationContext(), StoryNewActivity.class);
			      intentStoryAudio.putExtra("story_name", "Quick Story");
			      intentStoryAudio.putExtra("storymode", 1);
			      intentStoryAudio.putExtra("quickstory", 1);
			      intentStoryAudio.putExtra("auto_capture", true);
			      startActivity(intentStoryAudio);
			}
		});
        load_lessons_click = (RelativeLayout)findViewById(R.id.load_lessons);
        load_lessons_click.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), LessonsActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
        load_my_reports = (RelativeLayout)findViewById(R.id.load_reports);
        load_my_reports.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),ReportsActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
        load_assignments = (RelativeLayout)findViewById(R.id.load_assignments);
        load_assignments.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),AssignmentsActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
        
		}
    class GetAssignments extends AsyncTask<String, String, String> {
	   	 @Override
	        protected void onPreExecute() {
		   		super.onPreExecute();
	        }
	        protected String doInBackground(String... args) {
	        	
	        	
	        	try {
					 posts = StoryMakerApp.getServerManager().getRecentAssignments(10);
					
					
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlRpcFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	return null;
	        }
	        protected void onPostExecute(String file_url) {
	        	
	        	if(posts!=null){
	        		assignmentsCount = "" + posts.size();
	        		assignTV.setText(getResources().getString(R.string.title_assignments) + " (" + assignmentsCount + ")");
	        	
	        		//store new value in prefs
	        		Editor editor = prefs.edit();
	        		editor.putString("assignmentsCount", assignmentsCount);
	        		editor.commit();
	        	}
	        	
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
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_sync:            
        	//check if service is already running
        	//check if encryption is running
        	//check if export is running
        	if(isServiceRunning(XMLRPCSyncService.class)){
  	          	Toast.makeText(getBaseContext(), "Syncing is already started!", Toast.LENGTH_LONG).show();
        	}else if (isServiceRunning(EncryptionService.class)){
  	          	Toast.makeText(getBaseContext(), "Please wait for encryption to finish!", Toast.LENGTH_LONG).show();
        	}else if(isServiceRunning(Export2SDService.class)){
  	          	Toast.makeText(getBaseContext(), "Please wait for exporting to finish!", Toast.LENGTH_LONG).show();
        	}else{
	        	isInternetPresent = cd.isConnectingToInternet();
	  	       	if(!isInternetPresent){
	  	          	Toast.makeText(getBaseContext(), "You have no connection!", Toast.LENGTH_LONG).show();
	  	        }else{
	  	        	dialog.dismiss();
	  	        	startService(new Intent(HomePanelsActivity.this,XMLRPCSyncService.class));
	  	        }   
        	}
        	//Intent i = new Intent(getApplicationContext(),SyncActivity.class);
			//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//startActivity(i);
			
            break;
            
        case R.id.button_export:
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
        	}else if(isServiceRunning(XMLRPCSyncService.class)){
  	          	Toast.makeText(getBaseContext(), "Please wait for sync to finish!", Toast.LENGTH_LONG).show();
        	}else{
        		Intent eS = new Intent(HomePanelsActivity.this,Export2SDService.class);
        		eS.putExtra("includeExported", includeExported);
	  	        startService(eS); 
        	}
        	/*
        	Intent i2 = new Intent(getApplicationContext(), Export2SD.class);
        	i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i2);
            break;
            */
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
	public void onResume() {
		super.onResume();
		
		boolean isExternalStorageReady = ((StoryMakerApp)getApplication()).isExternalStorageReady();
		
		if (!isExternalStorageReady)
		{
			//show storage error message
			new AlertDialog.Builder(this)
            .setTitle(getString(R.string.app_name))
            .setIcon(android.R.drawable.ic_dialog_info)
            .setMessage(R.string.err_storage_not_ready)
            .show();
			
		}
		
	}    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


	private void checkForTor ()
    {
    	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

	     boolean useTor = settings.getBoolean("pusetor", false);
	     
	     if (useTor)
	     {
	    	 OrbotHelper oh = new OrbotHelper(this);
	    	 
	    	 if (!oh.isOrbotInstalled())
	    	 {
	    		 oh.promptToInstall(this);
	    	 }
	    	 else if (!oh.isOrbotRunning())
	    	 {
	    		 oh.requestOrbotStart(this);
	    	 }
	    	 
	     }
    }

    //if the user hasn't registered with the user, show the login screen
    private void checkCreds ()
    {	 
    	//if(userFunctions.isUserLoggedIn(getApplicationContext())){
    		//Do nothing
    	//}
    	//else{
    	//	Intent intent = new Intent(this,LoginPreferencesActivity.class);
        //	startActivity(intent);
    	//}
    	
    	
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       
        String user = settings.getString("logged_in",null);
        
        //Toast.makeText(getApplicationContext(), " " + user, Toast.LENGTH_LONG).show();
        
        if ((user == null)||(user.equals("0")))
        {
        	Intent intent = new Intent(this,LoginActivity.class);
        	startActivity(intent);
        	finish();
        }
    }
    
 
    /*

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
        	mSlidingMenu.toggle();
        }
        else if (item.getItemId() == R.id.menu_settings)
        {
			showPreferences();
		}
		else if (item.getItemId() == R.id.menu_logs)
		{
			collectAndSendLog();
		}
		else if (item.getItemId() == R.id.menu_new_project)
		{
			 startActivity(new Intent(this, StoryNewActivity.class));
		}
		else if (item.getItemId() == R.id.menu_bug_report)
		{
			String url = "https://docs.google.com/forms/d/1KrsTg-NNr8gtQWTCjo-7Fv2L5cml84EcmIuGGNiC4fY/viewform";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
		}
		else if (item.getItemId() == R.id.menu_about)
		{
			String url = "https://storymaker.cc";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
		}
        
		return true;
	}
    */
	void collectAndSendLog(){
		
		File fileLog = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"storymakerlog.txt");
		fileLog.getParentFile().mkdirs();
		
		try
		{
			writeLogToDisk("StoryMaker",fileLog);
			writeLogToDisk("FFMPEG",fileLog);
			writeLogToDisk("SOX",fileLog);
			
			Intent i = new Intent(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_EMAIL, "help@guardianproject.info");
			i.putExtra(Intent.EXTRA_SUBJECT, "StoryMaker Log");
			i.putExtra(Intent.EXTRA_TEXT, "StoryMaker log email: " + new Date().toGMTString());
			i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileLog));
			i.setType("text/plain");
			startActivity(Intent.createChooser(i, "Send mail"));
		}
		catch (IOException e)
		{
			
		}
    }
	
	private void showPreferences ()
	{
		Intent intent = new Intent(this,SimplePreferences.class);
		this.startActivityForResult(intent, 9999);
	}


    
    

    //for log sending
    public static final String LOG_COLLECTOR_PACKAGE_NAME = "com.xtralogic.android.logcollector";//$NON-NLS-1$
    public static final String ACTION_SEND_LOG = "com.xtralogic.logcollector.intent.action.SEND_LOG";//$NON-NLS-1$
    public static final String EXTRA_SEND_INTENT_ACTION = "com.xtralogic.logcollector.intent.extra.SEND_INTENT_ACTION";//$NON-NLS-1$
    public static final String EXTRA_DATA = "com.xtralogic.logcollector.intent.extra.DATA";//$NON-NLS-1$
    public static final String EXTRA_ADDITIONAL_INFO = "com.xtralogic.logcollector.intent.extra.ADDITIONAL_INFO";//$NON-NLS-1$
    public static final String EXTRA_SHOW_UI = "com.xtralogic.logcollector.intent.extra.SHOW_UI";//$NON-NLS-1$
    public static final String EXTRA_FILTER_SPECS = "com.xtralogic.logcollector.intent.extra.FILTER_SPECS";//$NON-NLS-1$
    public static final String EXTRA_FORMAT = "com.xtralogic.logcollector.intent.extra.FORMAT";//$NON-NLS-1$
    public static final String EXTRA_BUFFER = "com.xtralogic.logcollector.intent.extra.BUFFER";//$NON-NLS-1$


	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		
		super.onActivityResult(arg0, arg1, arg2);
		

		boolean changed = ((StoryMakerApp)getApplication()).checkLocale();
		if (changed)
		{
			finish();
			startActivity(new Intent(this,HomePanelsActivity.class));
			
		}
	}
	
	public class MyAdapter extends FragmentPagerAdapter {
		 
		 int[] mMessages;
		 int[] mTitles;
		 
	        public MyAdapter(FragmentManager fm, int[] titles, int[] messages) {
	            super(fm);
	            mTitles = titles;
	            mMessages = messages;
	        }

	        @Override
	        public int getCount() {
	            return mMessages.length;
	        }

	        @Override
	        public Fragment getItem(int position) {
	        	Bundle bundle = new Bundle();
	        	bundle.putString("title",getString(mTitles[position]));
	        	bundle.putString("msg", getString(mMessages[position]));
	        	
	        	Fragment f = new MyFragment();
	        	f.setArguments(bundle);
	        	
	            return f;
	        }
	    }
	
	public static final class MyFragment extends Fragment {
	
		String mMessage;
		String mTitle;
		
		 /**
       * When creating, retrieve this instance's number from its arguments.
       */
      @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          mTitle = getArguments().getString("title");
          mMessage = getArguments().getString("msg");
      }

      /**
       * The Fragment's UI is just a simple text view showing its
       * instance number.
       */
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState) {
          
          ViewGroup root = (ViewGroup) inflater.inflate(R.layout.card_pager_textview, null);
          
          ((TextView)root.findViewById(R.id.title)).setText(mTitle);
          
          ((TextView)root.findViewById(R.id.description)).setText(mMessage);
          
          return root;
      }
	
	}

    
	 private void writeLogToDisk (String tag, File fileLog) throws IOException
	 {
		 
		FileWriter fos = new FileWriter(fileLog,true);
		BufferedWriter writer = new BufferedWriter(fos);

		      Process process = Runtime.getRuntime().exec("logcat -d " + tag + ":D *:S");
		      BufferedReader bufferedReader = 
		        new BufferedReader(new InputStreamReader(process.getInputStream()));

		     
		      String line;
		      while ((line = bufferedReader.readLine()) != null) {
		    	  
		    	  writer.write(line);
		    	  writer.write('\n');
		      }
		      bufferedReader.close();

		      writer.close();
	 }    
}

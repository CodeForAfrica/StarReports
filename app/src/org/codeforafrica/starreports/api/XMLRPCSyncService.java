package org.codeforafrica.starreports.api;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Timer;

import javax.crypto.Cipher;

import org.codeforafrica.starreports.ConnectionDetector;
import org.codeforafrica.starreports.HomePanelsActivity;
import org.codeforafrica.starreports.Report_PageIndicatorActivity;
import org.codeforafrica.starreports.StoryMakerApp;
import org.codeforafrica.starreports.encryption.Encryption;
import org.codeforafrica.starreports.model.Media;
import org.codeforafrica.starreports.model.Project;
import org.codeforafrica.starreports.model.Report;
import org.codeforafrica.starreports.server.ServerManager;
import org.holoeverywhere.app.ProgressDialog;

import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class XMLRPCSyncService extends Service {
	String token;
	String user_id;
	ProgressBar pDialog;
	
	
	private ArrayList<Report> mListReports = new ArrayList<Report>();
		
 	Button done;
 	TextView log;
 	AsyncTask<String, String, String> check_token;
 	
 	//Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    Timer timer;
    SharedPreferences prefs;
    String delete_after_sync;
    int rid;
    Report report;
    @Override
    public IBinder onBind(Intent arg0) {
          return null;
    }
    @Override
    public void onCreate() {
          super.onCreate();
    }

    @Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);

	       Bundle extras = intent.getExtras();
		   showNotification("Syncing...");
		   cd = new ConnectionDetector(getApplicationContext());
        
	     //Bundle extras = intent.getExtras();
	       if(intent.hasExtra("rid")){
	    	   rid = extras.getInt("rid");
	    	   mListReports.add(Report.get(getApplicationContext(), rid));
	       }else{
	    	   mListReports = Report.getAllAsList(getApplicationContext());
	       }
	       
	       new publish_report().execute();
	       
	       return startId;
          
    }
    
    @Override
    public void onDestroy() {
          super.onDestroy();

    }
    
    private void showNotification(String message) {
    	 CharSequence text = message;
    	 
    	 Notification notification = new Notification(R.drawable.ic_menu_upload, text, System.currentTimeMillis());
    	 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
    	                new Intent(this, HomePanelsActivity.class), 0);
    	notification.setLatestEventInfo(this, "StarReports: Sync",
    	      text, contentIntent);
    	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notify("service started", 0, notification);
	}
    
    public void stopService(){
    	this.stopSelf();
    }
    	
    class publish_report extends AsyncTask<String, String, String> {
	   	 @Override
	        protected void onPreExecute() {
		   		super.onPreExecute();
		        
	        }
	        protected String doInBackground(String... args) {
	        	
	       for (int i = 0; i < mListReports.size(); i++) {
	    		 
	    	   if(mListReports.get(i)!=null){
	    		
	    		   report = mListReports.get(i);
	        	
	        	ServerManager sm = StoryMakerApp.getServerManager();
	            //sm.setContext(getBaseContext());

	            String postId="";
	            String urlPost="";
	            XmlRpcStruct structA = new XmlRpcStruct();
				structA.put("key","assignment_id");
				structA.put("value",report.getAssignment());
			 	
				
				try {
					
					
					StringBuffer sbBody = new StringBuffer();
					sbBody.append("");
					
					//upload media
					ArrayList<Project> mListProjects;
					mListProjects = Project.getAllAsList(getApplicationContext(), rid);
					String thumbnail= null;
				 	for (int j = 0; j < mListProjects.size(); j++) {
				 		Project project = mListProjects.get(j);
				 		
				 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
				 	
					 	for (Media media: mediaList){

					 		if(media!=null){
					 			
					 			
					 		String ppath = media.getPath();
						 	String ptype = media.getMimeType();
						 	
						 	String file = ppath;
						 	
						 	Log.d("encryption status:", "encryptionstatus" + media.getEncrypted());
						 	
						 	//if encrypted, decrypt before upload
						 	if(media.getEncrypted()!=0){
						 	
						 		Cipher cipher;
								try {				
									cipher = Encryption.createCipher(Cipher.DECRYPT_MODE);
									Encryption.applyCipher(file, file+"_", cipher);
								}catch (Exception e) {
									// TODO Auto-generated catch block
									Log.e("Encryption error", e.getLocalizedMessage());
									e.printStackTrace();
								}
								//Then delete original file
								File oldfile = new File(file);
								oldfile.delete();
								//Then remove _ on encrypted file
								File newfile = new File(file+"_");
								newfile.renameTo(new File(file));
						 	}	
						 		String murl = sm.addMedia(ptype, new File(file));
						 		//create link depending on media type
						 		if(ptype.contains("image")){
						 			murl = "<img width=\"100%\" src=\"" + murl + "\"/>";
						 		}else if(ptype.contains("video")){
						 			murl = "[video width=\"640\" height=\"272\" mp4=\""+ murl +"\"][/video]";
						 		}else{
						 			murl = "[audio mp3=\"" + murl +"\"][/audio]";
						 		}
						 		//post file here
						 		sbBody.append(murl);
						 		sbBody.append("\n\n");
						 		
						 		String bmp = Media.getThumbnailUrl(getApplicationContext(),media,project);
		            			if (bmp != null)
		            				thumbnail = bmp;
					 		}
				 		}
				 	}
					
				 	
				 	//upload thumbnail
				 	if(thumbnail!=null){
				 		thumbnail = sm.addThumbnail("image/jpeg", new File(thumbnail));
				 	}
				 	
				 	String pDescription = report.getDescription() + "==Media==\n\n" + sbBody.toString();
				 	
				 	postId = sm.post2(report.getTitle(), pDescription, null, null, null, null, null, null, structA, thumbnail);
					urlPost = sm.getPostUrl(postId);
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlRpcFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	   }
	    	   
	       }
	            
	            return "";    
				
	        }
	        protected void onPostExecute(String file_url) {
	        	showNotification("Syncing complete!");
	        	stopService();
		   }
	}
}

package org.codeforafrica.ziwaphi.api;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Timer;

import javax.crypto.Cipher;

import org.codeforafrica.ziwaphi.AppConstants;
import org.codeforafrica.ziwaphi.ConnectionDetector;
import org.codeforafrica.ziwaphi.HomePanelsActivity;
import org.codeforafrica.ziwaphi.Report_PageIndicatorActivity;
import org.codeforafrica.ziwaphi.StoryMakerApp;
import org.codeforafrica.ziwaphi.encryption.Encryption;
import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.model.Report;
import org.codeforafrica.ziwaphi.server.ServerManager;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class XMLRPCSyncService extends Service {
	String token;
	String user_id;
	ProgressBar pDialog;
	boolean syncSuccess = true;
	
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
    
    private Builder mNotifyBuilder;
    int notifyID = 1;
    NotificationManager mNotificationManager;
    int numMessages = 0;
    
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
	       showNotificationBuilder("Sync", "Initializing...");
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
    
private void showNotification(String header, String message) {
    	
    	if(header.equals(null)){
    		mNotifyBuilder.setContentText(message).setNumber(++numMessages);
    	}else{
    	mNotifyBuilder.setContentText(message).setContentTitle(header)
        .setNumber(++numMessages);
    	}
    // Because the ID remains unchanged, the existing notification is
    // updated.
    mNotificationManager.notify(
            notifyID,
            mNotifyBuilder.build());
	}
    
    public void showNotificationBuilder(String header, String message){
    	mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
    	   	
    	   	
    	mNotifyBuilder = new NotificationCompat.Builder(this)
    	    .setContentTitle(header)
    	    .setContentText(message)
    	    .setSmallIcon(R.drawable.ic_menu_upload);

    	mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
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
	    	   
	    	   int total = i+1;
	    	   
	    	   showNotification("Syncing Report " + total + " of " + mListReports.size(), "Started...");

	    		 
	    	   if(mListReports.get(i)!=null){
	    		
	    		report = mListReports.get(i);
	    		
	    		int report_id = report.getId();
	    		
	    		
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
										
					mListProjects = Project.getAllAsList(getApplicationContext(), report_id);
					String thumbnail= null;
					
					//find total media items
			 		int totalMedia = 0;
					for (int j = 0; j < mListProjects.size(); j++) {
				 		Project project = mListProjects.get(j);
				 		
				 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
				 		
				 		for(Media media: mediaList){
				 			totalMedia++;
				 		}
					}
					
					int currentItem = 0;
				 	for (int j = 0; j < mListProjects.size(); j++) {
				 		Project project = mListProjects.get(j);
				 		
				 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
				 		
				 	
					 	for (Media media: mediaList){
					 		
					 		String ptype = media.getMimeType();
						 	
						 	String optype = "video";
						 	if(ptype.contains("image")){
						 		optype = "image";
						 	}else if(ptype.contains("video")){
						 		optype = "video";
						 	}else if(ptype.contains("audio")){
						 		optype = "audio";
						 	}
						 						 		
				    		showNotification(null, "Uploading media " + currentItem + " of " + totalMedia + " ["+ optype +"]");		 	
						 	

					 		if(media!=null){
					 			
					 		if(media.getObject()!=1){
					 			
					 			
					 		String ppath = media.getPath();
						 	
						 	String file = ppath;
						 							 	
						 	//if encrypted, decrypt before upload
						 	if(media.getEncryptionCompleted()==1){
						 	
						 		Cipher cipher;
								try{				
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
						 		
						 		media.setObject_Id(1);
						 		
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
		            			
		            			//Re-encrypt
		    			 		Cipher cipher;
		    					try {
		    						//rename old file
		    						File oldfile = new File(file);
		    						oldfile.renameTo(new File(file+"_"));
		    						
		    						cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
		    						Encryption.applyCipher(file, file+"_", cipher);
		    						
		    						//Then delete decrypted file
		    						File newfile = new File(file);
		    						newfile.delete();
		    						
		    						//restore name of old file
		    						File restoredFile = new File(file+"_");
		    						restoredFile.renameTo(new File(file));
		    						
		    					}catch (Exception e) {
		    						// TODO Auto-generated catch block
		    						Log.e("Encryption error", e.getLocalizedMessage());
		    						e.printStackTrace();
		    					}
					 		}
					 	}
				 	}
				 }
					
				 	
				 	//upload thumbnail
				 	if(thumbnail!=null){
				 		thumbnail = sm.addThumbnail("image/jpeg", new File(thumbnail));
				 	}
				 				 	
				 	String pDescription = report.getDescription() + " " + sbBody.toString();
				 	
				 	
		    		//update report and upload media not already uploaded
					postId = sm.post2(report.getTitle(), pDescription, null, null, null, null, null, null, structA, thumbnail, Integer.parseInt(report.getServerId()));

					 	//check if report has server id
			    	if(report.getServerId().equals("0")){
			    		urlPost = sm.getPostUrl(postId);
						report.setServerId(postId);
						report.save();
		    		}
					
				} catch (MalformedURLException e) {
					syncSuccess = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlRpcFault e) {
					syncSuccess = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	   }
	       }
	            
	            return "";    
				
	        }
	        protected void onPostExecute(String file_url) {
	        	if(syncSuccess == false){
	        		
	        		showNotification("Warning! Sync was successfull!", "Try again. Include already synced!");

	        	}else{
	        		
	        		showNotification("Success", "Sync was successfull!");

	        	}
	        	stopService();
		   }
	}
}

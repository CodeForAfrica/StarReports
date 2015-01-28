package org.codeforafrica.ziwaphi.encryption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.crypto.Cipher;

import android.R;

import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.model.ProjectTable;
import org.codeforafrica.ziwaphi.model.Report;
import org.codeforafrica.ziwaphi.model.Scene;
import org.codeforafrica.ziwaphi.model.SceneTable;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class EncryptionService extends Service{
	String message;
	String file;
	String tempfile;
	Media media;
	
	int media_id;
	
	boolean success = true;

	private Builder mNotifyBuilder;
    int notifyID = 2;
    NotificationManager mNotificationManager;
    int numMessages = 0;
    
	@Override
    public void onCreate() {
          super.onCreate();
          
          
    }
	  @Override
	  public void onStart(Intent intent, int startId) {
	      
		  super.onStart(intent, startId);
	      
		  Bundle extras = intent.getExtras(); 
	       
		  
	      media_id = extras.getInt("media_id");
	      
	      media = Media.get(getApplicationContext(), media_id);
	       
          file = media.getPath();
          
          tempfile = file+"_";
          
          //TODO: message format -> Encrypting {media type}: {caption} (if !caption of {report title})
	      
          //get media type
          String ptype = media.getMimeType();
		 	
		 	String optype = "video";
		 	if(ptype.contains("image")){
		 		optype = "image";
		 	}else if(ptype.contains("video")){
		 		optype = "video";
		 	}else if(ptype.contains("audio")){
		 		optype = "audio";
		 	}
		 
		 String caption = "";
		 
		 //get caption or report title
		 Scene s = (Scene)(new SceneTable()).get(getApplicationContext(), media.getSceneId());		 
		 Project p = (Project)(new ProjectTable()).get(getApplicationContext(), s.getProjectId());	 
		 Report r = Report.get(getApplicationContext(), p.getReport());
		 
		 if(!p.getTitle().equals("")){
			 caption = p.getTitle();
			 message = optype + " : " + caption;
		 }else{
			 caption = r.getTitle();
			 message = optype + " : of report " + caption;
		 }
	       
	     showNotificationBuilder("Encryption", "Encrypting " + message);
	      
	     new encryptFileTask().execute();
	       	       
	  }
	  
	  public void encryptFile(){
		  
          
          Cipher cipher;
			
			try {
				
					//encrypt to temp file location
					cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
					Encryption.applyCipher(file, tempfile, cipher);
					
					//assuming encryption went well	
						//delete original
						File oldfile = new File(file);
						oldfile.delete();
						
						//rename temp to original
						File newfile = new File(tempfile);
						newfile.renameTo(new File(file));         
					
					media.setEncryptionCompleted(1);
					
                	media.save();
                  
				}catch (IOException e) {
					Log.e("Encryption error", e.getLocalizedMessage());
					e.printStackTrace();
					
					doEncryptionFailed();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					doEncryptionFailed();
				}
			
	  }
	  
	  public void doEncryptionFailed(){
		  //tempfile might have been created, original file still exists?
		  
		  media.setEncryptionCompleted(0);
		  media.setEncryptionStarted(0);
		  
		  media.save();
		  
		  //delete temp file
		  if(new File(tempfile).exists()){
			  
			  (new File(tempfile)).delete();
			  
		  }
		  //TODO: notify user encryption failed
		  showNotification(null, "Failed, will retry!");
		  success = false;
	  }
	  
	  class encryptFileTask extends AsyncTask<String, String, String> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				media.setEncryptionStarted(1);
				media.save();
				
			}
			protected String doInBackground(String... args) {
				 encryptFile();
				return null;
			}
		protected void onPostExecute(String file_url) {
			
			if(success){
				message = "Completed!";
				showNotification(null, message);
			}
			
			endEncryption();
				
			}
		}

	public void endEncryption(){
        
		this.stopSelf();
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
    	    .setSmallIcon(R.drawable.ic_secure);
    	
    	mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());

    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}

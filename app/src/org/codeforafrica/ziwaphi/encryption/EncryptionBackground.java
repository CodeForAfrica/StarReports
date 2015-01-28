package org.codeforafrica.ziwaphi.encryption;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.codeforafrica.ziwaphi.api.XMLRPCSyncService;
import org.codeforafrica.ziwaphi.export.Export2SDService;
import org.codeforafrica.ziwaphi.model.Media;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class EncryptionBackground extends Service {
	String message;
	String file;
	Media media;
	
	ArrayList<Media> unEncryptedMedia;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Timer timer = new Timer();
        TimerTask updateProfile = new mainTask();
        timer.scheduleAtFixedRate(updateProfile, 0, 1000);
  	}
	
	private class mainTask extends TimerTask
    { 
        public void run() 
        {
        	 new Thread(new Runnable() {
				public void run() {
						//only execute if no file is being encrypted, and sync/export are not running
						if(!isServiceRunning()){
							
							//get all unecrypted media(not completed, started but failed)
							unEncryptedMedia = Media.getUnEncrypted(getApplicationContext());
							
							//if they exist, pick first one
							if(unEncryptedMedia.size()>0){
								
								media = unEncryptedMedia.get(0);
								
								if(media!=null){									
									//pass to encryption service
									Intent startMyService= new Intent(getApplicationContext(), EncryptionService.class);
					                startMyService.putExtra("media_id", media.getId());
					                startService(startMyService);					                
								}else{									
									//media.delete();									
									//TODO:what are the consequences of delete?
								}
							}			                
			        	}
				}}).start();
        	
        }
    }   
	
	public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EncryptionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
            if (XMLRPCSyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
            if (Export2SDService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
	}
	
}
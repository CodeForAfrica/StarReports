package org.codeforafrica.ziwaphi.export;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;

import org.codeforafrica.ziwaphi.AppConstants;
import org.codeforafrica.ziwaphi.encryption.Encryption;
import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.model.Report;
import org.holoeverywhere.widget.Toast;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Export2SDService extends Service {
	private ArrayList<Report> mListReports;
	private ArrayList<Project> mListProjects;
	String data = "";
	String ext;
	int BUFFER = 2048;
	String delete_after_export;
	int reportscount = 0;
	String eI;
	SharedPreferences pref;
	String zipName;
	public File newFolder;
	
	private Builder mNotifyBuilder;
    int notifyID = 3;
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
	       eI = extras.getString("includeExported");
	       
	       showNotificationBuilder("Export to SD", "Initializing...");
	       
	        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	      	delete_after_export = pref.getString("delete_after_export",null);
	      	new export2SD().execute();
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
    	    .setSmallIcon(R.drawable.ic_menu_send);
    	
    	mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());

    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getIMEI(Context context){

	    TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); 
	    String imei = mngr.getDeviceId();
	    return imei;
	}
	class export2SD extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		protected String doInBackground(String... args) {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String userid = prefs.getString("user_id", "0");
			            
            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
            String timestamp = s.format(new Date());
            
            if(eI.equals("0")){
            	mListReports = Report.getAllAsList_EI(getApplicationContext(), eI);
            }else{
            	mListReports = Report.getAllAsList(getApplicationContext());
            }
		 	
            reportscount = mListReports.size();

			zipName = "ziwaphi"+userid+"-"+String.valueOf(reportscount)+"-"+timestamp;
			//create folder and transfer all content here
			newFolder = new File(Environment.getExternalStorageDirectory(), zipName);
			newFolder.mkdirs();
						
			ext = String.valueOf(Environment.getExternalStorageDirectory());
		 	ext += "/"+AppConstants.TAG;
		 	
			//Begin "XML" file
		 	data += "<?xml version='1.0' encoding='UTF-8'?>\n";
		 	data += "<reports>\n";
		 	data += "<imei>"+getIMEI(getApplicationContext())+"</imei>";
		 	data += "<user_id>"+userid+"</user_id>";
		 			 	
			 
			 for (int i = 0; i < reportscount; i++) {
				 	//check if report actually exists
				 	if(mListReports.get(i)!=null){
				 		
				 		int current = i + 1;
				 		showNotification("Export: Report " + current + " of " + mListReports.size(), "Started...");
					 	
					 	data += "<report>\n";
					 	
					 	Report report = mListReports.get(i);
					 	
					 	//TODO: use xml output to set exported
					 	report.setExported("1");
					 	report.save();
					 	
					 	data += "<id>"+String.valueOf(report.getId())+"</id>\n";
					 	
					 	//create report folder
					 	File rFolder = new File(newFolder, "/"+String.valueOf(report.getId()));
					 	rFolder.mkdirs();
					 	
					 	data += "<report_title>"+report.getTitle()+"</report_title>\n";
					 	
					 	String issue = report.getIssue();
					 	if(issue.equals("0")){
					 		issue = "1";
					 	}
					 	data += "<category>"+issue+"</category>\n";
					 	
					 	String category = report.getSector();
					 	if(category.equals("0")){
					 		category = "1";
					 	}
					 	data += "<sector>"+category+"</sector>\n";
					 	
					 	data += "<entity>"+report.getEntity()+"</entity>\n";
					 	data += "<location>"+report.getLocation()+"</location>\n";
					 	data += "<report_date>"+report.getDate()+"</report_date>\n";
					 	data += "<description>"+report.getDescription()+"</description>\n";
					 	data += "<report_objects>\n";
					 	
					 	mListProjects = Project.getAllAsList(getApplicationContext(), report.getId());
					 	for (int j = 0; j < mListProjects.size(); j++) {
					 		Project project = mListProjects.get(j);
						 	data += "<object>\n";
						 	data += "<object_id>"+project.getId()+"</object_id>\n";
						 	data += "<object_title>"+project.getTitle()+"</object_title>\n";
						 	
						 	Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
						 	for (Media media: mediaList){

						 		if(media!=null){
						 			String ptype = media.getMimeType();
								 	
								 	String optype = "video";
								 	if(ptype.contains("image")){
								 		optype = "image";
								 	}else if(ptype.contains("video")){
								 		optype = "video";
								 	}else if(ptype.contains("audio")){
								 		optype = "audio";
								 	}
								 	
						 			int total = j + 1;
						    		showNotification(null, "Adding media " + total + " of " + mListProjects.size() + " ["+ optype +"]");		 	
								 	
							 		String path = media.getPath();
	
							 		String file = path;
							 		
							 		
							 		path = path.replace(ext, "");
									String outfile = newFolder.getPath().toString()+path;
									
									if(media.getEncryptionCompleted()==1){
										//Decrypt file
								 		Cipher cipher;
										try {
											cipher = Encryption.createCipher(Cipher.DECRYPT_MODE);
											Encryption.applyCipher(file, outfile, cipher);
										}catch (Exception e) {
											// TODO Auto-generated catch block
											Log.e("Encryption error", e.getLocalizedMessage());
											e.printStackTrace();
										}
									}else{
										//copy file to zip location
										try {
											
											Log.d("i/o", "i: " + file + " o: " + outfile);
											
											copyFileUsingFileChannels(new File(file), new File(outfile));
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
							 		
										
								 	data += "<object_media>"+path+"</object_media>\n";
									//}
							 		data += "<object_type>"+media.getMimeType()+"</object_type>\n";
							 	}
								data += "</object>\n";
						 	}
					 	}
					 	data += "</report_objects>\n";
					 	data += "</report>\n";
					 	
				 	}
				}
			 data += "</reports>";
			 
			 writeToFile(data);
			 
			 
			 
			 	
			//Now create new zip
			zipFileAtPath(newFolder.getAbsolutePath(), String.valueOf(getSD())+"/"+zipName+".zip");
			
			//encrypt zip file
	    	String encrypt_zip_files = prefs.getString("encrypt_zip_files",null);
	    	if(encrypt_zip_files.equals("1")){
	    		
	    		String file = String.valueOf(getSD())+"/"+zipName+".zip";
				
				Cipher cipher;
				try {
					cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
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
			
	    	//delete zip source
	    	File zipsource = new File(newFolder.getAbsolutePath());
			DeleteRecursive(zipsource);
	    	
			
			return null;
		}
			
	protected void onPostExecute(String file_url) {
			showNotification("Export to SD", "Exported Successfully!");
			
			if(delete_after_export.equals("1")){
				deleteReports();
			}
			endExporting();
		}
	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	 public void deleteReports(){
			for(int i = 0; i<mListReports.size(); i++){
				if(mListReports.get(i)!=null){
				 	mListReports.get(i).delete();
				 	ArrayList<Project> mListProjects;
					mListProjects = Project.getAllAsList(getApplicationContext(), i);
				 	for (int j = 0; j < mListProjects.size(); j++) {
				 		mListProjects.get(j).delete();
				 	}
				}
			}
		}

	 public void endExporting(){
    	  this.stopSelf();
      }
	 
	public void writeToFile(final String fileContents) {
 		showNotification("Export to SD", "Attaching reports XML file...");

		try {
	            FileWriter out = new FileWriter(new File(newFolder, "/db.xml"));
	            out.write(fileContents);
	            out.close();
        	}catch (IOException e){
        		Log.d("Write Error!", e.getLocalizedMessage());
        	}

    }
	

	public boolean zipFileAtPath(String sourcePath, String toLocation) {
 		showNotification("Export to SD", "Zipping export...");

	    // ArrayList<String> contentList = new ArrayList<String>();
	    File sourceFile = new File(sourcePath);
	    try {
	        BufferedInputStream origin = null;
	        FileOutputStream dest = new FileOutputStream(toLocation);
	        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
	                dest));
	        if (sourceFile.isDirectory()) {
	            zipSubFolder(out, sourceFile, sourceFile.getParent().length());
	        }else {
	            byte data[] = new byte[BUFFER];
	            FileInputStream fi = new FileInputStream(sourcePath);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
	            out.putNextEntry(entry);
	            int count;
	            while ((count = origin.read(data, 0, BUFFER)) != -1) {
	                out.write(data, 0, count);
	            }
	        }
	        out.close();
	    }catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	    return true;
	}

	private void zipSubFolder(ZipOutputStream out, File folder,
	        int basePathLength) throws IOException {
	    File[] fileList = folder.listFiles();
	    BufferedInputStream origin = null;
	    for (File file : fileList) {
	        if (file.isDirectory()) {
	            zipSubFolder(out, file, basePathLength);
	        } else {
	            byte data[] = new byte[BUFFER];
	            String unmodifiedFilePath = file.getPath();
	            String relativePath = unmodifiedFilePath
	                    .substring(basePathLength);
	            Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
	            FileInputStream fi = new FileInputStream(unmodifiedFilePath);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(relativePath);
	            out.putNextEntry(entry);
	            int count;
	            while ((count = origin.read(data, 0, BUFFER)) != -1) {
	                out.write(data, 0, count);
	            }
	            origin.close();
	        }
	    }
	}

	public String getLastPathComponent(String filePath) {
	    String[] segments = filePath.split("/");
	    String lastPathComponent = segments[segments.length - 1];
	    return lastPathComponent;
	}
	
	public File getSD(){
		File extStorage = null;
		
		if(android.os.Build.VERSION.SDK_INT >= 19){
			extStorage = Environment.getExternalStorageDirectory();
		}else{
			if((new File("/mnt/external_sd/")).exists()){
				extStorage = new File("/mnt/external_sd/");
			}else if((new File("/mnt/extSdCard/")).exists()){
				extStorage = new File("/mnt/extSdCard/");
			}else{
				extStorage = Environment.getExternalStorageDirectory();
			}
		}
		
		return extStorage;
	}
	 
	private static void copyFileUsingFileChannels(File source, File dest)
			throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(dest).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if(inputChannel!=null){
				inputChannel.close();
				outputChannel.close();
			}else{
				Log.d("nullinput", "nullinput" + source.getAbsolutePath().toString());
			}
		}
	}
}

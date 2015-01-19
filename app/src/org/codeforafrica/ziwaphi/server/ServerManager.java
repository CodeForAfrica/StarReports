package org.codeforafrica.ziwaphi.server;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.codeforafrica.ziwaphi.AppConstants;
import org.codeforafrica.ziwaphi.StoryMakerApp;
import org.codeforafrica.ziwaphi.model.Auth;
import org.codeforafrica.ziwaphi.model.AuthTable;

import net.bican.wordpress.MediaObject;
import net.bican.wordpress.Category;
import net.bican.wordpress.Comment;
import net.bican.wordpress.MediaObject;
import net.bican.wordpress.Page;
import net.bican.wordpress.Wordpress;
import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServerManager {
    private static final String TAG = "ServerManager";
	private Wordpress mWordpress;	
	private String mServerUrl;
	private Context mContext;
	
	private final static String PATH_XMLRPC = "/xmlrpc.php";
	private final static String PATH_REGISTER = "/wp-login.php?action=register";
	private final static String PATH_LOGIN = "/wp-admin";
	
	public final static String CUSTOM_FIELD_MEDIUM = "medium"; //Text, Audio, Photo, Video
	
	public final static String CUSTOM_FIELD_MEDIUM_TEXT = "Text";
	public final static String CUSTOM_FIELD_MEDIUM_AUDIO = "Audio";
	public final static String CUSTOM_FIELD_MEDIUM_PHOTO = "Photo";
	public final static String CUSTOM_FIELD_MEDIUM_VIDEO = "Video";
	
	public final static String CUSTOM_FIELD_MEDIA_HOST = "media_value"; //youtube or soundcloud
	public final static String CUSTOM_FIELD_MEDIA_HOST_YOUTUBE = "youtube"; //youtube or soundcloud
	public final static String CUSTOM_FIELD_MEDIA_HOST_SOUNDCLOUD = "soundcloud"; //youtube or soundcloud

	private SharedPreferences mSettings;
	
	public ServerManager (Context context)
	{
		this(context, StoryMakerApp.initServerUrls(context));
		
	}
	
	public ServerManager (Context context, String serverUrl)
	{
		mContext = context;
		mServerUrl = serverUrl;
		
		mSettings = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
	       
	}
	
	public void setContext (Context context)
	{
		mContext = context;
	}
	
	//if the user hasn't logged in, show the login screen
    public boolean hasCreds ()
    {
        Auth checkAuth = (new AuthTable()).getAuthDefault(mContext, Auth.STORYMAKER);
        if (checkAuth == null) // added null check to prevent uncaught null pointer exception
            return false;
        else
            return checkAuth.credentialsAreValid();
    }
    
    private void connect() throws MalformedURLException, XmlRpcFault
    {
        if (mWordpress == null)
        {
            Auth auth = (new AuthTable()).getAuthDefault(mContext, Auth.STORYMAKER);
            if (auth != null) {
                String user = auth.getUserName();
                String pass = auth.getCredentials();
                if (user != null && user.length() > 0) {
                    connect(user, pass);
                    return;
                }
            }
            Log.e(TAG, "connect() bailing out, user credentials are null or blank");
        }
    }
	
	public void connect (String username, String password) throws MalformedURLException, XmlRpcFault
	{
		XmlRpcClient.setContext(mContext);
		

	    boolean useTor = mSettings.getBoolean("pusetor", false);
	    
		if (useTor)
		{
			XmlRpcClient.setProxy(true, "SOCKS", AppConstants.TOR_PROXY_HOST, AppConstants.TOR_PROXY_PORT);
		}
		else
		{
			XmlRpcClient.setProxy(false, null, null, -1);

		}
		
		Log.d(TAG, "Logging into Wordpress: " + username + '@' + mServerUrl + PATH_XMLRPC);
		mWordpress = new Wordpress(username, password, mServerUrl + PATH_XMLRPC);	
		
		mWordpress.getRecentPosts(1); //need to do a test to force authentication
	}
	
	public String getPostUrl (String postId) throws XmlRpcFault, MalformedURLException
	{
		connect();
		Page post = mWordpress.getPost(Integer.parseInt(postId));
		return post.getPermaLink();
		
	}
	public String getPostObject (String objectId) throws XmlRpcFault, MalformedURLException
	{
		connect();
		MediaObject object = mWordpress.getPostObject(objectId);
		return object.getUrl();
		
	}
	public Page getPost (String postId) throws XmlRpcFault, MalformedURLException
	{
		connect();
		Page post = mWordpress.getPost(Integer.parseInt(postId));
		return post;
		
	}
	public List<MediaObject> getPostAttachments (String postId) throws XmlRpcFault, MalformedURLException
	{
		connect();
		List<MediaObject> rPosts = mWordpress.getPostAttachments(postId);
		return rPosts;
	}
	public List<Page> getRecentAssignments (int num) throws XmlRpcFault, MalformedURLException
	{
		connect();
		List<Page> rPosts = mWordpress.getRecentAssignments(num);
		return rPosts;
	}
	public List<Page> getPostsByKeyword (int num, String keyword) throws XmlRpcFault, MalformedURLException
	{
		connect();
		List<Page> rPosts = mWordpress.getPostsByKeyword(num, keyword);
		return rPosts;
	}
	public List<Page> getRecentPosts (int num) throws XmlRpcFault, MalformedURLException
	{
		connect();
		List<Page> rPosts = mWordpress.getRecentPosts(num);
		return rPosts;
	}
	public List<Category> getCategories () throws XmlRpcFault, MalformedURLException
	{
		connect();
		return mWordpress.getCategories();
	}
	public List<Comment> getComments (Page page) throws XmlRpcFault, MalformedURLException
	{
		connect();
		return mWordpress.getComments(null, page.getPostid(), null, null);
	}

	public String post (String title, String body, String[] cats, String medium, String mediaService, String mediaGuid) throws XmlRpcFault, MalformedURLException
	{
		return post (title, body, cats, medium, mediaService, mediaGuid, null, null);
	}
	
	public String addMedia (String mimeType, File file) throws XmlRpcFault, MalformedURLException
	{
		connect();
		
		MediaObject mObj = null;
		
		if (file != null)
			mObj = mWordpress.newMediaObject(mimeType, file, false);
		
		return mObj.getUrl();
	}
	public String addThumbnail (String mimeType, File file) throws XmlRpcFault, MalformedURLException
	{
		connect();
		
		MediaObject mObj = null;
		
		if (file != null)
			mObj = mWordpress.newMediaObject(mimeType, file, false);
		
		return mObj.getID();
	}
	public String post (String title, String body, String[] catstrings, String medium, String mediaService, String mediaGuid, String mimeType, File file) throws XmlRpcFault, MalformedURLException
	{
		connect();
		
		MediaObject mObj = null;
		
		if (file != null)
			mObj = mWordpress.newMediaObject(mimeType, file, false);
		
		Page page = new Page ();
		page.setTitle(title);
		
		StringBuffer sbBody = new StringBuffer();
		sbBody.append(body);
		
		if (mObj != null)
		{
			sbBody.append("\n\n");
			sbBody.append(mObj.getUrl());
		}
		
		page.setDescription(sbBody.toString());
		
		if (catstrings != null && catstrings.length > 0)
		{
			XmlRpcArray cats = new XmlRpcArray();
			for (String catstr : catstrings)
				cats.add(catstr);
			page.setCategories(cats);
		}
		
		XmlRpcArray custom_fields = new XmlRpcArray();

		
		if (medium != null)
		{

			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","medium");
			struct.put("value",medium);			
			custom_fields.add(struct);

		}

		if (mediaService != null)
		{
			
			
			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","media_value");
			struct.put("value",mediaService);
			custom_fields.add(struct);

		}
		
		if (mediaGuid != null)
		{
			
			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","media_guid");
			struct.put("value",mediaGuid);
			custom_fields.add(struct);

		}
		
		

		page.setCustom_fields(custom_fields);
		
		boolean publish = true; //let's push it out!
		String postId = mWordpress.newPost(page, publish);
		
		
		return postId;
	}
	public String post2 (String title, String body, String[] catstrings, String medium, String mediaService, String mediaGuid, String mimeType, File file, XmlRpcStruct Assignment, String thumbID, int serverId) throws XmlRpcFault, MalformedURLException
	{
		connect();
		
		MediaObject mObj = null;
		
		if (file != null)
			mObj = mWordpress.newMediaObject(mimeType, file, false);
		
		Page page = new Page ();
		page.setTitle(title);
		
		if(serverId!=0){
			page.setPostid(serverId);
		}
		
		if(thumbID !=null){
			page.setThumbnail(thumbID);
		}
		
		StringBuffer sbBody = new StringBuffer();
		sbBody.append(body);
		
		if (mObj != null)
		{
			sbBody.append("\n\n");
			sbBody.append(mObj.getUrl());
		}
		
		page.setDescription(sbBody.toString());
		
		if (catstrings != null && catstrings.length > 0)
		{
			XmlRpcArray cats = new XmlRpcArray();
			for (String catstr : catstrings)
				cats.add(catstr);
			page.setCategories(cats);
		}
		
		XmlRpcArray custom_fields = new XmlRpcArray();

		
		if (medium != null)
		{

			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","medium");
			struct.put("value",medium);			
			custom_fields.add(struct);

		}

		if (mediaService != null)
		{
			
			
			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","media_value");
			struct.put("value",mediaService);
			custom_fields.add(struct);

		}
		
		if (mediaGuid != null)
		{
			
			XmlRpcStruct struct = new XmlRpcStruct();
			struct.put("key","media_guid");
			struct.put("value",mediaGuid);
			custom_fields.add(struct);

		}
		
		custom_fields.add(Assignment);

		page.setCustom_fields(custom_fields);
		
		boolean publish = false; //let's push it out!
		
		String postId;
		
		if(serverId!=0){
			mWordpress.editPost(serverId, page, publish);
			postId = "" + serverId;
			
		}else{
			postId = mWordpress.newPost(page, publish);

		}
				
		
		return postId;
	}
	public void createAccount (Activity activity)
	{
		//open web view here to reg form
		Intent intent = new Intent(mContext,WebViewActivity.class);
		intent.putExtra("title", "New Account");
		intent.putExtra("url", mServerUrl + PATH_REGISTER);
		
		activity.startActivity(intent);
	}
	
	public void showPost (String title, String url)
	{		
		Intent intent = new Intent(mContext,WebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		
		mContext.startActivity(intent);
	}

	public void postEdit(String title, String pDescription, Object object,
			Object object2, Object object3, Object object4, Object object5,
			Object object6, XmlRpcStruct structA, String thumbnail) {
		// TODO Auto-generated method stub
		
	}
	
}

package org.codeforafrica.starreports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.bican.wordpress.Attachment;
import net.bican.wordpress.MediaObject;
import net.bican.wordpress.Page;

import org.codeforafrica.starreports.ui.MyCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;

import redstone.xmlrpc.XmlRpcFault;

import com.animoto.android.views.DraggableGridView;
import com.fima.cardsui.views.CardUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
@SuppressLint("ValidFragment")
public class LatestReportsFragment extends Fragment {
    public ViewPager mAddClipsViewPager;
    View mView = null;
    ProgressDialog pDialog;
    private BaseActivity mActivity;
    private SharedPreferences mSettings;
    private ProgressBar pB;

    protected DraggableGridView mOrderClipsDGV;
    List<Page> posts = null;
    private CardUI mCardView;
    private void initFragment ()
    {
    	mActivity = (BaseActivity)getActivity();
    	
        //mHandlerPub = mActivity.mHandlerPub;

        mSettings = PreferenceManager
        .getDefaultSharedPreferences(getActivity().getApplicationContext());
	
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	initFragment ();
    	
    	int layout = R.layout.fragment_latest_reports;//getArguments().getInt("layout");
    	
        mView = inflater.inflate(layout, null);
        pB = (ProgressBar)mView.findViewById(R.id.pBLoading);

      //init CardView
        mCardView = (CardUI) mView.findViewById(R.id.cardsview);
        mCardView.setSwipeable(false);
        new GetRecentPosts().execute();
        
        return mView;
    }
    
    
    
    class GetRecentPosts extends AsyncTask<String, String, String> {
   	 @Override
        protected void onPreExecute() {
	   		super.onPreExecute();
	        /*pDialog = new ProgressDialog(mActivity.getBaseContext());
	        pDialog.setMessage("Retrieving latest posts...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        //pDialog.show();
	         *  
	         */
        }
        protected String doInBackground(String... args) {
        	
        	/*    
        	APIFunctions apiFunctions = new APIFunctions();
			JSONObject sjson = apiFunctions.getPosts();

			
			
			try {
				posts = sjson.getJSONArray("posts");
								
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
        	//Get Posts using xmlrpc
        	try {
				posts = StoryMakerApp.getServerManager().getRecentPosts(10);
				createCards();
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
        	pB.setVisibility(View.GONE);
        	
        	if(posts.size()==0){
        		Toast.makeText(mActivity.getApplicationContext(), "No posts found", Toast.LENGTH_LONG).show();
        	}
        	
        	mCardView.refresh();	
        }
	}
    
    public void createCards(){
    	final String[] postIds = new String[posts.size()];
    	
    	for(int i = 0; i<posts.size(); i++){
    		Page post = posts.get(i); 		
    		
    		String thumbnail = null;
    		thumbnail = post.getThumbnail();
    		
    		Drawable thumb = null;
    		
    		if(thumbnail!=null){
    			try {
					thumbnail = StoryMakerApp.getServerManager().getPostObject(thumbnail);
						if(thumbnail==null){
							  thumb = getResources().getDrawable(R.drawable.logo);
						}else{
							  try {
								thumb = drawableFromUrl(thumbnail);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlRpcFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		
    		
    		String url = post.getPermaLink();
    		final int localIndex = i;
    		postIds[i] = String.valueOf(post.getPostid());
    		
    		String title = post.getTitle();
    		String excerpt = post.getDescription();
    		    		
    		String[] excerptparts = excerpt.split("==Media==");
    		
    		String date = post.getDateCreated().toString();
    		MyCard newCard = new MyCard(title, excerptparts[0], thumb);
    		newCard.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(mActivity.getApplicationContext(), ReportRemoteViewActivity.class);
					i.putExtra("postId", postIds[localIndex]);
					startActivity(i);
				}
			});
    		
    		mCardView.addCard(newCard);
			
    		
    	}
    }
    public static Drawable drawableFromUrl(String url) throws IOException {
        try{
    	Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6; 
        
        x = BitmapFactory.decodeStream(input, null, options);
        return new BitmapDrawable(x);}
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            return null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    
}
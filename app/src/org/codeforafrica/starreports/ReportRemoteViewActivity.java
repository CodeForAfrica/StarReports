package org.codeforafrica.starreports;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.bican.wordpress.Page;
import redstone.xmlrpc.XmlRpcFault;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ReportRemoteViewActivity extends BaseActivity{
	
    private String postID;
    private ProgressBar pB;
    private LinearLayout contentLayout;
    private WebView wv;
    private String mTitle;
    private String mUrl;
    private String mDesc;
    private String mDate;
    private String mAuthor;
    
    private Page post;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_story_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        pB = (ProgressBar)findViewById(R.id.progressBar);
        contentLayout = (LinearLayout)findViewById(R.id.content);
        wv = (WebView) findViewById(R.id.webView);
        new getArticle().execute();
    }

    class getArticle extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
        	try {
				post = StoryMakerApp.getServerManager().getPost(getIntent().getStringExtra("postId"));
				mTitle = post.getTitle();
	    		mDesc = post.getDescription();
	    		mDate = "";
	    		mAuthor = "";
	    		
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlRpcFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	 SingleStoryCard(mTitle, mDesc, mDate, mAuthor);

            return null;
        }
        protected void onPostExecute(String file_url) {
            pB.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }


    public void SingleStoryCard(String title, String mDesc, String mDate, String mAuthor){
        TextView mTextViewTitle =((TextView) findViewById(R.id.title));
        mTextViewTitle.setText(title);

        ( (TextView) findViewById(R.id.date)).setText(mDate);
        ( (TextView) findViewById(R.id.author)).setText(mAuthor);



        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        final String html = mDesc;
        wv.post(new Runnable() {
            public void run() {
                wv.loadDataWithBaseURL("", html, mimeType, encoding, "");
                wv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                wv.getSettings().setUseWideViewPort(true);
                wv.getSettings().setLoadWithOverviewMode(true);
            }
        });
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        getSupportMenuInflater().inflate(R.menu.singlepost, menu);
       
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.menu_add_report).setVisible(false);
        menu.findItem(R.id.menu_sync_reports).setVisible(false);
        menu.findItem(R.id.menu_new_form).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_share:
	            Intent share = new Intent(android.content.Intent.ACTION_SEND);
	            share.setType("text/plain");
	            //share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	
	            // Add data to the intent, the receiving app will decide
	            // what to do with it.
	            share.putExtra(Intent.EXTRA_SUBJECT, mTitle);
	            share.putExtra(Intent.EXTRA_TEXT, mUrl);
	
	            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        
            case android.R.id.home:
            	Intent i = new Intent(ReportRemoteViewActivity.this, ReportsFragmentsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(i);
            	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
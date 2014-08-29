package org.codeforafrica.starreports;


import java.net.MalformedURLException;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.ui.MyCard;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;

import redstone.xmlrpc.XmlRpcFault;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
 
public class SearchResultsActivity extends BaseActivity {
 
	private ProgressBar pB;
	private CardUI mCardView;
	String query;
	List<Page> posts = null;
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_latest_reports);
 
        // get the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        
        pB = (ProgressBar)findViewById(R.id.pBLoading);
        //init CardView
        mCardView = (CardUI) findViewById(R.id.cardsview);
        mCardView.setSwipeable(false);
        
        handleIntent(getIntent());
    }
 
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
 
    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
 
            new GetRecentPosts().execute();
 
        }
 
    }
    class GetRecentPosts extends AsyncTask<String, String, String> {
      	 @Override
           protected void onPreExecute() {
   	   		super.onPreExecute();

           }
           protected String doInBackground(String... args) {

           	//Get Posts using xmlrpc
           	try {
   				posts = StoryMakerApp.getServerManager().getPostsByKeyword(10, query);
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
           		Toast.makeText(getApplicationContext(), "No posts found", Toast.LENGTH_LONG).show();
           	}
           	
           	try {
   				createCards();
   			} catch (JSONException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
           }
   	}
       
       public void createCards() throws JSONException{
       	final String[] postIds = new String[posts.size()];
       	
       	for(int i = 0; i<posts.size(); i++){
       		Page post = posts.get(i); 		
       		String url = post.getPermaLink();
       		final int localIndex = i;
       		postIds[i] = String.valueOf(post.getPostid());
       		
       		String title = post.getTitle();
       		String excerpt = post.getDescription();
       		
       		
       		String[] excerptparts = excerpt.split("==Media==");
       		
       		String date = post.getDateCreated().toString();
       		MyCard newCard = new MyCard(title, excerptparts[0]);
       		newCard.setOnClickListener(new View.OnClickListener() {
   				
   				@Override
   				public void onClick(View v) {
   					// TODO Auto-generated method stub
   					Intent i = new Intent(getApplicationContext(), ReportRemoteViewActivity.class);
   					i.putExtra("postId", postIds[localIndex]);
   					startActivity(i);
   				}
   			});
       		
       		mCardView.addCard(newCard);
   			mCardView.refresh();
       		
       	}
       }
       @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           super.onCreateOptionsMenu(menu);
           
           menu.findItem(R.id.about).setVisible(false);
           menu.findItem(R.id.menu_add_report).setVisible(false);
           menu.findItem(R.id.menu_sync_reports).setVisible(false);
           menu.findItem(R.id.menu_new_form).setVisible(false);
           menu.findItem(R.id.action_search).setVisible(false);

           return true;
       }

       @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           switch (item.getItemId()) {
           	
               case android.R.id.home:
               	Intent i = new Intent(SearchResultsActivity.this, ReportsFragmentsActivity.class);
                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               	startActivity(i);
               	
               return true;
           }
           return super.onOptionsItemSelected(item);
       }
}
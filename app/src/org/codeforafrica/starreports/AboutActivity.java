package org.codeforafrica.starreports;

import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.ui.MyCard;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class AboutActivity extends BaseActivity{
    private CardUI mCardView;

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	    
	    	super.onCreate(savedInstanceState);
	    	
	        setContentView(R.layout.activity_about);
	        
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
	      
	        
	        String title = "About ListeningPost";
	        String desc = getString(R.string.about);
	        
	        //init CardView
			mCardView = (CardUI) findViewById(R.id.cardsview);
			mCardView.setSwipeable(false);
			
	        MyCard androidViewsCard = new MyCard(title, desc);
			mCardView.addCard(androidViewsCard);
			
			//MyCard androidViewsCard2 = new MyCard(title, desc);
			//mCardView.addCard(androidViewsCard2);
			
			mCardView.refresh();
	  }
	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        
	       
	        menu.findItem(R.id.about).setVisible(false);
	        menu.findItem(R.id.menu_sync_reports).setVisible(false);
	        menu.findItem(R.id.menu_new_form).setVisible(false);

	        return true;
	    }
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        	
	            case android.R.id.home:
	            	Intent i = new Intent(AboutActivity.this, HomePanelsActivity.class);
	                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            	startActivity(i);
	            	
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }

}

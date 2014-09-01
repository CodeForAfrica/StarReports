package org.codeforafrica.starreports;

import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.LessonsActivity.LessonSectionFragment;
import org.codeforafrica.starreports.facebook.FacebookLogin;
import org.codeforafrica.starreports.lessons.WebViewSetupJB;
import org.codeforafrica.starreports.server.LoginActivity;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

public class ReportsFragmentsActivity extends BaseActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
     
        
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        
        setContentView(R.layout.activity_reports_viewpager);
        
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        String user = settings.getString("logged_in",null);
        
        //Toast.makeText(getApplicationContext(), " " + user, Toast.LENGTH_LONG).show();
        
        if ((user == null)||(user.equals("0")))
        {
        	Intent intent = new Intent(this,LoginActivity.class);
        	startActivity(intent);
        	finish();
        }
        
        MyReportsFragment fMyReports = new MyReportsFragment();
        LatestReportsFragment fLatestReports = new LatestReportsFragment();
        AssignmentsFragment fAssignments = new AssignmentsFragment();
        
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fMyReports, fLatestReports, fAssignments);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        getSupportActionBar().setHomeButtonEnabled(true);
        
    }

    @Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		
		if (resCode == RESULT_OK)
		{
				if (reqCode == 1)
				{
					//time to update the lesson list
					//mListView.refreshList();
				}
		}
    }

    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
    	
    	private MyReportsFragment fMyReports;
    	private LatestReportsFragment fLatestReports;
    	private AssignmentsFragment fAssignments;
    	
        public SectionsPagerAdapter(FragmentManager fm,MyReportsFragment mRFragment, LatestReportsFragment mLatestFragment, AssignmentsFragment mAssignmentsFragment ) {
            super(fm);
            
            fMyReports = mRFragment;
            fLatestReports = mLatestFragment;
            fAssignments = mAssignmentsFragment;
        }

		@Override
        public Fragment getItem(int i) {
        	Fragment fragment = null;

        	if (i == 0)
        	{
        		fragment = fLatestReports;
 	            
        	}
        	else if (i == 1)
        	{
        		fragment = fMyReports;
        	}
        	else
        	{
        		fragment = fAssignments;
        	}
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_latest_reports);
                case 1: return getString(R.string.title_my_reports);
                case 2: return getString(R.string.title_assignments);
            }
            return null;
        }
    }
   

}

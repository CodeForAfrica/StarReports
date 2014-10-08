package org.codeforafrica.starreports.facebook;

import org.codeforafrica.starreports.AboutActivity;
import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.HomePanelsActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.HomePanelsActivity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
 
public class UpdateActivity extends BaseActivity {
	EditText registerUsername;
	EditText first_name;
	EditText last_name;
	EditText email;
	EditText phone_number;
	EditText location;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUsername = (EditText)findViewById(R.id.registerUsername);
        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        email = (EditText)findViewById(R.id.email);
        phone_number = (EditText)findViewById(R.id.phone_number);
        location = (EditText)findViewById(R.id.location);
        
        setValues();
        
    }
    public void setValues(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	registerUsername.setText(prefs.getString("username", ""));
    	first_name.setText(prefs.getString("firstname", ""));
    	last_name.setText(prefs.getString("lastname", ""));
    	email.setText(prefs.getString("email", ""));
    	phone_number.setText(prefs.getString("phone_number", ""));
    	location.setText(prefs.getString("location", ""));

    	
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
            	Intent i = new Intent(UpdateActivity.this, HomePanelsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(i);
            	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
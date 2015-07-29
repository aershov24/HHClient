package com.aestudio.hhclient;

import com.aestudio.hhclient.objects.Requester;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Activity for viewing job 
 */
public class JobActivity extends Activity {
	
	private DataHelper dh;
  	static final private int MENU_ADD_DB = Menu.FIRST;
  	static final private int MENU_ADD_FAVORITES = Menu.FIRST+1;
    static final private int MENU_OPEN_WEB = Menu.FIRST+2;
    
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.job);
	        
	        String date = Requester.job.date.getDay()+"/"+Requester.job.date.getMonth()+"/"+Requester.job.date.getYear();
	        
	        TextView job_id = (TextView) findViewById(R.id.job_id);
	        job_id.setText(getText(R.string.vacancy).toString()+": "+Requester.job.id + ", "+date);
	        
	        TextView job_name = (TextView) findViewById(R.id.job_name);
	        job_name.setText(Requester.job.name);
	        
	        TextView employer_name = (TextView) findViewById(R.id.employer_name);
	        employer_name.setText(getText(R.string.employer).toString()+": "+Requester.job.employer);
	        
	        TextView region_name = (TextView) findViewById(R.id.region_name);
	        region_name.setText(getText(R.string.region).toString()+": "+Requester.job.region);
	        
	        TextView shedule_name = (TextView) findViewById(R.id.shedule_name);
	        shedule_name.setText(getText(R.string.shedule).toString()+": "+Requester.job.shedule);
	       
	        TextView salary_name = (TextView) findViewById(R.id.salary_name);
	        salary_name.setText(getText(R.string.salary).toString()+": "+Requester.job.getSalary());
	        
	        TextView employment_name = (TextView) findViewById(R.id.employment_name);
	        employment_name.setText(getText(R.string.employment).toString()+": "+Requester.job.employment);
	        
	        TextView description_label = (TextView) findViewById(R.id.description_label);
	        description_label.setText(getText(R.string.description).toString()+":");
	        
	        TextView description = (TextView) findViewById(R.id.description);
	        description.setText(Html.fromHtml(Requester.job.description));
	        
	        initDb();
	    }
	  
		private void initDb() {
			try
			{
				dh = new DataHelper(getApplicationContext());
			}
			catch(Exception ex)
			{
				Log.e("HHCLIENT", ex.getMessage());
			}
		}
	  
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);

	    	MenuItem menuItem;
	    	
	    	menuItem = menu.add(0, MENU_ADD_DB, Menu.NONE, R.string.menu_add_db);
	    	menuItem.setIcon(android.R.drawable.ic_menu_save);
	    	menuItem.setOnMenuItemClickListener(
	    			new OnMenuItemClickListener(){
	    					public boolean onMenuItemClick(MenuItem _menuItem) {
	    						try
	    						{
	    							Requester.job.dh = dh;
	    							long res = Requester.job.addToDB();
	    							if (res == 0)
	    								// TODO
	    								Toast.makeText(getApplicationContext(), R.string.msg_already_add, Toast.LENGTH_SHORT).show();
	    							if (res == -1)
	    								// TODO
	    								Toast.makeText(getApplicationContext(), R.string.msg_not_add_to_db, Toast.LENGTH_SHORT).show();
	    							else
	    								Toast.makeText(getApplicationContext(), R.string.msg_add_to_db, Toast.LENGTH_SHORT).show();
		    						
	    						}
	    						catch(Exception ex)
	    						{
	    							Log.e("HHCLIENT", ex.getMessage());
	    						}
	    						return true;
	    					}
	    			});
	    	
	    	menuItem = menu.add(0, MENU_ADD_FAVORITES, Menu.NONE, R.string.menu_add_favorites);
	    	menuItem.setIcon(android.R.drawable.star_big_off);
	    	menuItem.setOnMenuItemClickListener(
	    			new OnMenuItemClickListener(){
	    					public boolean onMenuItemClick(MenuItem _menuItem) {
	    						try
	    						{
	    							Requester.job.dh = dh;
	    							long res = Requester.job.addToFavorites();
	    							if (res == -1)
	    								// TODO
	    								Toast.makeText(getApplicationContext(), R.string.msg_not_add_to_favorites, Toast.LENGTH_SHORT).show();
	    							else
	    								Toast.makeText(getApplicationContext(), R.string.msg_add_to_favorites, Toast.LENGTH_SHORT).show();
		    						
	    						}
	    						catch(Exception ex)
	    						{
	    							Log.e("HHCLIENT", ex.getMessage());
	    						}
	    						return true;
	    					}
	    			});
	    	
	    	menuItem = menu.add(0, MENU_OPEN_WEB, Menu.NONE, R.string.menu_open_job_web);
	    	menuItem.setIcon(android.R.drawable.ic_menu_view);
	    	menuItem.setOnMenuItemClickListener(
	    			new OnMenuItemClickListener(){
	    					public boolean onMenuItemClick(MenuItem _menuItem) {
	    						Intent browserIntent = 
	    							new Intent("android.intent.action.VIEW", Uri.parse(Requester.job.webLink));
	    							startActivity(browserIntent);
	    							return true;
	    					}
	    			});
	   
	    	return true;
	    }

}

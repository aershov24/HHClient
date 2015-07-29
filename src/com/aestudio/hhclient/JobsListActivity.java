package com.aestudio.hhclient;

import java.util.ArrayList;
import java.util.List;

import com.aestudio.hhclient.objects.Job;
import com.aestudio.hhclient.objects.Requester;
import com.aestudio.hhclient.objects.SearchQuery;
import com.aestudio.hhclient.objects.Requester.Pair;
import com.aestudio.hhclient.objects.Requester.RequestURL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class JobsListActivity extends Activity {
	
	Context context;
	static final int PROGRESS_DIALOG = 0;
	Job selectedJob = null;
    ProgressThread progressThread;
    ProgressDialog progressDialog;
    private DataHelper dh;
	static final private int CATEGORY_DETAIL = 1;
    
    static final private int MENU_NEW_SEARCH = Menu.FIRST;
    static final private int MENU_SAVE_QUERY = Menu.FIRST+1;
    static final private int MENU_SAVE_SEARCH_RESULT = Menu.FIRST+2;
    
    int page_items_count = 10;
    boolean save_only_with_wifi = false;
    
    WifiManager wifi;
	
	public static class JobsListCache
	{
		public static int currentPage = 0;
		public static ArrayList<Job> jobs = new ArrayList<Job>(); 
		public static String selectedJobId;
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
	
    
    /** Nested class that performs progress calculations (counting) */
    private class ProgressThread extends Thread {
        Handler mHandler;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;
       
        ProgressThread(Handler h) {
            mHandler = h;
        }
       
        public void run() {
            mState = STATE_RUNNING;   
            //total = 0;
        	Requester.saveJobs = 0;
 		   	Requester.handler = mHandler;
            while (mState == STATE_RUNNING) {

            	   int all = Requester.found;
            	   // TODO
	        	   int pages = all/page_items_count;
	        	   for (int i=0; i< pages+1; i++)
	        	   {
	        		   ArrayList<Job> jobs = Requester.getJobsFromPage(Requester.currentUrl+"&page="+String.valueOf(i));
	        		   for (int q=0; q < jobs.size(); q++)
	        		   {
	        			   Job jb = jobs.get(q);
	        			   jb.dh = dh;
	        			   jb.addToDB();
	        		   }
	        	   }
            }
        }
        
        /* sets the current state for the thread,
         * used to stop the thread */
        public void setState(int state) {
            mState = state;
        }
    }
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(JobsListActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(getText(R.string.loading));
            progressDialog.setMax(Requester.found);
            return progressDialog;
        case CATEGORY_DETAIL:
            LayoutInflater li = LayoutInflater.from(this);
            View categoryDetailView = li.inflate(R.layout.querydialog, null);
            
            AlertDialog.Builder categoryDetailBuilder = new AlertDialog.Builder(this);
            categoryDetailBuilder.setTitle(getText(R.string.query_name));
            categoryDetailBuilder.setView(categoryDetailView);
            AlertDialog categoryDetail = categoryDetailBuilder.create();
            
            categoryDetail.setButton(getText(R.string.save), 
            	new DialogInterface.OnClickListener(){
	                public void onClick(DialogInterface dialog, int which) {
	                    AlertDialog categoryDetail = (AlertDialog)dialog;
	                    EditText et = (EditText)categoryDetail.findViewById(R.id.queryName);    
	                    
	                    SearchQuery query = new SearchQuery();
	                    query.dh = dh;
	                    query.name = et.getText().toString();
	                    query.type = RequestURL.VACANCY_SEARCH;
	                    query.parameters = Requester.parameters;
	                	
	                	long ret = query.addToDB();
	                	if (ret == -1)
	                		Toast.makeText(getApplicationContext(), R.string.query_not_save, Toast.LENGTH_SHORT).show();
	                	else
	                		Toast.makeText(getApplicationContext(), R.string.query_save, Toast.LENGTH_SHORT).show();

	                    return;
	                }
            }
            );
            
            categoryDetail.setButton2(getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }}); 
            
            return categoryDetail;
        default:
            break;

        }
		return m_ProgressDialog;
    }

    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.arg1;
            progressDialog.setProgress(total);
            if (total >= Requester.found){
                dismissDialog(PROGRESS_DIALOG);
                progressThread.setState(ProgressThread.STATE_DONE);
            }
        }
    };
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog.setProgress(0);
            progressThread = new ProgressThread(handler);
            progressThread.start();
            break;
        case CATEGORY_DETAIL:
            AlertDialog categoryDetail = (AlertDialog)dialog;
            EditText et = (EditText)categoryDetail.findViewById(R.id.queryName);
            et.setText("");
            break;
        }
    }

	
	 /** Called when the activity is first created. */
    //ListView that will hold our items references back to main.xml
    ListView jobList;
    //Array Adapter that will hold our ArrayList and display the items on the ListView
    JobListAdapter arrayAdapter;
    private ProgressDialog m_ProgressDialog = null; 
    
    Runnable viewJobs = new Runnable(){
        @Override
        public void run() {
            getJobs();
            
        }

		private void getJobs() {
			Requester.connect(Requester.currentUrl+"&page="+JobsListCache.currentPage);
        	//jobs = Requester.jobs;

        	runOnUiThread(returnRes);
		}
    };
    
    Runnable getJob = new Runnable(){
        @Override
        public void run() {
    		 JobsListCache.selectedJobId = String.valueOf(selectedJob.id);
    		 Requester.job = Requester.getJob(RequestURL.getJobURL(RequestURL.JOB, JobsListCache.selectedJobId));
    		 
    		 runOnUiThread(returnRes);
 	         
    		 Intent intent = new Intent(JobsListActivity.this, JobActivity.class);
    		 JobsListActivity.this.startActivity(intent);
        }

    };
    
	Runnable returnRes = new Runnable() {

        @Override
        public void run() {
        	//jobs = JobsListCache.jobs;
        	arrayAdapter = new JobListAdapter(JobsListActivity.this, R.layout.joblistitem, JobsListCache.jobs);
        	jobList.setAdapter(arrayAdapter);
        	jobList.postInvalidate();
        	TextView pageLbl = (TextView) findViewById(R.id.page_id);
        	int cp = JobsListCache.currentPage+1;
        	int pc = Requester.found/page_items_count + 1;
            pageLbl.setText(getText(R.string.page)+" "+cp+" "+getText(R.string.on)+pc);
            m_ProgressDialog.dismiss();
        }
    };
    
    Runnable returnRes2 = new Runnable() {

        @Override
        public void run() {
            m_ProgressDialog.dismiss();
        }
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);

    	MenuItem menuItem;
    	
    	// TODO
    	menuItem = menu.add(0, MENU_NEW_SEARCH, Menu.NONE, getText(R.string.new_search));
    	menuItem.setIcon(android.R.drawable.ic_menu_search);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						 Intent intent = new Intent(JobsListActivity.this, SearchActivity.class);
    						 JobsListActivity.this.startActivity(intent);
    						 return true;
    					}
    			});
    	
    	// TODO
    	menuItem = menu.add(0, MENU_SAVE_QUERY, Menu.NONE, getText(R.string.menu_save_query));
    	menuItem.setIcon(android.R.drawable.ic_menu_save);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
					public boolean onMenuItemClick(MenuItem _menuItem) {
						showDialog(CATEGORY_DETAIL);
						return true;
					}
			});
    	
    	// TODO
    	menuItem = menu.add(0, MENU_SAVE_SEARCH_RESULT, Menu.NONE, getText(R.string.menu_save_all_jobs));
    	menuItem.setIcon(android.R.drawable.ic_menu_save);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						String str = null;
    						
    						WifiInfo info = wifi.getConnectionInfo();
        					if (info != null)
        						str = info.getBSSID();
    						
    						if (save_only_with_wifi)
        					{
    							if (str != null)
	        					{
		    						AlertDialog.Builder builder = new AlertDialog.Builder(JobsListActivity.this);
		    						builder.setMessage(getText(R.string.dialog_save_all_jobs))
		    						       .setCancelable(false)
		    						       .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
		    						           public void onClick(DialogInterface dialog, int id) {
		    						        	   showDialog(PROGRESS_DIALOG);
		    						        	   return;
		    						           }
		    						       })
		    						       .setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
		    						           public void onClick(DialogInterface dialog, int id) {
		    						                dialog.cancel();
		    						           }
		    						       });
		    						AlertDialog alert = builder.create();
		    						alert.show();
	        					}
    							else
    							{
    								 Toast.makeText(getApplicationContext(), R.string.msg_wifi, Toast.LENGTH_SHORT).show();
    							}
        					}
    						else
    						{
    							AlertDialog.Builder builder = new AlertDialog.Builder(JobsListActivity.this);
	    						builder.setMessage(getText(R.string.dialog_save_all_jobs))
	    						       .setCancelable(false)
	    						       .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
	    						           public void onClick(DialogInterface dialog, int id) {
	    						        	   showDialog(PROGRESS_DIALOG);
	    						        	   return;
	    						           }
	    						       })
	    						       .setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
	    						           public void onClick(DialogInterface dialog, int id) {
	    						                dialog.cancel();
	    						           }
	    						       });
	    						AlertDialog alert = builder.create();
	    						alert.show();
    						}
    						return true;
    					}
    			});
    	
    	return true;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joblist);
        
        wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	// Retrieve the saved values.
    	page_items_count = Integer.parseInt(mySharedPreferences.getString("PREF_PAGE_COUNT", "10"));
    	save_only_with_wifi = mySharedPreferences.getBoolean("PREF_SAVE_WITH_WIFI", false);
    	
        initDb();
        
        Intent intent = this.getIntent();
        Bundle b = intent.getExtras();
        boolean isWidget = false;
        
        if (b != null)
        	isWidget = b.getBoolean("widget");
        
        if (isWidget)
        {
        	 List<SearchQuery> queries = dh.selectQueries("queryType = 1", null);
        	 if (queries.size() != 0)
        	 {
        	 
				Requester.currentUrl = RequestURL.getSearchURL(RequestURL.VACANCY_SEARCH, queries.get(0).parameters);
				Requester.parameters = queries.get(0).parameters;
				Requester.connect(Requester.currentUrl);
				JobsListCache.jobs = Requester.jobs;
        	 }
        	 else
        		 Toast.makeText(getApplicationContext(), R.string.msg_query_not_found, Toast.LENGTH_SHORT).show();
        }
        
        //Initialize ListView
        jobList = (ListView) findViewById(R.id.jobList);
        
        jobList.setOnItemClickListener(new OnItemClickListener() {
        	 @Override
        	 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		
        		 selectedJob  = (Job)jobList.getItemAtPosition(position);
            	 
        	      Thread thread =  new Thread(null, getJob, "hhclientBackground");
	                thread.start();
	                m_ProgressDialog = ProgressDialog.show(JobsListActivity.this,    
	                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
        	 }
        });
        
        jobList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
            	final int p = pos;
            	final CharSequence[] items = {getText(R.string.cnm_show_job), 
            			getText(R.string.menu_open_job_web)};

            	AlertDialog.Builder builder = new AlertDialog.Builder(JobsListActivity.this);
            	builder.setTitle(getText(R.string.do_item));
            	builder.setItems(items, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	        switch(item)
            	        {
            	        	// show
            	        	case 0: 
            	        		 selectedJob  = (Job)jobList.getItemAtPosition(p);
            	            	 
            	        	      Thread thread =  new Thread(null, getJob, "hhclientBackground");
            		                thread.start();
            		                m_ProgressDialog = ProgressDialog.show(JobsListActivity.this,    
            		                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            	        		break;
            	        	// Open in web
            	        	case 1: 
            	        		selectedJob  = (Job)jobList.getItemAtPosition(p);
            	        		Intent browserIntent = 
	    							new Intent("android.intent.action.VIEW", Uri.parse(selectedJob.webLink));
	    							startActivity(browserIntent);
            	        		break;
            	        	default: break;
            	        }
            	    }
            	});
            	AlertDialog alert = builder.create();
            	alert.show();
            	return true;
            }
        });
        
        TextView resLbl = (TextView) findViewById(R.id.search_result_label);
        resLbl.setText(getText(R.string.search_result)+" "+Requester.found);
        
        //Initialize our array adapter notice how it references the listitems.xml layout
        arrayAdapter = new JobListAdapter(JobsListActivity.this, R.layout.joblistitem, JobsListCache.jobs);
        //Set the above adapter as the adapter of choice for our list
        jobList.setAdapter(arrayAdapter);
        
        TextView pageLbl = (TextView) findViewById(R.id.page_id);
        int cp = JobsListCache.currentPage+1;
        int pc = Requester.found/page_items_count + 1;
        pageLbl.setText(getText(R.string.page)+" "+cp+" "+getText(R.string.on)+" "+pc);
        
        Button next = (Button) findViewById(R.id.next_page);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (JobsListCache.currentPage == Requester.found/page_items_count)
            		return;
            	JobsListCache.currentPage++;
                Thread thread =  new Thread(null, viewJobs, "hhclientBackground");
                thread.start();
                m_ProgressDialog = ProgressDialog.show(JobsListActivity.this,    
                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            }
        });
        
        Button prev = (Button) findViewById(R.id.prev_page);
        prev.setClickable(false);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (JobsListCache.currentPage == 0)
            		return;
            	JobsListCache.currentPage--;
                Thread thread =  new Thread(null, viewJobs, "hhclientBackground");
                thread.start();
                m_ProgressDialog = ProgressDialog.show(JobsListActivity.this,    
                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            }
        });
    }

}

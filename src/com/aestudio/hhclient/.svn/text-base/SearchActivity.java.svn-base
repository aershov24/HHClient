package com.aestudio.hhclient;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.aestudio.hhclient.JobsListActivity.JobsListCache;
import com.aestudio.hhclient.objects.Job;
import com.aestudio.hhclient.objects.Requester;
import com.aestudio.hhclient.objects.Requester.Pair;
import com.aestudio.hhclient.objects.Requester.RequestURL;
import com.aestudio.hhclient.objects.SearchQuery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Main activity with tabs
 * @author aershov
 *
 */
public class SearchActivity extends TabActivity {
	
	TabHost mTabHost;
	Context context;
	// class for sqllite database work
	DataHelper dh;
	
    private ProgressDialog m_ProgressDialog = null; 
	ArrayList<Pair> parameters = new ArrayList<Pair>();
	
	// list view's for tabs
	ListView queriesList;
	ListView localJobList;
	ListView favoriteJobList;
	
	// id for search query details dialog
	static final private int SEARCH_QUERY_DETAIL = 1;
	
	private final static String whitespace = "%20";
	
    static final private int MENU_NEW_QUERY = Menu.FIRST;
    static final private int MENU_SAVE_QUERY = Menu.FIRST+1;
    static final private int MENU_PREFERENCES = Menu.FIRST+2;
    static final private int MENU_QUIT = Menu.FIRST+3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        context = this.getApplicationContext();
        
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	// Retrieve the saved values.
    	boolean sound = mySharedPreferences.getBoolean("SOUND", false);
        
        initDb();
        initTabs();
        setSearchListeners();
        setCurrencySpiner();
        setRegionSpiner();
        setEmploymentSpiner();
        setSheduleSpiner();
    }
    
    /**
     *  Initialize database helper class
     */
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
	
	/**
	 * Initialize tabs names and data
	 */
	private void initTabs() {
		TabHost mTabHost = getTabHost();
        context = this.getBaseContext();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_search").
        		setIndicator(this.getText(R.string.tab_search), getResources().getDrawable(android.R.drawable.ic_search_category_default)).
        		setContent(R.id.tab_search_layout));
        mTabHost.addTab(mTabHost.newTabSpec("tab_favorites").
        		setIndicator(this.getText(R.string.tab_favorites), getResources().getDrawable(android.R.drawable.star_big_off)).
        		setContent(R.id.tab_vaforites_layout));
        
        loadFavoritesJobs();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_queries").
        		setIndicator(this.getText(R.string.tab_queries), getResources().getDrawable(android.R.drawable.ic_input_get)).
        		setContent(R.id.tab_queries_layout));
        
        loadQueries();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_local_jobs").
        		setIndicator(this.getText(R.string.tab_local_jobs), getResources().getDrawable(android.R.drawable.ic_menu_agenda)).
        		setContent(R.id.tab_local_jobs_layout));
        
        loadLocalJobs();
        
        mTabHost.setCurrentTab(0);
	}
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	// reload db data after resume activity
    	loadFavoritesJobsData();
    	loadLocalJobsData();
    	loadQueriesData();
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case SEARCH_QUERY_DETAIL:
                AlertDialog categoryDetail = (AlertDialog)dialog;
                EditText qn = (EditText)categoryDetail.findViewById(R.id.queryName);
                qn.setText("");
                break;
            default:
                break;
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        	// create save search query dialog...
            case SEARCH_QUERY_DETAIL:
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
		                    // set type = 0, set type = 1 for widget's query
		                    query.type = "0";
		                    
		                	EditText searchTxt = (EditText) findViewById(R.id.search_text);
		                	
		                	if (searchTxt.getText().toString().trim().length() != 0){
		                		Pair searchText = new Pair(RequestURL.TEXT, searchTxt.getText().toString().trim().replace(" ", whitespace));
		                		query.parameters.add(searchText);
		                		initSearchParams(query.parameters);
		                	}
		                	
		                	long ret = query.addToDB();
		                	if (ret == -1)
		                		Toast.makeText(getApplicationContext(), getText(R.string.query_not_save), Toast.LENGTH_SHORT).show();
		                	else
		                		Toast.makeText(getApplicationContext(), getText(R.string.query_save), Toast.LENGTH_SHORT).show();

		                	loadQueries();
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
        return null;
    }

    /**
     * Set search jobs schedule
     */
	private void setSheduleSpiner() {
		Spinner spinner = (Spinner) findViewById(R.id.shedule_spinner);
		List<Pair> shedules = dh.selectSchedules();
        
		ArrayAdapter<String> adapterForSpinner; 
		adapterForSpinner = new ArrayAdapter<String>(this, 
				 android.R.layout.simple_spinner_item); 
		adapterForSpinner.setDropDownViewResource 
		(android.R.layout.simple_spinner_dropdown_item); 
		                spinner.setAdapter(adapterForSpinner); 
		            
		for(Pair rg : shedules)
		{
			adapterForSpinner.add(rg.paramName);
		}
		
		spinner.setAdapter(adapterForSpinner);
	}

	/**
	 * Set search jobs Employment
	 */
	private void setEmploymentSpiner() {
		Spinner spinner = (Spinner) findViewById(R.id.employment_spinner);
		List<Pair> employments = dh.selectEmployments();
        
		ArrayAdapter<String> adapterForSpinner; 
		adapterForSpinner = new ArrayAdapter<String>(this, 
				 android.R.layout.simple_spinner_item); 
		adapterForSpinner.setDropDownViewResource 
		(android.R.layout.simple_spinner_dropdown_item); 
		                spinner.setAdapter(adapterForSpinner); 
		            
		for(Pair rg : employments)
		{
			adapterForSpinner.add(rg.paramName);
		}
		
		spinner.setAdapter(adapterForSpinner);
	}

	/**
	 * Set search jobs region
	 */
	private void setRegionSpiner() {
		Spinner spinner = (Spinner) findViewById(R.id.region_spinner);

		List<Pair> regions = dh.selectRegions();
        
		ArrayAdapter<String> adapterForSpinner; 
		adapterForSpinner = new ArrayAdapter<String>(this, 
				 android.R.layout.simple_spinner_item); 
		adapterForSpinner.setDropDownViewResource 
		(android.R.layout.simple_spinner_dropdown_item); 
		                spinner.setAdapter(adapterForSpinner); 
		            
		for(Pair rg : regions)
		{
			adapterForSpinner.add(rg.paramName);
		}
		
		spinner.setAdapter(adapterForSpinner);
	}
	
	/**
	 * Set search jobs currency
	 */
	private void setCurrencySpiner() {
		Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

		List<String> currencies = dh.selectCurrencies();
        
		ArrayAdapter<String> adapterForSpinner; 
		adapterForSpinner = new ArrayAdapter<String>(this, 
				 android.R.layout.simple_spinner_item); 
		adapterForSpinner.setDropDownViewResource 
		(android.R.layout.simple_spinner_dropdown_item); 
		                spinner.setAdapter(adapterForSpinner); 
		            
		for(String rg : currencies)
		{
			adapterForSpinner.add(rg);
		}
		
		spinner.setAdapter(adapterForSpinner);
	}
	
	/**
	 * Load saved search queries from database
	 */
	private void loadQueries() {
		queriesList = (ListView) findViewById(R.id.queriesList);
        
		queriesList.setOnItemClickListener(new OnItemClickListener() {
        	 @Override
        	 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		 SearchQuery query  = (SearchQuery)queriesList.getItemAtPosition(position);
	            	parameters = query.parameters; 
	        		
	        		 Thread thread =  new Thread(null, viewJobs, "hhclientBackground");
		                thread.start();
		                m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
		                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
        	 }
        });
    
		loadQueriesData();
		
		queriesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
            	final int p = pos;
            	final CharSequence[] items = {
            			getText(R.string.get_data), 
            			getText(R.string.delete), 
            			getText(R.string.show_on_widget)};

            	AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            	builder.setTitle(getText(R.string.do_item));
            	builder.setItems(items, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
						switch(item)
            	        {
            	        	// show
            	        	case 0: 
            	        		SearchQuery query  = (SearchQuery)queriesList.getItemAtPosition(p);
            	            	parameters = query.parameters; 
            	        		
            	        		 Thread thread =  new Thread(null, viewJobs, "hhclientBackground");
            		                thread.start();
            		                m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
            		                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            	        		break;
            	        	// Open in web
            	        	case 1: 
            	        		query  = (SearchQuery)queriesList.getItemAtPosition(p);
            	        		query.dh = dh;
            	        		if (query.delete())
            	        			Toast.makeText(getApplicationContext(), getText(R.string.msg_query_delete), 
            	        					Toast.LENGTH_SHORT).show();
            	        		else
            	        			Toast.makeText(getApplicationContext(), getText(R.string.msg_query_not_delete), Toast.LENGTH_SHORT).show();
            	        		
            	        		loadQueries();
	    						break;
	    						// Open in web
            	        	case 2: 
            	        		query  = (SearchQuery)queriesList.getItemAtPosition(p);
            	        		query.dh = dh;
            	        		
            	        		List<SearchQuery> queries1 = dh.selectQueries(null, null);
            	        		for (int i=0; i< queries1.size(); i++)
            	        		{
            	        			SearchQuery sq = queries1.get(i);
            	        			sq.dh = dh;
            	        			sq.delFromWidget();
            	        		}
            	        		
            	        		if (query.addToWidget() > 0)
            	        			Toast.makeText(getApplicationContext(), 
            	        					getText(R.string.msg_query_add_to_widget), Toast.LENGTH_SHORT).show();
            	        		else
            	        			Toast.makeText(getApplicationContext(), 
            	        					getText(R.string.msg_query_not_add_to_widget), Toast.LENGTH_SHORT).show();
            	        		
            	        		loadQueriesData();
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
	}

	/**
	 * Load queries data from db and sets it to viewlist
	 */
	private void loadQueriesData() {
		// select queries from database
		List<SearchQuery> queries = dh.selectQueries(null, null);
		
        //Initialize our array adapter notice how it references the listitems.xml layout
		ArrayAdapter<SearchQuery> arrayAdapter = 
			new QueryListAdapter(SearchActivity.this, R.layout.querylistitem, queries);
        //Set the above adapter as the adapter of choice for our list
		queriesList.setAdapter(arrayAdapter);
	}
	
	Runnable getJob = new Runnable(){
        @Override
        public void run() {
    		 //JobsListCache.selectedJobId = String.valueOf(selectedJob.id);
 	         //Requester.connect(RequestURL.getJobURL(RequestURL.JOB, JobsListCache.selectedJobId));
        	
    		 runOnUiThread(returnResJ);
 	         
    		 Intent intent = new Intent(SearchActivity.this, JobActivity.class);
    		 SearchActivity.this.startActivity(intent);
        }

    };
    
	Runnable returnResJ = new Runnable() {

        @Override
        public void run() {

            m_ProgressDialog.dismiss();
        }
    };

    /*
     * Load saved jobs from db
     */
	private void loadLocalJobs() {
		   //Initialize ListView
		localJobList = (ListView) findViewById(R.id.localJobList);
        
		localJobList.setOnItemClickListener(new OnItemClickListener() {
        	 @Override
        	 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		
        		 Requester.job  = (Job)(localJobList).getItemAtPosition(position);
            	 
        		 Thread thread =  new Thread(null, getJob, "hhclientBackground");
        		 thread.start();
        		 m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
	                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
        	 }
        });
    
		loadLocalJobsData();
		
		localJobList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
            	final int p = pos;
            	final CharSequence[] items = {getText(R.string.cnm_show_job), 
            			getText(R.string.menu_open_job_web),
            			getText(R.string.menu_add_favorites),
            			getText(R.string.delete),
            			getText(R.string.delete_all)};

            	AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            	builder.setTitle(getText(R.string.do_item));
            	builder.setItems(items, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	    	Job selectedJob;
            	        switch(item)
            	        {
            	        	// show
            	        	case 0: 
            	        			Requester.job  = (Job)localJobList.getItemAtPosition(p);
            	            	 
            	        	      Thread thread =  new Thread(null, getJob, "hhclientBackground");
            		                thread.start();
            		                m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
            		                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            	        		break;
            	        	// Open in web
            	        	case 1: 
            	        		selectedJob  = (Job)localJobList.getItemAtPosition(p);
            	        		Intent browserIntent = 
	    							new Intent("android.intent.action.VIEW", Uri.parse(selectedJob.webLink));
	    							startActivity(browserIntent);
            	        		break;
            	        	// Save
            	        	case 2: 
            	        		 selectedJob  = (Job)localJobList.getItemAtPosition(p);
            	        		 selectedJob.dh = dh;
            	        		 long resd = selectedJob.addToFavorites();

	    							if (resd == -1)
	    								// TODO
	    								Toast.makeText(getApplicationContext(), 
	    										getText(R.string.msg_not_add_to_favorites), Toast.LENGTH_SHORT).show();
	    							else
	    								Toast.makeText(getApplicationContext(),
	    										getText(R.string.msg_add_to_favorites), Toast.LENGTH_SHORT).show();
		    					
	    						loadFavoritesJobsData();	
	    						break;
            	        	case 3: 
            	        		 selectedJob  = (Job)localJobList.getItemAtPosition(p);
            	        		 selectedJob.dh = dh;
            	        		 
            	        		 long ret = selectedJob.delete();
            	        		 if (ret == -1)
            	        			 Toast.makeText(getApplicationContext(), 
            	        				 getText(R.string.msg_not_del_job), Toast.LENGTH_SHORT).show();
            	        		 else
            	        			 Toast.makeText(getApplicationContext(), 
            	        				 getText(R.string.msg_del_job), Toast.LENGTH_SHORT).show();
	    						
	    						loadFavoritesJobs();	
	    						break;
	    						// delete all
            	        	case 4: 
            	        		dh.deleteAll(DataHelper.JOBS_TABLE_NAME);
            	        		Toast.makeText(getApplicationContext(), getText(R.string.msg_dell_all_jobs), 
            	        				Toast.LENGTH_SHORT).show();
	    						
            	        		loadLocalJobsData();
	    						loadFavoritesJobsData();	
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
	}

	/**
	 * Load local jobs data from db and update list view
	 */
	private void loadLocalJobsData() {
		List<Job> localJobs = dh.selectJobs(DataHelper.JOBS_TABLE_NAME, null, null);
		
		TextView localJobListCount = (TextView) findViewById(R.id.localJobListCount);
		localJobListCount.setText(getText(R.string.jobs).toString()+" "+localJobs.size());
		
        //Initialize our array adapter notice how it references the listitems.xml layout
		ArrayAdapter<Job> arrayAdapter = new JobListAdapter(SearchActivity.this, R.layout.joblistitem, localJobs);
        //Set the above adapter as the adapter of choice for our list
		localJobList.setAdapter(arrayAdapter);
	}

	/**
	 * Load favorites jobs
	 */
	private void loadFavoritesJobs() {
		   //Initialize ListView
		favoriteJobList = (ListView) findViewById(R.id.favoriteJobList);
        
		favoriteJobList.setOnItemClickListener(new OnItemClickListener() {
        	 @Override
        	 public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        		
        		 Requester.job  = (Job)(favoriteJobList).getItemAtPosition(position);
            	 
        		 Thread thread =  new Thread(null, getJob, "hhclientBackground");
        		 thread.start();
        		 m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
	                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
        	 }
        });
    
		final List<Job> favoritesJobs = loadFavoritesJobsData();
		
		favoriteJobList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	            	final int p1 = pos;
	            	final CharSequence[] items = {
	            			getText(R.string.cnm_show_job), 
	                		getText(R.string.menu_open_job_web),
	            			getText(R.string.cnt_del_from_favorites), 
	            			getText(R.string.cnt_del_all_from_favorites)};

	            	AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
	            	builder.setTitle(getText(R.string.do_item));
	            	builder.setItems(items, new DialogInterface.OnClickListener() {
	            	    public void onClick(DialogInterface dialog, int item) {
	            	    	Job selectedJob;
	            	        switch(item)
	            	        {
	            	        	// show
	            	        	case 0: 
	            	        		Requester.job  = (Job)favoriteJobList.getItemAtPosition(p1);
	            	            	 
	            	        	      Thread thread =  new Thread(null, getJob, "hhclientBackground");
	            		                thread.start();
	            		                m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
	            		                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
	            	        		break;
	            	        	// Open in web
	            	        	case 1: 
	            	        		selectedJob  = (Job)favoriteJobList.getItemAtPosition(p1);
	            	        		Intent browserIntent = 
		    							new Intent("android.intent.action.VIEW", Uri.parse(selectedJob.webLink));
		    							startActivity(browserIntent);
	            	        		break;
	            	        	// del
	            	        	case 2: 
	            	        		 selectedJob  = (Job)favoriteJobList.getItemAtPosition(p1);
	            	        		 selectedJob.dh = dh;
	            	        		 long resd = selectedJob.deleteFromFavorites();
		    							if (resd == -1)
		    								// TODO
		    								Toast.makeText(getApplicationContext(), 
		    										getText(R.string.msg_not_del_job), Toast.LENGTH_SHORT).show();
		    							else
		    								Toast.makeText(getApplicationContext(), 
		    										getText(R.string.msg_del_job), Toast.LENGTH_SHORT).show();
	            	        		
		    						loadFavoritesJobsData();	
		    						break;
		    						// del all
	            	        	case 3: 
	            	        		for (int i = 0; i <favoritesJobs.size(); i++ )
	            	        		{
	            	        			Job jb = favoritesJobs.get(i);
	            	        			jb.dh = dh;
	            	        			jb.deleteFromFavorites();
	            	        		}
	            	        		
	            	        		Toast.makeText(getApplicationContext(), 
	            	        				getText(R.string.msg_del_all_from_favorites), Toast.LENGTH_SHORT).show();
		    						
	            	        		loadFavoritesJobsData();	
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
	}

	/**
	 * Load favorites jobs data from db
	 * @return
	 */
	private List<Job> loadFavoritesJobsData() {
		// select only favorite jobs
		final List<Job> favoritesJobs = dh.selectJobs(DataHelper.JOBS_TABLE_NAME, "favorite = 1", null);
		
		TextView favoritesJobListCount = (TextView) findViewById(R.id.favoriteJobListCount);
		favoritesJobListCount.setText(getText(R.string.jobs).toString()+" "+favoritesJobs.size());
		
        //Initialize our array adapter notice how it references the listitems.xml layout
		ArrayAdapter<Job> arrayAdapter = new JobListAdapter(SearchActivity.this, R.layout.joblistitem, favoritesJobs);
        //Set the above adapter as the adapter of choice for our list
		favoriteJobList.setAdapter(arrayAdapter);
		return favoritesJobs;
	}
    
	/*
	 * Thread for getting current search query jobs
	 */
	Runnable viewJobs = new Runnable(){
        @Override
        public void run() {
            getJobs();
            
            if (Requester.jobs.size() != 0)
            {
	        	Intent intent = new Intent(SearchActivity.this, JobsListActivity.class);
	        	SearchActivity.this.startActivity(intent);
            }
            else
            {
            	runOnUiThread(returnResEmptyResult);
            }
        }

		private void getJobs() {
			Requester.context = getBaseContext();
			Requester.currentUrl = RequestURL.getSearchURL(RequestURL.VACANCY_SEARCH, parameters);
			Requester.connect(Requester.currentUrl);
        	JobsListCache.jobs = Requester.jobs;       
        	JobsListCache.currentPage = 0;

        	runOnUiThread(returnRes);
		}
    };
    
    /*
     * Close progress dialog runable
     */
	Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            m_ProgressDialog.dismiss();
        }
    };
    
    /*
     * Close progress dialog runnable and make empty search result toast
     */
    Runnable returnResEmptyResult = new Runnable() {

        @Override
        public void run() {
        	Toast.makeText(getApplicationContext(), R.string.warning_nothing_search, Toast.LENGTH_SHORT).show();
            m_ProgressDialog.dismiss();
        }
    };
    
    /*
     * Add on click listeners to main search button
     */
	private void setSearchListeners() {
		final Button button = (Button) findViewById(R.id.ok);
		button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(android.R.drawable.ic_search_category_default), null, null, null);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	parameters.clear();
            	EditText searchTxt = (EditText) findViewById(R.id.search_text);
            	
            	if (searchTxt.getText().toString().trim().length() != 0){
	            	
            		Pair searchText = new Pair(RequestURL.TEXT, searchTxt.getText().toString().trim().replace(" ", whitespace));
	            	parameters.add(searchText);
            		
            		initSearchParams(parameters);
	            	
	                Thread thread =  new Thread(null, viewJobs, "hhclientBackground");
	                thread.start();
	                m_ProgressDialog = ProgressDialog.show(SearchActivity.this,    
	                      getText(R.string.please_wait), getText(R.string.retrieving_data), true);
            	}
            	else
            		Toast.makeText(getApplicationContext(), R.string.warning_empty_search, Toast.LENGTH_SHORT).show();
              
            }
        });
	}
	
	/**
	 * Gathering search parameters for new query
	 */
	private void initSearchParams(ArrayList<Pair> _params) {
		EditText salaryTxt = (EditText) findViewById(R.id.salary_text);
		Spinner currency_spinner = (Spinner) findViewById(R.id.currency_spinner);
		Spinner region_spinner = (Spinner) findViewById(R.id.region_spinner);
		Spinner employment_spinner = (Spinner) findViewById(R.id.employment_spinner);
		Spinner shedule_spinner = (Spinner) findViewById(R.id.shedule_spinner);
		CheckBox salary_check = (CheckBox) findViewById(R.id.onlysalary);
		
		if (salaryTxt.getText().toString().length() != 0)
		{
			Pair salaryText = new Pair(RequestURL.SALARY, salaryTxt.getText().toString().trim());
			_params.add(salaryText);
			
			Pair currency = new Pair(RequestURL.CURRENCY, currency_spinner.getSelectedItem().toString());
			_params.add(currency);
		}
		
		if (salary_check.isChecked())
		{
			Pair onlysalary = new Pair(RequestURL.ONLYSALARY, "true");
			_params.add(onlysalary);
		}
		
		int region_id = getRegionId(region_spinner.getSelectedItem().toString());
		Pair region = new Pair(RequestURL.REGION, String.valueOf(region_id));
		_params.add(region);
		
		int employment_id = getEmploymentId(employment_spinner.getSelectedItem().toString());
		if (employment_id != -1)
		{
			Pair employment = new Pair(RequestURL.EMPLOYMENT, String.valueOf(employment_id));
			_params.add(employment);
		}
		
		int shedule_id = getSheduleId(shedule_spinner.getSelectedItem().toString());
		if (shedule_id != -1)
		{
			Pair shedule = new Pair(RequestURL.SHEDULE, String.valueOf(shedule_id));
			_params.add(shedule);
		}
	}
	
	/*
	 * Get selected item id from employment spinner 
	 */
	private int getEmploymentId(String str) {
		List<Pair> employment = dh.selectEmployments();
		for (Pair r : employment)
		{
			if (r.paramName.equalsIgnoreCase(str))
				return Integer.parseInt(r.paramValue);
		}
		
		return -1;
	}
	
	/*
	 * Get selected item id from region spinner 
	 */
	private int getRegionId(String str) {
		List<Pair> regions = dh.selectRegions();
		for (Pair r : regions)
		{
			if (r.paramName.equalsIgnoreCase(str))
				return Integer.parseInt(r.paramValue);
		}
		
		return 113;
	}
	
	/*
	 * Get selected item id from schedule spinner 
	 */
	private int getSheduleId(String str) {
		List<Pair> schedules = dh.selectSchedules();
		for (Pair r : schedules)
		{
			if (r.paramName.equalsIgnoreCase(str))
				return Integer.parseInt(r.paramValue);
		}
		
		return -1;
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);

    	MenuItem menuItem;
    	
    	menuItem = menu.add(0, MENU_NEW_QUERY, Menu.NONE, R.string.menu_new_query);
    	menuItem.setIcon(android.R.drawable.ic_search_category_default);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						initNewQuery();
    						TabHost mTabHost = getTabHost();
    						mTabHost.setCurrentTab(0);
    						return true;
    					}
    			});
    	
    	menuItem = menu.add(0, MENU_SAVE_QUERY, Menu.NONE, R.string.menu_save_query);
    	menuItem.setIcon(android.R.drawable.ic_menu_save);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						EditText searchTxt = (EditText) findViewById(R.id.search_text);
    						
    						if (searchTxt.getText().length() != 0)
    						{
	    						showDialog(SEARCH_QUERY_DETAIL);
	    						return true;
    						}
    						else
    						{
    							Toast.makeText(getApplicationContext(), R.string.warning_empty_search, Toast.LENGTH_SHORT).show();
    							return true;
    						}
    					}
    			});
    	
    	menuItem = menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_pref);
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						startActivity(new Intent(SearchActivity.this, HHClientPreferenceActivity.class));
    						return true;
    					}
    			});
    	
    	menuItem = menu.add(0, MENU_QUIT, Menu.NONE, R.string.menu_quit);
    	menuItem.setIcon(android.R.drawable.ic_lock_power_off);
    	menuItem.setOnMenuItemClickListener(
    			new OnMenuItemClickListener(){
    					public boolean onMenuItemClick(MenuItem _menuItem) {
    						quit();
    						return true;
    					}
    			});
   
    	return true;
    }
    
    /**
     * Initialize new query
     */
	protected void initNewQuery() {
		EditText searchTxt = (EditText) findViewById(R.id.search_text);
		searchTxt.setText("");
		EditText salaryTxt = (EditText) findViewById(R.id.salary_text);
		salaryTxt.setText("");
		Spinner currency_spinner = (Spinner) findViewById(R.id.currency_spinner);
		currency_spinner.setSelection(0);
		Spinner region_spinner = (Spinner) findViewById(R.id.region_spinner);
		region_spinner.setSelection(0);
		Spinner employment_spinner = (Spinner) findViewById(R.id.employment_spinner);
		employment_spinner.setSelection(0);
		Spinner shedule_spinner = (Spinner) findViewById(R.id.shedule_spinner);
		shedule_spinner.setSelection(0);
		CheckBox salary_check = (CheckBox) findViewById(R.id.onlysalary);
		salary_check.setChecked(false);
	}

	/**
	 * quit from application
	 */
	private void quit() {
		this.finish();
	}
}
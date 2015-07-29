package com.aestudio.hhclient;

import java.util.List;

import com.aestudio.hhclient.JobsListActivity.JobsListCache;
import com.aestudio.hhclient.objects.Requester;
import com.aestudio.hhclient.objects.SearchQuery;
import com.aestudio.hhclient.objects.Requester.RequestURL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class HHClientWidgetProvider extends AppWidgetProvider {
	
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	DataHelper dh = new DataHelper(context);
    	
    	final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            List<SearchQuery> queries = dh.selectQueries("queryType = 1", null);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            
            if (queries.size() !=0)
            {
                // Create an Intent to launch ExampleActivity
                Intent intent = new Intent(context, JobsListActivity.class);
                intent.putExtra("widget", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Get the layout for the App Widget and attach an on-click listener to the button
                views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
            	
	            Requester.context = context;
				Requester.currentUrl = RequestURL.getSearchURL(RequestURL.VACANCY_SEARCH, queries.get(0).parameters);
				Requester.parameters = queries.get(0).parameters;
				Requester.connect(Requester.currentUrl);
				JobsListCache.jobs = Requester.jobs;
	            
	            if (Requester.found != 0)
	            {
		            views.setTextViewText(R.id.widget_query_label, "По запросу "+queries.get(0).name);
	            	views.setTextViewText(R.id.widget_jobs_label, "найдено "+Requester.found +" вакансий");
	            }
	            else
	            {
	            	views.setTextViewText(R.id.widget_query_label, "По запросу "+queries.get(0).name);
	            	views.setTextViewText(R.id.widget_jobs_label, "Нет подключения к сети...");
	            }
            }
            else
            {
            	// Create an Intent to launch ExampleActivity
                Intent intent = new Intent(context, SearchActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                // Get the layout for the App Widget and attach an on-click listener to the button
                views.setOnClickPendingIntent(R.id.widget_query_label, pendingIntent);
            	
            	views.setTextViewText(R.id.widget_query_label, "Добавьте запрос на виджет");
	            views.setTextViewText(R.id.widget_jobs_label, "");
            }
            //views.setOnClickPendingIntent(R.id.widget_text_label, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
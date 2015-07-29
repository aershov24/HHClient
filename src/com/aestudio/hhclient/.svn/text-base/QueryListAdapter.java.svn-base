package com.aestudio.hhclient;

import java.util.List;

import com.aestudio.hhclient.objects.Job;
import com.aestudio.hhclient.objects.SearchQuery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class QueryListAdapter extends ArrayAdapter<SearchQuery> {
 
    int resource;
    String response;
    Context context;
    //Initialize adapter
    public QueryListAdapter(Context _context, int resource, List<SearchQuery> items) {
        super(_context, resource, items);
        this.resource = resource;
        context = _context;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout alertView;
        //Get the current alert object
        SearchQuery al = getItem(position);
 
        //Inflate the view
        if(convertView == null)
        {
            alertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, alertView, true);
        }
        else
        {
            alertView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView queryName = (TextView)alertView.findViewById(R.id.queryName);
        //Assign the appropriate data from our alert object above
        queryName.setText(al.name);
        
        TextView queryParams = (TextView)alertView.findViewById(R.id.queryParams);
        queryParams.setText(al.getParamsString());
 
        return alertView;
    }
    
   
 
}


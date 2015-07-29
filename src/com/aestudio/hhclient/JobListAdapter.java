package com.aestudio.hhclient;

import java.util.List;

import com.aestudio.hhclient.objects.Job;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class JobListAdapter extends ArrayAdapter<Job> {
 
    int resource;
    String response;
    Context context;
    //Initialize adapter
    public JobListAdapter(Context _context, int resource, List<Job> items) {
        super(_context, resource, items);
        this.resource = resource;
        context = _context;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout alertView;
        //Get the current alert object
        Job al = getItem(position);
 
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
        TextView jobName = (TextView)alertView.findViewById(R.id.jobName);
        TextView jobDate = (TextView)alertView.findViewById(R.id.jobDate);
        TextView jobEmployer = (TextView)alertView.findViewById(R.id.jobEmployer);
        TextView jobSalary = (TextView)alertView.findViewById(R.id.jobSalary);
 
        //Assign the appropriate data from our alert object above
        jobName.setText(al.name);
        jobDate.setText(context.getText(R.string.updated).toString()+" "+ al.date.getDay()+"/"+al.date.getMonth()+"/"+al.date.getYear());
        jobEmployer.setText(al.employer+ ", "+al.region);
        jobSalary.setText(al.getSalary());
 
        return alertView;
    }
 
}

package com.aestudio.hhclient.objects;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;

import com.aestudio.hhclient.DataHelper;

/*
 * Class for encapsulating job logic
 */
public class Job {
	public DataHelper dh;
	
	public String name = "";
	public String description = "";
	public String employer = "";


	public Date date = new Date();
	public int salaryFrom = 0;
	public int salaryTo = 0;
	public String currency = "";
	public int id;
	public String webLink = ""; 
	public String xmlLink = ""; 
	public String region = ""; 
	public String shedule = "";
	public String employment = "";
	public String favorite = "0";
	
	public Job()
	{
	}
	
	public String getSalary() {
		StringBuilder sal = new StringBuilder();
		
		// TODO
		if (salaryFrom == 0 && salaryTo == 0)
			sal.append("з/п не указана");
		if (salaryFrom != 0)
			sal.append("От "+salaryFrom);
		if (salaryTo != 0)
			sal.append(" до "+salaryTo);
		if (currency.length() != 0)
			sal.append(", "+currency);
		
		return sal.toString();
	}
	
	public long addToFavorites()
	{
		if (dh != null)
		{
			if (notInDB())
				addToDB();
			
			ContentValues cv = new ContentValues();
			cv.put("favorite", "1");
			
			return dh.updateJob(cv, id);
		}
		else
			return -1;
		}

	private boolean notInDB() {
		if (dh != null)
		{
			List<Job> selJobs = dh.selectJobs(DataHelper.JOBS_TABLE_NAME, "jobId = "+id, null);
			if (selJobs.size() == 0)
				return true;
			else
				return false;
		}
		return false;
	}

	public long addToDB() {
		if (dh != null)
		{
			if (notInDB())
				return dh.insertJob(name, 
						id, 
						webLink, 
						xmlLink, 
						"0",
						description,
						region,
						shedule, 
						employment,
						employer,
						date,
						salaryFrom,
						salaryTo,
						currency);
			else
				return 0;
		}
		else
			return -1;
		}

	public long deleteFromFavorites() {
		if (dh != null)
		{
			if (!notInDB())
			{
				ContentValues cv = new ContentValues();
				cv.put("favorite", "0");
				
				return dh.updateJob(cv, id);
			}
			else
				return -1;
		}
		else
			return -1;
		}

	public long delete() {
		if (this.favorite == "1")
			return -2;
		if (dh != null)
		{
			return dh.delete(DataHelper.JOBS_TABLE_NAME, "jobId = "+id, null);
		}
		else
			return -1;
	}
		
}

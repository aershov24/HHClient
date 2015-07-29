package com.aestudio.hhclient.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.aestudio.hhclient.R;
import com.aestudio.hhclient.JobsListActivity.JobsListCache;
import com.aestudio.hhclient.R.string;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

/**
 * Class for get and parse requests for hh.api
 * @author aershov
 *
 */
public class Requester {

		public static int saveJobs = 0;
		public static Handler handler;
		public static int found;
		public static ArrayList<Job> jobs = new ArrayList<Job>();
		public static Job job = new Job();
		public static Context context;
		public static String currentUrl;
		public static ArrayList<Pair> parameters = new ArrayList<Pair>();
	
		public static class Pair
		{
			public Pair(String _paramName, String _paramValue)
			{
				paramName = _paramName;
				paramValue = _paramValue;
			}
			
			public Pair() {
				// Auto-generated constructor stub
			}

			public String paramName;
			public String paramValue;
		}
		
		/**
		 * Class for construct request url with input params
		 * @author aershov
		 *
		 */
		static public class RequestURL
		{
			public static String VACANCY_SEARCH = "http://api.hh.ru/1/xml/vacancy/search/";
			public static String JOB = "http://api.hh.ru/1/xml/vacancy/";
			public static String REGION = "region";
			public static String EMPLOYMENT = "employment";
			public static String SHEDULE = "schedule";
			public static String EXPERIENCE = "experience";
			public static String ONLYSALARY = "onlysalary";
			public static String ITEMS = "items"; 
			public static String TEXT = "text"; 
			public static String SALARY = "salary"; 
			public static String CURRENCY = "currency"; 
			
			/*
			 * Constructed request url
			 */
			public static String getSearchURL(String type, ArrayList<Pair> parameters)
			{
				Requester.parameters = parameters;
				StringBuilder str = new StringBuilder();
				
				if (type.contentEquals(VACANCY_SEARCH))
				{
					str.append(VACANCY_SEARCH);
					if (parameters.size() != 0)
					{
						str.append("?");
						for (int i = 0; i < parameters.size(); i++)
						{
							Pair param = parameters.get(i);
							str.append(param.paramName+"="+param.paramValue);
							int t = i+1;
							if (t < parameters.size())
								str.append("&");
						}
					}
					Log.i("HHClient", str.toString());
					return str.toString();
				}
				else
					return "";
				
			}

			public static String getJobURL(String type, String jobId) {
				StringBuilder str = new StringBuilder();
				
				if (type.contentEquals(JOB))
				{
					str.append(JOB);
					str.append(jobId+"/");
					return str.toString();
				}
				else
					return "";
			}
		}
	
		private static String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the BufferedReader.readLine()
			 * method. We iterate until the BufferedReader return null which means
			 * there's no more data to read. Each line will appended to a StringBuilder
			 * and returned as String.
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return sb.toString();
		}
		
		/*
		 * Parse xml for getting single row by hh id
		 */
		public static Job getJob(String url)
		{
			Job job = new Job();
			
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(url); 

			// Execute the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Examine the response status
				Log.i("HHClient", response.getStatusLine().toString());

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				// to worry about connection release

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					try {
						 DocumentBuilder builder = factory.newDocumentBuilder();
						 StringReader reader = new StringReader(result);
						 InputSource inputSource = new InputSource( reader );
						 Document dom = builder.parse(inputSource);
						 // root is always magic node
						 org.w3c.dom.Element root = dom.getDocumentElement();
						 // search result
						 // vacancy parse
						 if (root.getNodeName().equalsIgnoreCase("vacancy"))
						 {
							 job = new Job();
							 	Node vacancy = root;
								 job.id = Integer.parseInt(vacancy.getAttributes().getNamedItem("id").getNodeValue());
								 NodeList job_properties = vacancy.getChildNodes();
								 for (int k = 0; k < job_properties.getLength(); k++){
									 Node job_property = job_properties.item(k);
									 if (job_property.getNodeName().equalsIgnoreCase("link"))
									 {
										 if (job_property.getAttributes().
												 getNamedItem("rel").getNodeValue().equalsIgnoreCase("alternate"))
										 {
											 job.webLink = job_property.getAttributes().
											 	getNamedItem("href").getNodeValue();
										 }else if (job_property.getAttributes().
												 getNamedItem("rel").getNodeValue().equalsIgnoreCase("self"))
										 {
											 job.xmlLink = job_property.getAttributes().
											 	getNamedItem("href").getNodeValue();
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("name"))
									 {
										 job.name = job_property.getChildNodes().item(0).getNodeValue();
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("employer"))
									 {
										 Node employer = job_property;
										 NodeList employer_properties = employer.getChildNodes();
										 for (int q = 0; q < employer_properties.getLength(); q++){
											 Node employer_property = employer_properties.item(q);
											 if (employer_property.getNodeName().equalsIgnoreCase("name"))
											 {
												 if (employer_property.getChildNodes().item(0) != null)
													 job.employer = employer_property.getChildNodes().item(0).getNodeValue();
												 else
													 job.employer = "Нет наименования";
											 } 
										 }
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("salary"))
									 {
										 Node salary = job_property;
										 NodeList salary_properties = salary.getChildNodes();
										 for (int q = 0; q < salary_properties.getLength(); q++){
											 Node salary_property = salary_properties.item(q);
											 if (salary_property.getNodeName().equalsIgnoreCase("from"))
											 {
												 job.salaryFrom = Integer.parseInt(salary_property.getChildNodes().
														 item(0).getNodeValue());
											 } else	 if (salary_property.getNodeName().equalsIgnoreCase("to"))
											 {
												 job.salaryTo = Integer.parseInt(salary_property.getChildNodes().item(0).
														 getNodeValue());
											 } else	 if (salary_property.getNodeName().equalsIgnoreCase("currency"))
											 {
												 job.currency = salary_property.getChildNodes().item(0).getNodeValue();
											 } 
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("update"))
									 {
										 Node update = job_property;
										 int year = Integer.parseInt(update.getAttributes().getNamedItem("year").getNodeValue());
										 int month = Integer.parseInt(update.getAttributes().getNamedItem("month").getNodeValue());
										 int day = Integer.parseInt(update.getAttributes().getNamedItem("day").getNodeValue());
										 
										 job.date = new Date(year, month, day);
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("region"))
									 {
										 Node region = job_property;
										 
										 Node region_name = region.getChildNodes().item(0);
										 job.region = region_name.getChildNodes().item(0).getNodeValue();
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("description"))
									 {
										 Node description = job_property;
										 //description.normalize();
										 for (int q = 0; q < description.getChildNodes().getLength(); q++){
											 job.description += description.getChildNodes().item(q).getNodeValue();
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("schedule"))
									 {
										 job.shedule = job_property.getChildNodes().item(0).getNodeValue();
									 } else if (job_property.getNodeName().equalsIgnoreCase("employment"))
									 {
										 if (job_property.getChildNodes().item(0) != null)
											 job.employment = job_property.getChildNodes().item(0).getNodeValue();
									 }
								 }
								
						 }
			       } catch (Exception e) {
			           throw new RuntimeException(e);
			       } 

					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return job;
		}

		/*
		 * Get and parse response for current url
		 */
		public static void connect(String url)
		{
			//currentUrl = url;
			
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(url); 

			// Execute the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Examine the response status
				Log.i("HHClient", response.getStatusLine().toString());

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				// to worry about connection release

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					try {
						 DocumentBuilder builder = factory.newDocumentBuilder();
						 StringReader reader = new StringReader(result);
						 InputSource inputSource = new InputSource( reader );
						 Document dom = builder.parse(inputSource);
						 // root is always magic node
						 org.w3c.dom.Element root = dom.getDocumentElement();
						 // search result
						 if (root.getNodeName().equalsIgnoreCase("result")){
							 jobs.clear();
							 NodeList items = root.getChildNodes();            
							 for (int i=0; i < items.getLength(); i++){
								 Node item = items.item(i);
								 String name = item.getNodeName();
								 
								 if (name.equalsIgnoreCase("vacancies")){
									 NodeList vacancies = item.getChildNodes();
									 for (int j=0; j < vacancies.getLength(); j++){
										 Node vacancies_item = vacancies.item(j);
										 String vacancies_item_name = vacancies_item.getNodeName();
										 if (vacancies_item_name.equalsIgnoreCase("vacancy")){
											 Job job = new Job();
											 Node vacancy = vacancies_item;
											 job.id = Integer.parseInt(vacancy.getAttributes().getNamedItem("id").getNodeValue());
											 NodeList job_properties = vacancy.getChildNodes();
											 for (int k = 0; k < job_properties.getLength(); k++){
												 Node job_property = job_properties.item(k);
												 if (job_property.getNodeName().equalsIgnoreCase("link"))
												 {
													 if (job_property.getAttributes().
															 getNamedItem("rel").getNodeValue().equalsIgnoreCase("alternate"))
													 {
														 job.webLink = job_property.getAttributes().
														 	getNamedItem("href").getNodeValue();
													 }else if (job_property.getAttributes().
															 getNamedItem("rel").getNodeValue().equalsIgnoreCase("self"))
													 {
														 job.xmlLink = job_property.getAttributes().
														 	getNamedItem("href").getNodeValue();
													 }
												 } else if (job_property.getNodeName().equalsIgnoreCase("name"))
												 {
													 job.name = job_property.getChildNodes().item(0).getNodeValue();
												 }
												 else if (job_property.getNodeName().equalsIgnoreCase("employer"))
												 {
													 Node employer = job_property;
													 NodeList employer_properties = employer.getChildNodes();
													 for (int q = 0; q < employer_properties.getLength(); q++){
														 Node employer_property = employer_properties.item(q);
														 if (employer_property.getNodeName().equalsIgnoreCase("name"))
														 {
															 if (employer_property.getChildNodes().item(0) != null)
																 job.employer = employer_property.getChildNodes().item(0).getNodeValue();
															 else
																 job.employer = "Нет наименования";
														 } 
													 }
												 }
												 else if (job_property.getNodeName().equalsIgnoreCase("salary"))
												 {
													 Node salary = job_property;
													 NodeList salary_properties = salary.getChildNodes();
													 for (int q = 0; q < salary_properties.getLength(); q++){
														 Node salary_property = salary_properties.item(q);
														 if (salary_property.getNodeName().equalsIgnoreCase("from"))
														 {
															 job.salaryFrom = Integer.parseInt(salary_property.getChildNodes().
																	 item(0).getNodeValue());
														 } else	 if (salary_property.getNodeName().equalsIgnoreCase("to"))
														 {
															 job.salaryTo = Integer.parseInt(salary_property.getChildNodes().item(0).
																	 getNodeValue());
														 } else	 if (salary_property.getNodeName().equalsIgnoreCase("currency"))
															 job.currency = salary_property.getChildNodes().item(0).getNodeValue();
													 }
												 } else if (job_property.getNodeName().equalsIgnoreCase("update"))
												 {
													 Node update = job_property;
													 int year = Integer.parseInt(update.getAttributes().getNamedItem("year").getNodeValue());
													 int month = Integer.parseInt(update.getAttributes().getNamedItem("month").getNodeValue());
													 int day = Integer.parseInt(update.getAttributes().getNamedItem("day").getNodeValue());
													 
													 job.date = new Date(year, month, day);
												 }
												 else if (job_property.getNodeName().equalsIgnoreCase("region"))
												 {
													 Node region = job_property;
													 
													 Node region_name = region.getChildNodes().item(0);
													 job.region = region_name.getChildNodes().item(0).getNodeValue();
												 }
											 }
											 
											 jobs.add(job);
										 }
									 }
								 } else if (name.equalsIgnoreCase("found"))
								 {
									 found = Integer.parseInt(item.getChildNodes().item(0).getNodeValue());
								 }
							 }
						}
						 // vacancy parse
						 else if (root.getNodeName().equalsIgnoreCase("vacancy"))
						 {
							 job = new Job();
							 	Node vacancy = root;
								 job.id = Integer.parseInt(vacancy.getAttributes().getNamedItem("id").getNodeValue());
								 NodeList job_properties = vacancy.getChildNodes();
								 for (int k = 0; k < job_properties.getLength(); k++){
									 Node job_property = job_properties.item(k);
									 if (job_property.getNodeName().equalsIgnoreCase("link"))
									 {
										 if (job_property.getAttributes().
												 getNamedItem("rel").getNodeValue().equalsIgnoreCase("alternate"))
										 {
											 job.webLink = job_property.getAttributes().
											 	getNamedItem("href").getNodeValue();
										 }else if (job_property.getAttributes().
												 getNamedItem("rel").getNodeValue().equalsIgnoreCase("self"))
										 {
											 job.xmlLink = job_property.getAttributes().
											 	getNamedItem("href").getNodeValue();
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("name"))
									 {
										 job.name = job_property.getChildNodes().item(0).getNodeValue();
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("employer"))
									 {
										 Node employer = job_property;
										 NodeList employer_properties = employer.getChildNodes();
										 for (int q = 0; q < employer_properties.getLength(); q++){
											 Node employer_property = employer_properties.item(q);
											 if (employer_property.getNodeName().equalsIgnoreCase("name"))
											 {
												 if (employer_property.getChildNodes().item(0) != null)
													 job.employer = employer_property.getChildNodes().item(0).getNodeValue();
												 else
													 job.employer = "Нет наименования";
											 } 
										 }
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("salary"))
									 {
										 Node salary = job_property;
										 NodeList salary_properties = salary.getChildNodes();
										 for (int q = 0; q < salary_properties.getLength(); q++){
											 Node salary_property = salary_properties.item(q);
											 if (salary_property.getNodeName().equalsIgnoreCase("from"))
											 {
												 job.salaryFrom = Integer.parseInt(salary_property.getChildNodes().
														 item(0).getNodeValue());
											 } else	 if (salary_property.getNodeName().equalsIgnoreCase("to"))
											 {
												 job.salaryTo = Integer.parseInt(salary_property.getChildNodes().item(0).
														 getNodeValue());
											 } else	 if (salary_property.getNodeName().equalsIgnoreCase("currency"))
											 {
												 job.currency = salary_property.getChildNodes().item(0).getNodeValue();
											 } 
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("update"))
									 {
										 Node update = job_property;
										 int year = Integer.parseInt(update.getAttributes().getNamedItem("year").getNodeValue());
										 int month = Integer.parseInt(update.getAttributes().getNamedItem("month").getNodeValue());
										 int day = Integer.parseInt(update.getAttributes().getNamedItem("day").getNodeValue());
										 
										 job.date = new Date(year, month, day);
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("region"))
									 {
										 Node region = job_property;
										 
										 Node region_name = region.getChildNodes().item(0);
										 job.region = region_name.getChildNodes().item(0).getNodeValue();
									 }
									 else if (job_property.getNodeName().equalsIgnoreCase("description"))
									 {
										 Node description = job_property;
										 //description.normalize();
										 for (int q = 0; q < description.getChildNodes().getLength(); q++){
											 job.description += description.getChildNodes().item(q).getNodeValue();
										 }
									 } else if (job_property.getNodeName().equalsIgnoreCase("schedule"))
									 {
										 job.shedule = job_property.getChildNodes().item(0).getNodeValue();
									 } else if (job_property.getNodeName().equalsIgnoreCase("employment"))
									 {
										 job.employment = job_property.getChildNodes().item(0).getNodeValue();
									 }
								 }
								
						 }
							
			       } catch (Exception e) {
			           throw new RuntimeException(e);
			       } 

					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * Get jobs array from search's single page
		 */
		public static ArrayList<Job> getJobsFromPage(String url) {
			ArrayList<Job> jobs = new ArrayList<Job>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(url); 

			// Execute the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Examine the response status

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				// to worry about connection release

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);
					
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					try {
						 DocumentBuilder builder = factory.newDocumentBuilder();
						 StringReader reader = new StringReader(result);
						 InputSource inputSource = new InputSource( reader );
						 Document dom = builder.parse(inputSource);
						 // root is always magic node
						 org.w3c.dom.Element root = dom.getDocumentElement();
						 // search result
						 if (root.getNodeName().equalsIgnoreCase("result")){
							 ids.clear();
							 NodeList items = root.getChildNodes();            
							 for (int i=0; i < items.getLength(); i++){
								 Node item = items.item(i);
								 String name = item.getNodeName();
								 
								 if (name.equalsIgnoreCase("vacancies")){
									 NodeList vacancies = item.getChildNodes();
									 for (int j=0; j < vacancies.getLength(); j++){
										 Node vacancies_item = vacancies.item(j);
										 String vacancies_item_name = vacancies_item.getNodeName();
										 if (vacancies_item_name.equalsIgnoreCase("vacancy")){
											 Node vacancy = vacancies_item;
											 int id = Integer.parseInt(vacancy.getAttributes().getNamedItem("id").getNodeValue());
											 ids.add(id);
										 }
									 }
								 }
							 }
						}	
			       } catch (Exception e) {
			           throw new RuntimeException(e);
			       } 

					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// ids have jobs id for current page
			for (int i=0; i< ids.size(); i++)
			{
				Job jb = Requester.getJob(RequestURL.getJobURL(RequestURL.JOB, ids.get(i).toString()));
				
                Message msg = handler.obtainMessage();
                msg.arg1 = saveJobs++;
                handler.sendMessage(msg);
				jobs.add(jb);
			}
			
			return jobs;
		}
}

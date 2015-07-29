package com.aestudio.hhclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aestudio.hhclient.objects.Job;
import com.aestudio.hhclient.objects.Requester.Pair;
import com.aestudio.hhclient.objects.SearchQuery;

public class DataHelper {

   private static final String DATABASE_NAME = "hhclient.db";
   private static final int DATABASE_VERSION = 1;
   public static final String JOBS_TABLE_NAME = "jobs";
   public static final String FAVORITES_TABLE_NAME = "favorites";
   public static final String QUERIES_TABLE_NAME = "queries";
   public static final String QUERIES_PAR_TABLE_NAME = "queries_params";
   public static final String REGION_TABLE_NAME = "regions";
   public static final String EMPLOYMENT_TABLE_NAME = "employment";
   public static final String SCHEDULE_TABLE_NAME = "schedule";
   public static final String CURRENCY_TABLE_NAME = "currency";

   private Context context;
   private SQLiteDatabase db;

   private SQLiteStatement insertJobStmt;
   private SQLiteStatement insertQueryStmt;
   private SQLiteStatement insertQueryParamStmt;

   
   private static final String INSERT_JOB = "insert into " + JOBS_TABLE_NAME 
   + "(jobName, jobId, jobWebLink, jobXmlLink, favorite, description, region, shedule, employment, employer, updateDate, salaryFrom, salaryTo, currency) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   
   private static final String INSERT_QUERY = "insert into " + QUERIES_TABLE_NAME 
   + "(queryName, queryType) values (?,?)";
   
   private static final String INSERT_QUERY_PARAM = "insert into " + QUERIES_PAR_TABLE_NAME 
   + "(queryId, paramName, paramValue) values (?,?,?)";
   


   public DataHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertJobStmt = this.db.compileStatement(INSERT_JOB);
      this.insertQueryStmt = this.db.compileStatement(INSERT_QUERY);
      this.insertQueryParamStmt = this.db.compileStatement(INSERT_QUERY_PARAM);
   }

   public long insertQuery(String queryName, String queryType)
   {
	   this.insertQueryStmt.bindString(1, queryName);
	   this.insertQueryStmt.bindString(2, queryType);
	   
	   return this.insertQueryStmt.executeInsert();
   }
   
   public long insertQueryParam(long id, String paramName, String paramValue)
   {
	   this.insertQueryParamStmt.bindLong(1, id);
	   this.insertQueryParamStmt.bindString(2, paramName);
	   this.insertQueryParamStmt.bindString(3, paramValue);
	   
	   return this.insertQueryParamStmt.executeInsert();
   }
   
   public long insertJob(String jobName, 
		   int jobId, 
		   String jobWebLink, 
		   String jobXmlLink, 
		   String fv, 
		   String description, 
		   String region, 
		   String shedule, 
		   String employment,
		   String employer,
		   Date updateDate,
		   int salaryFrom,
		   int salaryTo,
		   String currency) {
	   
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	   
      this.insertJobStmt.bindString(1, jobName);
      this.insertJobStmt.bindDouble(2, jobId);
      this.insertJobStmt.bindString(3, jobWebLink);
      this.insertJobStmt.bindString(4, jobXmlLink);
      this.insertJobStmt.bindString(5, fv);
      this.insertJobStmt.bindString(6, description);
      this.insertJobStmt.bindString(7, region);
      this.insertJobStmt.bindString(8, shedule);
      this.insertJobStmt.bindString(9, employment);
      this.insertJobStmt.bindString(10, employer); 
      this.insertJobStmt.bindString(11, dateFormat.format(updateDate));
      this.insertJobStmt.bindDouble(12, salaryFrom);
      this.insertJobStmt.bindDouble(13, salaryTo);
      this.insertJobStmt.bindString(14, currency); 
      
      return this.insertJobStmt.executeInsert();
   }
   
   public int updateJob(ContentValues updateJob, int jobId)
   {
       return db.update(JOBS_TABLE_NAME, updateJob, "jobId = ?", new String[] {Long.toString(jobId)});
   }

   public int deleteAll(String table) {
      return this.db.delete(table, null, null);
   }
   
   public int delete(String table, String where, String[] params) {
	      return this.db.delete(table, where, params);
	   }
   
   public Job selectJob(int id)
   {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	   
	   Cursor cursor = this.db.query(JOBS_TABLE_NAME, null, 
			   "jobId = ?", null, null, null, null);
	   
	   Job jb = new Job();
	   
	   if (cursor.moveToFirst()) {
	         do 
	         {
	        	 jb = new Job(); 
	        	 jb.name = cursor.getString(1);
	        	 jb.id = cursor.getInt(2);
	        	 jb.webLink = cursor.getString(3);
	        	 jb.xmlLink = cursor.getString(4);
	        	 jb.favorite = cursor.getString(5);
	        	 jb.description = cursor.getString(6);
	        	 jb.region = cursor.getString(7);
	        	 jb.shedule = cursor.getString(8);
	        	 jb.employment = cursor.getString(9);
	        	 jb.employer = cursor.getString(10);
	        	 jb.salaryFrom = cursor.getInt(12);
	        	 jb.salaryTo = cursor.getInt(13);
	        	 jb.currency = cursor.getString(14);
	        	 try {
					jb.date = dateFormat.parse(cursor.getString(11));
				} catch (ParseException e) {
					jb.date = new Date();
				}
	         } 
	         while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return jb;
   }
   
   public List<SearchQuery> selectQueries(String where, String orderby)
   {
	      List<SearchQuery> list = new ArrayList<SearchQuery>();
	      Cursor cursor = this.db.query(QUERIES_TABLE_NAME, null, 
	    		  where, null, null, null, orderby);
	      if (cursor.moveToFirst()) {
	         do 
	         {
	        	 SearchQuery sq = new SearchQuery(); 
	        	 sq.id = cursor.getInt(0);
	        	 sq.name = cursor.getString(1);
	        	 sq.type = cursor.getString(2);
	        	 
	        	 Cursor cursor2 = this.db.query(QUERIES_PAR_TABLE_NAME, null, 
	   	    		  "queryId = "+sq.id, null, null, null, null);
	        	   if (cursor2.moveToFirst()) {
	      	         do 
	      	         {
	      	        	 Pair param = new Pair();
	      	        	 param.paramName = cursor2.getString(2);
	      	        	 param.paramValue = cursor2.getString(3);
	      	        	 
	      	        	 sq.parameters.add(param);
	      	         }

	      	       while (cursor2.moveToNext());
	      	     }
	        	 
	        	 list.add(sq);
	         } 
	         while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
   }

   public List<Job> selectJobs(String table, String where, String orderby) {
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	   
      List<Job> list = new ArrayList<Job>();
      Cursor cursor = this.db.query(table, null, 
    		  where, null, null, null, orderby);
      if (cursor.moveToFirst()) {
         do 
         {
        	 Job jb = new Job(); 
        	 jb.name = cursor.getString(1);
        	 jb.id = cursor.getInt(2);
        	 jb.webLink = cursor.getString(3);
        	 jb.xmlLink = cursor.getString(4);
        	 jb.favorite = cursor.getString(5);
        	 jb.description = cursor.getString(6);
        	 jb.region = cursor.getString(7);
        	 jb.shedule = cursor.getString(8);
        	 jb.employment = cursor.getString(9);
        	 jb.employer = cursor.getString(10);
        	 jb.salaryFrom = cursor.getInt(12);
        	 jb.salaryTo = cursor.getInt(13);
        	 jb.currency = cursor.getString(14);
        	 try {
				jb.date = dateFormat.parse(cursor.getString(11));
			} catch (ParseException e) {
				jb.date = new Date();
			}
        	 list.add(jb);
         } 
         while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
   
   public List<String> selectCurrencies() {
	   List<String> list = new ArrayList<String>();
	   Cursor cursor = this.db.query(DataHelper.CURRENCY_TABLE_NAME, null, 
	    		  null, null, null, null, null);
	   if (cursor.moveToFirst()) {
	         do 
	         {
	        	 String jb = cursor.getString(1);
	        	
	        	 list.add(jb);
	         } 
	         while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	}
   
   public List<Pair> selectSchedules() {
	   List<Pair> list = new ArrayList<Pair>();
	      Cursor cursor = this.db.query(DataHelper.SCHEDULE_TABLE_NAME, null, 
	    		  null, null, null, null, null);
	      if (cursor.moveToFirst()) {
	         do 
	         {
	        	 Pair jb = new Pair(); 
	        	 jb.paramValue = String.valueOf(cursor.getInt(1));
	        	 jb.paramName = cursor.getString(2);
	        	
	        	 list.add(jb);
	         } 
	         while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	}
   
   public List<Pair> selectEmployments() {
	   List<Pair> list = new ArrayList<Pair>();
	      Cursor cursor = this.db.query(DataHelper.EMPLOYMENT_TABLE_NAME, null, 
	    		  null, null, null, null, null);
	      if (cursor.moveToFirst()) {
	         do 
	         {
	        	 Pair jb = new Pair(); 
	        	 jb.paramValue = String.valueOf(cursor.getInt(1));
	        	 jb.paramName = cursor.getString(2);
	        	
	        	 list.add(jb);
	         } 
	         while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	}
   
   public List<Pair> selectRegions() {
	   
      List<Pair> list = new ArrayList<Pair>();
      Cursor cursor = this.db.query(DataHelper.REGION_TABLE_NAME, null, 
    		  null, null, null, null, null);
      if (cursor.moveToFirst()) {
         do 
         {
        	 Pair jb = new Pair(); 
        	 jb.paramValue = String.valueOf(cursor.getInt(1));
        	 jb.paramName = cursor.getString(2);
        	
        	 list.add(jb);
         } 
         while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
   
	public List<SearchQuery> selectSearchQueries(String queriesTableName,
			Object object, Object object2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public long updateQuery(ContentValues cv, long id) {
		return db.update(QUERIES_TABLE_NAME, cv, "id = ?", new String[] {Long.toString(id)});
	
	}

   private static class OpenHelper extends SQLiteOpenHelper {

	   private static final String INSERT_REGION = "insert into " + REGION_TABLE_NAME 
	   	+ "(regionId, regionName) values (?,?)";
	   private static final String INSERT_EMPLOYMENT = "insert into " + EMPLOYMENT_TABLE_NAME 
	   	+ "(employmentId, employmentName) values (?,?)";
	   private static final String INSERT_SCHELUDE = "insert into " + SCHEDULE_TABLE_NAME 
	   	+ "(scheduleId, scheduleName) values (?,?)";
	   private static final String INSERT_CURRENCY = "insert into " + CURRENCY_TABLE_NAME 
	   	+ "(currencyName) values (?)";
	   
	   private SQLiteStatement insertRegionStmt;
	   private SQLiteStatement insertEmploymentStmt;
	   private SQLiteStatement insertScheduleStmt;
	   private SQLiteStatement insertCurrencyStmt;
	   
	OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + JOBS_TABLE_NAME + " (id INTEGER PRIMARY KEY autoincrement, jobName TEXT, jobId INTEGER, jobWebLink TEXT, jobXmlLink TEXT, favorite TEXT, description TEXT, region TEXT, shedule TEXT, employment TEXT, employer TEXT, updateDate DATE, salaryFrom INTEGER, salaryTo INTEGER, currency TEXT)");
         db.execSQL("CREATE TABLE " + QUERIES_TABLE_NAME + " (id INTEGER PRIMARY KEY autoincrement, queryName TEXT, queryType TEXT)");
         db.execSQL("CREATE TABLE " + QUERIES_PAR_TABLE_NAME + " (id INTEGER PRIMARY KEY autoincrement, queryId INTEGER, paramName TEXT, paramValue)");
         db.execSQL("CREATE TABLE " + REGION_TABLE_NAME + " (id INTEGER PRIMARY KEY,regionId INTEGER, regionName TEXT)");
         db.execSQL("CREATE TABLE " + EMPLOYMENT_TABLE_NAME + " (id INTEGER PRIMARY KEY, employmentId INTEGER, employmentName TEXT)");
         db.execSQL("CREATE TABLE " + SCHEDULE_TABLE_NAME + " (id INTEGER PRIMARY KEY, scheduleId INTEGER, scheduleName TEXT)");
         db.execSQL("CREATE TABLE " + CURRENCY_TABLE_NAME + " (id INTEGER PRIMARY KEY, currencyName TEXT)");
         
         this.insertRegionStmt = db.compileStatement(INSERT_REGION);
         insertRegions();
         
         this.insertEmploymentStmt = db.compileStatement(INSERT_EMPLOYMENT);
         insertEmployments();
         
         this.insertScheduleStmt = db.compileStatement(INSERT_SCHELUDE);
         insertSchedules();
         
         this.insertCurrencyStmt = db.compileStatement(INSERT_CURRENCY);
         insertCurrencies();
      }

	@Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + QUERIES_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + QUERIES_PAR_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + REGION_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + EMPLOYMENT_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE_NAME);
         db.execSQL("DROP TABLE IF EXISTS " + CURRENCY_TABLE_NAME);
         onCreate(db);
      }
	
	 public long insertEmployment(int employmentId, String employmentName)
     {
  	   this.insertEmploymentStmt.bindLong(1, employmentId);
  	   this.insertEmploymentStmt.bindString(2, employmentName);
  	   
  	   return this.insertEmploymentStmt.executeInsert();
     }
	 
     public long insertRegion(int regionId, String regionName)
     {
  	   this.insertRegionStmt.bindLong(1, regionId);
  	   this.insertRegionStmt.bindString(2, regionName);
  	   
  	   return this.insertRegionStmt.executeInsert();
     }
     
     public long insertSchedule(int scheduleId, String scheduleName)
     {
  	   this.insertScheduleStmt.bindLong(1, scheduleId);
  	   this.insertScheduleStmt.bindString(2, scheduleName);
  	   
  	   return this.insertScheduleStmt.executeInsert();
     }
     
     public long insertCurrency(String currencyName)
     {
  	   this.insertCurrencyStmt.bindString(1, currencyName);
  	   
  	   return this.insertCurrencyStmt.executeInsert();
     }
     
 	private void insertCurrencies() {
 		insertCurrency("RUR");
 		insertCurrency("USD");
 		insertCurrency("EUR");
	}
     
	private void insertSchedules() {
		insertSchedule(-1, "Любой");
		insertSchedule(0, "Полный день");
		insertSchedule(1, "Сменный график");
		insertSchedule(2, "Гибкий график");
		insertSchedule(3, "Удаленная работа");
	}
	
     private void insertEmployments() 
     {
    	insertEmployment(-1, "Любой");
    	insertEmployment(0, "Полная занятость");
    	insertEmployment(1, "Частичная занятость");
    	insertEmployment(2, "Проектная работа");
     }
      
     public void insertRegions() {
  		insertRegion(113, "Россия");
    	insertRegion(1, "Москва");
    	insertRegion(2, "Санкт-Петербург");
    	insertRegion(4, "Новосибирск");
    	insertRegion(3, "Екатеринбург");
    	insertRegion(66, "Нижний Новгород");
    	insertRegion(88, "Казань");
    	insertRegion(1586, "Самара");
    	insertRegion(76, "Ростов-на-Дону");
    	insertRegion(115, "Киев");
    	insertRegion(1002, "Минск");
     }
   }
}

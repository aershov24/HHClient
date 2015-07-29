package com.aestudio.hhclient.objects;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import com.aestudio.hhclient.DataHelper;
import com.aestudio.hhclient.objects.Requester.Pair;
import com.aestudio.hhclient.objects.Requester.RequestURL;

public class SearchQuery {
	public long id;
	public String name;
	
	public String type = "0";
	public ArrayList<Pair> parameters = new ArrayList<Pair>();
	public DataHelper dh;
	
	public long addToWidget()
	{
		if (dh != null)
		{
			ContentValues cv = new ContentValues();
			cv.put("queryType", "1");
			
			return dh.updateQuery(cv, id);
		}
		else
			return -1;
	}
	
	public long delFromWidget()
	{
		if (dh != null)
		{
			ContentValues cv = new ContentValues();
			cv.put("queryType", "0");
			
			return dh.updateQuery(cv, id);
		}
		else
			return -1;
	}
	
	public long addToDB() {
		if (dh != null)
		{
			if (notInDB())
			{
				id = dh.insertQuery(name, type);
				for (int i= 0; i< parameters.size(); i++)
				{
					dh.insertQueryParam(id, parameters.get(i).paramName, parameters.get(i).paramValue);
				}
			}
			else
				return 0;
		}
		else
			return -1;
		
		return id;
	}
	
	public boolean delete() {
		if (dh != null)
		{
			dh.delete(DataHelper.QUERIES_PAR_TABLE_NAME, "queryId = "+id, null);
			dh.delete(DataHelper.QUERIES_TABLE_NAME, "id = "+id, null);
			
			return true;
		}
		else 
			return false;
	}

	private boolean notInDB() {
		if (dh != null)
		{
			List<SearchQuery> lst = dh.selectQueries("id = "+id, null);
			if (lst.size() == 0)
				return true;
			else
				return false;
		}
		else 
			return false;
	}

	public String getParamsString() {
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < parameters.size(); i++)
		{
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.TEXT))
				str.append(parameters.get(i).paramValue);
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.REGION))
				str.append(", "+getRegionString(parameters.get(i).paramValue));
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.EMPLOYMENT))
				str.append(", "+getEmploymentString(parameters.get(i).paramValue));
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.SHEDULE))
				str.append(", "+getSheduleString(parameters.get(i).paramValue));
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.SALARY))
				str.append(", "+parameters.get(i).paramValue);
			if (parameters.get(i).paramName.equalsIgnoreCase(RequestURL.CURRENCY))
				str.append(", "+parameters.get(i).paramValue);
		}
		
		if (type.equalsIgnoreCase("1"))
			str.append(", �� �������");
		
		return str.toString();
		
	}

	 private String getEmploymentString(String str) {
			if (str.equalsIgnoreCase("-1"))
				return "�����";
			else if (str.equalsIgnoreCase("0"))
				return "������ ���������";
			else if (str.equalsIgnoreCase("1"))
				return "��������� ���������";
			else if (str.equalsIgnoreCase("2"))
				return "��������� ������";
			return "";
		}
		
		private String getRegionString(String str) {
			if (str.equalsIgnoreCase("1"))
				return "������";
			else if (str.equalsIgnoreCase("113"))
				return "������";
			else if (str.equalsIgnoreCase("2"))
				return "�����-���������";
			else if (str.equalsIgnoreCase("4"))
				return "�����������";
			else if (str.equalsIgnoreCase("3"))
				return "������������";
			else if (str.equalsIgnoreCase("66"))
				return "������ ��������";
			else if (str.equalsIgnoreCase("88"))
				return "������";
			else if (str.equalsIgnoreCase("1586"))
				return "������";
			else if (str.equalsIgnoreCase("76"))
				return "������-��-����";
			else if (str.equalsIgnoreCase("115"))
				return "����";
			else if (str.equalsIgnoreCase("1002"))
				return "�����";
			else
				return "";
		}
		
		private String getSheduleString(String str) {
			if (str.equalsIgnoreCase("-1"))
				return "�����";
			else if (str.equalsIgnoreCase("9"))
				return "������ ����";
			else if (str.equalsIgnoreCase("1"))
				return "������� ������";
			else if (str.equalsIgnoreCase("2"))
				return "������ ������";
			else if (str.equalsIgnoreCase("3"))
				return "��������� ������";
			else
				return "";
		}
		
}

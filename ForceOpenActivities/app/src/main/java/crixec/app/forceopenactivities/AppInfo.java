package crixec.app.forceopenactivities;
import java.util.*;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;

public class AppInfo
{
	private String appName;
	private String packageName;
	private Drawable icon;
	private List<String> activities = new ArrayList<String>();
	public AppInfo()
	{

	}
	public AppInfo(String appName, String packageName, Drawable icon, int versionCode, String versionName)
	{
		this.appName = appName;
		this.packageName = packageName;
		this.icon = icon;
	}

	public void setActivities(List<String> activities)
	{
		this.activities.clear();
		this.activities.addAll(activities);
	}

	public List<String> getActivities()
	{
		return activities;
	}
	public String[] getActivitiesArray(){
		String[] classes = new String[activities.size()];
		int i = 0;
		for(String s : activities){
			classes[i] = s;
			i++;
		}
		return classes;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public String getAppName()
	{
		return appName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}

	public Drawable getIcon()
	{
		return icon;
	}
}

package crixec.app.forceopenactivities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.List;
import java.util.ArrayList;
import android.content.pm.PackageInfo;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.util.AndroidRuntimeException;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener
{

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		// TODO: Implement this method
		final AppInfo app = apps.get(p3);
		if(app.getActivities().size() == 0) {
			Toast.makeText(getApplicationContext(), "该应用无Activity", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("选择一个要打开的Activity");
		dialog.setItems(app.getActivitiesArray(), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					String pkgName = app.getPackageName();
					String cls = app.getActivitiesArray()[p2];
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClassName(pkgName, cls);
					try
					{
						startActivity(intent);
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialog.show();
	}

	private List<AppInfo> apps = new ArrayList<>();
	private AppListAdapter adapter;
	private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.listView);
		adapter = new AppListAdapter(getLayoutInflater(), apps);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		new LoadInstalledApps().execute();
    }
	private class PackageEntity
	{
		int max;
		int current;
		String appName;

		public void setMax(int max)
		{
			this.max = max;
		}

		public int getMax()
		{
			return max;
		}

		public void setCurrent(int current)
		{
			this.current = current;
		}

		public int getCurrent()
		{
			return current;
		}

		public void setAppName(String appName)
		{
			this.appName = appName;
		}

		public String getAppName()
		{
			return appName;
		}}

	class LoadInstalledApps extends AsyncTask<Void, PackageEntity, Void>
	{

		ProgressDialog dialog = null;
		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			//dialog.setTitle("Loading ");
			dialog.setTitle("正在加载应用列表…");
			dialog.setProgress(0);
			dialog.show();
			apps.clear();
		}

		@Override
		protected void onProgressUpdate(PackageEntity[] values)
		{
			// TODO: Implement this method
			super.onProgressUpdate(values);
			dialog.setMax(values[0].getMax());
			dialog.setProgress(values[0].getCurrent());
			dialog.setMessage(values[0].getAppName());
		}

		@Override
		protected void onPostExecute(Void result)
		{
			// TODO: Implement this method
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
			dialog.dismiss();
		}

		@Override
		protected Void doInBackground(Void[] p1)
		{
			// TODO: Implement this method
			List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
			for (int i=0; i < packs.size(); i++)
			{
				PackageInfo p = packs.get(i);
				if ((p.versionName == null))
				{
					continue ;
				}
				String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
				PackageEntity entity = new PackageEntity();
				entity.setMax(packs.size());
				entity.setAppName(appName);
				entity.setCurrent(i);
				publishProgress(entity);
				AppInfo newInfo = new AppInfo();
				newInfo.setAppName(appName);
				newInfo.setPackageName(p.packageName);
				newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
				try {
					ActivityInfo[] list = getPackageManager().getPackageInfo(p.packageName, PackageManager.GET_ACTIVITIES).activities;
					List<String> classes = new ArrayList<String>();
					for (ActivityInfo activityInfo : list) {
						classes.add(activityInfo.name);
					}
					newInfo.setActivities(classes);
				} catch (Exception e) {
					e.printStackTrace();
				}
				apps.add(newInfo);
			}
			return null; 
		}
	}
}

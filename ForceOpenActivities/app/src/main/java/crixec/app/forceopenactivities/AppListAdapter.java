package crixec.app.forceopenactivities;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import java.util.List;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private List<AppInfo> apps;

	public AppListAdapter(LayoutInflater inflater, List<AppInfo> apps)
	{
		this.inflater = inflater;
		this.apps = apps;
	}
	
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return apps == null ? 0 : apps.size();
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return apps == null ? null : apps.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		// TODO: Implement this method
		ViewHolder holder = null;
		if(p2 == null){
			p2 = inflater.inflate(R.layout.app_item, null, false);
			holder = new ViewHolder();
			holder.appIcon = (ImageView) p2.findViewById(R.id.appIcon);
			holder.appName = (TextView) p2.findViewById(R.id.appName);
			holder.appPkgName = (TextView) p2.findViewById(R.id.pkgName);
			p2.setTag(holder);
		}else{
			holder = (AppListAdapter.ViewHolder) p2.getTag();
		}
		AppInfo appInfo = apps.get(p1);
		holder.appIcon.setImageDrawable(appInfo.getIcon());
		holder.appName.setText(appInfo.getAppName());
		holder.appPkgName.setText(appInfo.getPackageName());
		return p2;
	}
	private class ViewHolder{
		ImageView appIcon;
		TextView appName;
		TextView appPkgName;

	}
	
}

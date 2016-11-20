package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import cn.unas.app.R;
import cn.unas.app.unas.data.Adapter_BaseTask;
import cn.unas.app.unas.data.Adapter_BaseTask.onLoadListener;

public class MySearchListView extends ListView{
	
	private Context context;
	private View footer;
	private TextView tv_nodata;
	private TextView tv_allloaded;
	private TextView tv_loading;
	private ProgressBar pb_loading;
	
	public MySearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		initView();
	}

	public MySearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initView();
	}

	public MySearchListView(Context context) {
		super(context);
		this.context=context;
		initView();
	}
	
	private void initView()
	{
		footer=LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
		addFooterView(footer);
		tv_allloaded=(TextView)footer.findViewById(R.id.tv_allloaded);
		tv_allloaded.setText(R.string.search_complete);
		
		tv_nodata=(TextView)footer.findViewById(R.id.tv_nodata);
		tv_nodata.setText(R.string.no_search_result);
		
		tv_loading=(TextView)footer.findViewById(R.id.tv_loading);
		tv_loading.setText(R.string.searching);
		
		pb_loading=(ProgressBar)footer.findViewById(R.id.pb_loading);
		
		onLoadStateChange(true,true);
	}
	
	public void onLoadStateChange(boolean nodata, boolean completed)
	{
		if (!completed)
		{
			tv_allloaded.setVisibility(View.GONE);
			pb_loading.setVisibility(View.VISIBLE);
			tv_loading.setVisibility(View.VISIBLE);
			tv_nodata.setVisibility(View.GONE);
		} 
		else 
		{
			if (nodata)
			{
				tv_allloaded.setVisibility(View.GONE);
				pb_loading.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.VISIBLE);
			} 
			else
			{
				tv_allloaded.setVisibility(View.VISIBLE);
				pb_loading.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.GONE);
			}
		}
	}
	
	
}
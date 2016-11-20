package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import cn.unas.app.R;
import cn.unas.app.swipelistview.SwipeMenuLayout;
import cn.unas.app.unas.Viewgroups.ViewTaskBase;
import cn.unas.app.unas.Viewgroups.ViewTaskUpload;
import cn.unas.app.unas.data.Adapter_BaseTask;
import cn.unas.app.unas.data.Adapter_BaseTask.onLoadListener;

public class MyTaskListView extends ListView implements AbsListView.OnScrollListener, onLoadListener{
	
	private Context context;
	private View footer;
	private TextView tv_nodata;
	private TextView tv_allloaded;
	private TextView tv_loading;
	private ProgressBar pb_loading;
	
	private boolean isLoading=false;
	private boolean isLoadFull=false;
	
	private ViewTaskBase taskView=null;
	
	public MyTaskListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		initView();
	}

	public MyTaskListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initView();
	}

	public MyTaskListView(Context context) {
		super(context);
		this.context=context;
		initView();
	}
	
	private void initView()
	{
		footer=LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
		addFooterView(footer);
		tv_allloaded=(TextView)footer.findViewById(R.id.tv_allloaded);
		tv_nodata=(TextView)footer.findViewById(R.id.tv_nodata);
		tv_loading=(TextView)footer.findViewById(R.id.tv_loading);
		pb_loading=(ProgressBar)footer.findViewById(R.id.pb_loading);
		
		setOnScrollListener(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
	{
	
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) 
	{
		try
		{
			if(scrollState==SCROLL_STATE_IDLE
					&&view.getLastVisiblePosition()==view.getPositionForView(footer)
					&&!isLoading
					&&!isLoadFull)
			{
				isLoading=true;
				adapter.addShowCount(20);
				adapter.notifyDataSetChanged();
				//onLoadComplete(adapter.data.size()==0,adapter.showedAll());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private Adapter_BaseTask adapter;
	
	@Override
	public void setAdapter(ListAdapter adapter)
	{
		this.adapter=(Adapter_BaseTask)adapter;
		this.adapter.setonLoadListener(this);
		super.setAdapter(adapter);
		this.adapter.notifyDataSetChanged();
		
	}
	
	public void onLoadComplete(boolean nodata, boolean allloaded)
	{
		isLoading=false;
		if (nodata)
		{
			isLoadFull = true;
			tv_allloaded.setVisibility(View.GONE);
			pb_loading.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
			tv_nodata.setVisibility(View.VISIBLE);
		} 
		else if (allloaded)
		{
			isLoadFull = true;
			tv_allloaded.setVisibility(View.VISIBLE);
			pb_loading.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
			tv_nodata.setVisibility(View.GONE);
		} 
		else
		{
			isLoadFull = false;
			tv_allloaded.setVisibility(View.GONE);
			pb_loading.setVisibility(View.VISIBLE);
			tv_loading.setVisibility(View.VISIBLE);
			tv_nodata.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch(ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(taskView!=null)
				{
					taskView.closeMenu();
					taskView=null;
					return false;	//return false means will not receive move&up event
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		if(ev.getAction()==MotionEvent.ACTION_DOWN)
		{
			int mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());// get item location by x, y value
			View touchedView = getChildAt(mTouchPosition - getFirstVisiblePosition());
			
			int ChildCount=getChildCount();
			for(int i=0;i<ChildCount;i++)
			{
				if(getChildAt(i) instanceof ViewTaskBase)
				{
					ViewTaskBase view=(ViewTaskBase)getChildAt(i);
					if(view.isMenuOpen())
					{
						if(view!=touchedView)	//not touched the opened task view
						{
							taskView=view;
							return true;
						}
						else	//the touched view is menu-opened
						{
							boolean result=ev.getX()<this.getWidth()-ViewTaskBase.DELETEBARWIDTH;
							return result;
						}
					}
				}
			}
		}
		
		return super.onInterceptTouchEvent(ev);
	}
}
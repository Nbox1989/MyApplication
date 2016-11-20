package cn.unas.app.unas.UI.Controls;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.unas.app.R;
import cn.unas.app.Utils.Util;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;

public class MyAutoListView extends ListView implements AbsListView.OnScrollListener{

	//is current operation refresh or load data.
	public static final int REFRESH=0;
	public static final int LOAD=1;
	
	//space to distinguish PULL and RELEASE.
	//critical point is where top of head view lies 20px below top of list view
	private static final int SPACE=20;
	
	//the four state of header view and current state.
	private static final int NONE=0;
	private static final int PULL=1;
	private static final int RELEASE=2;
	private static final int REFRESHING=3;
	private int state;
	
	
	private LayoutInflater inflater;
	private View header;			//header view
	private View footer;			//footer view
	private TextView tv_refresh;	
	private TextView tv_lastUpdate;	//last update time
	private ImageView iv_arrow;		//an arrow image to prompt user to pull
	private ProgressBar pb_refreshing;	
	
	private TextView tv_nodata;
	private TextView tv_allloaded;
	private TextView tv_loading;
	private ProgressBar pb_loading;
	
	private RotateAnimation animation;			//animation of rotate 
	private RotateAnimation reverseAnimation;	//animation of reverse rotate
	
	private int startY;					
	
	private int firstVisibleItem;		//the first visible item
	private int scrollState;			//the scroll state
	private int headerContentInitialHeight;
	private int headerContentHeight;
	
	
	//only when the first item is showing can the listview be pulled down to refresh
	//otherwise the pull down action will just slide the listview.
	private boolean isRecorded;
	private boolean isLoading;			//judge whether it is loading data now.
	private boolean loadEnabled=true;   //open or close loading more function.	
	private boolean isLoadFull;
	private int pageSize=20;			//the item count each time loads
	
	private OnRefreshListener onRefreshListener;
	private OnLoadMoreListener onLoadListener;
	private onScrollStateChangeListener onScollListener;
	
	public MyAutoListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public MyAutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MyAutoListView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context)
	{
		//arrow animation
		animation =new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF,0.5f,
				RotateAnimation.RELATIVE_TO_SELF,0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(300);
		animation.setFillAfter(true);
		
		reverseAnimation=new RotateAnimation(-180,0,
				RotateAnimation.RELATIVE_TO_SELF,0.5f,
				RotateAnimation.RELATIVE_TO_SELF,0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(300);
		reverseAnimation.setFillAfter(true);
		
		inflater=LayoutInflater.from(context);
		
		footer=inflater.inflate(R.layout.listview_footer, null);
		tv_allloaded=(TextView)footer.findViewById(R.id.tv_allloaded);
		tv_nodata=(TextView)footer.findViewById(R.id.tv_nodata);
		tv_loading=(TextView)footer.findViewById(R.id.tv_loading);
		pb_loading=(ProgressBar)footer.findViewById(R.id.pb_loading);
		
		header=inflater.inflate(R.layout.listview_header, null);
		iv_arrow=(ImageView)header.findViewById(R.id.iv_arrow);
		tv_refresh=(TextView)header.findViewById(R.id.tv_releasetorefresh);
		tv_lastUpdate=(TextView)header.findViewById(R.id.tv_lastupdate);
		pb_refreshing=(ProgressBar)header.findViewById(R.id.pb_refreshing);
		
		headerContentInitialHeight=header.getPaddingTop();
		measureView(header);
		headerContentHeight=header.getMeasuredHeight();
		setHeaderPaddingTop(-headerContentHeight);
		this.addHeaderView(header);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}
	
	//adjust the top padding of header view
	private void setHeaderPaddingTop(int topPadding) 
	{
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}
	
	//calculate the size of header view 
	private void measureView(View view) 
	{
		ViewGroup.LayoutParams p=view.getLayoutParams();
		if(null==p)
		{
			p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) 
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		}
		else 
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, 
			int visibleItemCount, int totalItemCount) 
	{
		this.firstVisibleItem=firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		this.scrollState=scrollState;
		checkIfNeedLoad(view, scrollState);
		if(onScollListener!=null)
		{
			onScollListener.onScrollStateChanged(scrollState);
		}
	}

	//check if need to request for loading data
	//trigger time: scroll state changed
	private void checkIfNeedLoad(AbsListView view, int scrollState) 
	{
		if(!loadEnabled)
		{
			return;
		}
		//condition: 1.scroll state is idle
		//           2.not in loading
		//			 3.footer view is visible
		//			 4.data is not loaded completely
		try
		{
			if(scrollState==OnScrollListener.SCROLL_STATE_IDLE
					&&!isLoading
					&&view.getLastVisiblePosition()==view.getPositionForView(footer)
					&&!isLoadFull)
			{
				if(null!=onLoadListener)
				{
					isLoading=true;
					onLoadListener.onLoadMore();
				}
			}
		}
		catch(Exception e)		//view.getPositionForView(footer) may catch exception
		{
			
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch(ev.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(0==firstVisibleItem)
				{
					isRecorded=true;
					startY=(int)ev.getY();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				refreshHeaderWhenMove(ev);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if(PULL==state)
				{
					state=NONE;
					refreshHeadViewByState();
				}
				else if(RELEASE==state)
				{
					state=REFRESHING;
					refreshHeadViewByState();
					if(null!=onRefreshListener)
					{
						onRefreshListener.onRefresh();
					}
				}
				isRecorded=false;
				break;
		}
		return super.onTouchEvent(ev);
	}
	
	//analyze gesture in order to refresh header view
	private void refreshHeaderWhenMove(MotionEvent ev) 
	{
		if(!isRecorded)
		{
			return;
		}
		int tempY=(int)ev.getY();
		int movedY=tempY-startY;	//gesture moved distance in vertical direction
		int paddingTop=movedY-headerContentHeight;
		switch(state)
		{
			case NONE:
				if(movedY>0)
				{
					state=PULL;
					refreshHeadViewByState();
				}
				break;
			case PULL:
				setHeaderPaddingTop(paddingTop);
				if(SCROLL_STATE_TOUCH_SCROLL==scrollState
						&&movedY>headerContentHeight+SPACE)
				{
					state=RELEASE;
					refreshHeadViewByState();
				}
				break;
			case RELEASE:
				setHeaderPaddingTop(paddingTop);
				if(movedY>0&&movedY<headerContentHeight+SPACE)
				{
					state=PULL;
					refreshHeadViewByState();
				}
				else if(movedY<=0)
				{
					state=NONE;
					refreshHeadViewByState();
				}
				break;
		}
	}

	//adjust header view according to current state
	private void refreshHeadViewByState() 
	{
		switch(state)
		{
			case NONE:
				setHeaderPaddingTop(-headerContentHeight);
				tv_refresh.setText(R.string.pull_to_refresh);
				pb_refreshing.setVisibility(View.GONE);
				iv_arrow.clearAnimation();
				iv_arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
				break;
			case PULL:
				iv_arrow.setVisibility(View.VISIBLE);
				tv_refresh.setVisibility(View.VISIBLE);
				tv_lastUpdate.setVisibility(View.VISIBLE);
				pb_refreshing.setVisibility(View.GONE);
				tv_refresh.setText(R.string.pull_to_refresh);
				iv_arrow.clearAnimation();
				iv_arrow.setAnimation(reverseAnimation);
				break;
			case RELEASE:
				iv_arrow.setVisibility(View.VISIBLE);
				tv_refresh.setVisibility(View.VISIBLE);
				tv_lastUpdate.setVisibility(View.VISIBLE);
				pb_refreshing.setVisibility(View.GONE);
				tv_refresh.setText(R.string.pull_to_refresh);
				tv_refresh.setText(R.string.release_to_refresh);
				iv_arrow.clearAnimation();
				iv_arrow.setAnimation(animation);
				break;
			case REFRESHING:
				setHeaderPaddingTop(headerContentInitialHeight);
				pb_refreshing.setVisibility(View.VISIBLE);
				iv_arrow.clearAnimation();
				iv_arrow.setVisibility(View.GONE);
				tv_refresh.setVisibility(View.GONE);
				tv_lastUpdate.setVisibility(View.GONE);
				break;
		}
	}
	
	//change footer view expend on data count
	public void setResultSize(int resultSize) {
		if (resultSize == 0) {
			isLoadFull = true;
			tv_allloaded.setVisibility(View.GONE);
			pb_loading.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
			tv_nodata.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			tv_allloaded.setVisibility(View.VISIBLE);
			pb_loading.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
			tv_nodata.setVisibility(View.GONE);
		} else if (resultSize == pageSize) {
			isLoadFull = false;
			tv_allloaded.setVisibility(View.GONE);
			pb_loading.setVisibility(View.VISIBLE);
			tv_loading.setVisibility(View.VISIBLE);
			tv_nodata.setVisibility(View.GONE);
		}

	}

	public void onRefreshComplete()
	{
		String currentTime = Util.getFormatForFileRefreshTime(System.currentTimeMillis());
		tv_lastUpdate.setText(getContext().getString(R.string.last_update_time,
				currentTime));
		state = NONE;
		refreshHeadViewByState();
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
	
	public void reset()
	{
		isRecorded=false;
		isLoading=false;
		loadEnabled=true;
		isLoadFull=false;
		tv_allloaded.setVisibility(View.GONE);
		pb_loading.setVisibility(View.VISIBLE);
		tv_loading.setVisibility(View.VISIBLE);
		tv_nodata.setVisibility(View.GONE);
	}
	
	public void setOnRefreshListener(OnRefreshListener l)
	{
		onRefreshListener=l;
	}
	
	public void setOnLoadMoreListener(OnLoadMoreListener l)
	{
		onLoadListener=l;
	}
	
	public void addonScrollStateChangeListener(onScrollStateChangeListener l)
	{
		onScollListener=l;
	}
	
	//pull down refresh interface
	public interface OnRefreshListener{
		public void onRefresh();
	}
	
	//load more interface
	public interface OnLoadMoreListener{
		public void onLoadMore();
	}

	public interface onScrollStateChangeListener
	{
		public void onScrollStateChanged(int scrollState);
	}
}

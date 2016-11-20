package cn.unas.app.unas.UI.Controls;

import java.util.Arrays;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View;
import cn.unas.app.R;
import cn.unas.app.unas.data.Adapter_ServerInfo;

public class DropSpinner extends FrameLayout implements View.OnClickListener, OnItemClickListener {
	private TextView mTvTitle;  
	private ImageView mDropImage; 
	private PopupWindow mPopup; 
	private WrapListView mPopView; 
	
	private int mDrawableLeft;
	private int mDropMode; // flower_parent or wrap_content
	private String mTitle;
	
	private int mSelectedIndex=-1;
	
	private Adapter_ServerInfo adapter;
	
	public DropSpinner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DropSpinner(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_drop_spinner, this);
		mPopView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.popup_window_dropedittext, null);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropSpinner, defStyle, 0);
		mDrawableLeft = ta.getResourceId(R.styleable.DropSpinner_spinnerRight, R.drawable.ic_launcher);
		mDropMode = ta.getInt(R.styleable.DropSpinner_dropSpinnerMode, 0);
		mTitle = ta.getString(R.styleable.DropSpinner_title);
		ta.recycle();
		
		String[] types=context.getResources().getStringArray(R.array.servertype);
		adapter=new Adapter_ServerInfo(Arrays.asList(types), context);
		setAdapter(adapter);
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mTvTitle = (TextView) findViewById(R.id.dropview_tv);
		mDropImage = (ImageView) findViewById(R.id.dropview_image);
		
		mDropImage.setImageResource(mDrawableLeft);
	
		if(!TextUtils.isEmpty(mTitle)) {
			mTvTitle.setText(mTitle);
		}
		
		setOnClickListener(this);
		mPopView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if(changed && 0 == mDropMode) {
			mPopView.setListWidth(getMeasuredWidth());
		}
	}
	
	public void setAdapter(BaseAdapter adapter) {
		mPopView.setAdapter(adapter);
		
		mPopup = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPopup.setBackgroundDrawable(new ColorDrawable(color.transparent));
		mPopup.setFocusable(true); 
	}
	
	public String getText() {
		return mTvTitle.getText().toString();
	}
	
	public int getSelectedIndex()
	{
		return mSelectedIndex;
	}
	
	@Override
	public void onClick(View v) {
		if(mPopup.isShowing()) 
		{
			mPopup.dismiss();
		}
		else
		{
			mPopup.showAsDropDown(this, 0, 5);
		}
	}

	public void setAdapterList(List<String> list)
	{
		adapter.setDataList(list);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mTvTitle.setText(mPopView.getAdapter().getItem(position).toString());
		mSelectedIndex=position;
		mPopup.dismiss();
	}
}

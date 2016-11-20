package cn.unas.app.unas.UI.Controls;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View;
import cn.unas.app.R;
import cn.unas.app.unas.data.Adapter_ServerInfo;

public class DropEditText extends FrameLayout implements View.OnClickListener, OnItemClickListener {
	private EditText mEditText;  
	private ImageView mDropImage; 
	private PopupWindow mPopup; 
	private WrapListView mListView; 
	private TextView footer;
	
	private int mDrawableLeft;
	private int mDropMode; // flow_parent or wrap_content
	private String mHit;
	
	private Adapter_ServerInfo adapter;
	
	public DropEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DropEditText(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_drop_edittext, this);
		mListView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.popup_window_dropedittext, null);
		
		footer=new MarqueeTextView(context);
		footer.setTextSize(18f);
		footer.setText(R.string.no_unas_found);
		footer.setSingleLine();
		footer.setEllipsize(TruncateAt.MARQUEE);
		mListView.addFooterView(footer);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyle, 0);
		mDrawableLeft = ta.getResourceId(R.styleable.DropEditText_drawableRight, R.drawable.ic_launcher);
		mDropMode = ta.getInt(R.styleable.DropEditText_dropMode, 0);
		mHit = ta.getString(R.styleable.DropEditText_hint);
		ta.recycle();
		
		adapter=new Adapter_ServerInfo(new ArrayList<String>(), context);
		setAdapter(adapter);
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mEditText = (EditText) findViewById(R.id.dropview_edit);
		mDropImage = (ImageView) findViewById(R.id.dropview_image);
		
		mEditText.setSelectAllOnFocus(true);
		mDropImage.setImageResource(mDrawableLeft);
	
		if(!TextUtils.isEmpty(mHit)) {
			mEditText.setHint(mHit);
		}
		
		mDropImage.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if(changed && 0 == mDropMode) {
			mListView.setListWidth(getMeasuredWidth());
		}
	}

	public void setAdapter(BaseAdapter adapter) {
		mListView.setAdapter(adapter);
		
		mPopup = new PopupWindow(mListView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPopup.setBackgroundDrawable(new ColorDrawable(color.transparent));
		mPopup.setFocusable(true);
	}
	
	
	public String getText() {
		return mEditText.getText().toString();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.dropview_image)
		{
			if(mPopup.isShowing()) 
			{
				mPopup.dismiss();
				return;
			}
			else
			{
				dropDownListener.onDropDown();
				mPopup.showAsDropDown(this, 0, 5);
			}
		}
	}

	public void setAdapterList(List<String> list)
	{
		//adapter.clearDataList();
		if(list==null||list.isEmpty())
		{
			footer.setVisibility(View.VISIBLE);
		}
		else
		{
			footer.setVisibility(View.GONE);
		}
		adapter.setDataList(list);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position==adapter.getCount())
		{
			mPopup.dismiss();
			dropDownListener.onDropDown();
			mPopup.showAsDropDown(this, 0, 5);
		}
		else
		{
			mEditText.setText(mListView.getAdapter().getItem(position).toString());
			mPopup.dismiss();
		}
	}
	
	private onDropDownListener dropDownListener;
	
	public void setonDropDownListener(onDropDownListener listener)
	{
		dropDownListener=listener;
	}
	
	public interface onDropDownListener
	{
		public void onDropDown();
	}
	
	public void addTextChangedListener(TextWatcher watcher)
	{
		mEditText.addTextChangedListener(watcher);
	}
	
}

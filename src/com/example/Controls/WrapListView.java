package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import cn.unas.app.R;

public class WrapListView extends ListView{
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
	}

	private int mWidth = 0;
	
	public WrapListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WrapListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBackgroundResource(R.drawable.bg_shadow);
		setPadding(10, 0, 0, 20);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int height = getMeasuredHeight();
		for(int i=0;i<getChildCount();i++) 
		{
			View v=getChildAt(i);
			int childWidth = v.getMeasuredWidth();
			//mWidth = Math.max(mWidth, childWidth);
			v.getLayoutParams().width=mWidth;
		}
		
		int w=MeasureSpec.makeMeasureSpec(mWidth,MeasureSpec.UNSPECIFIED);
		int h=MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
				
		
		setMeasuredDimension(w, h);

	}
	
	protected void setListWidth(int width) {
		mWidth = width;
	}
}

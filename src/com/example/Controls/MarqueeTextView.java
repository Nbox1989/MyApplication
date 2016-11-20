package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MarqueeTextView extends TextView{
	public MarqueeTextView(Context con) {
	  super(con);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
	  super(context, attrs);
	}
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		if(getVisibility()==View.VISIBLE&&isShown())
		{
			return true;
		}
		return super.isShown();
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
	   Rect previouslyFocusedRect) {  
	}
}

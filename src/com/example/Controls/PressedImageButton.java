package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import cn.unas.app.R;

public class PressedImageButton extends ImageButton{
	
	private int releaseId;
	private int pressedId;
	
	public PressedImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta= context.obtainStyledAttributes(attrs,R.styleable.PressedImageButton);
		releaseId=ta.getResourceId(R.styleable.PressedImageButton_releasesrc, -1);
		pressedId=ta.getResourceId(R.styleable.PressedImageButton_pressedsrc, -1);
		
		
		ta.recycle();
		
		if(releaseId>0)
		{
			setImageResource(releaseId);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(this.isEnabled())
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN) 
	    	{ 
				setImageResource(pressedId);
		    }
	    	else if(event.getAction() == MotionEvent.ACTION_UP) 
	    	{ 
	    		setImageResource(releaseId);
		    } 
		}
		return true;
	}
}

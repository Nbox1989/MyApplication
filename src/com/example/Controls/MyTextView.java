package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import cn.unas.app.R;
import android.view.MotionEvent;
import android.view.View;

public class MyTextView extends TextView implements View.OnTouchListener{

	public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOnTouchListener(this);
	}

	public MyTextView(Context context) {
		super(context);
		setOnTouchListener(this);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN) 
    	{ 
			setBackgroundResource(R.drawable.rectangle); 
	    }
    	else if(event.getAction() == MotionEvent.ACTION_UP) 
    	{ 
    		setBackgroundResource(R.drawable.transparent); 
	    } 
    	else if(event.getAction() == MotionEvent.ACTION_CANCEL) 
    	{ 
    		setBackgroundResource(R.drawable.transparent); 
	    } 
		return false;
	}

}

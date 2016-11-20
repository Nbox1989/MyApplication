package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProgressTextView extends TextView{
	
	private Runnable runnable;
	
	public ProgressTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initTextViewRunnable();
	}

	public ProgressTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTextViewRunnable();
	}

	public ProgressTextView(Context context) {
		super(context);
		initTextViewRunnable();
	}

	private void initTextViewRunnable() 
	{
		runnable=new Runnable() {
			
			@Override
			public void run() 
			{
				textProgress();
				postDelayed(runnable, 500);
			}
		};
	}
	
	private synchronized void textProgress()
	{
		if(getText().toString().endsWith("..."))
		{
			setText(getText().subSequence(0, getText().length()-3));
		}
		else
		{
			setText(getText()+".");
		}
	}
	
	public void startProgress()
	{
		postDelayed(runnable, 500);
	}
	
	public void stopProgress()
	{
		removeCallbacks(runnable);
	}
}

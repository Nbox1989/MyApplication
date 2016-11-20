package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import cn.unas.app.R;

public class TextButton extends TextView{

	private Paint paintShadow;
	
	private int textColor;
	
	private float fontHeight;
	
	private float fontBottom;
	
	private float textBaseY;
	
	public TextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
        
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.TextButton);
        textColor=ta.getColor(R.styleable.TextButton_textColor, 0xff0fa7ff);
        ta.recycle();
        
        FontMetrics fontMetrics=getPaint().getFontMetrics();
		float fontHeight=fontMetrics.bottom-fontMetrics.top;
		
		fontBottom=fontMetrics.bottom;
        
        getPaint().setColor(textColor);
		
		paintShadow=new Paint();
		paintShadow.setARGB(0x30, 0x00, 0x00, 0x00);
		paintShadow.setStyle(Style.FILL);
	}
	
	private boolean mIsPressed=false;
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mIsPressed=true;
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				if(event.getX()>0&&event.getX()<getWidth()&&
						event.getY()>0&&event.getY()<getHeight())
				{
					performClick();
				}
				mIsPressed=false;
				invalidate();
				break;
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		textBaseY=(getHeight()+fontHeight)/2+fontBottom;
		canvas.drawText(getText().toString(), 
				(getWidth()-getPaint().measureText(getText().toString())) / 2,
				textBaseY, 
				getPaint());
		
		if(mIsPressed)
		{
			canvas.drawRect(0,0,getWidth(),getHeight(), paintShadow);
		}
	}
}

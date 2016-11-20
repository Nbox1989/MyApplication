package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.unas.app.R;

public class MyToggleButton extends View implements View.OnTouchListener{

	private boolean toggleValue=false;
	
	private static final int MARGIN_BORDER=4;
	
	private static final int BODER_WIDTH=2;
	
	private int touchState=TOUCH_STATE_NONE;
	
	private static final int TOUCH_STATE_NONE=0x01;
	
	private static final int TOUCH_STATE_MOVE=0x02;
	
	private float touchX;
	
	private Paint borderPaint;
	
	private Paint bgPaintON;
	
	private Paint bgPaintOFF;
	
	private Paint btnPaint;
	
	private RectF roundRectF=new RectF(0,0,0,0);

	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		bgPaintON=new Paint();
		bgPaintON.setColor(getResources().getColor(R.color.toggle_btn));
		bgPaintON.setStyle(Style.FILL);
		bgPaintON.setAntiAlias(true);
		
		bgPaintOFF=new Paint();
		bgPaintOFF.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
		bgPaintOFF.setStyle(Style.FILL);
		bgPaintOFF.setAntiAlias(true);
		
		borderPaint=new Paint();
		borderPaint.setColor(getResources().getColor(R.color.toggle_border));
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(BODER_WIDTH);
		borderPaint.setAntiAlias(true);
		
		btnPaint=new Paint();
		btnPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
		btnPaint.setStyle(Style.FILL);
		btnPaint.setAntiAlias(true);
		
		setOnTouchListener(this);
	}


	@Override
	protected void onDraw(Canvas canvas) 
	{
		int width=getWidth();
		int height=getHeight();
		int radius=height/2-MARGIN_BORDER;
		
		if(toggleValue)
		{
			roundRectF.set(0, 0, width, height);
			canvas.drawRoundRect(roundRectF, height/2, height/2, bgPaintON);
			if(touchState==TOUCH_STATE_NONE||touchX>=width-MARGIN_BORDER-2*radius)
			{
				canvas.drawCircle(width-MARGIN_BORDER-radius, height/2, radius, btnPaint);
			}
			else
			{
				roundRectF.set(Math.max(0, touchX)+MARGIN_BORDER, MARGIN_BORDER, 
						width-MARGIN_BORDER, height-MARGIN_BORDER);
				canvas.drawRoundRect(roundRectF,
						height/2-MARGIN_BORDER, height/2-MARGIN_BORDER, btnPaint);
			}
		}
		else
		{
			roundRectF.set(0, 0, width, height);
			canvas.drawRoundRect(roundRectF, height/2, height/2, bgPaintOFF);
						
			roundRectF.set(0, 0, width, height);
			canvas.drawRoundRect(roundRectF, height/2, height/2, borderPaint);
			if(touchState==TOUCH_STATE_NONE||touchX<=2*radius+MARGIN_BORDER)
			{
				canvas.drawCircle(MARGIN_BORDER+radius, height/2, radius, btnPaint);
				canvas.drawCircle(MARGIN_BORDER+radius, height/2, radius, borderPaint);
			}
			else
			{
				roundRectF.set(MARGIN_BORDER, MARGIN_BORDER, 
						Math.min(width, touchX)-MARGIN_BORDER, height-MARGIN_BORDER);
				canvas.drawRoundRect(roundRectF, 
						height/2-MARGIN_BORDER, height/2-MARGIN_BORDER, btnPaint);
								
				roundRectF.set(MARGIN_BORDER, MARGIN_BORDER, 
						Math.min(width, touchX)-MARGIN_BORDER, height-MARGIN_BORDER);
				canvas.drawRoundRect(roundRectF, 
						height/2-MARGIN_BORDER, height/2-MARGIN_BORDER, borderPaint);
			}
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				break;			
			case MotionEvent.ACTION_MOVE:
				touchState=TOUCH_STATE_MOVE;
				touchX=event.getX();
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touchState=TOUCH_STATE_NONE;
				if(event.getX()<getWidth()/2)
				{
					setToggleValue(false);
				}
				else
				{
					setToggleValue(true);
				}
				break;
		}
		return true;
	}

	public void setToggleValue(boolean value)
	{
		if(this.toggleValue!=value)
		{
			this.toggleValue=value;
			invalidate();
			if(listener!=null)
			{
				listener.onToggle(this, value);
			}
		}
	}
	
	public boolean getToggleValue()
	{
		return this.toggleValue;
	}
	
	public interface onToggleListener
	{
		public void onToggle(MyToggleButton v, boolean value);
	}
	
	public void setOnToggleListener(onToggleListener l)
	{
		listener=l;
	}
	
	private onToggleListener listener;
}

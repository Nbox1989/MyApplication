package cn.unas.app.unas.UI.Controls;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.unas.app.R;

public class NumericSlider extends View{

	private int maxValue=100;
	
	private List<String> degrees=new ArrayList<String>();
	
	private RectF rectBar=new RectF(0,0,0,0);
	
	private RectF rectBarLeft=new RectF(0,0,0,0);
	
	private RectF rectCursor=new RectF(0,0,0,0);
	
	private static int barHeight=8;
	
	private static int cursorRadius=15;
	
	private Paint paintBar=new Paint();
	
	private Paint paintCursor=new Paint();
	
	private Paint paintText=new Paint();
	
	private Paint paintTextSelected=new Paint();
	
	private int curDegreeIndex=0;
	
	private boolean isTouchDown=false;
	
	private float touchDownX;
	
	private float fontAscent;
	
	private float fontDescent;
	
	private float minTextSize=12f;
	
	private float maxTextSize=30f;
	
	public NumericSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray ta=getContext().obtainStyledAttributes(attrs, R.styleable.NumericSlider);
		maxValue=ta.getInteger(R.styleable.NumericSlider_maxNumber, 100);
		
		paintBar.setARGB(0xFF, 0x52, 0x52, 0x52);
		paintBar.setAntiAlias(true);
		paintBar.setStyle(Style.FILL);
		
		paintCursor.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
		paintCursor.setAntiAlias(true);
		paintCursor.setStyle(Style.FILL);
		
		paintText.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
		paintText.setAntiAlias(true);
		paintText.setStyle(Style.FILL);
		paintText.setTextSize(20f);
		
		paintTextSelected.setARGB(0xFF, 0x52, 0x52, 0x52);
		paintTextSelected.setAntiAlias(true);
		paintTextSelected.setStyle(Style.FILL);
		paintTextSelected.setFakeBoldText(true);
		paintTextSelected.setTextSize(20f);
		
		FontMetrics fm= paintText.getFontMetrics();
		
		fontAscent=fm.ascent;
		fontDescent=fm.descent;
	}
	
	public void setDegreeNames(List<String> degreeNames)
	{
		degrees.clear();
		degrees.addAll(degreeNames);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		adjustTextPaintSize();
	}
	
	private void adjustTextPaintSize() 
	{
		float oldSize=minTextSize;
		for(float size=oldSize;size<=maxTextSize;size+=2)
		{
			paintText.setTextSize(size);
			if(paintText.descent()-paintText.ascent()>getHeight()/3)
			{
				break;
			}
			oldSize=size;
		}
		paintText.setTextSize(oldSize);
		paintTextSelected.setTextSize(oldSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width=getWidth();
		int height=getHeight();
		rectBar.left=cursorRadius;
		rectBar.right=width-cursorRadius;
		rectBar.top=height/3-barHeight/2;
		rectBar.bottom=height/3+barHeight/2;
		canvas.drawRoundRect(rectBar, barHeight/2, barHeight/2, isEnabled()?paintBar:paintCursor);
		
		if(isEnabled()&&isTouchDown)
		{
			touchDownX=Math.max(rectBar.left, touchDownX);
			touchDownX=Math.min(rectBar.right,touchDownX);
			rectCursor.left=touchDownX-cursorRadius;
		}
		else
		{
			if(degrees.size()>1)
			{
				rectCursor.left=rectBar.width()*curDegreeIndex/(degrees.size()-1);
			}
		}
		rectCursor.right=rectCursor.left+cursorRadius*2;
		rectCursor.top=height/3-cursorRadius;
		rectCursor.bottom=height/3+cursorRadius;
		
		rectBarLeft.left=rectBar.left;
		rectBarLeft.right=rectCursor.centerX();
		rectBarLeft.top=rectBar.top;
		rectBarLeft.bottom=rectBar.bottom;
		
		canvas.drawRoundRect(rectBarLeft, barHeight/2, barHeight/2, paintCursor);

		canvas.drawOval(rectCursor, paintCursor);
		
		for(int i=0;i<degrees.size();i++)
		{
			float baseX=0;
			float fontLenght=paintText.measureText(degrees.get(i));
			if(i==0)
			{
				baseX=rectBar.left;
			}
			else if(i==degrees.size()-1)
			{
				baseX=rectBar.right-fontLenght;
			}
			else
			{
				baseX=rectBar.left+rectBar.width()*i/(degrees.size()-1)-fontLenght/2;
			}
			if(isEnabled()&&i==curDegreeIndex)
			{
				canvas.drawText(degrees.get(i), baseX, getHeight()-fontDescent,paintTextSelected);
			}
			else
			{
				canvas.drawText(degrees.get(i), baseX, getHeight()-fontDescent,paintText);
			}
		}
		
	}
	
	public void setDegree(int index)
	{
		curDegreeIndex=index;
	}
	
	public int getCurrentDegree()
	{
		return curDegreeIndex;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isEnabled())
		{
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					isTouchDown=true;
					touchDownX=event.getX();
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touchDownX=event.getX();
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					isTouchDown=false;
					getNearestDegree((int)event.getX());
					invalidate();
					break;
				default:
					break;
			}
		}
		return true;
	}

	private void getNearestDegree(int x)
	{
		int degreeCount=degrees.size();
		int scaleCount=degreeCount-1;
		
		int scaleLength=getWidth()/scaleCount;
		
		curDegreeIndex=(x+(scaleLength/2))/scaleLength;
	}
}

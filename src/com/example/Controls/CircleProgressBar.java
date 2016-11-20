package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import cn.unas.app.R;
import cn.unas.app.Utils.TextSizeUtil;

public class CircleProgressBar extends View implements View.OnClickListener{
		
	private Rect bitmapRec;
	
	private Paint paint_ring;
	
	private Paint paint_arc;
	
	private Paint paint_text;
	
	private RectF rec;
	
	private static final float ringWidth=5.0f;
	
	private int maxProgress=100;
	
	private int curProgress=66;
	
	private static final double sqrt2=Math.sqrt(2.0f);
	
	private int textRegionOffsetX;
	
	private int textRegionOffsetY;
	
	private int textRegionWidth;
	
	private int textRegionHeight;
	
	private static final String maxText="100%";
	
	private static String format; 
		
	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
				
		this.paint_arc = new Paint();  
        this.paint_arc.setAntiAlias(true); 
        this.paint_arc.setARGB(155, 0, 185, 209); 
        this.paint_arc.setStyle(Paint.Style.STROKE);  
        this.paint_arc.setStrokeWidth(ringWidth);
        
        this.paint_ring = new Paint();  
        this.paint_ring.setAntiAlias(true); 
        this.paint_ring.setARGB(155, 240, 240, 240); 
        this.paint_ring.setStyle(Paint.Style.STROKE); 
        this.paint_ring.setStrokeWidth(ringWidth);
        
        this.paint_text = new Paint();  
        this.paint_text.setAntiAlias(true); 
        this.paint_text.setARGB(155, 0, 0, 0); 
        this.paint_text.setStyle(Paint.Style.STROKE); 
        rec=new RectF();
        	
        format=getResources().getString(R.string.percentage_format);
	}

	@Override
	public void onClick(View v) 
	{
		
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		textRegionOffsetX=getDrawRegionOffsetX();
		textRegionOffsetY=getDrawRegionOffsetY();
		textRegionWidth=getDrawRegionWidth();
		textRegionHeight=getDrawRegionHeight();
		
		String textProgress=String.format(format, curProgress);
		
		paint_text.setTextSize(TextSizeUtil.getMaxTextSizeInArea(
				textRegionWidth, textRegionHeight, textProgress, 0, 70));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);  
		rec.set(ringWidth/2, ringWidth/2, getWidth()-ringWidth/2, getHeight()-ringWidth/2);
    
		canvas.drawArc(rec, 0, 360, false, paint_ring);
		
		canvas.drawArc(rec, -90, 360.0f*curProgress/maxProgress, false, paint_arc);	
		
		double textHeight=TextSizeUtil.getTextHeight(paint_text);
		double textWidth=TextSizeUtil.getTextWidth(paint_text, String.format(format, curProgress));
		
		float textbaseX=(float) (textRegionOffsetX+(textRegionWidth-textWidth)/2);
		float textbaseY=(float) (textRegionOffsetY+(textRegionHeight+textHeight)/2-paint_text.descent());
		
		canvas.drawText(String.format(format, curProgress), textbaseX, textbaseY, paint_text);
	}
	
	private int getDrawRegionWidth()
	{
		return (int)(getWidth()/sqrt2-sqrt2*ringWidth);
	}
	
	private int getDrawRegionHeight()
	{
		return (int)(getHeight()/sqrt2-sqrt2*ringWidth);
	}
	
	private int getDrawRegionOffsetX()
	{
		return (int)((2-sqrt2)/4*getWidth()+ringWidth/sqrt2);
	}
	
	private int getDrawRegionOffsetY()
	{
		return (int)((2-1.4f)/4*getHeight()+ringWidth/1.4f);
	}
	
	public void setProgress(int progress)
	{
		curProgress=progress;
		invalidate();
	}
}

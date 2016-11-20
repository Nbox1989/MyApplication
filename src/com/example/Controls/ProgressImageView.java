package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.unas.app.Utils.TextSizeUtil;

public class ProgressImageView extends ImageView{

	private static Paint shadowPaint;
	private static Paint textPaint;
	
	static
	{
		shadowPaint=new Paint();
		shadowPaint.setARGB(0x55, 0x00, 0x00, 0x00);
		shadowPaint.setStyle(Style.FILL);
		
		textPaint=new Paint();
		textPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
		textPaint.setStyle(Style.FILL);
		textPaint.setTextSize(25);
		textPaint.setAntiAlias(true);
	}
	
	private Rect shadowRect=new Rect(0,0,0,0);
	private Rect partialShadowRect=new Rect(0,0,0,0);
	private Rect drawingRect=new Rect(0,0,0,0);
	private Rect drawableRect=new Rect(0,0,0,0);
	
	private int textHeight;
	private int progressPercent=0;
	
	public ProgressImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ProgressImageView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		textHeight=getHeight()/10;
		textPaint.setTextSize(TextSizeUtil.getMaxTextSizeByHeight(textHeight, 0, 30));

		getDrawingRect(shadowRect);
		
		Drawable drawable=getDrawable();

		if(drawable!=null)
		{
			drawableRect=drawable.getBounds();
			getDrawingRect(drawingRect);
			shadowRect.set(drawingRect);
			float drawableRatio=drawableRect.width()*1.0f/drawableRect.height();
			float drawingRatio=drawingRect.width()*1.0f/drawingRect.height();
			
			if(drawableRatio>drawingRatio)	//fit width
			{
				shadowRect.top=(int)(drawingRect.height()-drawingRect.width()/drawableRatio)/2;
				shadowRect.bottom=(int)(drawingRect.height()+drawingRect.width()/drawableRatio)/2;
			}
			else	//fit height
			{
				shadowRect.left=(int) (drawingRect.width()-drawingRect.height()*drawableRatio)/2;
				shadowRect.right=(int) (drawingRect.width()+drawingRect.height()*drawableRatio)/2;
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(progressPercent>=0)
		{
			partialShadowRect.set(shadowRect);
			partialShadowRect.top=shadowRect.top+shadowRect.height()*progressPercent/100;
			canvas.drawRect(partialShadowRect, shadowPaint);
			String text=progressPercent+"%";
			canvas.drawText(text, 
					(this.getWidth()-textPaint.measureText(text))/2,
					(this.getHeight()+textHeight)/2, 
					textPaint);
		}
	}
	
	public void setProgressPercent(int percent)
	{
		progressPercent=percent;
		
		this.invalidate();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		
	}
}

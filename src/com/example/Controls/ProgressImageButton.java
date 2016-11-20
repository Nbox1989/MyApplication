package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class ProgressImageButton extends MyImageButton{

	private Paint paint;
	
	private RectF rec;
	
	private float ringWidth=10.0f;
	
	private int maxProgress=100;
	
	private int curProgress=66;
	
	public ProgressImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.paint = new Paint();  
        this.paint.setAntiAlias(true); //�������  
        this.paint.setARGB(155, 0, 185, 209); 
        this.paint.setStyle(Paint.Style.STROKE); //���ƿ���Բ   
        this.paint.setStrokeWidth(ringWidth);
        rec=new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);  
		rec.set(0+ringWidth/2, 0+ringWidth/2, getWidth()-ringWidth, getHeight()-ringWidth);
    
		canvas.drawArc(rec, -90, 360.0f*curProgress/maxProgress, false, paint);
	}
	  
	public void setProgressPercent(int progress)
	{
		curProgress=progress;
	}
}

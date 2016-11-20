package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import cn.unas.app.R;

public class ProgressButton extends View implements View.OnClickListener{
		
	private Rect bitmapRec;
	
	private Bitmap bitmap;
	
	private int resource;
	
	private Paint paint;
	
	private RectF rec;
	
	private float ringWidth=6.0f;
	
	private int maxProgress=100;
	
	private int curProgress=100;
	
	private static double sqrt2;
	
	public ProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		sqrt2=Math.sqrt(2.0f);
		
		this.paint = new Paint();  
        this.paint.setAntiAlias(true); //�������  
        this.paint.setARGB(155, 0, 185, 209); 
        this.paint.setStyle(Paint.Style.STROKE); //���ƿ���Բ   
        this.paint.setStrokeWidth(ringWidth);
        rec=new RectF();
        
        bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.closedialog);
        bitmapRec=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
	
	}

	@Override
	public void onClick(View v) 
	{
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);  
		rec.set(ringWidth/2, ringWidth/2, getWidth()-ringWidth/2, getHeight()-ringWidth/2);
    
		canvas.drawArc(rec, -90, 360.0f*curProgress/maxProgress, false, paint);
		
		canvas.drawBitmap(bitmap, bitmapRec, getDrawRect(), paint);
	
	
	}
	
	private Rect getDrawRect()
	{
		int offX=getDrawRegionOffsetX();
		int offY=getDrawRegionOffsetY();
		return new Rect(offX,offY,
				getDrawRegionWidth()+offX,
				getDrawRegionHeight()+offY);
//		Toast.makeText(getContext(), getWidth()+" "+getDrawRegionWidth(), Toast.LENGTH_SHORT).show();
//		return new Rect(0,
//				0,
//				getWidth(),
//				getHeight());
		
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
	  
//	public void setImage(int resId)
//	{
//		resource=resId;
//		bitmap=BitmapFactory.decodeResource(getResources(), resource);
//		bitmapRec=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
//		this.invalidate();
//	}
	
	public void setImage(Bitmap bmp)
	{
		bitmap=bmp;
		bitmapRec=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		this.invalidate();
	}
	
	public void setProgress(int progress)
	{
		curProgress=progress;
		invalidate();
	}
}

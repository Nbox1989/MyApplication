package cn.unas.app.unas.UI.Controls;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import cn.unas.app.Utils.TextSizeUtil;

public class TextProgressBar extends ProgressBar{

	protected String text;
	protected Paint mPaint;
	protected Rect rect=new Rect();
	
	public TextProgressBar(Context context) {
		super(context);
        initText();
	}
	
    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initText();
    }
	
	
    public TextProgressBar(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	initText();
    }
    
    @Override
    public void setProgress(int progress) {
         setText(progress);
         super.setProgress(progress);
    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int textSize=TextSizeUtil.getMaxTextSizeByHeight(bottom-top, 10, 25);
		mPaint.setTextSize(textSize);
	}
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
    	int x = (getWidth() / 2) - rect.centerX();
    	int y = (getHeight() / 2) - rect.centerY();
    	canvas.drawText(this.text, x, y, this.mPaint);
    }
	
    private void initText(){
    	this.mPaint = new Paint();
    	this.mPaint.setColor(Color.WHITE);
		
    }
	
    private void setText(){
    	setText(this.getProgress());
    }
	
	private void setText(int progress){ 
		int i=progress*100/getMax();
		this.text = String.valueOf(i) + "%";
	}
}

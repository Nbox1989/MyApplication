package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Scroller;

public class MyImageButton extends ImageButton implements View.OnTouchListener{
	
	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}
	
	private static float RGBrate=1.5f;
	private static float RGBoffset=-50f;
	private static float alpharate=3.0f;
	private static float alphaoffset=0;
	/**   
    * ��ť������   
    */ 
	private final static float[] BUTTON_PRESSED = new float[] {       
			RGBrate,       0,       0,        0,  RGBoffset,       
			      0, RGBrate,       0,        0,  RGBoffset,       
	              0,       0, RGBrate,        0,  RGBoffset,       
	              0,       0,       0,alpharate,alphaoffset};     
	         
	   /**   
	    * ��ť�ָ�ԭ״   
	    */     
	private final static float[] BUTTON_RELEASED = new float[] {       
			1, 0, 0, 0, 0,       
			0, 1, 0, 0, 0,       
			0, 0, 1, 0, 0,       
			0, 0, 0, 1, 0}; 

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(this.isEnabled())
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN) 
	    	{ 
				getDrawable().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED)); 
		    }
	    	else if(event.getAction() == MotionEvent.ACTION_UP) 
	    	{ 
				getDrawable().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED)); 
		    } 
		}
		return false;
	}
	

	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		if(!enabled)
		{
			getDrawable().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED)); 
		}
		else
		{
			getDrawable().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED));
		}
	}
}

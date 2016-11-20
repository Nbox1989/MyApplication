package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;

public class SquareImageView extends ImageView{
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width=MeasureSpec.getSize(widthMeasureSpec);
		//int heightMode=MeasureSpec.getMode(heightMeasureSpec);
		int heightMode=MeasureSpec.EXACTLY;
		int heightSpec=MeasureSpec.makeMeasureSpec(width, heightMode);
		
		super.onMeasure(widthMeasureSpec, heightSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
}

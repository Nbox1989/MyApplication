package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import cn.unas.app.R;

public class MySearchBar extends EditText implements View.OnKeyListener, View.OnFocusChangeListener {
	
	//whether icon shows on the left
    private boolean isLeft = false;
    
    //whether pressed the search button on soft keyboard
    private boolean pressSearch = false;
	
	private Drawable drawableLeft;
	private int paddingleft;
	private int paddingright;
	private int paddingdrawable;
	private float textWidth;
	
	private float bodyWidth;
	
	private int translateOffset;
	
	private final int drawableWidth=40;
	private final int drawableHeight=40;
	
	public MySearchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() 
	{
		setOnKeyListener(this);
		setOnFocusChangeListener(this);
		drawableLeft=getResources().getDrawable(R.drawable.gosearch);
		drawableLeft.setBounds(0,0,drawableWidth,drawableHeight);
		setCompoundDrawables(drawableLeft, null, null, null);
	}
	
	@Override
    protected void onDraw(Canvas canvas) 
    {
		if(!isLeft)
		{
			paddingleft=getPaddingLeft();
			paddingright=getPaddingRight();
			paddingdrawable=getCompoundDrawablePadding();
			textWidth = getPaint().measureText(getHint().toString());
			
			bodyWidth=paddingleft+paddingright+textWidth+paddingdrawable+drawableWidth;
			
			translateOffset=(int) ((getWidth()-bodyWidth)/2);
			
			canvas.translate(translateOffset,0);
		}
        super.onDraw(canvas);
    }

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
		if (pressSearch) 
		{
		    //search button is pressed
		    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		    if (imm.isActive()) 
		    {
		        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		    }
		    if (event.getAction() == KeyEvent.ACTION_UP) 
		    {
		    	if(l!=null)
		    	{
		    		l.startSearch(this);
		    	}
		    }
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
//		if (!pressSearch && TextUtils.isEmpty(getText().toString()))
//		{
//            isLeft = hasFocus;
//      }
		isLeft=hasFocus||(!TextUtils.isEmpty(getText().toString()));
	}
	
	public void setonStartSearchListener(onStartSearchListener listener)
	{
		l=listener;
	}
	
	private onStartSearchListener l;
	
	public interface onStartSearchListener
	{
		public void startSearch(View v);
	}

}

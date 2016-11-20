package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.view.MotionEvent;
import android.view.View;

public class MyScrollView extends ScrollView implements View.OnTouchListener{

	private float eventY=-1;
	
	private float eventYnew;
	
	private int height;
	
	private int measureHeight;
	
	private int scrollY;
	
	private boolean shouldDragReleaseAtTop=false;
	
	private boolean shouldDragReleaseAtBottom=false;
	
	private boolean handled=false;
	
	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			
			height=v.getHeight();
			scrollY=v.getScrollY();
			measureHeight=v.getMeasuredHeight();
			if(scrollY==0)
			{
				Log.i(" y", "y:"+eventY);
				if(!handled)		//��һ��move
				{
					eventY=event.getY();	//��¼scrollY=0��ʱ���eventY��ֵ
					Log.i("set y", "" + eventY);
					handled=true;
				}
				else
				{
					eventYnew=event.getY();
					if(eventYnew -eventY >150)		//���eventY������ֵ���򴥷�dragAtTop�¼�
					{
						Log.i("scroll", "_y1:"+eventYnew+" y:"+eventY);
						l.onDragAtTop();
						shouldDragReleaseAtTop=true;
						Log.i("scroll", "move,visible");
					}
				}
			}
			else if (scrollY+height>=measureHeight)	//�ײ����� ���������ʾ
			{
				l.onDragAtBottom();
				shouldDragReleaseAtBottom=true;
				handled=true;
			} 
			break;
		case MotionEvent.ACTION_UP:
			v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			
			height=v.getHeight();
			scrollY=v.getScrollY();
			measureHeight=v.getMeasuredHeight();
			if (shouldDragReleaseAtTop) 	//��������
			{
				if(scrollY==0)	//���action_up��ʱ����ȻscrollY=0����ʾû��ȡ��
				{
					l.onReleaseAtTop();
				}
				else
				{
					l.onCancelReleaseAtTop();	//���action_up��ʱ��scrollY��0����ʾȡ����
				}
			}
			else if (shouldDragReleaseAtBottom)	//�ײ����� ���������ʾ
			{
				if(height+scrollY>=measureHeight)
				{
					Log.i("scroll", "refresh");
					l.onReleaseAtBottom();
				}
				else
				{
					l.onCancelReleaseAtBottom();
				}
			}  
			Log.i("y", ""+eventY);
			reset();
			break;
		default:
			Log.i("event", "event "+event.getAction());
			break;
		}
		return false;

	}

	private void reset() {
		eventY=-1;
		shouldDragReleaseAtTop=false;
		shouldDragReleaseAtTop=false;
		handled=false;
	}

	public interface onDragListener
	{
		public void onDragAtBottom();
		public void onDragAtTop();
		public void onReleaseAtBottom();
		public void onReleaseAtTop();
		public void onCancelReleaseAtBottom();
		public void onCancelReleaseAtTop();
	}

	private onDragListener l;
	
	public void setonDragListener(onDragListener listener)
	{
		this.l=listener;	}
	
}



package cn.unas.app.unas.UI.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.unas.app.R;

public class CheckedImagView extends RelativeLayout implements View.OnClickListener{

	private View contentView;
	private ImageView imageView;
	private TextView textView;
	
	private int checkedResId;
	private int uncheckedResId;
	private int partialcheckedResId;
	
	private int checkedState;
	
	private int textResId;
	
	public static final int checked=0x00;
	public static final int unchecked=0x01;
	public static final int partialchecked=0x02;
	
	public CheckedImagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		contentView=LayoutInflater.from(context).inflate(R.layout.view_checkimageview, this,true);
		imageView=(ImageView)contentView.findViewById(R.id.iv_check);
		textView=(TextView)contentView.findViewById(R.id.tv_check);
		
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.CheckedImageView);
		checkedResId=ta.getResourceId(R.styleable.CheckedImageView_checkeddrawable, R.drawable.selectall);
		uncheckedResId=ta.getResourceId(R.styleable.CheckedImageView_uncheckeddrawable, R.drawable.selectnone);
		partialcheckedResId=ta.getResourceId(R.styleable.CheckedImageView_partialcheckeddrawable, R.drawable.selectpart);
		checkedState=ta.getInteger(R.styleable.CheckedImageView_checkedstate, 0);
		textResId=ta.getResourceId(R.styleable.CheckedImageView_text, R.string.application_name);
		ta.recycle();//IMPORTANT! Do not forget to add this sentence, otherwise the setting next time will be influenced
		
		setCheckedState(checkedState);
		textView.setText(textResId);
		
		setOnClickListener(this);
	}
	
	public void setText(String str)
	{
		textView.setText(str);
	}
	
	public void setText(int resId)
	{
		textView.setText(resId);
	}
	

	public void setCheckedState(int state) {
		// TODO Auto-generated method stub
		switch(state)
		{
			case checked:
				imageView.setBackgroundResource(checkedResId);
				checkedState=checked;
				break;
			case unchecked:
				imageView.setBackgroundResource(uncheckedResId);
				checkedState=unchecked;
				break;
			case partialchecked:
				imageView.setBackgroundResource(partialcheckedResId);
				checkedState=partialchecked;
				break;
			default:
				break;
		}
	}
	
	public int getCheckedState()
	{
		return checkedState;
	}

	@Override
	public void onClick(View v) 
	{
		switch(checkedState)
		{
			case partialchecked:
			case checked:
				setCheckedState(unchecked);
				break;
			case unchecked:
				setCheckedState(checked);
				break;
			default:
				break;
		}
		if(listener!=null)
		{
			listener.onCheckedChange(this.getId(), getCheckedState()==checked?true:false);
		}
	}
	
	private onCheckedStateChangeListener listener;	
		
	public void setOnCheckedStateChangeListener(onCheckedStateChangeListener l)
	{
		listener=l;
	}
		
	public interface onCheckedStateChangeListener
	{
		public void onCheckedChange(int viewId, boolean checked);
	}
}

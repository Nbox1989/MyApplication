package cn.unas.app.Utils;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class TextSizeUtil {
	
	public static int getMaxTextSizeByHeight(double height,int minSize, int maxSize)
	{
		Paint p=new Paint();
		FontMetrics fm;
		for(int i=minSize;i<maxSize;i++)
		{
			p.setTextSize(minSize); 
			fm = p.getFontMetrics();
			if(Math.ceil(fm.descent - fm.ascent)>height)
			{
				return Math.max(i-1, minSize);
			}
		}
		return maxSize;
	}
	
	public static int getMaxTextSizeInArea(double width, double height, String text, int minSize, int maxSize)
	{
		Paint p=new Paint();
		for(int i=minSize;i<maxSize;i++)
		{
			p.setTextSize(i); 
			
			if(getTextWidth(p, text)>width||getTextHeight(p)>height)
			{				
				return Math.max(i-1, minSize);
			}
		}
		return maxSize;
	}
	
	public static double getTextWidth(Paint p, String text)
	{
		float f=p.measureText(text);
		return p.measureText(text);
	}
	
	public static double getTextHeight(Paint p)
	{
		FontMetrics fm = p.getFontMetrics();
		
		return Math.ceil(fm.descent - fm.ascent)+2;
				
	}
}

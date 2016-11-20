package cn.unas.app.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import cn.unas.app.unas.data.CallLogInfo;
import cn.unas.app.unas.data.ContactInfo;

public class CallLogUtil {
	
	public static final String CALLLOGFILENAME="CallLogTable.vcf";
	
	//get query cursor
	//@param projection: the specified columns array to get,set null to get all
	public static Cursor queryCallLog(Context context, String[] projection){
		//get call log cursor
		Cursor cur = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, projection, null, null, null);
		return cur;
	}
	
	
	//get call log information
    public static List<CallLogInfo> getContactInfo(Context context) {
    
    	List<CallLogInfo> infoList = new ArrayList<CallLogInfo>();

    	Cursor cur=queryCallLog(context,null);

        if (cur.moveToFirst())
        {
            do 
            {
            	CallLogInfo calllog=new CallLogInfo();
            	String id=cur.getString(cur.getColumnIndex(CallLog.Calls._ID));
            	String callType = cur.getString(cur.getColumnIndex(CallLog.Calls.TYPE));
            	String name=cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME));
            	String date = cur.getString(cur.getColumnIndex(CallLog.Calls.DATE));
                String number = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));
                String duration = cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION));
                
                calllog.setId(id);
                calllog.setType(callType);
                calllog.setName(name);
                calllog.setDate(date);
                calllog.setNumber(number);
                calllog.setDuration(duration);
                
            	infoList.add(calllog);
            } while (cur.moveToNext());
        }
        cur.close();
        return infoList;
    }

	public static int getCallLogCount(Context context)
	{
		Cursor cursorCallLog =null;
		try
		{
			cursorCallLog=context.getContentResolver().query(  
		
                CallLog.Calls.CONTENT_URI, null, null, null,  
                CallLog.Calls.DATE + " desc");  
			return cursorCallLog.getCount();
		}
		finally
		{
			if(cursorCallLog!=null)
			{
				cursorCallLog.close();
			}
		}
	}
	
    
    public static boolean backupCallLogs(List<CallLogInfo> infos, String path)
    {
    	try 
    	{   
    		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
    		StringBuilder sb = new StringBuilder();      
			for (CallLogInfo info : infos)
			{
				sb.setLength(0);
                sb.append("BEGIN:VCALL").append("\n");
                String id = info.getId();
            	String callType = info.getType();
            	String name = info.getName();
            	String date = info.getDate();
                String number = info.getNumber();
                String duration = info.getDuration();
                                
                sb.append(CallLog.Calls._ID + ":").append(id).append("\n");
                sb.append(CallLog.Calls.TYPE + ":").append(callType).append("\n");
                sb.append(CallLog.Calls.CACHED_NAME + ":").append(name).append("\n");
                sb.append(CallLog.Calls.DATE + ":").append(date).append("\n");
                sb.append(CallLog.Calls.NUMBER + ":").append(number).append("\n");
                sb.append(CallLog.Calls.DURATION + ":").append(duration).append("\n");
                sb.append("END:VCALL").append("\n");
				
                writer.write(sb.toString());                         
                writer.flush();
          	}
			writer.close();
			return true;
       	} 
    	catch (UnsupportedEncodingException e) 
    	{
    		e.printStackTrace();
    		return false;
        }
    	catch (FileNotFoundException e) 
    	{
    		e.printStackTrace();
    		return false;
        } 
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    		return false;
     	}
               
    }
    
    //get call log information in vcf file
    public static List<ContactInfo> restoreContacts(String filePath) throws Exception 
    {
    	return null;
    }

   
    //store call log into phone
    public static void addCallLog(Context context, CallLogInfo info)
    {
    	
    }
}

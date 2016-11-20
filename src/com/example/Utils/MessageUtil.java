package cn.unas.app.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.unas.app.unas.data.MessageInfo;

public class MessageUtil {
	
	public static final String MESSAGEFILENAME="SmsTable.xml";
	
	public static int getMessageCount(Context context)
	{
		Cursor cursorSendbox =null;
		try
		{
			int count=0;
			Cursor cursorInbox = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
	
					null, null, null, null);
			count+=cursorInbox.getCount();
			
			cursorSendbox= context.getContentResolver().query(Uri.parse("content://sms/sent"),
	
					null, null, null, null);
			return count+cursorSendbox.getCount();
		}
		finally
		{
			if(cursorSendbox!=null)
			{
				cursorSendbox.close();
			}
		}
	}
	
	public static List<MessageInfo> getSMSInfo(Context context)
	{
		List<MessageInfo> messageList=new ArrayList<MessageInfo>();
        Cursor cusor = context.getContentResolver().query(
        		Uri.parse("content://sms/"), null, "protocol='0'", null,null);
        int threadColumn = cusor.getColumnIndex(MessageInfo.THREAD_ID);
        int addressColumn = cusor.getColumnIndex(MessageInfo.ADDRESS);
        int personColumn = cusor.getColumnIndex(MessageInfo.PERSON);
        int dateColumn = cusor.getColumnIndex(MessageInfo.DATE);
        int protocolColumn = cusor.getColumnIndex(MessageInfo.PROTOCOL);
        int readColumn = cusor.getColumnIndex(MessageInfo.READ);
        int statusColumn = cusor.getColumnIndex(MessageInfo.STATUS);
        int typeColumn = cusor.getColumnIndex(MessageInfo.TYPE);
        int bodyColumn = cusor.getColumnIndex(MessageInfo.BODY);
        int service_centerColumn = cusor.getColumnIndex(MessageInfo.SERVICE_CENTER);
        
        if (cusor != null) 
        {
            while (cusor.moveToNext()) 
            {
            	MessageInfo smsinfo = new MessageInfo();
            	smsinfo.setThreadId(cusor.getString(threadColumn));
            	smsinfo.setAddress(cusor.getString(addressColumn));
            	smsinfo.setPerson(cusor.getString(personColumn));
            	smsinfo.setDate(cusor.getString(dateColumn));
            	smsinfo.setProtocol(cusor.getString(protocolColumn));
            	smsinfo.setread(cusor.getString(readColumn));
            	smsinfo.setStatus(cusor.getString(statusColumn));
            	smsinfo.setType(cusor.getString(typeColumn));
            	smsinfo.setBody(cusor.getString(bodyColumn));
            	smsinfo.setServiceCenter(cusor.getString(service_centerColumn));
            	
                messageList.add(smsinfo);
            }
            cusor.close();
        }
        return messageList;
    }
	
	public static boolean backupSMS(List<MessageInfo> messagelist, String backupPath)
	{
		boolean result= XMLParserUtil.writeXML(messagelist, backupPath);
		
		return result;
	}
}


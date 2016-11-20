package cn.unas.app.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import android.util.Xml;
import cn.unas.app.unas.data.MessageInfo;

public class XMLParserUtil{

    public static List<MessageInfo> readPULLXML(InputStream inputStream) 
    {
    	List<MessageInfo> MessageList=null;
    	MessageInfo mMessage=null;
        try 
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) 
            {
                String name = parser.getName();
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    MessageList = new ArrayList<MessageInfo>();
                    break;

                case XmlPullParser.START_TAG:
                    if (MessageInfo.MESSAGE.equals(name))
                    {
                        mMessage = new MessageInfo();
                    } 
                    else if(MessageInfo.THREAD_ID.equals(name))
                    {
                    	mMessage.setThreadId(parser.nextText());
                    }
                    else if(MessageInfo.ADDRESS.equals(name))
                    {
                    	mMessage.setAddress(parser.nextText());
                    }
                    else if(MessageInfo.PERSON.equals(name))
                    {
                    	mMessage.setPerson(parser.nextText());
                    }
                    else if(MessageInfo.DATE.equals(name))
                    {
                    	mMessage.setDate(parser.nextText());
                    }
                    else if(MessageInfo.PROTOCOL.equals(name))
                    {
                    	mMessage.setProtocol(parser.nextText());
                    }
                    else if(MessageInfo.READ.equals(name))
                    {
                    	mMessage.setread(parser.nextText());
                    }
                    else if(MessageInfo.STATUS.equals(name))
                    {
                    	mMessage.setStatus(parser.nextText());
                    }
                    else if(MessageInfo.TYPE.equals(name))
                    {
                    	mMessage.setType(parser.nextText());
                    }
                    else if(MessageInfo.BODY.equals(name))
                    {
                    	mMessage.setBody(parser.nextText());
                    }
                    else if(MessageInfo.SERVICE_CENTER.equals(name))
                    {
                    	mMessage.setServiceCenter(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (MessageInfo.MESSAGE.equals(name)) {
                        MessageList.add(mMessage);
                    }
                    break;

                default:
                    break;
                }

                eventType = parser.next();
            }
            return MessageList;
        } 
        catch (XmlPullParserException e) 
        {
            e.printStackTrace();
        	return null;
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        	return null;
        } 
        finally 
        {
            if (inputStream != null) 
            {
                try 
                {
                    inputStream.close();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }

    }

    //not used, for illegal character (U+0) exception
    public static boolean writePULLXML(List<MessageInfo> MessageList, String filePath) {
        XmlSerializer serializer = Xml.newSerializer();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath,false);
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", MessageInfo.MESSAGES);

            for (MessageInfo message:MessageList) 
            {
                serializer.startTag("", MessageInfo.MESSAGE);

                if(message.getThreadId()!=null)
                {
	                serializer.startTag("", MessageInfo.THREAD_ID);
	                serializer.text(message.getThreadId());
	                serializer.endTag("",  MessageInfo.THREAD_ID);
                }

                if(message.getAddress()!=null)
                {
	                serializer.startTag("", MessageInfo.ADDRESS);
	                serializer.text(message.getAddress());
	                serializer.endTag("",  MessageInfo.ADDRESS);
                }
                
                if(message.getPerson()!=null)
                {
	                serializer.startTag("", MessageInfo.PERSON);
	                serializer.text(message.getPerson());
	                serializer.endTag("",  MessageInfo.PERSON);
                }
                
                if(message.getDate()!=null)
                {
	                serializer.startTag("", MessageInfo.DATE);
	                serializer.text(message.getDate());
	                serializer.endTag("",  MessageInfo.DATE);
                }
                
                if(message.getProtocol()!=null)
                {
	                serializer.startTag("", MessageInfo.PROTOCOL);
	                serializer.text(message.getProtocol());
	                serializer.endTag("",  MessageInfo.PROTOCOL);
                }
                
                if(message.getRead()!=null)
                {
	                serializer.startTag("", MessageInfo.READ);
	                serializer.text(message.getRead());
	                serializer.endTag("",  MessageInfo.READ);
                }
                
                if(message.getStatus()!=null)
                {
	                serializer.startTag("", MessageInfo.STATUS);
	                serializer.text(message.getStatus());
	                serializer.endTag("",  MessageInfo.STATUS);
                }
                
                if(message.getType()!=null)
                {
	                serializer.startTag("", MessageInfo.TYPE);
	                serializer.text(message.getType());
	                serializer.endTag("",  MessageInfo.TYPE);
                }
                
                if(message.getBody()!=null)
                {
	                serializer.startTag("", MessageInfo.BODY);
	                serializer.text(message.getBody());
	                serializer.endTag("",  MessageInfo.BODY);
                }
                
                if(message.getServiceCenter()!=null)
                {
	                serializer.startTag("", MessageInfo.SERVICE_CENTER);
	                serializer.text(message.getServiceCenter());
	                serializer.endTag("",  MessageInfo.SERVICE_CENTER);
                }
                
                serializer.endTag("",  MessageInfo.MESSAGE);
            }

            serializer.endTag("", MessageInfo.MESSAGES);
            serializer.endDocument();
            return true;
        }
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            return false;
        }
        catch (IllegalArgumentException e) 
        {
            e.printStackTrace();
            return false;
        } 
        catch (IllegalStateException e) 
        {
            e.printStackTrace();
            return false;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return false;
        } 
        finally 
        {
            if (fos != null) 
            {
                try 
                {
                    fos.close();
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    //save to xml file using xstream jar
    public static boolean writeXML(List<MessageInfo> MessageList, String filePath)
	{
        FileOutputStream fos = null;
        try 
        {
            fos = new FileOutputStream(filePath,false);
		    XStream xstream = new XStream(new DomDriver());
		    xstream.alias("SMSRecord", List.class);  
		    xstream.alias("SMS", MessageInfo.class);  
		    xstream.toXML(MessageList, fos);  
		    return true;
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        	return false;
        } 
        finally 
        {
            if (fos != null) 
            {
                try 
                {
                    fos.close();
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
	}
    
    public static List<MessageInfo> readXML(String filePath)
    {
    	InputStream is=null;
    	try
    	{
    		is = new FileInputStream(filePath);
	    	XStream xstream = new XStream(new DomDriver());
		    xstream.alias("SMS", MessageInfo.class);
	        return (List<MessageInfo>)xstream.fromXML(is);
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    	finally
    	{
    		if(is!=null)
    		{
    			try 
                {
                    is.close();
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
    		}
    	}
    }
    
}

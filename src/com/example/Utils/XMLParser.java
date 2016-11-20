package cn.unas.app.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class XMLParser {

	public XMLParser() {
	 }

	/**
	 * ��������xml
	 * 
	 * @param url
	 * @return
	 */
	public static String getXmlFromUrl(String strurl) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// ����һ��URL����
			URL url = new URL(strurl);
			// ����һ��Http����
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// ʹ��IO����ȡ����
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	//��ȡXML
	public static String readHostNameFromXML(String strXMl) {
		
		InputStream inStream = null;
		try {
			byte[] bytes=strXMl.getBytes("UTF-8");
			inStream = new ByteArrayInputStream(strXMl.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(inStream, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT://�ĵ���ʼ�¼�,���Խ������ݳ�ʼ������
                    break;

                 case XmlPullParser.START_TAG://��ʼԪ���¼�
                    String name = parser.getName();
                    if (name.equalsIgnoreCase("device")) 
                    {
                    	
                    } 
                    if (name.equalsIgnoreCase("devicetype")) 
                    {
                                
                    }
                    if(name.equalsIgnoreCase("friendlyName"))
                    {
                    	return parser.nextText();
                    	
                    }
                    
                    break;
 
                    case XmlPullParser.END_TAG://����Ԫ���¼�
                    break;
                }

                 eventType = parser.next();
            }
            inStream.close();
        }
        catch (Exception e) 
        {
                    e.printStackTrace();
        }

        return "UnknownHost";
	}
}

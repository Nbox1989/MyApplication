package cn.unas.app.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.ContactStruct.ContactMethod;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import cn.unas.app.R;
import cn.unas.app.net.RemoteServer;
import cn.unas.app.net.ServerContainer;
import cn.unas.app.net.ServerFactory;
import cn.unas.app.unas.data.ConfigWifi;
import cn.unas.app.unas.data.ContactInfo;
import cn.unas.app.unas.data.ContactInfo.EmailInfo;
import cn.unas.app.unas.data.ContactInfo.PhoneInfo;

public class Util {
	private Util()
	{
		//ensure util class will never be instantiate
		throw new AssertionError();
	}
	//-------------------------------for viewtype--------------------------
	public static ViewType getViewType()
	{
		return t;
	}
	
	public static void setViewType(ViewType type)
	{
		t=type;
	}
	
	private static ViewType t=ViewType.LOCAL;
	
	public enum ViewType
	{
		LOCAL,
		SERVER,
		SELECTLOCAL,
		SELECTSERVER,
		TASK,
		MORE;
	}
	
	//---------------------------for order method-------------------
	public static OrderMethod getOrderMethod()
	{
		return order;
	}
	
	public static void setOrderMethod(OrderMethod o)
	{
		order=o;
	}
	
	private static OrderMethod order=OrderMethod.NameAsc;
	
	public enum OrderMethod
	{
		TimeDes,
		NameAsc;
	}
	//---------------------------for SSDP-------------------------
	public static String resolveLocation(String detail) {
		String[] strArray=detail.split("\r\n");
		for(String str:strArray)
		{
			if(str.startsWith("LOCATION"))
			{
				return str.substring(9);
			}
		}
		return null;
	}
	
	//---------------------for save and load server list-----------
	private static SharedPreferences sp_Server;
	private static boolean loaded=false;
	
	private static final String CONTENT_DIVIDER="&";
	
	private static final String ITEM_DIVIDER="%";
	
	private static final String ElEMENT_DIVIDER=";";
	
	public static void setServerSharedPreferences(SharedPreferences sp)
	{
		sp_Server=sp;
	}
	
	public static void saveServer()
	{
		SharedPreferences.Editor editor = sp_Server.edit(); 
		editor.putString("SERVERLIST", serverListToString()); 
		editor.commit(); 
	}
	
	public static void loadServer(SharedPreferences sharedPreferences)
	{
		if(!loaded)
		{
			sp_Server = sharedPreferences;
			String serverlist = sharedPreferences.getString("SERVERLIST", "");
	
			StringToServerList(serverlist);
			loaded=true;
		}
	}
	
	public static String serverListToString()
	{
		StringBuilder str= new StringBuilder();
		for(RemoteServer server:ServerContainer.getinstance().getServerList())
		{
			StringBuilder s=new StringBuilder();
			s.append("TYPE"+CONTENT_DIVIDER+String.valueOf(server.getType()));
			s.append(ITEM_DIVIDER+"IP"+CONTENT_DIVIDER+server.getHostName());
			s.append(ITEM_DIVIDER+"NICKNAME"+CONTENT_DIVIDER+server.getNickName());
			s.append(ITEM_DIVIDER+"ANONYMOUS"+CONTENT_DIVIDER+String.valueOf(server.isAnonymous()));
			s.append(ITEM_DIVIDER+"REMEMBER"+CONTENT_DIVIDER+String.valueOf(server.isRemember()));
			s.append(ITEM_DIVIDER+"USER"+CONTENT_DIVIDER+server.getUser());
			if(server.isRemember())
			{
				s.append(ITEM_DIVIDER+"PWD"+CONTENT_DIVIDER+server.getPWD());
				if(server.getBackupPath()!=null)
				{
					s.append(ITEM_DIVIDER+"PATH"+CONTENT_DIVIDER+server.getBackupPath());
				}
				s.append(ITEM_DIVIDER+"BACKUPSETTING"+CONTENT_DIVIDER+String.valueOf((int)server.getBackupCode()));
			}
			s.append(ElEMENT_DIVIDER);
			str.append(s);
		}
		return str.toString();
	}
	
	public static void StringToServerList(String str)
	{
		ServerContainer.getinstance().RemoveAll();
		String[] serverList=str.split(ElEMENT_DIVIDER);
		int noNameServerIndex=1;	//if server does not have a nick name, we will take a name for it like "server_1" "server_2"
		for(String s:serverList)
		{
			if(s==null||s.isEmpty())
			{
				continue;
			}
			String[] params=s.split(ITEM_DIVIDER);
			
			RemoteServer server = null;
			String IP = null,usr = null,pwd = null,path=null,type=null,nickname=null;
			boolean anonymous=false;
			boolean rem = false;
			byte setting=0x00;
			for(String c:params)
			{
				String[] item=c.split(CONTENT_DIVIDER);
				if(item==null||item.length==0)
				{
					continue;
				}
				if(item[0].equals("IP"))
				{
					IP=item[1];
				}
				else if(item[0].equals("ANONYMOUS"))
				{
					anonymous=item.length>1?Boolean.valueOf(item[1]):false;
				}
				else if(item[0].equals("REMEMBER"))
				{
					rem=item.length>1?Boolean.valueOf(item[1]):false;
				}
				else if(item[0].equals("NICKNAME"))
				{
					nickname=item.length>1?item[1]:"SERVER_"+noNameServerIndex++;
				}
				else if(item[0].equals("TYPE"))
				{
					type=item.length>1?item[1]:"FTP";
				}
				else if(item[0].equals("USER"))
				{
					usr=item.length>1?item[1]:"";
				}
				else if(item[0].equals("PWD"))
				{
					pwd=item.length>1?item[1]:"";
				}
				else if(item[0].equals("PATH"))
				{
					path=item.length>1?item[1]:"";
				}
				else if(item[0].equals("BACKUPSETTING"))
				{
					setting=item.length>1?Byte.valueOf(item[1]):0x00;
				}
			}
			server=anonymous?ServerFactory.CreateServer(type, IP, nickname, rem):
					ServerFactory.CreateServer(type, IP, nickname, usr, pwd, rem);
			if(path!=null)
			{
				server.setBackupPath(path);
			}
			server.setBackupCode(setting);
			
			if(server!=null)
			{
				ServerContainer.getinstance().AddServer(server);
			}
		}
	}
	//--------------------for configs:language wifi etc.-----------------------
	private static SharedPreferences sp_Config;
	
	public static void setConfigSharedPreferences(SharedPreferences sharedPreference)
	{
		sp_Config=sharedPreference;
	}
	
	public static SharedPreferences getConfigSharedPreferences()
	{
		return sp_Config;
	}
	
//	public static List<String> Languages = new ArrayList<String>()
//	{
//		{
//			add("default");
//			add("EN");
//			add("zh-CN");
//			add("zh-TW");
//			add("fr");
//			add("de-DE");
//			add("jp");
//			add("ko");
//			add("th");
//		}
//	};
//
//	public static Locale[] Locales=
//	{
//			Locale.getDefault(), 
//			Locale.ENGLISH, 
//			Locale.SIMPLIFIED_CHINESE, 
//			Locale.TRADITIONAL_CHINESE, 
//			Locale.FRENCH, 
//			Locale.GERMANY,
//			Locale.JAPANESE, 
//			Locale.KOREAN, 
//			Locale.forLanguageTag("th")
//	};
	//------------------------------------for UUID----------------------
	
	 
	public static String getUUID(Context context)
	{
		try
		{
			if(uniqueId==null||uniqueId.isEmpty())
			{
				final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);   
		
				final String tmDevice, tmSerial, tmPhone, androidId;   
		
		        tmDevice = "" + tm.getDeviceId();  
		
		        tmSerial = "" + tm.getSimSerialNumber();   
		
		        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);   
		
		        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());   
		
		        uniqueId = deviceUuid.toString();
			}
			return uniqueId;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
		return uniqueId;
	}
	
	public static void setUUID(Context context)
	{
		if(uniqueId==null||uniqueId.isEmpty())
		{
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);   
	
			final String tmDevice, tmSerial, tmPhone, androidId;   
	
	        tmDevice = "" + tm.getDeviceId();  
	
	        tmSerial = "" + tm.getSimSerialNumber();   
	
	        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);   
	
	        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());   
	
	        uniqueId = deviceUuid.toString();
		}
	}
	
	private static String uniqueId=null;
	
	

	//----------------------------------for pixel------------------------------------

	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	//----------------------------------for constants-------------------------------------
	public static List<String> video;
	public static List<String> audio;
	public static List<String> image;
	public static List<String> text;
	public static List<String> zip;
	public static List<String> apk;
	
    public static void initMIME(Context context)
    {
    	video =Arrays.asList(context.getResources().getStringArray(R.array.video));
        audio =Arrays.asList(context.getResources().getStringArray(R.array.audio));
        image =Arrays.asList(context.getResources().getStringArray(R.array.image));
        text =Arrays.asList(context.getResources().getStringArray(R.array.text));
        zip =Arrays.asList(context.getResources().getStringArray(R.array.zip));
        apk=Arrays.asList(context.getResources().getStringArray(R.array.apk));        
    }
    
    public static final int CONNECT_SUCCESSS = 0x00;  
    public static final int CONNECT_FAIL = 0x01;  
    public static final int DISCONNECT_SUCCESS = 0x02; 
    
    public static final int UPLOAD_PRELOADING=0x10;
    public static final int UPLOAD_LOADING = 0x11;  
    public static final int UPLOAD_SUCCESS = 0x12;  
    public static final int UPLOAD_FAIL = 0x13; 
    public static final int UPLOAD_CREATEFOLDER=0x14;
    public static final int UPLOAD_DELETETASK=0x15;
  
    public static final int DOWNLOAD_PRELOADING=0x20;
    public static final int DOWNLOAD_LOADING = 0x21;  
    public static final int DOWNLOAD_SUCCESS = 0x22;  
    public static final int DOWNLOAD_FAIL = 0x23;  
    public static final int DOWNLOAD_CREATEFOLDER=0x24;
    public static final int DOWNLOAD_DELETETASK=0x25;
      
    public static final int DELETEFILE_SUCCESS = 0x30;  
    public static final int DELETEFILE_FAIL = 0x31; 
	
    public static final int FILE_NOTEXISTS = 0x40; 
    //-------------unify filename and path format------------
    public static String unifyFileName(String name)
    {
    	//filename should not end with suffix "/"
    	if(name==null||name.isEmpty())
    	{
    		return "";
    	}
    	else if(name.endsWith("//"))
    	{
    		return name.substring(0,name.length()-2);
    	}
    	else if(name.endsWith("/"))
    	{
    		return name.substring(0,name.length()-1);
    	}
    	else
    	{
    		return name;
    	}
    }
    
    public static String unifyFolderPath(String path)
    {
    	//folder path should always end with a single suffix "/" 
    	if(path==null||path.isEmpty())
    	{
    		return "/";
    	}
    	else if(path.endsWith("//"))
    	{
    		return path.substring(0,path.length()-1);
    	}
    	else if(!path.endsWith("/"))
    	{
    		return path+"/";
    	}
    	else
    	{
    		return path;
    	}
    }
    
    public static String unifyFilePath(String path)
    {
    	//file path should never end with suffix "/" 
    	if(path==null||path.isEmpty())
    	{
    		return "";
    	}
    	else if(path.endsWith("//"))
    	{
    		return path.substring(0,path.length()-2);
    	}
    	else if(path.endsWith("/"))
    	{
    		return path.substring(0,path.length()-1);
    	}
    	else
    	{
    		return path;
    	}
    }
    
    //--------------------string format-----------------------

	private static final String[] strLevel
			=new String[]{"B","KB","MB","GB"};
    
    //consider to G level
  	public static String getLengthFormat(long length)
  	{
  		int level=0;
  		length=length*10;
  		while(length/10240>0)
  		{
  			length=length*10;
  			length=length/10240;
  			level++;
  		}
  		if(level==0)
  		{
  			return length/10+" "+strLevel[level];
  		}
  		else
  		{
  			return length/10f+" "+strLevel[level];
  		}
  	}
    
    public static String getFormatForTaskCompleteTime(long time)
    {
    	SimpleDateFormat format;
    	Calendar c=Calendar.getInstance();
    	Calendar c_now=Calendar.getInstance();
    	c.setTimeInMillis(time);
    	if(c.get(Calendar.DATE)!=c_now.get(Calendar.DATE))
    	{
    		format = new SimpleDateFormat("MM-dd");
    	}
    	else
    	{
    		format = new SimpleDateFormat("HH:mm");
    	}
    	return format.format(new Date(time));
    }
    
    public static String getFormatForTaskCompleteFullTime(long time)
    {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	return format.format(new Date(time));
    }
    
    public static String getFormatForFileModifyTime(long time)
    {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	return format.format(new Date(time));
    }
    
    public static String getFormatForFileRefreshTime(long time)
    {
    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    	return format.format(new Date(time));
    }
    
    //---------------------bitmap handle------------------------------
    public static Bitmap small(Bitmap map,float x, float y){
        Matrix matrix = new Matrix();
        matrix.postScale(x, y);
        return Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),matrix,true);
    }
    
    //-----------------------file type--------------------------------
    //get MIMEString for a file, so that android system would know how to open the file
    public static String getMIMEString(String filename)
    {
		String type = getMIMEType(filename).toString();
		if(type.equals(MIMETYPE.unknown.toString()))
		{
            type = "*";
		}
        else 
        {
            type = getMIMEType(filename).toString();
        }
        type += "/*";
        return type;
    }
	
	public static MIMETYPE getMIMEType(String fileName){
        //get MIMEType from file name
        String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        
        if (Util.audio.contains(end))
        {
        	return MIMETYPE.audio;
        }
        else if(Util.video.contains(end)) 
        {
        	return MIMETYPE.video;
        }
        else if (Util.image.contains(end))
        {
        	return MIMETYPE.image;
        }
        else if (Util.text.contains(end))
        {
        	return MIMETYPE.text;
        }
        else if(Util.zip.contains(end))
        {
        	return MIMETYPE.zip;
        }
        else if(Util.apk.contains(end))
        {
        	return MIMETYPE.apk;
        }
        else 
        {
        	return MIMETYPE.unknown;
        }
    }
	
	public enum FILETYPE
	{		
		directory,	
		link,	
		ordinaryfile,	
	}
	
	public enum MIMETYPE
	{
		unknown,
		text,
		image,
		audio,
		video,
		zip,
		apk;
	}	
	//----------------------wifi state---------------------------------
	public static boolean isWiFiActive(Context c) {   
        Context context = c.getApplicationContext();   
        ConnectivityManager connectivity = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (connectivity != null)
        {   
        	NetworkInfo info=connectivity.getActiveNetworkInfo();
        	if(info!=null&&info.getType()==ConnectivityManager.TYPE_WIFI)
        	{
        		return true;
        	}
        }   
        return false;  
        
    }
	
	public static volatile boolean WifiConnected=false;
	
	public static boolean getWifiState()
	{
		return WifiConnected;
	}
	
	public static void setWifiState(boolean b)
	{
		WifiConnected=b; 
	}
	
	public static boolean checkTransmitionPermitted()
	{
		if(ConfigWifi.getInstance().get()&&!WifiConnected)
		{
			return false;
		}
		return true;
	}
	
	//-----------------------screen width and height---------------------------------------------------
	private static int SCREEN_WIDTH;
	
	private static int SCREEN_HEIGHT;
	
	public static void setScreenWidth(int width)
	{
		SCREEN_WIDTH=width;
	}
	
	public static void setScreenHeight(int height)
	{
		SCREEN_HEIGHT=height;
	}
	
	public static int getScreenWidth()
	{
		return SCREEN_WIDTH;
	}
	
	public static int getScreenHeight()
	{
		return SCREEN_HEIGHT;
	}
	
	//--------------------------------view file manager height--------------------------------------------------
	private static int vfmWidth;
	
	private static int vfmHeight;
	
	public static void setVFMWidth(int width)
	{
		vfmWidth=width;
	}
	
	public static int getVFMWidth()
	{
		return vfmWidth;
	}
	
	public static void setVFMHeight(int height)
	{
		vfmHeight=height;
	}
	
	public static int getVFMHeight()
	{
		return vfmHeight;
	}
	
	//--------------------------file operation------------------------------------
	public static void removeFolderContent(File folder)
	{
		for(File f:folder.listFiles())
		{
			if(f.isDirectory())
			{
				removeFolder(f);
			}
			else
			{
				f.delete();
			}
		}
	}

	private static void removeFolder(File folder) 
	{
		removeFolderContent(folder);
		folder.delete();
	}
	
	//--------------------------photo util------------------------------------------
	private static String[] photoSuffix=new String[]{".bmp",".jpg",".jpeg",".png",".gif"};
	
	public static boolean isPhoto(String fileName)
	{
		for(int i=0;i<photoSuffix.length;i++)
		{
			if(fileName.endsWith(photoSuffix[i]))
			{
				return true;
			}
		}
		return false;
	}
	
}

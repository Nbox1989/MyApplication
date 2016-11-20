package cn.unas.app.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import cn.unas.app.unas.data.Applications;

public class ApplicationUtil {
	
	public static List<Applications> getApplicationCount(Context context)
	{
		List<ApplicationInfo> applist=context.getPackageManager().getInstalledApplications(0);
		List<ApplicationInfo> nonsystemapplist=new ArrayList<ApplicationInfo>();
		for(ApplicationInfo appinfo:applist)
		{
			if((appinfo.flags&ApplicationInfo.FLAG_SYSTEM)<=0)
			{
				nonsystemapplist.add(appinfo);
			}
		}
		return Applications.Create(nonsystemapplist, true);
	}
	
	public static boolean backupApplications(List<Applications> mAppList , String folderPath)
	{
		List<File> filelist=new ArrayList<File>();
		for(Applications app:mAppList)
		{
			if(app.isSelected())
			{
				File apkFile=new File(app.getAppInfo().sourceDir);
				File desFile=new File(folderPath+File.separator+app.getAppInfo().packageName+".apk");
				
				InputStream input = null;
				FileOutputStream output = null;
				try
				{
					input=new FileInputStream(apkFile);
					output=new FileOutputStream(desFile);
					byte[] buffer=new byte[1024*32];	//32K bytes
					int byteread=0;
					while((byteread=input.read(buffer))>=0)
					{
						output.write(buffer,0,byteread);
					}
					filelist.add(desFile);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return false;
				}
				finally 
				{
					if(input!=null)
					{
						try
						{
							input.close();
						}
						catch(IOException e)
						{
							
						}
					}
					if(output!=null)
					{
						try
						{
							output.close();
						}
						catch(IOException e)
						{
							
						}
					}
				}
				
			}
		}
		return true;
	}
	
	public static boolean backupApplication(Applications app , String folderPath)
	{
		File apkFile=new File(app.getAppInfo().sourceDir);
		File desFile=new File(folderPath+File.separator+app.getAppInfo().packageName+".apk");
		
		InputStream input = null;
		FileOutputStream output = null;
		try
		{
			input=new FileInputStream(apkFile);
			output=new FileOutputStream(desFile);
			byte[] buffer=new byte[1024*32];	//32K bytes
			int byteread=0;
			while((byteread=input.read(buffer))>=0)
			{
				output.write(buffer,0,byteread);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally 
		{
			if(input!=null)
			{
				try
				{
					input.close();
				}
				catch(IOException e)
				{
					
				}
			}
			if(output!=null)
			{
				try
				{
					output.close();
				}
				catch(IOException e)
				{
					
				}
			}
		}
		return true;
	}
}

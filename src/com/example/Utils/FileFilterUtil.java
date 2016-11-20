package cn.unas.app.Utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilterUtil {
	private static String[] photos=new String[]{".jpg", ".png", ".jpeg", ".bmp", ".gif"};
	
	private static String[] musics=new String[]{".m4a", ".mp3", ".wav"};

	private static String[] videos=new String[]{".mp4", ".3gp", ".rmvb", ".mkv", ".rm"};
	
	private static String[] documents=new String[]{".txt", ".ini",  ".log", ".html"};
	
	private static String[] apks=new String[]{".apk"};
	
	private static String[] zips=new String[]{".rar", ".zip", ".7z"};
		    
    public static class PhotoFilter implements FilenameFilter{  

        public boolean accept(File dir,String name){  
        	
        	for(String str: photos)
        	{
        		if(name.endsWith(str))
        		{
        			return true;
        		}
        	}
        	return false;
        }
    }  
    
    public static class MusicFilter implements FilenameFilter{  

    	 public boolean accept(File dir,String name){  
         	
         	for(String str: musics)
         	{
         		if(name.endsWith(str))
         		{
         			return true;
         		}
         	}
         	return false;
         }
    }  

    public static class VideoFilter implements FilenameFilter{  

    	 public boolean accept(File dir,String name){  
         	
         	for(String str: videos)
         	{
         		if(name.endsWith(str))
         		{
         			return true;
         		}
         	}
         	return false;
         }
    }  
    
    public static class DocFilter implements FilenameFilter{  

    	 public boolean accept(File dir,String name){  
         	
         	for(String str: documents)
         	{
         		if(name.endsWith(str))
         		{
         			return true;
         		}
         	}
         	return false;
         }
    }  


    public static class ApkFilter implements FilenameFilter
    {  

    	 public boolean accept(File dir,String name)
    	 {  
         	for(String str: apks)
         	{
         		if(name.endsWith(str))
         		{
         			return true;
         		}
         	}
         	return false;
         }
    }  

    public static class ZipFilter implements FilenameFilter
    {  
    	 public boolean accept(File dir,String name)
    	 {  
         	for(String str: zips)
         	{
         		if(name.endsWith(str))
         		{
         			return true;
         		}
         	}
         	return false;
         }
    }  
	
	public static String getFirstPhotoPathInDirectory(String path)
	{
		return new File(path).listFiles(new PhotoFilter())[0].getPath();
	}
}  

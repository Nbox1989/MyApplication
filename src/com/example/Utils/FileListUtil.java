package cn.unas.app.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileListUtil {
	
	public static long generateFileQueue(List<File> fileList, File file)
	{
		if(fileList==null)
		{
			throw new NullPointerException();
		}
		LinkedList<File> fileQueue=new LinkedList<File>();
		File[] files;
		long size=0;
		fileQueue.offerLast(file);
		while(!fileQueue.isEmpty())
		{
			files=fileQueue.removeFirst().listFiles();
			for(File subFile:files)
			{
				if(subFile.canRead())
				{
					if(subFile.isDirectory())
					{
						fileQueue.offerLast(subFile);
					}
					else if(subFile.isFile())
					{
						fileList.add(subFile);
						size+=subFile.length();
					}
				}
			}
		}
		
		return size;
	}
}

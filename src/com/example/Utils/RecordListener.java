package cn.unas.app.Utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;
import cn.unas.app.background.EnQueueThread;
import cn.unas.app.net.RemoteServer;
import cn.unas.app.net.ServerContainer;
import cn.unas.app.unas.data.UploadTask;

public class RecordListener extends FileObserver{
	private String backupPath="";
	private List<UploadTask> tasklist=new ArrayList<UploadTask>();
	
	private Context context;
	public RecordListener(String path, Context context ) {
		super(path, FileObserver.CLOSE_WRITE | FileObserver.CREATE | FileObserver.DELETE  
                | FileObserver.DELETE_SELF | FileObserver.MODIFY);
		backupPath=path;
		this.context=context;
	}

	@Override
	public void onEvent(int event, String path) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
        // ����ע�� event ֵ���� 0x40000000���Ϻ��ֵ��������Ҫcaseʱ��Ҫ�Ƚ��� &FileObserver.ALL_EVENTS  
        int el = event & FileObserver.ALL_EVENTS;  
        Log.i("nbox", "onEvent event:" + el);  
		switch(el)
		{
			case FileObserver.CREATE:
				tasklist.clear();
				Log.i("nbox", "servercount:"+ServerContainer.getinstance().getServerList().size()); 
				for(RemoteServer server:ServerContainer.getinstance().getServerList())
				{
					if(server.isDoBackup())
					{
						tasklist.add(new UploadTask(backupPath+"/"+path, server.getHostName(), server.getBackupPath()+"/Recordings_"+Util.getUUID(context)));
					}
				}
				try
				{
					EnQueueThread.getInstance().InsertUploadTasks(tasklist);
					Log.i("nbox", "insert success");  
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}

}

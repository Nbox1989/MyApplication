package cn.unas.app.Utils;


import java.io.File;

import a_vcard.android.text.TextUtils;
import android.content.Context;
import android.os.FileObserver;
import android.util.Log;
import cn.unas.app.background.EnQueueThread;
import cn.unas.app.net.RemoteServer;
import cn.unas.app.net.ServerContainer;
import cn.unas.app.unas.data.UploadTask;

public class AlbumListener extends FileObserver{

	private String albumPath="";
	private UploadTask task;
	
	private Context context;
	public AlbumListener(String path, Context context) {
		super(path, FileObserver.CLOSE_WRITE | FileObserver.CREATE | FileObserver.DELETE  
                | FileObserver.DELETE_SELF | FileObserver.MODIFY);
		albumPath=path;
		this.context=context;
	}

	@Override
	public void onEvent(int event, final String path) {
		// TODO Auto-generated method stub
        // variable event needs to &FileObserver.ALL_EVENTS before operation  
        int el = event & FileObserver.ALL_EVENTS;  
        Log.i("nbox", "onEvent event:" + el);  
		switch(el)
		{
			//should choose CLOSE_WRITE but not CREATE
			//if local file is uploaded before completely written, the file on server may be 0 byte
			case FileObserver.CLOSE_WRITE:
				
				Log.i("nbox", "servercount:"+ServerContainer.getinstance().getServerList().size()); 
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						for(final RemoteServer server:ServerContainer.getinstance().getServerList())
						{
							if(!TextUtils.isEmpty(server.getBackupPath()))
							{
								try
								{
									server.backupPhoto(new File(albumPath+path), Util.getUUID(context), null);
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
				}).start();
				break;
			default:
				break;
		}
	}

}

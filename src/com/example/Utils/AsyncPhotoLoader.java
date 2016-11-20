package cn.unas.app.Utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class AsyncPhotoLoader {
	private static final String TAG = "AsynImageLoader";
	
	private static final int imageWidth=500;
	
	private static final int imageHeight=500;
	
	public static final String CACHE_DIR = null;  
	    //cache loaded picture map 
	    private Map<String, SoftReference<Bitmap>> caches;  
	    //task queue  
	    private List<Task> taskQueue;  
	    private boolean isRunning = false;  
	      
	    public AsyncPhotoLoader(){  
	        //initialize local variables 
	        caches = new HashMap<String, SoftReference<Bitmap>>();  
	        taskQueue = new ArrayList<AsyncPhotoLoader.Task>();  
	        
	        isRunning = true;  
	        //start picture load thread
	        new Thread(runnable).start();  
	    }  
	      
	    /** 
	     *  
	     * @param imageView object which needs delayed loading
	     * @param url url of picture
	     * @param resId picture resource that showed during loading 
	     */  
	    public void showImageAsyn(ImageView imageView, String path, int resId)
	    {  
	        imageView.setTag(path);  
	        Bitmap bitmap = loadImageAsyn(path, getImageCallback(imageView, resId));  
	          
	        if(bitmap == null)
	        {  
	            imageView.setImageResource(resId);  
	        }
	        else
	        {  
	            imageView.setImageBitmap(bitmap);  
	        }  
	    }  
	      
	    private Bitmap loadImageAsyn(String path, ImageCallback callback)
	    {  
	        // judge whether picture is in cache  
	        if(caches.containsKey(path))
	        {  
	            // get soft reference
	            SoftReference<Bitmap> rf = caches.get(path);  
	            // get bitmap through soft reference  
	            Bitmap bitmap = rf.get();  
	            // if bitmap is deposed than remove corresponding path key from Map  
	            if(bitmap == null)
	            {  
	                caches.remove(path);  
	            }
	            else
	            {  
	                // if bitmap is not deposed then return bitmap
	                Log.i(TAG, "return image in cache" + path);  
	                return bitmap;  
	            }  
	        }
	        else
	        {  
	            // if cannot find bitmap in cache,than start downloading task  
	        	     	
	            Task task = new Task();  
	            task.path = path;  
	            task.callback = callback;  
	            if(!taskQueue.contains(task))
	            {  
	                taskQueue.add(task);  
	                // wake task download queue up  
	                synchronized (runnable) 
	                {  
	                    runnable.notify();  
	                }  
	            }  
	        }  
	        // bitmap is not is cache ,return null
	        return null;  
	    }  
	      
	    /** 
	     *  
	     * @param imageView  
	     * @param resId resource id showed before bitmap is loaded 
	     * @return 
	     */  
	    private ImageCallback getImageCallback(final ImageView imageView, final int resId)
	    {  
	        return new ImageCallback() 
	        {   
	            @Override  
	            public void loadImage(String path, Bitmap bitmap) 
	            {  
	                if(path.equals(imageView.getTag().toString()))
	                {  
	                    imageView.setImageBitmap(bitmap);  
	                }
	                else
	                {  
	                    imageView.setImageResource(resId);  
	                }  
	            }  
	        };  
	    }  
	      
	    private Handler handler = new Handler(){  
	  
	        @Override  
	        public void handleMessage(Message msg) {  
	            // returned completed task from sub thread  
	            Task task = (Task)msg.obj;  
	            // call loadImage method and deliver path and bitmap to adapter  
	            task.callback.loadImage(task.path, task.bitmap);  
	        }  
	          
	    };  
	      
	    private Runnable runnable = new Runnable() {  
	          
	        @Override  
	        public void run() {  
	            while(isRunning){  
	                // if there is uncompleted task in queue, do download task  
	                while(taskQueue.size() > 0){  
	                    // get first task and remove from queue  
	                    Task task = taskQueue.remove(0);  
	                    // add down loaded bitmap to cache   
	                    
	                    Bitmap bmp=BitmapFactory.decodeFile(FileFilterUtil.getFirstPhotoPathInDirectory(task.path));
        	    		
	    	        	bmp = ThumbnailUtils.extractThumbnail(bmp, imageWidth, imageHeight); 
	    	        	
	    	        	task.bitmap=bmp;
	                    
	                    caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));  
	                    if(handler != null)
	                    {  
	                        Message msg = handler.obtainMessage();  
	                        msg.obj = task; 
	                        handler.sendMessage(msg);  
	                    }  
	                }  
	                  
	                //if queue is empty ,than wait  
	                synchronized (this) 
	                {  
	                    try 
	                    {  
	                        this.wait();  
	                    } 
	                    catch (InterruptedException e) 
	                    {  
	                        e.printStackTrace();  
	                    }  
	                }  
	            }  
	        }  
	    };  
	      
	    //callback interface
	    public interface ImageCallback{  
	        void loadImage(String path, Bitmap bitmap);  
	    }  
	      
	    class Task{  
	        // download path
	        String path;  
	        // bitmap to download
	        Bitmap bitmap;  
	        // callback object
	        ImageCallback callback;  
	          
	        @Override  
	        public boolean equals(Object o) 
	        {  
	            Task task = (Task)o;  
	            return task.path.equals(path);  
	        }  
	    }  
}

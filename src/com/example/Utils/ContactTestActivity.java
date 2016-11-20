package cn.unas.app.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.view.View;
import android.widget.Button;
import cn.unas.app.R;

public class ContactTestActivity extends Activity {
    /** Called when the activity is first created. */
   private Button btn;
   private Button btn2;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_test);
        btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		setTitle("ok");
        		//setTitle(str);
        		
        		getContact();
        		
        		File saveFile=new File("/sdcard/test.txt");
                FileOutputStream outStream;
				try {
					outStream = new FileOutputStream(saveFile);
			        outStream.write(str.getBytes());
			        outStream.close();
				} catch (Exception e) {

					setTitle(e.toString());
				} 

        
        	}
        	
        	
        });
        
        
        
        btn2=(Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v)
        	{
        		setTitle("read");

   

        		  try {
              		File file = new File("/sdcard/test.txt");
            		FileInputStream inStream = new FileInputStream(file);
        		   ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        		   byte[] buffer = new byte[1024*5];
        		   int length = -1;
        		   while((length = inStream.read(buffer)) != -1 ){
        		    outStream.write(buffer, 0, length);
        		   }
        		   outStream.close();
        		   inStream.close();
        		   String txt= outStream.toString();
        		   //setTitle(txt);
        		   
        		   String [] str=txt.split("\n");
        		   for(int i=0;i<str.length;i++)
        		   {
        		   if(str[i].indexOf(",")>=0)
        		   {
        		     String [] NameAndTel=str[i].split(",");
        		     addContacts(NameAndTel[0],NameAndTel[1]);
        		   }
        		   }
        		    

        		   
        		   
        		   
        		   
        		  } catch (IOException e){
        		      setTitle(e.toString());
        		  }
            		
        	}
        	
        	
        });
    }
    
    
    
    public String str;
    public void getContact(){
    	
    	str="";
        //������е���ϵ��  
       Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);  
       //ѭ������  
       if (cur.moveToFirst()) {  
           int idColumn  = cur.getColumnIndex(ContactsContract.Contacts._ID);  
             
           int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);  
           do {  
               //�����ϵ�˵�ID��  
              String contactId = cur.getString(idColumn);  
              //�����ϵ������  
              String disPlayName = cur.getString(displayNameColumn);  
              str+=disPlayName;
              //�鿴����ϵ���ж��ٸ��绰���롣���û���ⷵ��ֵΪ0  
              int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));  
              if(phoneCount>0){  
                  //�����ϵ�˵ĵ绰����  
                  Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null, null);
                  int i=0;
                  String phoneNumber;
                  if(phones.moveToFirst()){  
                      do{  
                    	  i++;
                          phoneNumber= phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                          if(i==1)
                          str=str+","+phoneNumber;
                          System.out.println(phoneNumber);  
                      }while(phones.moveToNext());  
                  }  
                
              }  
              str+="\r\n";
              } while (cur.moveToNext());  
     
       }  
       }  
    private void addContacts(String name,String num) {
    	ContentValues values = new ContentValues();
    	Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
    	long rawContactId = ContentUris.parseId(rawContactUri);

    	values.clear();
    	values.put(Data.RAW_CONTACT_ID, rawContactId);
    	values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
    	values.put(StructuredName.GIVEN_NAME, name);
//    	values.put(StructuredName.FAMILY_NAME, "Mike");
    	

    	getContentResolver().insert(Data.CONTENT_URI, values);

    	values.clear();
    	values.put(Data.RAW_CONTACT_ID, rawContactId);
    	values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
    	values.put(Phone.NUMBER, num);
    	values.put(Phone.TYPE, Phone.TYPE_HOME);
//    	values.put(Email.DATA, "ligang.02@163.com");
//    	values.put(Email.TYPE, Email.TYPE_WORK);
    	getContentResolver().insert(Data.CONTENT_URI, values);
    	}
    
 
     
}
package cn.unas.app.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import a_vcard.android.syncml.pim.vcard.ContactStruct.ContactMethod;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import cn.unas.app.unas.data.ContactInfo;
import cn.unas.app.unas.data.ContactInfo.EmailInfo;
import cn.unas.app.unas.data.ContactInfo.PhoneInfo;

public class ContactUtil {
	
	public static final String CONTACTFILENAME="ContactTable.vcf";
	
	//get query cursor
	//@param projection: the specified columns array to get,set null to get all
	public static Cursor queryContact(Context context, String[] projection){
		//get contact cursor
		Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
		return cur;
	}
	
	
	//get contacts information
    public static List<ContactInfo> getContactInfo(Context context) {
    
    	List<ContactInfo> infoList = new ArrayList<ContactInfo>();

    	Cursor cur=queryContact(context,null);
    	
     	if (cur.moveToFirst()) 
     	{
     		do 
     		{
 				//get contact id
     			String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                //get contact name
                String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ContactInfo info = new ContactInfo(displayName);//initialize contact information

                //check contact phone number count 
                int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                //if has at least one phone number
                if (phoneCount > 0) 
                {
                    Cursor phonesCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

                    if (phonesCursor.moveToFirst())
                    {
                        List<PhoneInfo> phoneNumberList = new ArrayList<PhoneInfo>();
                        do 
                        {
                            //traversal phone number
                            String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //the coordinate phone number type eg. home cell work
                            int type = phonesCursor.getInt(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                            //initialize phone information
                            ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
                            phoneInfo.type = type;
                            phoneInfo.number = phoneNumber;

                            phoneNumberList.add(phoneInfo);
	                    } while (phonesCursor.moveToNext());
	                    //set contact phone information
	                    info.setPhoneList(phoneNumberList);
                    }
                    phonesCursor.close();
                }

                //get contact email
                Cursor emailCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id, null, null);

                if (emailCur.moveToFirst()) 
                {
                    List<EmailInfo> emailList = new ArrayList<EmailInfo>();
                    do 
                    {
                        //traversal all email
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
                        int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        //initialize contact email information
                        ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
                        emailInfo.type = type;    //set email type
                        emailInfo.email = email;    //set email

                        emailList.add(emailInfo);
                    } while (emailCur.moveToNext());

                    info.setEmailList(emailList);
                }
                emailCur.close();

                //Cursor postalCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + id, null, null);
                
                //Cursor photoCursor = context.getContentResolver().query(ContactsContract.DisplayPhoto.CONTENT_URI, null, ContactsContract.CommonDataKinds.Photo.CONTACT_ID + "=" + id, null, null);
                
                
                infoList.add(info);
            }while (cur.moveToNext());
        }
        cur.close();
        return infoList;
    }
    
    public static int getContactCount(Context context)
	{
		Cursor cursorContact=null;
		try
		{
			cursorContact = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
			return cursorContact.getCount();
		}
		finally
		{
			if(cursorContact!=null)
			{
				cursorContact.close();
			}
		}
	}
    
    public static boolean backupContacts(Context context, List<ContactInfo> infos, String path)
    {
   
    	try 
    	{    
    		//String path = Environment.getExternalStorageDirectory() + "/contacts.vcf";
    		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
    		VCardComposer composer = new VCardComposer();
                     
			for (ContactInfo info : infos)
			{
				ContactStruct contact = new ContactStruct();
                contact.name = info.getName();
                //get contact phone information, add to ContactStruct 
                List<ContactInfo.PhoneInfo> numberList = info.getPhoneList();
                for (ContactInfo.PhoneInfo phoneInfo : numberList)
                {
                	contact.addPhone(phoneInfo.type, phoneInfo.number, null, true);
                }
                //get contact email information add to ContactStruct 
                List<ContactInfo.EmailInfo> emailList = info.getEmailList();
                for (ContactInfo.EmailInfo emailInfo : emailList)
                {
                	contact.addContactmethod(Contacts.KIND_EMAIL,emailInfo.type, emailInfo.email, null, true);
                }
                String vcardString = composer.createVCard(contact,VCardComposer.VERSION_VCARD30_INT);
                writer.write(vcardString);
                writer.write("\n");
                         
                writer.flush();
          	}
			writer.close();
			return true;
                
       	} 
    	catch (UnsupportedEncodingException e) 
    	{
    		e.printStackTrace();
    		return false;
        }
    	catch (FileNotFoundException e) 
    	{
    		e.printStackTrace();
    		return false;
        } 
    	catch (VCardException e)
    	{
    		e.printStackTrace();
    		return false;
        }
    	catch (IOException e) 
    	{
    		e.printStackTrace();
    		return false;
     	}
               
    }
    
    //get contact information in vcard file
    public static List<ContactInfo> restoreContacts(String filePath) throws Exception 
    {
    	List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
       
    	VCardParser parse = new VCardParser();
    	VDataBuilder builder = new VDataBuilder();
    	//String filePath = Environment.getExternalStorageDirectory() + "/contacts.vcf";
       
    	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
       
    	String vcardString = "";
    	String line;
    	while((line = reader.readLine()) != null)
    	{
    		vcardString += line + "\n";
    	}
    	reader.close();
       
    	boolean parsed = parse.parse(vcardString, "UTF-8", builder);
       
    	if(!parsed)
    	{
    		throw new VCardException("Could not parse vCard file: "+ filePath);
    	}
       
    	List<VNode> pimContacts = builder.vNodeList;
       
    	for (VNode contact : pimContacts) 
    	{           
    		ContactStruct contactStruct=ContactStruct.constructContactFromVNode(contact, 1);
    		//get contact phone information in backup file
    		List<PhoneData> phoneDataList = contactStruct.phoneList;
    		List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
    		for(PhoneData phoneData : phoneDataList)
    		{
    			ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
    			phoneInfo.number=phoneData.data;
    			phoneInfo.type=phoneData.type;
    			phoneInfoList.add(phoneInfo);
    		}
           
    		//get contact email information in backup file
    		List<ContactMethod> emailList = contactStruct.contactmethodList;
    		List<ContactInfo.EmailInfo> emailInfoList = new ArrayList<ContactInfo.EmailInfo>();
    		//email information exists
    		if (null!=emailList)
    		{
    			for (ContactMethod contactMethod : emailList)
    			{
    				if (Contacts.KIND_EMAIL == contactMethod.kind)
    				{
    					ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
    					emailInfo.email = contactMethod.data;
    					emailInfo.type = contactMethod.type;
    					emailInfoList.add(emailInfo);
    				}
    			}
    		}
    		ContactInfo info = new ContactInfo(contactStruct.name).setPhoneList(phoneInfoList).setEmailList(emailInfoList);
    		contactInfoList.add(info);
    	}
    	return contactInfoList;
    }

   
    //store contacts into phone
    public static void addContacts(Context context, ContactInfo info)
    {
    	ContentValues values = new ContentValues();
    	//insert an empty value into RawContacts.CONTENT_URI first in order to get the returned rawContactId
    	Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
    	long rawContactId = ContentUris.parseId(rawContactUri);
       
    	//insert name into data table
    	values.clear();
    	values.put(Data.RAW_CONTACT_ID, rawContactId);
    	values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
    	values.put(StructuredName.GIVEN_NAME, info.getName());
    	context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
       
    	//get contact phone information
    	List<ContactInfo.PhoneInfo> phoneList = info.getPhoneList();
    	//input phone information
    	for (ContactInfo.PhoneInfo phoneInfo : phoneList)
    	{
    		values.clear();
    		values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
    		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
    		//set phone information
    		values.put(Phone.NUMBER, phoneInfo.number);
    		values.put(Phone.TYPE, phoneInfo.type);
    		//insert phone data into data table
    		context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    	}
       
    	//get contact email information
    	List<ContactInfo.EmailInfo> emailList = info.getEmailList();
       
    	//input email information
    	for (ContactInfo.EmailInfo email : emailList) 
    	{
    		values.clear();
    		values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
    		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
    		//set email information
    		values.put(Email.DATA, email.email);
    		values.put(Email.TYPE, email.type);
    		//insert email information into data table
    		context.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    	}
       
    }
}

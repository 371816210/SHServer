/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.inhuasoft.shserver.Screens;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.inhuasoft.shserver.CustomDialog;
import com.inhuasoft.shserver.Engine;
import com.inhuasoft.shserver.IMSDroid;
import com.inhuasoft.shserver.Main;
import com.inhuasoft.shserver.R;
import com.inhuasoft.shserver.Utils.MD5;
import com.inhuasoft.shserver.Utils.SipAdminUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class ScreenLogin extends Activity  implements OnClickListener {
	private static String TAG = ScreenLogin.class.getCanonicalName();
	
	private static final int MENU_EXIT = 0;
	private static final int MENU_SETTINGS = 1;
	
	
	private BroadcastReceiver mSipBroadCastRecv;
	
	Button btnSubmit;
	
	private static final int Admin_Login_Action = 0X01;
	private static final int Admin_Login_Fail = 0X02;
	private static final int Admin_Login_Success = 0X03;
	private static final int Sip_Add_User_Action = 0X04;
	private static final int Sip_Add_User_Fail = 0X05;
	private static final int Sip_Add_User_Success = 0X06;
	private static final int Sip_Device_Reg_Action = 0X07;
	
	public String  sip_UserName;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {

			case Admin_Login_Action:
				SipAdminLoginThread thread_admin_login = new SipAdminLoginThread();
				thread_admin_login.start();
				break;
			case Admin_Login_Fail:
				 int errorcode =  msg.arg1;
				 final AlertDialog dialog = CustomDialog.create(
						                                   ScreenLogin.this,
						                                  R.drawable.exit_48,
						                                 null,
						                                  " a error has occurred, error code is  "+ errorcode,
						                                 "Yes",
						                                 new DialogInterface.OnClickListener() {
						                                    @Override
						                                      public void onClick(DialogInterface dialog, int which) {
						                                     
						                                      }
						                                  }, null,null);
						                          dialog.show();
				break;
			case Admin_Login_Success:
				Message message = mHandler.obtainMessage(Sip_Add_User_Action);
				message.sendToTarget();
				break;
			case Sip_Add_User_Action:
				SipAddUserThread thread_add_user = new SipAddUserThread();
				thread_add_user.start();
				break;
			case Sip_Add_User_Fail:
				 int user_error_code =  msg.arg1;
				 final AlertDialog dialog1 = CustomDialog.create(
						                                   ScreenLogin.this,
						                                  R.drawable.exit_48,
						                                 null,
						                                  " a error has occurred, error code is  "+ user_error_code,
						                                 "Yes",
						                                 new DialogInterface.OnClickListener() {
						                                    @Override
						                                      public void onClick(DialogInterface dialog, int which) {
						                                     
						                                      }
						                                  }, null,null);
						                          dialog1.show();
				break;
			case Sip_Add_User_Success:
				Message device_reg_message = mHandler.obtainMessage(Sip_Add_User_Action);
				device_reg_message.sendToTarget();
				break;
			case Sip_Device_Reg_Action:
				break;

			}
		}
	};
	

	   public static String getDeviceNo()
	    {
	      return Build.SERIAL;
	    }
	   
	   
	   
	    public static String getValByTagName(Document doc, String tagName) {
			NodeList list = doc.getElementsByTagName(tagName);
			if (list.getLength() > 0) {
				   Node node = list.item(0);
				   Node valNode = node.getFirstChild();
				   if (valNode != null) {
				   String val = valNode.getNodeValue();
				   return val;
				  }
			 }
			 return null;   
		}
	
	class SipAddUserThread extends Thread {

		public void run() {
			String httpUrl = "http://sip.inhuasoft.cn/tools/users/user_management/user_management.php?action=add_verify&id=";
			HttpPost request = new HttpPost(httpUrl);
			HttpClient httpClient = new DefaultHttpClient();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			System.out.println("=================username:"+sip_UserName);
			params.add(new BasicNameValuePair("uname", sip_UserName));
			params.add(new BasicNameValuePair("email", sip_UserName+"@inhuasoft.cn"));
			params.add(new BasicNameValuePair("alias", sip_UserName));
			params.add(new BasicNameValuePair("domain", "115.28.9.71"));
			params.add(new BasicNameValuePair("alias_type", "dbaliases"));
			params.add(new BasicNameValuePair("passwd", sip_UserName));
			params.add(new BasicNameValuePair("confirm_passwd", sip_UserName));
			HttpResponse response;
			IMSDroid appCookie = ((IMSDroid) ScreenLogin.this.getApplication());
			((AbstractHttpClient) httpClient).setCookieStore(appCookie
					.getCookie());
			try {
				HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(entity);
				response = httpClient.execute(request);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String str = EntityUtils.toString(response.getEntity());
					String strmd5 = MD5.getMD5(str);
					if (str.contains("is already a valid user")) {
						//sip_add_user = true;
						Message message = mHandler.obtainMessage(Admin_Login_Success,200);
						message.sendToTarget();
						System.out.println("=================is already a valid user");
					}
					if (str.contains("New User added!")) {
						Message message = mHandler.obtainMessage(Admin_Login_Success,201);
						message.sendToTarget();
						System.out.println("New User added!");
						//sip_add_user = true;
					}
					//System.out.println(" login in add user");
				} else {
					System.out.println("add user fail ");
				    //	sip_add_user = false;
					Message message = mHandler.obtainMessage(Admin_Login_Fail,400);
					message.sendToTarget();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message message = mHandler.obtainMessage(Admin_Login_Fail,401);
				message.sendToTarget();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message message = mHandler.obtainMessage(Admin_Login_Fail,402);
				message.sendToTarget();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message message = mHandler.obtainMessage(Admin_Login_Fail,403);
				message.sendToTarget();
			}
		}
	}
	
	
	
	class SipAdminLoginThread extends Thread {

		public void run() {
			String login_ok = "8010210925-35110-444-10984-4122-322463124";
			String httpUrl = "http://sip.inhuasoft.cn/login.php";
			HttpPost request = new HttpPost(httpUrl);
			HttpClient httpClient = new DefaultHttpClient();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", "admin"));
			params.add(new BasicNameValuePair("password", "admin"));
			HttpResponse response;

			try {
				HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(entity);
				response = httpClient.execute(request);

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String str = EntityUtils.toString(response.getEntity());
					String strmd5 = MD5.getMD5(str);
					System.out.println("response:" + strmd5);
					if (strmd5.equals(login_ok)) {
						System.out.println("sip admin web login success");
						Message message = mHandler.obtainMessage(Admin_Login_Success);
						message.sendToTarget();
						CookieStore cookies = ((AbstractHttpClient) httpClient)
								.getCookieStore();
						IMSDroid appCookie = ((IMSDroid) ScreenLogin.this
								.getApplication());
						// ((AbstractHttpClient)
						// httpClient).setCookieStore(cookies);
						appCookie.setCookie(cookies);
						// Toast.makeText(getApplicationContext(),
						// " login success ", Toast.LENGTH_SHORT).show();
					} else {
						System.out.println("sip admin web login fail ");
						Message message = mHandler.obtainMessage(Admin_Login_Fail,501);
						message.sendToTarget();
						//sip_admin_login_flag = false;
						// Toast.makeText(getApplicationContext(),
						// " login fail ", Toast.LENGTH_SHORT).show();
					}
				} else {
					System.out.println("sip admin web reponse fail");
					Message message = mHandler.obtainMessage(Admin_Login_Fail,505);
					message.sendToTarget();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	class SipDeviceRegThread extends Thread {

		public void run() {
			String RequestUrl = "http://ota.inhuasoft.cn/SHS_WS/ShsService.asmx?op=DeviceRegist";
			URL url = null;
			try {
				url = new URL(RequestUrl);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			String envelope="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			  "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
			  "<soap12:Header>"+
		      "<MySoapHeader xmlns=\"http://tempuri.org/\">"+
		      "<UserName>SysAdmin</UserName>"+
		      "<PassWord>SysAdminSysAdmin</PassWord>"+
		      "</MySoapHeader>"+
		    "</soap12:Header>"+
		    "<soap12:Body>"+
		    "<DeviceRegist xmlns=\"http://tempuri.org/\">"+
		      "<DeviceNo>"+getDeviceNo()+"</DeviceNo>"+
		      "<SipAccount></SipAccount>"+
		      "<SipPwd></SipPwd>"+
		    "</DeviceRegist>"+
		    "</soap12:Body>"+
		    "</soap12:Envelope>";
			HttpURLConnection httpConnection=null;
			OutputStream output=null;
			InputStream input=null;
			try{			   
			    httpConnection = (HttpURLConnection)url.openConnection();
			    httpConnection.setRequestMethod("POST");
			    httpConnection.setRequestProperty( "Content-Length",String.valueOf( envelope.length() ) );
			    httpConnection.setRequestProperty("Content-Type","text/xml; charset=utf-8");
			    httpConnection.setDoOutput(true);
			    httpConnection.setDoInput(true);
			    output=httpConnection.getOutputStream();
			    output.write(envelope.getBytes());
			    output.flush();
			    input=httpConnection.getInputStream();
			    DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance(); 
			    DocumentBuilder builder = factory.newDocumentBuilder();   
			    Document dom = builder.parse(input);
			    String returncode = getValByTagName(dom,"DeviceRegistResult");//·µ»ØÂë
			    
			}catch(Exception ex)
			{
				Log.d(TAG, "-->getResponseString:catch"+ex.getMessage());
			}finally
			{
				try{
					output.close();
					input.close();
					httpConnection.disconnect();
				}catch(Exception e)
				{
					Log.d(TAG, "-->getResponseString:finally"+e.getMessage());
				}
			}
			
	}
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_box_login);
		btnSubmit = (Button)findViewById(R.id.btnsubmit);
		btnSubmit.setOnClickListener(this);
		sip_UserName = getDeviceNo();
	/*	mSipBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				
				// Registration Event
				if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
					NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
					if(args == null){
						Log.e(TAG, "Invalid event args");
						return;
					}
					switch(args.getEventType()){
						case REGISTRATION_NOK:
						case UNREGISTRATION_OK:
						case REGISTRATION_OK:
						case REGISTRATION_INPROGRESS:
						case UNREGISTRATION_INPROGRESS:
						case UNREGISTRATION_NOK:
						default:
							//((ScreenHomeAdapter)mGridView.getAdapter()).refresh();
							break;
					}
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
	    registerReceiver(mSipBroadCastRecv, intentFilter);*/
	}

	@Override
	protected void onDestroy() {
      /* if(mSipBroadCastRecv != null){
    	   unregisterReceiver(mSipBroadCastRecv);
    	   mSipBroadCastRecv = null;
       }
        */
       super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.btnsubmit)
		{
			//SipAdminUtils sipAdmin =  new SipAdminUtils(this);
		    //boolean flag = sipAdmin.Sip_Add_User(SipAdminUtils.getDeviceNo());
		    //System.out.println("======================flag:"+flag);
			//sipAdmin.Sip_Admin_Login();
			
			//Intent intent = new Intent();
			//intent.setClass(getApplicationContext(), ScreenMainAV.class);
			//startActivity(intent);
			
			
			Message message = mHandler.obtainMessage(Admin_Login_Action);
			message.sendToTarget();
		}
	}
	
//	@Override
//	public boolean hasMenu() {
//		return true;
//	}
//	
//	@Override
//	public boolean createOptionsMenu(Menu menu) {
//		menu.add(0, ScreenLogin.MENU_SETTINGS, 0, "Settings");
//		MenuItem itemExit = menu.add(0, ScreenLogin.MENU_EXIT, 0, "Exit");
//		
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch(item.getItemId()){
//			case ScreenLogin.MENU_EXIT:
//				((Main)getEngine().getMainActivity()).exit();
//				break;
//			case ScreenLogin.MENU_SETTINGS:
//				mScreenService.show(ScreenSettings.class);
//				break;
//		}
//		return true;
//	}
}


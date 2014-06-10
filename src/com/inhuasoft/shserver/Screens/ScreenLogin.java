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

import com.inhuasoft.shserver.CustomDialog;
import com.inhuasoft.shserver.Engine;
import com.inhuasoft.shserver.Main;
import com.inhuasoft.shserver.R;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_box_login);
		btnSubmit = (Button)findViewById(R.id.btnsubmit);
		btnSubmit.setOnClickListener(this);
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
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), ScreenMainAV.class);
			startActivity(intent);
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

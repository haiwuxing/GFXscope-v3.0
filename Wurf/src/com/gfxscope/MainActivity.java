package com.gfxscope;

import java.io.File;
import java.io.FileOutputStream;
import com.gfxscope.R;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
public class MainActivity extends ActionBarActivity implements TabListener {
	
// ������������ ������ IDE
private static final String LOG_TAG = "FlankLOGS";
// ��������� 
static Fragment frag_main;
static Fragment frag_settings;

private  int currentApiVersion;

@Override
public void onWindowFocusChanged(boolean hasFocus)
{
    super.onWindowFocusChanged(hasFocus);
    if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
    {
        getWindow().getDecorView().setSystemUiVisibility(
                 View.SYSTEM_UI_FLAG_LAYOUT_STABLE            
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
    }
}

//Storage Permissions
//private static final int REQUEST_EXTERNAL_STORAGE = 1;
//private static String[] PERMISSIONS_STORAGE = {
//     Manifest.permission.READ_EXTERNAL_STORAGE,
//     Manifest.permission.WRITE_EXTERNAL_STORAGE
//};
//public static void verifyStoragePermissions(Activity activity) {
//		
// // Check if we have write permission
// int permission = ActivityCompat.checkSelfPermission(activity,
//		 Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
// if (permission != PackageManager.PERMISSION_GRANTED) {
//     // We don't have permission so prompt the user
//     ActivityCompat.requestPermissions(
//             activity,
//             PERMISSIONS_STORAGE,
//             REQUEST_EXTERNAL_STORAGE
//     );
// }
//}


public static Boolean checkpermissions(Activity activity) {
    PackageManager mPackageManager = activity.getPackageManager();
    int hasPermStorage = mPackageManager.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
    if (hasPermStorage != PackageManager.PERMISSION_GRANTED) {
        return false;
    } else if (hasPermStorage == PackageManager.PERMISSION_GRANTED) {
        return true;
    }else
        return false;
}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  ��������������
				
		if (checkpermissions(this)==false) Toast.makeText(this,"������ ������� � ������, �������� ��������� ���������� ��� ����� ����������", Toast.LENGTH_LONG).show();;
		
		currentApiVersion = android.os.Build.VERSION.SDK_INT;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
	    final int flags = 
	    	View.SYSTEM_UI_FLAG_LAYOUT_STABLE	       
	        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	        | View.SYSTEM_UI_FLAG_FULLSCREEN
	        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	        ;
	    // This work only for android 4.4+
	    if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
	    {
	        getWindow().getDecorView().setSystemUiVisibility(flags);
	        // Code below is to handle presses of Volume up or Volume down.
	        // Without this, after pressing volume buttons, the navigation bar will
	        // show up and won't hide
	        final View decorView = getWindow().getDecorView();
	        decorView
	            .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
	            {
	                @Override
	                public void onSystemUiVisibilityChange(int visibility)
	                {
	                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
	                    {
	                        decorView.setSystemUiVisibility(flags);
	                    }
	                }
	            });
	    }
		
		Log.d(LOG_TAG, "[GFX ���������]");

		// ������������� ������
		android.support.v7.app.ActionBar navbar = getSupportActionBar();
		navbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		/* 1-� �������� "�����" */
		Tab tab = navbar.newTab();
		//tab.setIcon(R.drawable.ic_home);
		tab.setText("�������");
		tab.setTabListener(this);
		navbar.addTab(tab);
		/* 2-� �������� */
		tab = navbar.newTab();
		//tab.setIcon(R.drawable.ic_settings);
		tab.setText("���������");	
		tab.setTabListener(this);
		navbar.addTab(tab);
		Log.d(LOG_TAG, "���� �������");

		// ���� �������� ���� ����������� (�������� ����������), �������� ���������� ����� ��� ActionBar
        if(savedInstanceState != null) 
        {
            int index = savedInstanceState.getInt("TabIndex");
            getSupportActionBar().setSelectedNavigationItem(index);
        }
	}

	// ��������� ������ ���� (���� ������������ �� ������� �� ���������� ������ ����)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "������������");
		menu.add(0, 2, 0, "�����������");
		menu.add(0, 3, 0, "������� ����");	
		menu.add(0, 4, 0, "��������� � ����");			
		menu.add(0, 5, 0, "���������");
		//menu.add(0, 5, 0, "�����");
		menu.add(0, 6, 0, "�����");
		return super.onCreateOptionsMenu(menu);
	}

	// ���������� ������� ���� (�� ActionBar)
	// return true ��������, ��� ���� ����� ��������� MainActivity, return false - ��� ���� ����� ��������� ���������� FragmentClientActivity
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//Toast.makeText(this, Integer.toString(item.getItemId()), Toast.LENGTH_SHORT).show();
		switch (item.getItemId())
		{
			case 1:
				// ������������
				getSupportActionBar().setSelectedNavigationItem(0);
				return false;
			case 2:
				// �����������
				getSupportActionBar().setSelectedNavigationItem(0);
				return false;
			case 3:
				// ������� ����			
				return false;
			case 4:
				// ��������� ����		
				return false;	
				
			case 5:
				// ���������
				getSupportActionBar().setSelectedNavigationItem(1);
				return true;
//			case 5:			
//				// �����
//				DisplayMetrics displaymetrics = new DisplayMetrics();
//				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//				int screenWidth = displaymetrics.widthPixels;
//				int screenHeight = displaymetrics.heightPixels;
//				View v1 = getWindow().getDecorView().getRootView();
//				getBitmapFromView(v1,screenHeight,screenWidth);				
//				//Prt(v1);
//				return true;					
			case 6:
				// �����. ��������� ������ ����������
				finish();
				return true;
			default: return super.onOptionsItemSelected(item);
		}
	}

	public static Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();       
        
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.measure( View.MeasureSpec.makeMeasureSpec(totalWidth,View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(totalHeight,View.MeasureSpec.EXACTLY));
        view.layout(0,0,totalWidth,totalHeight);
        view.draw(canvas);
        
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "savedBitmap.png");
        
        try {
          FileOutputStream fos = null;
          try {
            fos = new FileOutputStream(file);
            returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
          } finally {
            if (fos != null) fos.close();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }        
        return returnedBitmap;
    }
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		//Log.d(LOG_TAG, "������ ��� ��������� ���: " + tab.getText());
	}
	String CurrentFragmentTag = "fragment_main";
	// ����� ������� ���������� � ���������� ��������
	public void LoadFragment(Fragment NewFragment, String NewFragmentTag) {
	    FragmentManager fm = getSupportFragmentManager();
	    Fragment currFragment  =  fm.findFragmentByTag(CurrentFragmentTag);
	    Fragment newFragment   =  fm.findFragmentByTag(NewFragmentTag);
	    FragmentTransaction ft =  fm.beginTransaction();
	    if(currFragment != null) {ft.hide(currFragment);}
	    if(newFragment == null) {
	    	// ������ ��������� ������� (����� ����� ������ ����������/������� ������ ��������)
	    	if (NewFragmentTag.equals(getString(R.string.fragment_main)))  	  {newFragment  = new FragmentMain();}//FragmentClientActivity FragmentMain();}
	    	else
	    	if (NewFragmentTag.equals(getString(R.string.fragment_settings))) {newFragment  = new FragmentSettings();}
	    	// ��������� �������� � �������� ����������
	        ft.add(R.id.FragmentContainer, newFragment, NewFragmentTag);
	    } else {
		    //if (NewFragmentTag.equals(getString(R.string.fragment_main))) 
		    //{
				//((FragmentTop10) newFragment).refresh();
		    //}	   	
		    // ��������� ��������� ���������, ������ ������ ������� � ���������� ������
		    // �������� ��� ���������� �������, ����� NewFragmentTag == CurrentFragmentTag == "fragment_main"
		    if (NewFragmentTag != CurrentFragmentTag)
		    {
			    ft.show(newFragment);
		    }
	    }
	    ft.commit();
	    this.CurrentFragmentTag = NewFragmentTag;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// ������������� ������� ���� ���������� (�� ���������)
		// ��� �������� 3.xx ������� ������ ������ ����, ����� ���� (�� ���������� ������) �� ��������� ����������
		if(android.os.Build.VERSION.RELEASE.startsWith("3")) {setTheme(R.style.AppCompatLight);}
		else
		{
			// ��� �������� 2.xx ������� ������ ������ ���� (Translucent), ����� ���� (�� ���������� ������) �� ��������� ����������
			if(android.os.Build.VERSION.RELEASE.startsWith("2")) {setTheme(R.style.Translucent);}
		}
		// ������ ������� ����
		setTheme(R.style.AppTheme);
		// 0,1,2 - ��������� ��������������� ��������� �� ������� ����� � navbar
		switch (getSupportActionBar().getSelectedNavigationIndex())
		{
			case 0: LoadFragment(frag_main, getString(R.string.fragment_main));
				break;			
			case 1:	LoadFragment(frag_settings, getString(R.string.fragment_settings));
				break;
			default: Toast.makeText(this, getString(R.string.You_did_not_select_any_menu_item), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		//Log.d(LOG_TAG, "��� ������ �� ������: " + tab.getText());
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  // ��������� ����� ���������� ���� � ActionBar
	  int index = getSupportActionBar().getSelectedNavigationIndex();
	  savedInstanceState.putInt("TabIndex", index);
	}

}
package com.gfxscope;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import com.utils.Utils;

public class MainActivity extends BaseActivity {
    public static final @NonNull String TAG = Utils.getTag(MainActivity.class);

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
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

    public static Boolean checkpermissions(Activity activity) {
        PackageManager mPackageManager = activity.getPackageManager();
        int hasPermStorage = mPackageManager.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity.getPackageName());
        return hasPermStorage == PackageManager.PERMISSION_GRANTED;
    }

	@Override
	protected void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  горизонтальная
				
		if (!checkpermissions(this)) {
		    Toast.makeText(this,"Ошибка доступа к памяти, проверте настройки разрешений для этого приложения", Toast.LENGTH_LONG).show();
        }

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
	    // This work only for android 4.4+
	    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

	        getWindow().getDecorView().setSystemUiVisibility(flags);
	        // Code below is to handle presses of Volume up or Volume down.
	        // Without this, after pressing volume buttons, the navigation bar will
	        // show up and won't hide
	        final View decorView = getWindow().getDecorView();
	        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
	    }

	    final FragmentManager fm = getSupportFragmentManager();
	    if (fm.findFragmentByTag(FragmentMain.TAG) == null) {
            fm.beginTransaction()
                    .replace(R.id.FragmentContainer, new FragmentMain(), FragmentMain.TAG)
                    .commitAllowingStateLoss();
        }
	}

	public void showSettings() {
        final FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(FragmentSettings.TAG) == null) {
            fm.beginTransaction()
                    .add(R.id.FragmentContainer, new FragmentSettings(), FragmentSettings.TAG)
                    .commitAllowingStateLoss();
            //TODO .addToBackStack()
        }
    }

    @Override
    public void onDestroy() {
        FragmentMain.osc_set_param("POWER_OFF 1");

        super.onDestroy();
    }
}
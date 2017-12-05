package com.gfxscope;

import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private boolean mSupportDestroyed;

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mSupportDestroyed = true;
    }

    public boolean isSupportDestroyed() {
        return this.mSupportDestroyed;
    }

}

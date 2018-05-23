package com.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Stormeg on 27.12.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class TcpClientForegroundService extends IntentService {

    public TcpClientForegroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

}

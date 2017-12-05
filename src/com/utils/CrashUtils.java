package com.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CrashUtils {

    private CrashUtils () {}

    public static void reportAndPrint(final @Nullable Throwable t) {
        if (t == null) {
            return;
        }

        t.printStackTrace();

        //TODO add Firebase and/or Crashlytics

    }
}

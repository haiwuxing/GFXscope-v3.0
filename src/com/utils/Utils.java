package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;
import com.gfxscope.BaseActivity;
import java.io.Closeable;
import java.util.Collection;

public final class Utils {
    public static final @NonNull String EMPTY_STRING = "";

    private Utils() {}

    public static @NonNull String getTag(final @NonNull Class c) {
        final int TAG_MAX_LEN = 23;

        final String tagFullName = c.getSimpleName();
        return tagFullName.length() > TAG_MAX_LEN ? tagFullName.substring(0, TAG_MAX_LEN) : tagFullName;
    }

    /**
     * API level 16, Android 4.1.2
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API level 17, Android 4.2.2
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * API level 18, Android 4.3
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * API level 19, Android 4.4.2
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API level 21, Android 5.0
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API level 23, Android 6.0
     */
    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * API level 24, Android 7.0
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * API level 26, Android 8.0
     */
    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static boolean isEmpty(final @Nullable Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri);
    }

    public static boolean isEmpty(final @Nullable Bundle o) {
        return o == null || o.isEmpty();
    }

    public static boolean isEmpty(final @Nullable CharSequence title) {
        return TextUtils.isEmpty(title);
    }

    public static boolean isEmpty(final @Nullable Collection o) {
        return o == null || o.isEmpty();
    }

    public static boolean isEmpty(final @Nullable Object[] o) {
        return o == null || o.length <= 0;
    }

    public static boolean isEmpty(final @Nullable int[] ints) {
        return ints == null || ints.length <= 0;
    }


    public static void closeSilently(final @Nullable Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (final Throwable t) {
                CrashUtils.reportAndPrint(t);
            }
        }
    }

    // old version of Cursor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable Cursor c) {
        if (c != null) {
            try {
                c.close();
            } catch (final Throwable t) {
                CrashUtils.reportAndPrint(t);
            }
        }
    }

    // old version of ParcelFileDescriptor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable ParcelFileDescriptor pfd) {
        if (pfd != null) {
            try {
                pfd.close();
            } catch (final Throwable t) {
                CrashUtils.reportAndPrint(t);
            }
        }
    }

    // old version of AssetFileDescriptor class does not implemented Closeable interface
    public static void closeSilently(final @Nullable AssetFileDescriptor afd) {
        if (afd != null) {
            try {
                afd.close();
            } catch (final Throwable t) {
                CrashUtils.reportAndPrint(t);
            }
        }
    }

    public static boolean equals(final @Nullable String s1, final @Nullable String s2) {
        //noinspection StringEquality
        return s1 == s2 || (s1 != null && s1.equals(s2));
    }

    public static boolean isDeadActivity(final @Nullable Activity activity) {
        return activity == null || activity.isFinishing() || (Utils.hasJellyBeanMR1() ? activity.isDestroyed()
                : activity instanceof BaseActivity && ((BaseActivity) activity).isSupportDestroyed());
    }

    public static boolean isDeadFragment(final @Nullable Fragment fragment) {
        return fragment == null || !fragment.isAdded() || isDeadActivity(fragment.getActivity());
    }

    public static boolean isDead(final @Nullable Context context) {
        if (context == null) {
            return true;
        } else if (context instanceof Activity) {
            return isDeadActivity((Activity) context);
        } else if (context instanceof ContextWrapper) {
            return isDead(((ContextWrapper) context).getBaseContext());
        }

        return false;
    }

    public static boolean isInternetConnectionAvailable(final @NonNull Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        } else {
            CrashUtils.reportAndPrint(new IllegalStateException("getSystemService(Context.CONNECTIVITY_SERVICE) = null!"));
            Toast.makeText(context, "The ConnectivityManager is unavailable!", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    public static boolean isMainThread() {
        return hasMarshmallow() ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}

package com.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.gfxscope.R;
import com.utils.Utils;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public enum  TGraficType implements Parcelable {
    AVERAGE, MINMAX;

    public static final @NonNull String TAG = Utils.getTag(TGraficType.class);
    public static final @NonNull String PREFS_NAME = TAG;
    public static final @NonNull TGraficType DEFAULT = TGraficType.AVERAGE;

    public @NonNull String getName(final @NonNull Context context) {
        return getName(context, this);
    }

    public static @NonNull String getName(final @NonNull Context context, final @NonNull TGraficType type) {
        return context.getResources().getStringArray(R.array.tgrafic_type)[type.ordinal()];
    }

    public void writeToPrefs(final @NonNull SharedPreferences.Editor editor) {
        editor.putInt(PREFS_NAME, ordinal());
    }

    public static @Nullable TGraficType readFromPrefs(final @NonNull SharedPreferences prefs) {
        if (!prefs.contains(PREFS_NAME)) {
            return null;
        }

        final int ordinal = prefs.getInt(PREFS_NAME, -1);
        return ordinal >=0 && ordinal < TGraficType.values().length ? TGraficType.values()[ordinal] : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeInt(ordinal());
    }

    public static final Parcelable.ClassLoaderCreator<TGraficType> CREATOR = new Parcelable.ClassLoaderCreator<TGraficType>() {
        public TGraficType createFromParcel(final @NonNull Parcel in, final @Nullable ClassLoader loader) {
            return TGraficType.values()[in.readInt()];
        }

        public TGraficType createFromParcel(final @NonNull Parcel in) {
            return createFromParcel(in, null);
        }

        public TGraficType[] newArray(final int size) {
            return new TGraficType[size];
        }
    };
}

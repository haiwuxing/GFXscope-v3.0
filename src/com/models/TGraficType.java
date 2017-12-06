package com.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.gfxscope.R;

public enum  TGraficType implements Parcelable {
    AVERAGE, MINMAX;

    public static @NonNull
    String getNameResId(final @NonNull Context context, final @NonNull TGraficType type) {
        return context.getResources().getStringArray(R.array.tgrafic_type)[type.ordinal()];
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

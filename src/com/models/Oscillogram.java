package com.models;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import com.utils.Constants;
import java.io.File;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class Oscillogram {

    protected Oscillogram() { }

    public abstract void put(final @NonNull byte[] buff);

    public abstract int getValue(final @Size(min=0) int index);

    public abstract void increasePosition(final @Size(min=1) int value);

    public boolean checkStartAndIncreasePosition(final @Size(min=0) int index) {
        /*
        if ((ADC_Buff[bufpoz] == (byte)'o') &&
        (ADC_Buff[bufpoz + 1] == (byte)'s') &&
        (ADC_Buff[bufpoz + 2] == (byte)'c') &&
        (ADC_Buff[bufpoz + 3] == (byte)' ') &&
        (ADC_Buff[bufpoz + 4] == (byte)'v') &&
        (ADC_Buff[bufpoz + 5] == (byte)'3')) {
            bufpoz = bufpoz + SIZE_BUF_SETTINGS;
        }
        */

        if (startWith(Constants.PROTOCOL_VERSION, index)) {
            this.increasePosition(Constants.PROTOCOL_VERSION.length);
            return true;
        }

        return false;
    }

    public abstract boolean startWith(final @NonNull byte[] data, final @Size(min=0) int offset);

    /**
     * @return message with error or file path and size
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public abstract @NonNull String saveToFile(final @NonNull File directoryFile, final @NonNull String fileName);

}

package com.models;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import com.utils.CrashUtils;
import com.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class OscillogramByteArray extends Oscillogram {
    private final @NonNull byte mBuff[];
    private int mRxCount = 0;
    private int mPosition = 0; // old name: bufpoz

    public OscillogramByteArray(final @Size(min=0) int size) {
        super();

        this.mBuff = new byte[size];
    }

    public void put(final @NonNull byte[] buff) {
        for (final byte b : buff) {
            if (this.mRxCount < this.mBuff.length) {
                this.mBuff[this.mRxCount] = b;
                this.mRxCount++;
            } else {
                //noinspection StatementWithEmptyBody
                while (dont_use_bufpoz == 1) { //TODO wait for modification permission
                    // empty
                }

                this.mRxCount = 0;
                mPosition = 0;
            }
        }
    }

    public int get(final @Size(min=0) int byteIndex) {
        return (int) mBuff[byteIndex];
    }

    public void increasePosition(final @Size(min=1) int value) {
        this.mPosition += value;
    }

    public boolean startWith(final @NonNull byte[] data, final @Size(min=0) int offset) {
        if (offset + data.length > this.mBuff.length) {
            return false;
        }

        for (int i=0; i<data.length; i++) {
            if (this.mBuff[offset+i] != data[i]) { //TODO disable mBuff modification
                return false;
            }
        }

        return true;
    }

    /**
     * @return message with error or file path and size
     */
    @Override
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public @NonNull String saveToFile(final @NonNull File directoryFile, final @NonNull String fileName) {
        if (this.mRxCount <= 0) {
            return "rxCount <= 0";
        }

        if (!directoryFile.isDirectory() && !directoryFile.mkdirs()) {
            return "The directory can not be created.\n" + directoryFile.getAbsolutePath();
        }

        final String outputFilePath = directoryFile.getAbsoluteFile() + File.separator + fileName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFilePath);
            fos.write(this.mBuff, 0, this.mRxCount);  //TODO disable mBuff modification
        } catch (Throwable t) {
            CrashUtils.reportAndPrint(t);
            return t.getMessage();
        } finally {
            Utils.closeSilently(fos);
        }

        return "Saved " + (this.mRxCount < (1024*1024) ? (this.mRxCount/1024) + "KB" : (this.mRxCount/(1024*1024)) + "MB" ) + "to " + outputFilePath;
    }

}

package com.models;

import android.support.annotation.NonNull;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class OscillogramByteBuffer /*extends Oscillogram*/ { //TODO
    private final @NonNull ByteBuffer mBuff;

    protected OscillogramByteBuffer(final ByteBuffer byteBuffer) {
        super();

        byteBuffer.order(ByteOrder.nativeOrder());
        this.mBuff = byteBuffer;
    }

}

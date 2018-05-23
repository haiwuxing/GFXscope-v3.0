package com.models;

import android.support.annotation.Size;
import java.nio.ByteBuffer;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class OscillogramHeapByteBuffer extends OscillogramByteBuffer {

    public OscillogramHeapByteBuffer(final @Size(min=1) int size) {
        super(ByteBuffer.allocate(size));
    }
}

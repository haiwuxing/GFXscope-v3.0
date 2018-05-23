package com.models;

import android.support.annotation.Size;

/**
 * Created by Stormeg on 15.01.2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class OscillogramIntArray /*extends Oscillogram*/ { //TODO

    private final int mBuff[];

    public OscillogramIntArray(final @Size(min=1) int size) {
        super();

        this.mBuff = new int[size];
    }
}

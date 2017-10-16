package com.rak;

import com.google.android.things.pio.UartDevice;

/**
 * Created by Naresh Krish on 9/17/2017.
 * Base class for all Wisnode boards (launched and planned in future)
 */

public abstract class Wisnode implements AutoCloseable {
    protected volatile boolean mConnected = false;

    public boolean isConnected(){
        return mConnected;
    }

    public abstract UartDevice getUARTDevice();
}

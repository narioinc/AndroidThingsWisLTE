package com.rak;

import com.google.android.things.pio.UartDevice;

/**
 * Created by Naresh Krish on 9/17/2017.
 */

public interface UARTResponseListener {
    public void onSuccess(int requestCode, byte[] response, UartDevice device);
    public void onFailure(int requestCode, int errorCode, UartDevice device);
}

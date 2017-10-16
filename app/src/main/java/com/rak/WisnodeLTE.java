package com.rak;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Naresh Krish on 9/17/2017.
 * Concrete impleentation for the WisNode LoRa node board by RAK Wireless
 */

public class WisnodeLTE extends Wisnode{

    private final String TAG = "WisnodeLTE";
    protected UartDevice mDevice;

    /***
     * GENERAL COMMANDS REQUEST CODES
     */
    public static final int DPI_REQUEST = 1;
    public static final int MANUFACTURER_REQUEST = 2;
    public static final int TA_MODEL_ID_REQUEST = 3;
    public static final int TA_REV_ID_REQUEST = 4;
    public static final int MODEL_ID_REQUEST = 5;
    public static final int IMEI_REQUEST = 6;
    public static final int PROD_SERIAL_REQUEST = 7;
    public static final int DEFAULT_SETTINGS_REVERT_REQUEST = 8;
    public static final int GET_CURRENT_SETTINGS_REQUEST = 9;
    public static final int STORE_CURRENT_SETTINGS_REQUEST = 10;
    public static final int TA_RESPONSE_FORMAT_REQUEST = 11;
    public static final int ECHO_MODE_REQUEST = 12;
    public static final int TERM_CHAR_REQUEST = 13;
    public static final int RESPONSE_FORMAT_CHAR_REQUEST = 14;
    public static final int CMDLINE_EDIT_CHAR_REQUEST = 15;
    public static final int ERROR_MESSAGE_FORMAT_REQUEST = 16;
    public static final int SELECT_TE_CHAR_REQUEST = 17;
    public static final int CONFIGURE_URC_INDICATION_REQUEST = 18;


    /**
     * Constrcutor that takes the portname for the Uart on which the wisnode board
     * is located
     * @param wisnodePortName - name of the wisnode port
     */
    public WisnodeLTE(String wisnodePortName, int baudrate){
        if(wisnodePortName != null & wisnodePortName.length() > 0){
            Log.d(TAG, "Wisnode module requested on port :: " + wisnodePortName + " with baudarate :: " + baudrate );
            try{
                mDevice = new PeripheralManagerService().openUartDevice(wisnodePortName);

                // Some basic UART configuration.
                // Make sure to configure the other device the exact same way.
                mDevice.setBaudrate(baudrate);
                mDevice.setDataSize(8);
                mDevice.setParity(UartDevice.PARITY_NONE);
                mDevice.setStopBits(1);
                mConnected = true;

            }catch (IOException exp){
                Log.e(TAG, "There was an issue opening the UART port with name :: " + wisnodePortName);
            }
        }

    }

    @Override
    public void close() throws IOException {
        if (mDevice != null) {
            try {
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    @Override
    public UartDevice getUARTDevice() {
        return mDevice;
    }
}

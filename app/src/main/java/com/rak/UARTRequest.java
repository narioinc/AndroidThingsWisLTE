package com.rak;

import android.util.Log;

import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Naresh Krish on 9/17/2017.
 */

public class UARTRequest {

    private UartDevice mDevice;
    private UARTResponseListener mListener;
    private int mRequestCode;

    private final String TAG = "UARTRequest";

    public UARTRequest(Wisnode device){
        mDevice = device.getUARTDevice();
        try {
            mDevice.registerUartDeviceCallback(mCallback);
        }catch (IOException exp){
            Log.e(TAG, "Error while attaching a callback to the UART for");
        }
    };

    public void execute(int requestCode, String request, UARTResponseListener listener) throws IOException{
        if(mDevice !=null && request!=null){
            sendMessageToUart(mDevice, request);
            mListener = listener;
            mRequestCode = requestCode;
        }else{
            Log.i(TAG, "Either the device or request string is empty or incorrect");
        }
    }

    private void sendMessageToUart(UartDevice device, String message) throws IOException {
        if(device != null && message.length() >0){
            byte[] buffer = message.getBytes();
            int count = device.write(buffer, buffer.length);
            Log.d(TAG, "Wrote " + count + " bytes to peripheral");
        }
    }

    /**
     * Callback invoked when new data arrives in the UART buffer.
     */
    private UartDeviceCallback mCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                byte[] response = readUartBuffer();
                Log.d(TAG, "Response from uart :: " + new String(response));
                if(mCallback != null){
                    if(isSuccessResponse(response)) {
                        mListener.onSuccess(mRequestCode, response, uart);
                    }else{
                        int eCode = getErrorCode(response);
                        if(eCode<0) {
                            mListener.onFailure(mRequestCode, eCode, uart);
                        }
                    }
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to read UART data", e);
            }finally {
                uart.unregisterUartDeviceCallback(this);
            }

            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, "Error receiving incoming data: " + error);
        }
    };

    private boolean isSuccessResponse(byte[] response){
        String responseStr = new String(response);
        return !responseStr.contains("ERROR");

    }

    private int getErrorCode(byte[] response) {
        int index = "ERROR".length()-1;
        int errorCode =  0;
        try{
            Integer.parseInt(new String(response).substring(index, index+2));
        }catch(NumberFormatException exp){
            Log.e(TAG, "Error while parsing the serial data :: " + new String(response));
        }
        return errorCode;
    }

    /**
     * Drain the current contents of the UART buffer.
     */
    private static final int CHUNK_SIZE = 512;
    private byte[] readUartBuffer() throws IOException {
        byte[] buffer = new byte[CHUNK_SIZE];
        byte[] processedBuffer = null;
        int count;
        while ((count = mDevice.read(buffer, buffer.length)) > 0) {
            Log.d(TAG, "readUartBuffer got :: " + new String(buffer));
            processedBuffer = processBuffer(buffer, count);
        }
        return processedBuffer;
    }

    private byte[] processBuffer(byte[] buffer, int length) {
        ByteBuffer mMessageBuffer = ByteBuffer.allocate(length);
        for (int i = 0; i < length; i++) {
            if(buffer[i] != 0) {
                //Insert all other characters into the buffer
                mMessageBuffer.put(buffer[i]);
            }else{
                break;
            }
        }
        Log.i(TAG, "processed buffer = " + new String(mMessageBuffer.array()));
        return mMessageBuffer.array();
    }

}

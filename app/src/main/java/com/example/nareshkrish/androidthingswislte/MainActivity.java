package com.example.nareshkrish.androidthingswislte;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.UartDevice;
import com.rak.UARTRequest;
import com.rak.UARTResponseListener;
import com.rak.Wisnode;
import com.rak.WisnodeLTE;

import java.io.IOException;

public class MainActivity extends Activity implements UARTResponseListener {

    private static final String TAG = "MainActivity";
    private WisnodeLTE mWisnodeLTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting WisLTE activity");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mWisnodeLTE = new WisnodeLTE("USB1-1.4:1.0", 115200);
        UARTRequest request = new UARTRequest(mWisnodeLTE);
        try {
            request.execute(WisnodeLTE.VERSION_REQUEST, "at+version\r\n", this);
        }catch (IOException exp){
            Log.e(TAG, "Error while executing request.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeUart(mWisnodeLTE);
        }catch (IOException exp){
            Log.e(TAG, "Unable to close UART device. " + exp.getLocalizedMessage());
        }
    }

    private void closeUart(Wisnode device) throws IOException {
        if (device != null) {
            try {
                device.close();
            }catch (Exception exp){
                Log.e(TAG, "Error while closing the UART connection ::");
            }finally {
                device = null;
            }
        }
    }

    @Override
    public void onSuccess(int requestCode, byte[] response, UartDevice device) {
        Log.i(TAG, "Response for request code :: " + requestCode + " is :: " + new String(response));
    }

    @Override
    public void onFailure(int requestCode, int errorCode, UartDevice device) {
        Log.e(TAG, "Error Response for request code :: " + requestCode + " is :: " + errorCode);
    }
}

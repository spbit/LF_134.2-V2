package com.example.lfdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortTool;

public abstract class LFBaseActivity extends Activity {

    public static final String PATH = "/dev/ttyS3";
    public static final int BAUTRATE = 9600;

    protected SerialPortTool serialPortTool;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("ERROR");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }

    public boolean open() {
        boolean bret = false;
        if (serialPortTool == null) {
            serialPortTool = new SerialPortTool();
        }

        try {
            mSerialPort = serialPortTool.getSerialPort(PATH, BAUTRATE);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            if (mReadThread == null) {
                mReadThread = new ReadThread();
                mReadThread.start();
            }
            bret = true;
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        } catch (Exception e) {
            DisplayError(R.string.error_unknown);
        }

        return bret;
    }

    public void close() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }
        if (serialPortTool != null) {
            serialPortTool.closeSerialPort();
        }
        mSerialPort = null;
    }
}

package com.example.lfdemo;

import java.math.BigInteger;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lfdemo.utils.PowerUtil;

public class LFActivity extends LFBaseActivity implements View.OnClickListener {

    private Button btnOpen, btnClose;
    private Button btPowerOn, btPowerOff;
    private EditText mReception;
    private int soundid;
    private SoundPool soundpool = null;
    private String TAG = "LFActivity";

    byte[] id = new byte[64];
    int len = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ulf_activity_main);

        initView();
    }

    public void initView() {
        mReception = (EditText) findViewById(R.id.EditTextReception);
        btnOpen = (Button) this.findViewById(R.id.btnOpen);
        btnClose = (Button) this.findViewById(R.id.btnClose);
        btPowerOn = (Button) this.findViewById(R.id.btnPowerOn);
        btPowerOff = (Button) this.findViewById(R.id.btnPowerOff);

        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btPowerOn.setOnClickListener(this);
        btPowerOff.setOnClickListener(this);

        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100);
        soundid = soundpool.load(this, R.raw.rfid_beep, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpen:
                if (this.open()) {
                    btnOpen.setEnabled(false);
                    btnClose.setEnabled(true);
                }
                break;
            case R.id.btnClose:
                this.close();
                btnOpen.setEnabled(true);
                btnClose.setEnabled(false);
                break;
            case R.id.btnPowerOn:
                PowerUtil.power("1");
                btPowerOn.setEnabled(false);
                btPowerOff.setEnabled(true);
                break;
            case R.id.btnPowerOff:
                PowerUtil.power("0");
                btPowerOn.setEnabled(true);
                btPowerOff.setEnabled(false);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        // TODO Auto-generated method stub
        runOnUiThread(new Runnable() {

            public void run() {
                if (mReception != null) {
                    if (size > 0) {
                        if (buffer[0] == 36) {
                            len = 0;
                            System.arraycopy(buffer, 0, id, 0, size);
                            len += size;
                            return;
                        }
                        if (len < 20) {
                            System.arraycopy(buffer, 0, id, len, size);
                            len += size;
                        }
                        if ((len >= 20) && (id[19] == 35)) {
                            len = 0;
                            byte[] temp = new byte[15];
                            System.arraycopy(id, 2, temp, 0, 15);
                            try {
                                String s = new String(temp, "ascii");
                                soundpool.play(soundid, 1, 1, 0, 0, 1);
                                mReception.setText("");
                                mReception.append(s);
                            } catch (java.io.UnsupportedEncodingException e) {
                            }
                        }
                    }
                }
            }
        });
    }


    public String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }

    public void showResult(byte[] buff) {

        String temStr = bytesToHexString(buff);
        soundpool.play(soundid, 1, 1, 0, 0, 1);
        mReception.setText("");
        mReception.append(temStr);
    }

    public static byte getXor(byte[] buff, int length) {

        byte temp = buff[0];

        for (int i = 1; i < length; i++) {
            temp ^= buff[i];
        }

        return temp;
    }


    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);
    }


}

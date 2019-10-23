package com.example.lfdemo

import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.lfdemo.utils.PowerUtil
import java.nio.charset.Charset

class LFActivity : LFBaseActivity() {

    private lateinit var btnOpen: Button
    private lateinit var btnClose: Button
    private lateinit var btPowerOn: Button
    private lateinit var btPowerOff: Button
    private lateinit var mReception: EditText
    private var soundid: Int = 0
    private lateinit var soundpool: SoundPool
    private var id = ByteArray(64)
    private var len = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ulf_activity_main)

        initView()
    }

    private fun initView() {
        mReception = findViewById(R.id.EditTextReception)
        btnOpen = findViewById(R.id.btnOpen)
        btnClose = findViewById(R.id.btnClose)
        btPowerOn = findViewById(R.id.btnPowerOn)
        btPowerOff = findViewById(R.id.btnPowerOff)

        btnOpen.setOnClickListener {
            if (this.open()) {
                btnOpen.isEnabled = false
                btnClose.isEnabled = true
            }
        }
        btnClose.setOnClickListener {
            this.close()
            btnOpen.isEnabled = true
            btnClose.isEnabled = false
        }
        btPowerOn.setOnClickListener {
            PowerUtil.power("1")
            btPowerOn.isEnabled = false
            btPowerOff.isEnabled = true
        }
        btPowerOff.setOnClickListener {
            PowerUtil.power("0")
            btPowerOn.isEnabled = true
            btPowerOff.isEnabled = false
        }

        soundpool = SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100)
        soundid = soundpool.load(this, R.raw.rfid_beep, 1)
    }

    override fun onDataReceived(buffer: ByteArray, size: Int) {
        runOnUiThread(Runnable {
            if (size > 0) {
                if (buffer[0].toInt() == 36) {
                    len = 0
                    System.arraycopy(buffer, 0, id, 0, size)
                    len += size
                    return@Runnable
                }
                if (len < 20) {
                    System.arraycopy(buffer, 0, id, len, size)
                    len += size
                }
                if (len >= 20 && id[19].toInt() == 35) {
                    len = 0
                    val temp = ByteArray(15)
                    System.arraycopy(id, 2, temp, 0, 15)
                    try {
                        val s = String(temp, Charset.forName("ascii"))
                        soundpool.play(soundid, 1f, 1f, 0, 0, 1f)
                        mReception.setText("")
                        mReception.append(s)
                    } catch (e: java.io.UnsupportedEncodingException) {
                    }
                }
            }
        })
    }
}

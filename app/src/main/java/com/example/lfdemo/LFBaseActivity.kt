package com.example.lfdemo

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android_serialport_api.SerialPort
import android_serialport_api.SerialPortTool
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidParameterException

abstract class LFBaseActivity : AppCompatActivity() {

    private val serialPortTool = SerialPortTool()
    private lateinit var mSerialPort: SerialPort
    private lateinit var mOutputStream: OutputStream
    private lateinit var mInputStream: InputStream
    private var mReadThread: ReadThread? = null

    companion object {
        const val PATH = "/dev/ttyS3"
        const val BITRATE = 9600
    }

    private inner class ReadThread : Thread() {
        override fun run() {
            super.run()
            while (!isInterrupted) {
                val size: Int
                try {
                    val buffer = ByteArray(64)
                    if (!::mInputStream.isInitialized) {
                        return
                    }
                    size = mInputStream.read(buffer)
                    if (size > 0) {
                        onDataReceived(buffer, size)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }

            }
        }
    }

    private fun displayError(resourceId: Int) {
        val b = AlertDialog.Builder(this)
        b.setTitle("ERROR")
        b.setMessage(resourceId)
        b.setPositiveButton("OK") { _, _ -> finish() }
        b.show()
    }

    protected abstract fun onDataReceived(buffer: ByteArray, size: Int)

    override fun onDestroy() {
        close()
        super.onDestroy()
    }

    fun open(): Boolean {
        var bret = false
        try {
            mSerialPort = serialPortTool.getSerialPort(PATH, BITRATE)
            mOutputStream = mSerialPort.outputStream
            mInputStream = mSerialPort.inputStream

            /* Create a receiving thread */
            if (mReadThread == null) {
                mReadThread = ReadThread()
                mReadThread?.start()
            }
            bret = true
        } catch (e: SecurityException) {
            displayError(R.string.error_security)
        } catch (e: IOException) {
            displayError(R.string.error_unknown)
        } catch (e: InvalidParameterException) {
            displayError(R.string.error_configuration)
        } catch (e: Exception) {
            displayError(R.string.error_unknown)
        }
        return bret
    }

    fun close() {
        if (mReadThread != null) {
            mReadThread?.interrupt()
            mReadThread = null
        }
        serialPortTool.closeSerialPort()
    }
}

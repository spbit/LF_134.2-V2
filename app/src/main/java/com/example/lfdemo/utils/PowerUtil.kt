package com.example.lfdemo.utils

import android.util.Log
import java.io.File
import java.io.FileWriter

object PowerUtil {

    private const val TAG = "PowerUtil"
    private const val SAM = "/proc/gpiocontrol/set_sam"
    private const val UHF = "/proc/gpiocontrol/set_uhf"

    fun power(id: String) {
        power1(id)
        power2(id)
    }

    private fun power1(id: String) {
        try {
            val file = File(SAM)
            Log.d(TAG, "power: " + file.path)
            val localFileWriterOn = FileWriter(File(SAM))
            localFileWriterOn.write(id)
            localFileWriterOn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun power2(id: String) {
        try {
            val file = File(UHF)
            Log.d(TAG, "power: " + file.path)
            val localFileWriterOn = FileWriter(File(UHF))
            localFileWriterOn.write(id)
            localFileWriterOn.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

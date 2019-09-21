package com.example.lfdemo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class PowerUtil {

    private static String TAG = "PowerUtil";
    private final static String SAM = "/proc/gpiocontrol/set_sam";
    private final static String UHF = "/proc/gpiocontrol/set_uhf";
      public static void power(String id) {
          power1(id);
        power2(id);

      }


    public static void power1(String id) {
        try {

            File file = new File(SAM);
            Log.d(TAG, "power: " + file.getPath());
            FileWriter localFileWriterOn = new FileWriter(new File(SAM));
            localFileWriterOn.write(id);
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public static void power2(String id) {
        try {

            File file = new File(UHF);
            Log.d(TAG, "power: " + file.getPath());
            FileWriter localFileWriterOn = new FileWriter(new File(UHF));
            localFileWriterOn.write(id);
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

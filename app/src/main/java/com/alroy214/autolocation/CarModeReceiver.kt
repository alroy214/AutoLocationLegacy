package com.alroy214.autolocation

import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.io.DataOutputStream
import java.io.IOException

class CarModeReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val action : String? = p1?.action;
        if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(action)) {
            Log.d("CarModeReceiver", "Entered Car Mode");
            rootCommand("3")
            if(p0 != null) {
                Toast.makeText(p0, "Turning on location", Toast.LENGTH_LONG).show()
            }
        } else if (UiModeManager.ACTION_EXIT_CAR_MODE.equals(action)) {
            Log.d("CarModeReceiver", "Exited Car Mode");
            rootCommand("0")
            Toast.makeText(p0, "Turning off location", Toast.LENGTH_LONG).show()
        }
    }

    private fun rootCommand(string: String) {
        val cmd =
            arrayOf("cd /system/bin", "settings put secure location_mode $string")
        try {
            val p = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(p.outputStream)
            for (tmpCmd in cmd) {
                os.writeBytes(
                    """
                $tmpCmd
                
                """.trimIndent()
                )
            }
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

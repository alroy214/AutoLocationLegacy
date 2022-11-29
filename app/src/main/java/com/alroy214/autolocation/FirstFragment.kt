package com.alroy214.autolocation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alroy214.autolocation.databinding.FragmentFirstBinding
import android.location.LocationManager
import android.provider.Settings
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Exception


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {

            val cmds =
                arrayOf("cd /system/bin", "settings put secure location_mode 3")
            try {
                val p = Runtime.getRuntime().exec("su")
                val os = DataOutputStream(p.outputStream)
                for (tmpCmd in cmds) {
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

    var beforeEnable : String? = null
    private fun turnGpsOn(context: Context) {
        beforeEnable = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )
        val newSet = java.lang.String.format(
            "%s,%s",
            beforeEnable,
            LocationManager.GPS_PROVIDER
        )
        try {
            Settings.Secure.putString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_MODE,
                newSet
            )
        } catch (e: Exception) {
        }
    }


    private fun turnGpsOff(context: Context) {
        if (null == beforeEnable) {
            var str: String = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            if (null == str) {
                str = ""
            } else {
                val list = str.split(",".toRegex()).toTypedArray()
                str = ""
                var j = 0
                for (i in list.indices) {
                    if (list[i] != LocationManager.GPS_PROVIDER) {
                        if (j > 0) {
                            str += ","
                        }
                        str += list[i]
                        j++
                    }
                }
                beforeEnable = str
            }
        }
        try {
            Settings.Secure.putString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                beforeEnable
            )
        } catch (e: Exception) {
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package dk.nodes.template.presentation.util

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

object SharedPreferenceUtil {
    @JvmStatic
    fun writeToSharedPreferences(c: Context?) {
        val d = Date()
        val dateInMilliSec = d.time
        PreferenceManager.getDefaultSharedPreferences(c)
                .edit()
                .putLong("lastAnalysis", dateInMilliSec).apply()
        println("Written to shared pref: $dateInMilliSec")
    }

    @JvmStatic
    fun getLastAnalysisInMilliSecSharedPreferences(c: Context?): Long {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getLong("lastAnalysis", 0)
    }
}